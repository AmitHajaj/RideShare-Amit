package app.model.utils;

import app.model.users.Driver;
import app.model.users.Passenger;

public class UserEdge {
    private final Driver drive;
    private final Passenger passenger;
    private double weight;
    private final int id;
    private static int  keyGenerator = 0;


    public UserEdge(Driver drive, Passenger ride){
        this.id = ++keyGenerator;
        this.drive = drive;
        this.passenger = ride;
    }

    /*  GETTERS */

    public int getId() { return id; }

    public Driver getDrive() {
        return drive;
    }

    public Passenger getRider() {
        return passenger;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

//    public Coordinates getLocation(){
//        return drive.getLocation();
//    }
}
