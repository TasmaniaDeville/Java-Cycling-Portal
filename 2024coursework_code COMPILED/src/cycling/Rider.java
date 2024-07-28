package cycling;
import java.io.Serializable;

public class Rider implements Serializable{
    // initialising and declaring variables
    private String name;
    private int teamID;
    private int yearOfBirth;
    private int riderId;
    public static int lastRiderId = 0;

    // method to create an object from this class
    public Rider(String name, int teamID, int yearOfBirth) {
        this.riderId = lastRiderId++;
        this.name = name;
        this.teamID = teamID;
        this.yearOfBirth = yearOfBirth;
    }

    // method getters to return the value of rider id
    public int getRiderId() {return riderId;}
}