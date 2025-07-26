
import java.util.HashMap;
import java.util.HashSet;


public class Train extends Entity {
  private Train(String name) { super(name); }
  static HashMap<String, Train> _hm = new HashMap<>();
  public static Train make(String name) {
    if (_hm.containsKey(name)) {
      return _hm.get(name);
    }
    var e = new Train(name);
    _hm.put(name, e);
    return e;
  }
}
