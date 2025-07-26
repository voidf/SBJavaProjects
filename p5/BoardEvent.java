import java.util.*;

public class BoardEvent implements Event {
    public final Passenger p;
    public final Train t;
    public final Station s;

    public BoardEvent(Passenger p, Train t, Station s) {
        this.p = p;
        this.t = t;
        this.s = s;
    }

    public boolean equals(Object o) {
        if (o instanceof BoardEvent e) {
            return p.equals(e.p) && t.equals(e.t) && s.equals(e.s);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(p, t, s);
    }

    public String toString() {
        return "Passenger " + p + " boards " + t + " at " + s;
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
        if (ps == null || !ps.currStation.equals(s)) {
            String debuginfo = ps == null? "null Expected:" + s:ps.currStation.toString() + " Expected:"+ s;
            throw new IllegalStateException("Passenger " + p + " is not at station " + s + " debuginfo:" + debuginfo);
        }

        var currStations = mbta.journeyHM.get(ps.journeyName);
        var expected = currStations.get(ps.journeyIdx);
        if(expected != s) {
            throw new IllegalStateException("Passenger " + p + " Board at unexpected station " + t + ", expected:" + expected);
        }

        ts.passengers.add(p);
        ps.currTrain = t;
        ps.currStation = null;
    }
}