package app.model;

/* third party */
import java.util.*;

import app.controller.Simulator;
import app.model.interfaces.ElementOnMap;
import app.model.interfaces.Located;
import org.jetbrains.annotations.NotNull;

/* local */
import app.controller.GraphAlgo;
import utils.HashPriorityQueue;
import utils.SimulatorLatch;

/* static imports */
import static utils.LogHandler.LOGGER;
import static utils.Utils.*;


/*
 * todo make long way with stop-point instead of paths
 *      make drives thread-pool
 */
public class Drive implements Runnable, ElementOnMap, Located {
    private static final int MAX_PASSENGERS_NUM = 2; //todo set to 3
    private final int id;
    private final HashPriorityQueue<Rider> passengers;
    private final Date leaveTime;
    private Path path;
    private Node currNode, destination;
    private Rider onTheWayTo;
    private double originalTime, detoursTime;
    private boolean pathChange;
    Timer timer = new Timer();
    private static SimulatorLatch latch = SimulatorLatch.INSTANCE;
    /** CONSTRUCTORS */

    private Drive(Date leaveTime) {
        this.id = UserMap.keyGenerator.incrementAndGet();
        this.leaveTime = leaveTime;
        this.passengers = new HashPriorityQueue<>(
                Comparator.comparingDouble(rider -> rider.getNextStop().distanceTo(currNode))
        );
    }

    public Drive(@NotNull Path path, Date leaveTime) {
        this(leaveTime);
        initPathVariables(path);
    }

    public Drive(@NotNull Node src, @NotNull Node dst, Date leaveTime) {
        this(leaveTime);
        initPathVariables(Objects.requireNonNull(GraphAlgo.getShortestPath(src, dst)));
    }

    public void initPathVariables(@NotNull Path path) {
        setPath(path);
        currNode = path.getSrc();
        destination = path.getDest();
        originalTime = path.getWeight();
    }



    /** RUN */

    @Override
    public void run() {
        LOGGER.fine("Drive "+ id +" started.");
        validate(getPath() != null, "can't start a drive if path is null. Drive owner id - " + id);

        while(!passengers.isEmpty() || currNode!=getDest()){
            getNextDest();

            driveToNextStop();
        }

        UserMap.INSTANCE.finishUserEvent(this);
        LOGGER.finest("Drive "+ id +" finishUserEvent!.");
    }

    private void driveToNextStop(){
        pathChange = false;

        List<Node> nodes = getPath().getNodes();
        Iterator<Node> nodeIter = getPath().iterator();
        Node nextNode = nodeIter.next();

        while(nodeIter.hasNext()){

            currNode = nextNode;
            nextNode = nodeIter.next();

            long timeToNextNode = currNode.getEdgeTo(nextNode).getWeight();

            sleep(timeToNextNode);

            originalTime -= timeToNextNode;

            lock(false);

            currNode = nextNode;

            if(pathChange){
                return;
            }


            latch.waitIfPause();
        }
        pathChange = true;
    }

    TimerTask moveToNext = new TimerTask(){

        @Override
        public void run() {
            Iterator<Node> nodeIter = getPath().iterator();
            Node nextNode = nodeIter.next();

            while(nodeIter.hasNext()){
                currNode = nextNode;
                nextNode = nodeIter.next();

                long timeToNextNode = currNode.getEdgeTo(nextNode).getWeight();

                sleep(timeToNextNode);

                originalTime -= timeToNextNode;

                lock(false);

                currNode = nextNode;

            }
        }
    };

    /**
     * todo improve detour time heuristic
     *
     * add  detour to pick new passenger
     * @param rider
     */
    public void addPassenger(Rider rider){
        /* extend path to pick up pedestrian */
        rider.markTaken();
        addStop(rider);

        if(passengers.peek() == rider){
            if(onTheWayTo != null) {
                onTheWayTo.setCarNextTarget(false);
            }
            onTheWayTo = rider;
            detoursTime += distTo(onTheWayTo.getDest()) + distTo(onTheWayTo.getCurrentNode());
            setSecondaryPathTo(onTheWayTo.getNextStop());
        }
    }

