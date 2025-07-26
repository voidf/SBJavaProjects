import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
// public HashMap<String, Train> trainCache = new HashMap<>();
// public HashMap<String, Passenger> passengerCache = new HashMap<>();
// public HashMap<String, > trainCache = new HashMap<>();

// public HashMap<Train, HashSet<Passenger>> train2pass = new HashMap<>();
// public HashMap<Train, Station> train2station = new HashMap<>();
public class MBTA {
  public static class TrainState {
    volatile Station currStation;
    volatile HashSet<Passenger> passengers;
    volatile String lineName;
    // boolean isReverse;
  }

  public static class PassengerState {
    volatile Train currTrain;
    volatile Station currStation;
    volatile String journeyName;
    volatile int journeyIdx;
  }

  // Creates an initially empty simulation
  public MBTA() {
  }

  public java.util.concurrent.ConcurrentHashMap<String, List<Station>> lineHM = new java.util.concurrent.ConcurrentHashMap<>();
  public java.util.concurrent.ConcurrentHashMap<String, List<Station>> journeyHM = new java.util.concurrent.ConcurrentHashMap<>();
  public java.util.concurrent.ConcurrentHashMap<Train, TrainState> trainStates = new java.util.concurrent.ConcurrentHashMap<>();
  public java.util.concurrent.ConcurrentHashMap<Passenger, PassengerState> passengerStates = new java.util.concurrent.ConcurrentHashMap<>();
  public java.util.concurrent.ConcurrentHashMap<Station, Train> station2Train = new java.util.concurrent.ConcurrentHashMap<>();

  // Adds a new transit line with given name and stations
  public void addLine(String name, List<String> stations) {
    ArrayList<Station> sl = new ArrayList<>();
    for (String s : stations) {
      sl.add(Station.make(s));
    }
    lineHM.put(name, sl);
    Train tr = Train.make(name); // assume train name equals to line name
    MBTA.TrainState ts = new MBTA.TrainState();
    var starting = sl.get(0);
    ts.currStation = starting;
    ts.passengers = new HashSet<>();
    ts.lineName = name;
    trainStates.put(tr, ts);
    station2Train.put(starting, tr);
  }

  // Adds a new planned journey to the simulation
  public void addJourney(String name, List<String> stations) {
    ArrayList<Station> sl = new ArrayList<>();
    for (String s : stations) {
      sl.add(Station.make(s));
    }
    journeyHM.put(name, sl);
    MBTA.PassengerState ps = new MBTA.PassengerState();
    Passenger p = Passenger.make(name); // assume train name equals to line name
    ps.currStation = sl.get(0);
    ps.currTrain = null;
    ps.journeyName = name;
    ps.journeyIdx = 0;
    passengerStates.put(p, ps);

  }

  // Return normally if initial simulation conditions are satisfied, otherwise
  // raises an exception
  public void checkStart() {
    for (var pair : trainStates.entrySet()) {
      Train t = pair.getKey();
      TrainState ts = pair.getValue();
      var linesta = lineHM.get(ts.lineName);
      if (linesta == null || linesta.size() <= 0 || !ts.currStation.equals(linesta.get(0))) {
        throw new IllegalStateException("Train " + t + " is not at the beginning station.");
      }
    }
    for (var pair : passengerStates.entrySet()) {
      Passenger p = pair.getKey();
      PassengerState ps = pair.getValue();
      var journeysta = journeyHM.get(ps.journeyName);
      if (journeysta == null || journeysta.size() <= 0 || !ps.currStation.equals(journeysta.get(0))) {
        throw new IllegalStateException("Passenger " + p + " is not at the beginning station.");
      }
    }
  }

  // Return normally if final simulation conditions are satisfied, otherwise
  // raises an exception
  public void checkEnd() {
    // for (var pair : trainStates.entrySet()) {
    // Train t = pair.getKey();
    // TrainState ts = pair.getValue();
    // var linesta = lineHM.get(ts.lineName);
    // if (linesta == null || linesta.size() <= 0 ||
    // !ts.currStation.equals(linesta.get(linesta.size()-1))) {
    // throw new IllegalStateException("Train " + t + " is not at the final
    // station.");
    // }
    // }
    for (var pair : passengerStates.entrySet()) {
      Passenger p = pair.getKey();
      PassengerState ps = pair.getValue();
      var journeysta = journeyHM.get(ps.journeyName);
      if (journeysta == null || journeysta.size() <= 0
          || !ps.currStation.equals(journeysta.get(journeysta.size() - 1))) {
        throw new IllegalStateException("Passenger " + p + " is not at the final station.");
      }
    }
  }

  // reset to an empty simulation
  public void reset() {
    journeyHM.clear();
    lineHM.clear();
    trainStates.clear();
    passengerStates.clear();
    station2Train.clear();
  }

  // Adds simulation configuration from a file
  public void loadConfig(String filename) {
    Gson gson = new Gson();
    try (FileReader reader = new FileReader(filename)) {
      // Parse the JSON file into a JsonObject
      JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

      // Parse and add lines
      JsonObject lines = jsonObject.getAsJsonObject("lines");
      for (String lineName : lines.keySet()) {
        JsonArray stations = lines.getAsJsonArray(lineName);
        List<String> stationList = new ArrayList<>();
        for (JsonElement station : stations) {
          stationList.add(station.getAsString());
        }
        addLine(lineName, stationList);
      }

      // Parse and add trips
      JsonObject trips = jsonObject.getAsJsonObject("trips");

      for (String tripName : trips.keySet()) {
        JsonArray stations = trips.getAsJsonArray(tripName);
        List<String> stationList = new ArrayList<>();
        for (JsonElement station : stations) {
          stationList.add(station.getAsString());
        }
        addJourney(tripName, stationList);
      }
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to load configuration file.");
    }
  }
}
