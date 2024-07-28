package cycling;
import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class StageResult extends Result {

    private LocalTime[] times;
    private LocalTime adjustedTime;
    private CheckpointType[] checkpointTypes;


    public StageResult(int riderId, CheckpointType[] checkpointTypes, LocalTime... times) {
        super(riderId, getTotalTime(times), 0, 0);
        this.times = getCumulativeTimes(times);
        this.adjustedTime = elapsedTime;
        this.checkpointTypes = checkpointTypes;

    }

    public LocalTime[] getCumulativeTimes(LocalTime... times) {
        // returns the total time of a race
        LocalTime[] cumulativeTimes = new LocalTime[times.length];
        LocalTime start = times[0];
        for (int i = 0; i < times.length; i++) {
            long nanoseconds = TimeUnit.NANOSECONDS.convert(Duration.between(start, times[i]).toNanos(), TimeUnit.NANOSECONDS);
            cumulativeTimes[i] = LocalTime.ofNanoOfDay(nanoseconds);
            start = times[i];
        }
        return cumulativeTimes;
    }

    public LocalTime[] getTimes() {
        return times;
    }

    public CheckpointType[] getCheckpointTypes() {
        return checkpointTypes;
    }

    public LocalTime getSpecificTime(int index) {
        return times[index];
    }

    public void addPoints(int points) {
        this.totalPoints += points;
    }

    public void addMountainPoints(int points) {
        this.mountainPoints += points;
    }

    public void setAdjustedTime(LocalTime adjustedTime) {
        this.adjustedTime = adjustedTime;
    }

    public LocalTime getAdjustedTime() {
        return this.adjustedTime;
    }

    public static LocalTime getTotalTime(LocalTime[] times) {
        // returns the total time of a rider in a race
        LocalTime start = times[0];
        LocalTime end = times[times.length - 1];
        long nanoseconds = TimeUnit.NANOSECONDS.convert(Duration.between(start, end).toNanos(), TimeUnit.NANOSECONDS);
        return LocalTime.ofNanoOfDay(nanoseconds);
    }
}