import java.util.List;
import java.util.Objects;

public class MoveEvent implements Event {
    public final Train t;
    public final Station s1, s2;

    public MoveEvent(Train t, Station s1, Station s2) {
        this.t = t;
        this.s1 = s1;
        this.s2 = s2;
    }

    public boolean equals(Object o) {
        if (o instanceof MoveEvent e) {
            return t.equals(e.t) && s1.equals(e.s1) && s2.equals(e.s2);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(t, s1, s2);
    }

    public String toString() {
        return "Train " + t + " moves from " + s1 + " to " + s2;
    }

    public List<String> toStringList() {
        return List.of(t.toString(), s1.toString(), s2.toString());
    }

    public void executeWithoutCheck(MBTA mbta) {
        var ts = mbta.trainStates.get(t);
        mbta.station2Train.put(s2, t);
        mbta.station2Train.remove(s1);
        ts.currStation = s2;
    }

    public void replayAndCheck(MBTA mbta) {
        // Check if the train is currently at s1
        var ts = mbta.trainStates.get(t);
        if (ts == null || !ts.currStation.equals(s1)) {
            throw new IllegalStateException("Train " + t + " is not at the expected station " + s1);
        }

        List<Station> lineStations = mbta.lineHM.get(ts.lineName);
        int currentIndex = lineStations.indexOf(s1);
        int nextIndex = lineStations.indexOf(s2);
        if (currentIndex == -1 || nextIndex == -1 || (currentIndex + 1 != nextIndex && currentIndex - 1 != nextIndex)) {
            throw new IllegalStateException("Invalid move from " + s1 + " to " + s2 + " for train " + t);
        }
        var tr2 = mbta.station2Train.get(s2);
        if(tr2 != null){
            throw new IllegalStateException("Invalid move from " + s1 + " to " + s2 + " for train " + t + ": Destination not empty " + tr2);
        }
        var tr1 = mbta.station2Train.get(s1);
        if(!tr1.equals(t)){
            throw new IllegalStateException("Invalid move from " + s1 + " to " + s2 + " for train " + t + ": Train not in Source stop");
        }
        executeWithoutCheck(mbta);
    }
}