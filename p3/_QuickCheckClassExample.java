
public class _QuickCheckClassExample {

    // @Property
    // public int 

    @Property
    public boolean absNonNeg(@IntRange(min = -10, max = 10) Integer i) {
        return Math.abs(i) >= 0;
    }

    // @Property
    // public int iMi(@IntRange(min = -10, max = 10) Integer i) {
    //     return i*i;
    // }

    @Property
    public boolean startsWithHello(@StringSet(strings = {"Hello", "World"}) String s) {
        return s.length() == 5;
    }

    // @Property
    // public boolean testSetContainsFoo(@ForAll(name = "genIntSet", times = 10) Set<String> set) {
    //     return set.contains("foo");
    // }

    // public Set<String> genIntSet() {
    //     Set<String> set = new HashSet<>();
    //     set.add("foo");
    //     set.add("bar");
    //     return set;
    // }
}
