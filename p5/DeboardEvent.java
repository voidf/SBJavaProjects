import java.util.*;

public class DeboardEvent implements Event {
    public final Passenger p;
    public final Train t;
    public final Station s;

    public DeboardEvent(Passenger p, Train t, Station s) {
        this.p = p;
        this.t = t;
        this.s = s;
    }

    public boolean equals(Object o) {
        if (o instanceof DeboardEvent e) {
            return p.equals(e.p) && t.equals(e.t) && s.equals(e.s);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(p, t, s);
    }

    public String toString() {
        return "Passenger " + p + " deboards " + t + " at " + s;
    }

    public List<String> toStringList() {
        return List.of(p.toString(), t.toString(), s.toString());
    }

    public void replayAndCheck(MBTA mbta) {
        var ts = mbta.trainStates.get(t);
        if (ts == null || !ts.currStation.equals(s)) {
            throw new IllegalStateException("Train " + t + " is not at station " + s);
        }

        var ps = mbta.passengerStates.get(p);
        if (!ts.passengers.contains(p) || ps == null || !ps.currTrain.equals(t)) {
            throw new IllegalStateException("Passenger " + p + " is not on train " + t);
        }

        var currStations = mbta.journeyHM.get(ps.journeyName);
        var expected = currStations.get(ps.journeyIdx+1);
        if(expected != s) {
            throw new IllegalStateException("Passenger " + p + " Deboard at unexpected station " + t);
        }

        ps.currTrain = null;
        ps.currStation = ts.currStation;
        ps.journeyIdx += 1;
        ts.passengers.remove(p);
        // t.removePassenger(p);
        // p.setCurrentStation(s);
    }
}