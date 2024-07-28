package cycling;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Race implements Serializable {
    // initialising and declaring variables
    private int raceId;
    private String name;
    private String description;
    private Stage[] stages;
    public static int lastRaceId = 0;

    private Result[] raceResults;

    // object creation method
    public Race(String name, String description, Stage[] stages) {
        // setters
        this.raceId = lastRaceId++;
        this.name = name;
        this.description = description;
        this.stages = stages;
        this.raceResults = new Result[0];
    }

    // getters to return individual variables
    public int getRaceId() {return raceId;}
    public String getName() {return name;}
    public Stage[] getStages() {return stages;}
    public String getDescription() {return description;}

    // getter to return an array of all the stage ids of the race
    public int[] getStageIds() {
        // initialises an integer list to then append each stage id to
        List<Integer> stageIds= new ArrayList<>();
        for (Stage stage : stages) {
            stageIds.add(stage.getStageId());
        }
        // converts integer list to integer array
        int[] array = new int[stageIds.size()];
        for(int i = 0; i < stageIds.size(); i++) array[i] = stageIds.get(i);
        return array;
    }

    // method to add a stage to a race
    public void addStage(Stage stage) {
        // creates a new array and appending the wanted value to the end of it
        Stage[] newStages = new Stage[this.stages.length + 1];
        System.arraycopy(this.stages, 0, newStages, 0, this.stages.length);
        newStages[newStages.length - 1] = stage;
        this.stages = newStages;
    }

    // method to remove a stage from a race
    public void removeStage(Stage stage) {
        // creates a list to be able to remove the last element
        List<Stage> newStages = new ArrayList<>();
        for (Stage value : this.stages) {
            if (value != stage) {
                newStages.add(value);
            }
        }
        // converts the list to an array
        this.stages = newStages.toArray(new Stage[0]);
    }

    // method to add the result of a race
    public void addRaceResult(Result result) {
        // initialises an array of Result to store the newest result
        Result[] newResults = new Result[this.raceResults.length + 1];
        System.arraycopy(this.raceResults, 0, newResults, 0, this.raceResults.length);
        newResults[newResults.length - 1] = result;
        this.raceResults = newResults;
    }

    // setter method to input race result to the object
    public void setRaceResults(Result[] raceResults) {
        this.raceResults = raceResults;
    }

    // method to sort race results by time
    public Result[] getSortedRaceResultsByTime() {
        // loops through the array twice and comparing values by time
        for (int i = 0; i < this.raceResults.length; i++) {
            for (int j = i + 1; j < this.raceResults.length; j++) {
                if (this.raceResults[i].getTime().isAfter(this.raceResults[j].getTime())) {
                    Result temp = this.raceResults[i];
                    this.raceResults[i] = this.raceResults[j];
                    this.raceResults[j] = temp;
                }
            }
        }
        return raceResults;
    }

    // method to sort the race results based on mountainous stages
    public Result[] getSortedRaceResultsByMountain() {
        // loops through the results twice and comparing the values
        for (int i = 0; i < this.raceResults.length; i++) {
            for (int j = i + 1; j < this.raceResults.length; j++) {
                if (this.raceResults[i].getMountainPoints() < this.raceResults[j].getMountainPoints()) {
                    Result temp = this.raceResults[i];
                    this.raceResults[i] = this.raceResults[j];
                    this.raceResults[j] = temp;
                }
            }
        }
        return raceResults;
    }

    // method to sort the race results based on points
    public Result[] getSortedRaceResultsByPoints() {
        // loops through results twice and comparing the values
        for (int i = 0; i < this.raceResults.length; i++) {
            for (int j = i + 1; j < this.raceResults.length; j++) {
                if (this.raceResults[i].getPoints() < this.raceResults[j].getPoints()) {
                    Result temp = this.raceResults[i];
                    this.raceResults[i] = this.raceResults[j];
                    this.raceResults[j] = temp;
                }
            }
        }
        return raceResults;
    }
}