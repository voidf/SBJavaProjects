package jrails;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Model implements Serializable {

    private static final long serialVersionUID = 1L;
    private static String storagePath = "./data"; // Global storage path
    private static final String ID_FILE_NAME = "_"; // File to store auto-increment ID
    private int id = 0; // Unique ID for each instance

    // Method to set the global storage path
    public static void setStoragePath(String path) {
        storagePath = path;
    }

    // Method to generate and get a unique ID for each class
    private static int generateUniqueId(Class<?> clazz) throws IOException {
        File idFile = new File(storagePath + "/" + clazz.getSimpleName() + "/" + ID_FILE_NAME);
        int uniqueId = 1;

        // Ensure the directory structure exists before proceeding with file operations
        if (!idFile.getParentFile().exists()) {
            idFile.getParentFile().mkdirs();
        }

        if (idFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(idFile))) {
                uniqueId = Integer.parseInt(reader.readLine()) + 1;
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(idFile))) {
            writer.println(uniqueId);
        }

        return uniqueId;
    }

    // Save the current model to disk, handling @Column fields only
    public void save() {
        if (this.id == 0) {
            try {
                this.id = generateUniqueId(this.getClass());
            } catch (IOException e) {
                throw new RuntimeException("Error generating unique ID", e);
            }
        }

        File dir = new File(storagePath + "/" + this.getClass().getSimpleName());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, this.id + ".ser");

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(getSerializedData());
            System.out.println("Save to " + file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Error saving model to file", e);
        }
    }

    public int id() {
        return this.id;
    }

    public static <T> T find(Class<T> c, int id) {
        File file = new File(storagePath + "/" + c.getSimpleName() + "/" + id + ".ser");
        if (!file.exists()) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            HashMap<String, Object> data = (HashMap<String, Object>) ois.readObject();
            return deserializeData(c, data);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error loading model from file", e);
        }
    }

    // Helper method to serialize @Column annotated fields
    private HashMap<String, Object> getSerializedData() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("id", this.id);

        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                field.setAccessible(true);
                try {
                    Object value = field.get(this);
                    validateColumnType(field);
                    data.put(field.getName(), value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Error accessing field " + field.getName(), e);
                }
            }
        }
        return data;
    }

    // Helper method to validate @Column field types
    private void validateColumnType(Field field) {
        Class<?> type = field.getType();
        if (!(type == String.class || type == int.class || type == boolean.class)) {
            throw new RuntimeException("Invalid @Column type for field " + field.getName() + ": " + type.getName());
        }
    }

    public static <T> List<T> all(Class<T> c) {
        List<T> results = new ArrayList<>();
        File dir = new File(storagePath + "/" + c.getSimpleName());
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                if (file.getName().endsWith(".ser")) {
                    int id = Integer.parseInt(file.getName().replace(".ser", ""));
                    results.add(find(c, id));
                }
            }
        }
        return results;
    }

    // Helper method to deserialize data into an instance of the class
    private static <T> T deserializeData(Class<T> clazz, HashMap<String, Object> data) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Column.class) && data.containsKey(field.getName())) {
                    field.setAccessible(true);
                    field.set(instance, data.get(field.getName()));
                }
            }
            if (data.containsKey("id")) {
                Field idField = clazz.getSuperclass().getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(instance, data.get("id"));
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing data to model", e);
        }
    }

    // Delete the current model from disk
    public void destroy() {
        if (this.id == 0) {
            throw new RuntimeException("Model is not saved in the database.");
        }

        File file = new File(storagePath + "/" + this.getClass().getSimpleName() + "/" + this.id + ".ser");
        if (file.exists() && !file.delete()) {
            throw new RuntimeException("Error deleting model file");
        }
        this.id = 0; // Reset ID to indicate it's no longer saved
    }

    // Reset the database for a particular model class by deleting all files in the folder
    public static void reset() {
        deleteDirectory(new File(storagePath));
        // File dir = new File(storagePath);
        // if (dir.exists()) {
        //     for (File file : dir.listFiles()) {
        //         if (!file.delete()) {
        //             throw new RuntimeException("Error deleting file: " + file.getName());
        //         }
        //     }
        //     dir.delete();
        // }
    }

    public static void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }
}
