// imports necessary packages
package cycling;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.io.Serializable;

public class Stage implements Serializable{
    // initialises and declares variables for use
    private final int stageId;
    private final String stageName;
    private final String description;
    private final double length;
    private final LocalDateTime startTime;
    private final StageType type;
    private String state;
    public static int lastStageId = 0;
    private StageResult[] results;
    private int[] pointDistribution;

    // method to allow the creation of an object of this class
    public Stage(String stageName, String description, double length, LocalDateTime startTime, StageType type, String state) {
        this.stageId = lastStageId++;
        this.stageName = stageName;
        this.description = description;
        this.length = length;
        this.startTime = startTime;
        this.type = type;
        this.state = state;
        this.results = new StageResult[0];

        // distributes the points based on the type of stage
        switch(type){
            case FLAT:
                this.pointDistribution = new int[]{50,30,20,18,16,14,12,10,8,7,6,5,4,3,2};
                break;
            case MEDIUM_MOUNTAIN:
                this.pointDistribution = new int[]{30,25,22,19,17,15,13,11,9,7,6,5,4,3,2};
                break;
            case HIGH_MOUNTAIN, TT:
                this.pointDistribution = new int[]{20,17,15,13,11,10,9,8,7,6,5,4,3,2,1};
                break;
        }
    }

    // method getters to return specified variables
    public int getStageId() {
        return stageId;
    }

    public double getLength() {
        return length;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public String getType() {
        return type.toString();
    }
    public String getState() {
        return state;
    }


    // method setters to store values in specified variables
    public void setState(String state) {
        this.state = state;
    }

    // method to add a result to the results array
    public void addResult(StageResult result) {
        // creates a new array with the result appended
        StageResult[] newResults = new StageResult[this.results.length + 1];
        System.arraycopy(this.results, 0, newResults, 0, this.results.length);
        newResults[newResults.length - 1] = result;
        this.results = newResults;
    }

    // method to remove a result from the results array
    public void removeResult(StageResult result) {
        // creates a new list and only removes the result wanted based on a conditional statement
        ArrayList<StageResult> newResults = new ArrayList<>();
        for (StageResult value : this.results) {
            if (value != result) {
                newResults.add(value);
            }
        }
        this.results = newResults.toArray(new StageResult[0]);
    }

    // getter method to return the results array
    public StageResult[] getResults() {
        return this.results;
    }

    // method to sort the results by time
    public StageResult[] getSortedResults() {
        // loop through the results array twice and replace the indexes which are before each other
        for (int i = 0; i < this.results.length; i++) {
            for (int j = i + 1; j < this.results.length; j++) {
                if (this.results[i].getTime().isAfter(this.results[j].getTime())) {
                    StageResult temp = this.results[i];
                    this.results[i] = this.results[j];
                    this.results[j] = temp;
                }
            }
        }
        return results;
    }

    public StageResult[] getSpecificSortedResults(int index) {
        // loops through the results and checks with itself that the times are okay between stores values in the array
        for (int i = 0; i < this.results.length; i++) {
            for (int j = i + 1; j < this.results.length; j++) {
                if (this.results[i].getSpecificTime(index).isAfter(this.results[j].getSpecificTime(index))) {
                    StageResult temp = this.results[i];
                    this.results[i] = this.results[j];
                    this.results[j] = temp;
                }
            }
        }
        return results;
    }

    public StageResult[] getAdjustedResults() {
        StageResult[] adjustedResults = this.getSortedResults();
        // adjusts the results based on duration between other riders
        for (int i = 0; i < adjustedResults.length; i++) {
            for (int j = adjustedResults.length - 1; j > 0; j--) {
                long difference = Duration.between(adjustedResults[j-1].getAdjustedTime(), adjustedResults[j].getAdjustedTime()).toSeconds();
                if (difference < 1){
                    adjustedResults[j].setAdjustedTime(adjustedResults[j-1].getAdjustedTime());
                }
            }
        }
        return adjustedResults;
    }

    public void calculatePoints(int[] checkpoints, boolean isMountain) {
        // loops through the result array and sets the points
        for (StageResult result : this.getResults()){
            result.setMountainPoints(0);
            result.setPoints(0);
        }
        // generates the points distribution based on the checkpoints and terrain
        for (int i = 0; i < checkpoints.length; i++) {
            StageResult[] results = this.getSpecificSortedResults(i);
            CheckpointType type = results[0].getCheckpointTypes()[i];
            int[] points;
            if (!isMountain) {
                // if it is not a mountain it initialises the point distribution
                points = switch (type) {
                    case C4 -> new int[]{1};
                    case C3 -> new int[]{2, 1};
                    case C2 -> new int[]{5, 3, 2, 1};
                    case C1 -> new int[]{10, 8, 6, 4, 2, 1};
                    case HC -> new int[]{20, 15, 12, 10, 8, 6, 4, 2};
                    case SPRINT -> new int[]{20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
                };
            } else {
                // depending on the type of checkpoint, initialises the point distribution for a mountain type
                points = switch (type) {
                    case SPRINT -> new int[]{0};
                    case C4 -> new int[]{1};
                    case C3 -> new int[]{2, 1};
                    case C2 -> new int[]{5, 3, 2, 1};
                    case C1 -> new int[]{10, 8, 6, 4, 2, 1};
                    case HC -> new int[]{20, 15, 12, 10, 8, 6, 4, 2};
                };
            }
            // loops through results to add the mountain points and general points to the results
            for (int j = 0; j < results.length; j++) {
                if (j < points.length) {
                    results[j].addMountainPoints(points[j]);
                    results[j].addPoints(points[j]);
                }
            }
        }
    }

    public void calculateGeneralPoints(int[] checkpoints){
        // calls calculate points to determine the points for results
        this.calculatePoints(checkpoints,false);
        StageResult[] results = this.getSortedResults();
        // loops through the array and adds the wanted point distribution to the results
        for (int i = 0; i < results.length; i++) {
            if (i < this.pointDistribution.length) {
                results[i].addPoints(this.pointDistribution[i]);
            }
        }
    }

}