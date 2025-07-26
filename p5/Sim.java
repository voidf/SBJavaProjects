import java.io.*;
import java.util.*;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Sim {

    public static void run_sim(MBTA mbta, Log log) {
        // Create a lock for each station
        var trainStationLocks = new ConcurrentHashMap<Station, ReentrantLock>();
        var stationLocks = new ConcurrentHashMap<Station, ReentrantLock>();
        var stationConditions = new ConcurrentHashMap<Station, Condition>();
        var trainLocks = new ConcurrentHashMap<Train, ReentrantLock>();

        // Initialize locks and conditions for all stations
        for (List<Station> line : mbta.lineHM.values()) {
            for (Station station : line) {
                trainStationLocks.putIfAbsent(station, new ReentrantLock());
                stationLocks.putIfAbsent(station, new ReentrantLock());
                stationConditions.putIfAbsent(station, stationLocks.get(station).newCondition());
            }
        }
        ConcurrentHashMap<String, Passenger> undonePassengers = new ConcurrentHashMap<>();

        // Create threads for each train
        var trainThreads = new ArrayList<Thread>();
        for (var pair : mbta.lineHM.entrySet()) {
            var sta = pair.getValue();
            var name = pair.getKey();
            Train tr = Train.make(name); // assume train name equals to line name
            trainLocks.putIfAbsent(tr, new ReentrantLock());
            trainThreads.add(new Thread(() -> {
                // System.err.println(Thread.currentThread().getName() + " started (train" + name);
                List<Station> lineStations = sta;
                int direction = 1; // 1 for forward, -1 for reverse
                int index = 0;

                var initStation = lineStations.get(index);
                var lockA = trainStationLocks.get(initStation);
                var prvlocktrace = initStation;
                lockA.lock();
                // System.err.println("[TRAIN LOCK]" + initStation + " " + name);
                try {
                    var staLock = stationLocks.get(initStation);
                    staLock.lock();
                    stationConditions.get(initStation).signalAll();
                    staLock.unlock();
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    lockA.unlock();
                    return;
                }

                while (undonePassengers.size() > 0) {
                    Station currentStation = lineStations.get(index);
                    Station nextStation = lineStations
                            .get((index + direction + lineStations.size()) % lineStations.size());

                    // Lock the next station
                    ReentrantLock lockB = trainStationLocks.get(nextStation);
                    // System.err.println("[TRAIN tryLOCK]" + nextStation + " " + name);
                    lockB.lock();
                    // System.err.println("[TRAIN LOCK]" + nextStation + " " + name);
                    try {
                        // Log the train's movement

                        var tLock = trainLocks.get(tr);
                        tLock.lock();
                        var ev = new MoveEvent(tr, currentStation, nextStation);
                        ev.replayAndCheck(mbta);
                        log.train_moves(tr, currentStation, nextStation);
                        tLock.unlock();
                        // System.err.println("[Arrived]"+currentStation.toString()+tr+mbta.station2Train.get(currentStation));

                        lockA.unlock();
                        // System.err.println("[TRAIN UNLOCK]" + prvlocktrace + " " + name);
                        lockA = lockB;
                        prvlocktrace = nextStation;

                        // Notify passengers waiting to board
                        var staLock = stationLocks.get(nextStation);
                        // System.err.println("[TRAIN tryLOCK stalock]" + nextStation + " " + name);
                        staLock.lock();
                        // System.err.println("[TRAIN LOCK stalock]" + nextStation + " " + name);
                        Condition condition = stationConditions.get(nextStation);
                        condition.signalAll();
                        staLock.unlock();
                        // System.err.println("[TRAIN UNLOCK stalock]" + nextStation + " " + name);

                        // Wait at the station for 10 milliseconds
                        Thread.sleep(10);
                        // System.err.println("[Leaved]"+currentStation.toString()+tr+mbta.station2Train.get(currentStation));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        lockB.unlock();
                        // System.err.println("interrupted "+name);
                        return;
                    }

                    // Move index and possibly reverse direction
                    index = (index + direction + lineStations.size()) % lineStations.size();
                    if (index == 0 || index == lineStations.size() - 1) {
                        direction *= -1;
                    }
                }
                // System.err.println("<<DONE Train>>" + name);
            }));
        }

        // Create threads for each passenger
        var passengerThreads = new ArrayList<Thread>();
        for (var pair : mbta.journeyHM.entrySet()) {
            var sta = pair.getValue();
            var name = pair.getKey();
            Passenger p = Passenger.make(name); // assume train name equals to line name

            undonePassengers.put(name, p);
            passengerThreads.add(new Thread(() -> {
                // System.err.println(Thread.currentThread().getName() + " started (passenger" + name);
                List<Station> journey = sta;
                try {

                    for (int i = 0; i < journey.size() - 1; i++) {
                        Station currentStation = journey.get(i);
                        // ensure current train can arrive next stop
                        Condition condition = stationConditions.get(currentStation);
                        Train currTrain;
                        ReentrantLock tLock;
                        while (true) {
                            currTrain = mbta.station2Train.get(currentStation);
                            if (currTrain == null) {
                                var staLock = stationLocks.get(currentStation);
                                staLock.lock();
                                // System.err.println("PW1"+name+currentStation);
                                condition.await();
                                // System.err.println("PA1"+name+currentStation);
                                staLock.unlock();
                                continue;
                            }
                            // currTrain = mbta.station2Train.get(currentStation);
                            tLock = trainLocks.get(currTrain);
                            tLock.lock();
                            currTrain = mbta.station2Train.get(currentStation);
                            if (currTrain == null) { // Double check
                                // System.err.println("PWW1"+name+currentStation);
                                tLock.unlock();
                                // System.err.println("PAA1"+name+currentStation);
                                continue;
                            }

                            var currLine = mbta.trainStates.get(currTrain).lineName;
                            var lineSta = mbta.lineHM.get(currLine);
                            if (lineSta.contains(journey.get(i)) && lineSta.contains(journey.get(i + 1))) {
                                break;
                            } else {
                                tLock.unlock();
                                var staLock = stationLocks.get(currentStation);
                                staLock.lock();
                                // System.err.println("PW2"+name+currentStation+" Invalid:");
                                condition.await();
                                // System.err.println("PA2"+name+currentStation);
                                staLock.unlock();
                                continue;
                            }
                        }

                        // var bev = new BoardEvent(p, currTrain, currentStation);
                        // bev.replayAndCheck(mbta);
                        log.passenger_boards(p, currTrain, currentStation);
                        tLock.unlock();

                        Station nextStation = journey.get(i + 1);
                        Condition nextCond = stationConditions.get(nextStation);
                        var staLock = stationLocks.get(nextStation);
                        // var ts = mbta.trainStates.get(currTrain);
                        while (true) {
                            tLock = trainLocks.get(currTrain);
                            tLock.lock();
                            var nextStaTr = mbta.station2Train.get(nextStation);
                            if (nextStaTr == null || !nextStaTr.equals(currTrain)) {
                                tLock.unlock();
                                staLock.lock();
                                // System.err.println("PW3"+name+nextStation+currTrain+nextStaTr);
                                nextCond.await();
                                // System.err.println("PA3"+name+nextStation);
                                staLock.unlock();
                                continue;
                            }
                            break;
                        }
                        // var ubev = new DeboardEvent(p, currTrain, nextStation);
                        // ubev.replayAndCheck(mbta);
                        log.passenger_deboards(p, currTrain, nextStation);
                        tLock.unlock();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                undonePassengers.remove(name);
                // System.err.println("<<DONE PASSENGER>>" + name + undonePassengers.size());
            }));
        }

        // Start all threads
        passengerThreads.forEach(Thread::start);
        trainThreads.forEach(Thread::start);

        // Wait for all threads to complete
        passengerThreads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        trainThreads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("usage: ./sim <config file>");
            System.exit(1);
        }

        MBTA mbta = new MBTA();
        mbta.loadConfig(args[0]);

        Log log = new Log();

        run_sim(mbta, log);

        String s = new LogJson(log).toJson();
        PrintWriter out = new PrintWriter("log.json");
        out.print(s);
        out.close();

        mbta.reset();
        mbta.loadConfig(args[0]);
        Verify.verify(mbta, log);
    }
}
