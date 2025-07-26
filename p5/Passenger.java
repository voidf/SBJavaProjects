
import java.util.HashMap;

public class Passenger extends Entity {
  private Passenger(String name) { super(name); }
  static HashMap<String, Passenger> _hm = new HashMap<>();

  public static Passenger make(String name) {
    if (_hm.containsKey(name)) {
      return _hm.get(name);
    }
    var e = new Passenger(name);
    _hm.put(name, e);
    return e;
  }
}
