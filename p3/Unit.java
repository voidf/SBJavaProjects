
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

public class Unit {

    public static Map<String, Throwable> testClass(String name) {
        Map<String, Throwable> results = new HashMap<>();
        try {
            Class<?> clazz = Class.forName(name);
            Object testInstance = clazz.getDeclaredConstructor().newInstance();
            Method[] methods = clazz.getDeclaredMethods();

            TreeMap<String, Method> beforeClassMethods = new TreeMap<>();
            TreeMap<String, Method> afterClassMethods = new TreeMap<>();
            TreeMap<String, Method> beforeMethods = new TreeMap<>();
            TreeMap<String, Method> afterMethods = new TreeMap<>();
            TreeMap<String, Method> testMethods = new TreeMap<>();

            for (Method method : methods) {
                if (method.getAnnotations().length > 1) {
                    throw new IllegalArgumentException("Method " + method.getName() + " has multiple annotations.");
                }
                if (method.isAnnotationPresent(Test.class)) {
                    testMethods.put(method.getName(), method);
                } else if (method.isAnnotationPresent(BeforeClass.class)) {
                    if (!Modifier.isStatic(method.getModifiers())) {
                        throw new IllegalArgumentException("@BeforeClass methods must be static.");
                    }
                    beforeClassMethods.put(method.getName(), method);
                } else if (method.isAnnotationPresent(AfterClass.class)) {
                    if (!Modifier.isStatic(method.getModifiers())) {
                        throw new IllegalArgumentException("@AfterClass methods must be static.");
                    }
                    afterClassMethods.put(method.getName(), method);
                } else if (method.isAnnotationPresent(Before.class)) {
                    beforeMethods.put(method.getName(), method);
                } else if (method.isAnnotationPresent(After.class)) {
                    afterMethods.put(method.getName(), method);
                }
            }

            // System.err.println("beforeMethods COUNT"+beforeClassMethods.size());
            for (Method method : beforeClassMethods.values()) {
                method.invoke(null);
            }

            for (Method testMethod : testMethods.values()) {
                String testName = testMethod.getName();
                for (Method beforeMethod : beforeMethods.values()) {
                    // System.err.println("testName"+testName+","+beforeMethod.getName());
                    beforeMethod.invoke(testInstance);
                }
                try {
                    testMethod.invoke(testInstance);
                    results.put(testName, null);
                } catch (InvocationTargetException e) {
                    results.put(testName, e.getCause());
                } catch (Exception e) {
                    results.put(testName, e);
                }

                for (Method afterMethod : afterMethods.values()) {
                    afterMethod.invoke(testInstance);
                }
            }

            for (Method method : afterClassMethods.values()) {
                method.invoke(null);
            }

        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Test class not found: " + name, e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    public static Map<String, Object[]> quickCheckClass(String name) {
        Map<String, Object[]> results = new HashMap<>();

        try {
            Class<?> clazz = Class.forName(name);
            Object instance = clazz.getDeclaredConstructor().newInstance();
            Method[] methods = clazz.getDeclaredMethods();
            TreeMap<String, Method> order = new TreeMap<>();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Property.class)) {
                    order.put(method.getName(), method);
                }
            }

