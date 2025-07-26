
// Boolean Assertion Class
public class BooleanAssertion {

    private final boolean value;

    public BooleanAssertion(boolean value) {
        this.value = value;
    }

    public BooleanAssertion isEqualTo(boolean b2) throws Exception {
        if (value != b2) {
            throw new Exception("Expected boolean to be " + b2);
        }
        return this;
    }

    public BooleanAssertion isTrue() throws Exception {
        if (!value) {
            throw new Exception("Expected boolean to be true");
        }
        return this;
    }

    public BooleanAssertion isFalse() throws Exception {
        if (value) {
            throw new Exception("Expected boolean to be false");
        }
        return this;
    }
}