    private void getNextDest(){//System.out.println(this.currNode.getId());
        if(pathChange){
            if (!passengers.isEmpty()) {
                onTheWayTo = passengers.poll();

                if (!onTheWayTo.isCarNextTarget()) { /* passenger picked up */
//                    System.out.println(this.id + " picked up " + onTheWayTo.getId());
                    addStop(onTheWayTo);
                    passengers.add(onTheWayTo);
                    onTheWayTo.setCarNextTarget(true);
                    setSecondaryPathTo(onTheWayTo.getNextStop());
                    UserMap.INSTANCE.finishUserEvent(onTheWayTo);
                } else { /* passenger dropped */
                    if (currNode == onTheWayTo.getDest()) {
//                        System.out.println(this.id + " dropped up " + onTheWayTo.getId());
                        getNextDest();
                    }
//                        setSecondaryPathTo(rider.getNextStop());
                }
            } else {
                setSecondaryPathTo(getDest());
            }
        }
    }

    public void setSecondaryPathTo(Node dst) {//todo add null check
        this.pathChange = true;

        /* set new path without first node */
        Path shortestPath = GraphAlgo.getShortestPath(getCurrentNode(), dst);
        shortestPath.getNodes().remove(0);

        setPath(shortestPath);
    }





    /* TODO add multiple middle paths (TSP):
        public void addPassenger(Path currPath) {
            this.currPath = currPath;
        }
    */
    private void sleep(long sleepTime ) {
        if(sleepTime <1 ){
            LOGGER.warning("drive " + getId()+" sleep time "+ FORMAT(sleepTime) +" seconds is too small.");
        }

        try {
            Thread.sleep( (long) (sleepTime * 1000 / Simulator.INSTANCE.speed()));
        } catch (InterruptedException e) {
            LOGGER.severe(e.getMessage() +"\n"+ Arrays.toString(e.getStackTrace()));
        }
    }


    public double distTo(Node node){
        double minDist = Math.min(getCurrentNode().distanceTo(node), getDest().distanceTo(node));

        for(Rider rider: getPassengers()) {
            minDist = Math.min(minDist, rider.getDest().distanceTo(node));
            if (!rider.isCarNextTarget()) {
                minDist = Math.min(minDist, rider.getCurrentNode().distanceTo(node));
            }
        }
        return minDist;
    }

    /** GETTERS */

    public double getDetoursTime() {
        return detoursTime;
    }

//    private Node getNextPassengerStop(ElementOnMap rider){
//        return rider.isPickedUp()? rider.getDest() : rider.getCurrentNode();
//    }

    public boolean canSqueezeOneMore(){
        return passengers.size() < MAX_PASSENGERS_NUM;
    }

    public double getOriginalTime() {
        return originalTime;
    }

    @Override
    public GeoLocation getLocation(){
        return currNode.getLocation();
    }

    @Override
    public Node getDest() {
        return this.destination;
//        return paths.get(paths.size() -1).getDest(); // TODO check if last
    }

    @Override
    public Node getCurrentNode() {
        return currNode;
    }

    @Override
    public int getId() {
        return id;
    }

    public Set<Rider> getPassengers() {
        return passengers;
    }

    @Override
    public Date getStartTime() {
        return leaveTime;
    }

    private void addStop(Rider rider){
        passengers.add(rider);
    }

    @Override
    public Node getNextStop() {
        return getDest();
    }

    public Path getPath(){
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public List<Node> getNodes(){
        return getPath().getNodes();
    }

    @Override
    public String toString() {
        return "Drive{" +
                "id=" + id +
                ", passengers=" + passengers.size() +
                ", leaveTime=" + FORMAT(leaveTime) +
                ", pathSize=" + getPath().getSize() +
                ", currNode=" + currNode.getId() +
                '}';
    }
}