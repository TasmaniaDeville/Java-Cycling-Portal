// imports the local time package
// creates a class called Result
// implements the Serializable interfac
package cycling;
import java.time.LocalTime;
import java.io.Serializable;

public class Result implements Serializable{
    // initialising variables
    int riderId;
    LocalTime elapsedTime;
    int totalPoints;
    int mountainPoints;

    // method to create the object off given inputs
    public Result(int riderId, LocalTime time, int points, int mountainPoints) {
        this.elapsedTime = time;
        this.riderId = riderId;
        this.totalPoints = points;
        this.mountainPoints = mountainPoints;
    }

    // getter methods to return specified variables
    public LocalTime getTime() {
        return elapsedTime;
    }

    public int getRiderId() {
        return riderId;
    }

    public int getPoints() {
        return totalPoints;
    }

    public int getMountainPoints() {
        return mountainPoints;
    }

    // method setters to store given values in certain variables
    public void setPoints(int points) {
        this.totalPoints = points;
    }

    public void setMountainPoints(int points) {
        this.mountainPoints = points;
    }
}