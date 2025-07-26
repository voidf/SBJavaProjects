
import java.util.Map;

public class _Executor {
    public static void main(String[] args) {
        // Map<String, Throwable> testResults = Unit.testClass("_TestClassExample");
        // System.out.println("Test Results:");
        // testResults.forEach((testName, result) -> {
        // System.out.println(testName + ": " + (result == null ? "Passed" : "Failed
        // with " + result));
        // });
        // System.out.println("Execution Log: " + _TestClassExample.getLog());

        // _TestClassExample.resetLog();

        Map<String, Object[]> quickCheckResults = Unit.quickCheckClass("_QuickCheckClassExample");
        System.out.println("\nQuickCheck Results:");
        quickCheckResults.forEach((propertyName, failingArgs) -> {
            if (failingArgs == null) {
                System.out.println(propertyName + " passed.");
            } else {
                System.out.print(propertyName + " failed with arguments: ");
                for (Object arg : failingArgs) {
                    System.out.print(arg + " ");
                }
                System.out.println();
            }
        });

        // Assertion.assertThat(true).isTrue();
        // Assertion.assertThat(false).isFalse();
        // Assertion.assertThat(false).isEqualTo(false);

        // Assertion.assertThat("Hello").contains("H").isInstanceOf(String.class).isInstanceOf(Integer.class);
        // Assertion.assertThat("Hello")
    }
}
