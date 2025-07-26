
// Integer Assertion Class
public class IntAssertion {

    private final int value;

    public IntAssertion(int value) {
        this.value = value;
    }

    public IntAssertion isEqualTo(int i2) throws Exception {
        if (value != i2) {
            throw new Exception("Expected int to be " + i2);
        }
        return this;
    }

    public IntAssertion isLessThan(int i2) throws Exception {
        if (value >= i2) {
            throw new Exception("Expected int to be less than " + i2);
        }
        return this;
    }

    public IntAssertion isGreaterThan(int i2) throws Exception {
        if (value <= i2) {
            throw new Exception("Expected int to be greater than " + i2);
        }
        return this;
    }
}
