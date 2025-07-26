    // Object Assertion Class

public class ObjectAssertion {

    private final Object obj;

    public ObjectAssertion(Object obj) {
        this.obj = obj;
    }

    public ObjectAssertion isNotNull() throws Exception {
        if (obj == null) {
            throw new Exception("Expected object to be not null");
        }
        return this;
    }

    public ObjectAssertion isNull() throws Exception {
        if (obj != null) {
            throw new Exception("Expected object to be null");
        }
        return this;
    }

    public ObjectAssertion isEqualTo(Object o2) throws Exception {
        if (!obj.equals(o2)) {
            throw new Exception("Expected objects to be equal");
        }
        return this;
    }

    public ObjectAssertion isNotEqualTo(Object o2) throws Exception {
        if (obj.equals(o2)) {
            throw new Exception("Expected objects to be not equal");
        }
        return this;
    }

    public ObjectAssertion isInstanceOf(Class c) throws Exception {
        if (!c.isInstance(obj)) {
            throw new Exception("Expected object to be instance of " + c.getName());
        }
        return this;
    }
}
