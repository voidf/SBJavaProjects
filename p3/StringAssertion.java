
public class StringAssertion {

    private final String str;

    // @Override
    // public StringAssertion isEqualTo(Object o2) {
    //     if (!str.equals(o2)) {
    //         throw new Exception("Expected objects to be equal");
    //     }
    //     return this;
    // }
    public StringAssertion isNotNull() throws Exception {
        if (str == null) {
            throw new Exception("Expected object to be not null");
        }
        return this;
    }

    public StringAssertion isNull() throws Exception {
        if (str != null) {
            throw new Exception("Expected object to be null");
        }
        return this;
    }

    public StringAssertion isEqualTo(Object o2) throws Exception {
        if (!str.equals(o2)) {
            throw new Exception("Expected objects to be equal");
        }
        return this;
    }

    public StringAssertion isNotEqualTo(Object o2) throws Exception {
        if (str.equals(o2)) {
            throw new Exception("Expected objects to be not equal");
        }
        return this;
    }

    public StringAssertion isInstanceOf(Class c) throws Exception {
        if (!c.isInstance(str)) {
            throw new Exception("Expected object to be instance of " + c.getName());
        }
        return this;
    }

    public StringAssertion(String str) {
        // super(str);
        this.str = str;
    }

    public StringAssertion startsWith(String s2) throws Exception {
        if (!str.startsWith(s2)) {
            throw new Exception("Expected string to start with " + s2);
        }
        return this;
    }

    public StringAssertion isEmpty() throws Exception {
        if (!str.isEmpty()) {
            throw new Exception("Expected string to be empty");
        }
        return this;
    }

    public StringAssertion contains(String s2) throws Exception {
        if (!str.contains(s2)) {
            throw new Exception("Expected string to contain " + s2);
        }
        return this;
    }
}
