package cycling;
import java.io.Serializable;
public class Checkpoint implements Serializable{
    // initialising and declaring variables
    private int stageId;
    private Double location;
    private CheckpointType type;
    private Double averageGradient;
    private Double length;
    private int checkpointId;
    public static int lastCheckpointId = 0;

    // object creation method
    public Checkpoint(int stageId, Double location, CheckpointType type, Double averageGradient,
                      Double length) {
        this.checkpointId = lastCheckpointId++;
        this.stageId = stageId;
        this.location = location;
        this.type = type;
        this.averageGradient = averageGradient;
        this.length = length;
    }

    // functions to return the variables individually
    public int getCheckpointId() {
        return checkpointId;
    }
    public int getStageId() {
        return stageId;
    }
    public Double getLocation() {
        return location;
    }
    public CheckpointType getType() {
        return type;
    }
}