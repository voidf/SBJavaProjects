
import java.util.HashMap;

public class Station extends Entity {
  private Station(String name) { super(name); }
  static HashMap<String, Station> _hm = new HashMap<>();

  public static Station make(String name) {
    if (_hm.containsKey(name)) {
      return _hm.get(name);
    }
    var e = new Station(name);
    _hm.put(name, e);
    return e;
  }
}
