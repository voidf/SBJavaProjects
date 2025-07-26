package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileUtils {
    private FileUtils() {
    }

    public static String loadAsString(String file) {
        StringBuilder res = new StringBuilder();
//        ByteBuffer bf = null;
        try {
            var reader = new BufferedReader(new FileReader(file));
            var sb = "";
            while ((sb = reader.readLine()) != null) {
                res.append(sb+"\n");
            }
            reader.close();
//            bf =  utils.Utils.ioResourceToByteBuffer(file,16384);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res.toString();
        //        bf.flip();
//        return bf.toString();

    }

}
