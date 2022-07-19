package app.model;

import org.jetbrains.annotations.NotNull;
import java.util.Date;

import app.controller.GraphAlgo;
import app.model.interfaces.ElementsOnMap;
import static utils.Utils.FORMAT;



public class Rider implements ElementsOnMap  {
    private final Date askTime;
    private final Node currNode, destination;
    private final String id;
    private final Path path;
    private boolean taken;

    public Rider(String id, @NotNull Node currNode, @NotNull Node destination, Date askTime) {
        this.id = "R" + id;
        this.askTime = askTime;
        this.destination = destination;
        this.currNode = currNode;
        path = GraphAlgo.getShortestPath(currNode, destination);
    }

    public void markTaken() {
        /* remove this from waiting list */
        taken = true;
        UserMap.INSTANCE.pickPedestrian(this);
    }

    public boolean isTaken() {
        return taken;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public Node getDestination() { return destination; }

    @Override
    public Node getCurrNode() { return currNode; }

    @Override
    public Date getStartTime() { return askTime; }

    @Override
    public String getId() { return id; }

    @Override
    public GeoLocation getLocation() { return currNode.getLocation(); }

    @Override
    public String toString() {

        return "Rider{" +
                "id='" + id.substring(1) + '\'' +
                ", long id='" + id + '\'' +
                ", askTime=" + FORMAT(askTime) +
                ", currNode=" + currNode.getOsmID() +
                ", destination=" + destination.getOsmID() +
                '}';
    }
}