            for (Method method : order.values()) {
                List<List<Object>> argValues = generateArgumentValues(instance, method);

                for (List<Object> args : getCombinations(argValues)) {
                    try {
                        Object[] argsArray = args.toArray();
                        boolean result = (boolean) method.invoke(instance, argsArray);

                        if (!result) {
                            results.put(method.getName(), argsArray);
                            break;
                        }
                        else {
                            results.put(method.getName(), null);
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        results.put(method.getName(), args.toArray());
                        break;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    private static List<List<Object>> generateArgumentValues(Object instance, Method method) throws Exception {
        List<List<Object>> argValues = new ArrayList<>();

        for (Parameter param : method.getParameters()) {
            Annotation[] annotations = param.getAnnotations();

            if (annotations.length != 1) {
                throw new IllegalArgumentException("Each parameter must have exactly one annotation");
            }

            Annotation annotation = annotations[0];
            switch (annotation) {
                case IntRange intRange -> {
                    List<Object> values = new ArrayList<>();
                    for (int i = intRange.min(); i <= intRange.max(); i++) {
                        values.add(i);
                    }
                    argValues.add(values);
                }
                case StringSet stringSet ->{
                    List<Object> values = new ArrayList<>();
                    for (String str : stringSet.strings()) {
                        values.add(str);
                    }
                    argValues.add(values);
                }
                case ListLength listLength -> {
                    argValues.add(generateListValues(param, listLength, instance));
                }
                case ForAll forAll -> {
                    List<Object> values = new ArrayList<>();
                    Method generator = instance.getClass().getMethod(forAll.name());
                    for (int i = 0; i < forAll.times(); i++) {
                        values.add(generator.invoke(instance));
                    }
                    argValues.add(values);
                }
                default ->
                    throw new IllegalArgumentException("Unsupported annotation: " + annotation);
            }
        }

        return argValues;
    }

    private static List<Object> generateListValues(Parameter param, ListLength listLength, Object instance) throws Exception {
        // Get the type of the elements within the list (e.g., List<@IntRange(min=1, max=5) Integer>)
        ParameterizedType paramType = (ParameterizedType) param.getParameterizedType();
        Type listType = paramType.getActualTypeArguments()[0];

        // Find the inner annotation for the List's element type (only supporting IntRange and StringSet here)
        Annotation innerAnnotation = null;
        if (param.getAnnotatedType() instanceof AnnotatedParameterizedType) {
            AnnotatedType[] annotatedArgs = ((AnnotatedParameterizedType) param.getAnnotatedType()).getAnnotatedActualTypeArguments();
            if (annotatedArgs.length > 0) {
                innerAnnotation = annotatedArgs[0].getAnnotations()[0];
            }
        }

        List<Object> possibleElements = new ArrayList<>();
        if (innerAnnotation instanceof IntRange intRange) {
            for (int i = intRange.min(); i <= intRange.max(); i++) {
                possibleElements.add(i);
            }
        } else if (innerAnnotation instanceof StringSet stringSet) {
            possibleElements.addAll(Arrays.asList(stringSet.strings()));
        } else {
            throw new IllegalArgumentException("Unsupported inner annotation in List: " + innerAnnotation);
        }

        List<Object> listValues = new ArrayList<>();
        for (int len = listLength.min(); len <= listLength.max(); len++) {
            generateListsWithLength(possibleElements, new ArrayList<>(), listValues, len);
        }
        return listValues;
    }

    private static void generateListsWithLength(List<Object> elements, List<Object> currentList, List<Object> result, int len) {
        if (currentList.size() == len) {
            result.add(new ArrayList<>(currentList));
            return;
        }

        for (Object element : elements) {
            currentList.add(element);
            generateListsWithLength(elements, currentList, result, len);
            currentList.remove(currentList.size() - 1);
        }
    }

    private static List<List<Object>> getCombinations(List<List<Object>> lists) {
        List<List<Object>> combinations = new ArrayList<>();
        int maxCombinations = 100;

        getCombinationsRecursive(combinations, new ArrayList<>(), lists, 0, maxCombinations);
        return combinations;
    }

    private static void getCombinationsRecursive(
            List<List<Object>> combinations,
            List<Object> current,
            List<List<Object>> lists,
            int depth,
            int maxCombinations) {

        if (depth == lists.size()) {
            combinations.add(new ArrayList<>(current));
            return;
        }

        for (Object item : lists.get(depth)) {
            current.add(item);
            if (combinations.size() < maxCombinations) {
                getCombinationsRecursive(combinations, current, lists, depth + 1, maxCombinations);
            }
            current.remove(current.size() - 1);
        }
    }
}
