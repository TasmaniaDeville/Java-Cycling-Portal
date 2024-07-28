package cycling;
import java.util.Arrays;
import java.io.Serializable;

public class Team implements Serializable{

    private String name;
    private String description;
    private int teamId;
    private Rider[] riders;

    public static int lastTeamId = 0;

    public Team(String name, String description, Rider[] riders) {
        this.name = name;
        this.description = description;
        this.teamId = lastTeamId++;
        this.riders = riders;
    }

    public int getTeamId(){
        return teamId;
    }

    public Rider[] getRiders() {

        return this.riders;
    }

    public static void main(String[] args) {
    }

    public String getName(){
        return name;
    }

    public void addRider(Rider rider) {
        // adds a rider to the list of riders
        Rider[] newRiders = Arrays.copyOf(this.riders, this.riders.length + 1);
        newRiders[newRiders.length - 1] = rider;
        this.riders = newRiders;
    }

    public void removeRider(Rider rider) {
        // removes a rider based on the rider id
        Rider[] newRiders = new Rider[this.riders.length - 1];
        int j = 0;
        for (Rider value : this.riders) {
            if (value != rider) {
                newRiders[j] = value;
                j++;
            }
        }
        this.riders = newRiders;
    }

}