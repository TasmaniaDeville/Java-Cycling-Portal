package cycling;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * BadMiniCyclingPortal is a minimally compiling, but non-functioning implementor
 * of the MiniCyclingPortal interface.
 * 
 * @author Diogo Pacheco
 * @version 2.0
 *
 */
public class MiniCyclingPortalImpl implements MiniCyclingPortal {
	private List<Race> races = new ArrayList<>();
	private List<Team> teams = new ArrayList<>();
	private List<Stage> stages = new ArrayList<>();
	private List<Checkpoint> checkpoints = new ArrayList<>();

	public MiniCyclingPortalImpl(){}

	@Override
	public int[] getRaceIds() {
		List<Integer> raceIds = new ArrayList<>();

		for (Race element : this.races) {
			raceIds.add(element.getRaceId());
		}

		int[] array = new int[raceIds.size()];

		for(int i = 0; i < raceIds.size(); ++i) {
			array[i] = (Integer)raceIds.get(i);
		}

		return array;
	}

	@Override
	public int createRace(String name, String description) throws IllegalNameException, InvalidNameException {
		Iterator<Race> var3 = this.races.iterator();

		Race element;
		do {
			if (!var3.hasNext()) {
				this.checkName(name);
				Stage[] stages = new Stage[0];
				element = new Race(name, description, stages);
				this.races.add(element);
				return element.getRaceId();
			}

			element = (Race)var3.next();
		} while(!Objects.equals(element.getName(), name));

		throw new IllegalNameException();
	}

	@Override
	public String viewRaceDetails(int raceId) throws IDNotRecognisedException {
		double totalLength = 0.0;
		Race thisRace = null;
		boolean idFound = false;
		Iterator<Race> var6 = this.races.iterator();

		Race element;
		while(var6.hasNext()) {
			element = (Race)var6.next();
			if (element.getRaceId() == raceId) {
				thisRace = element;
				idFound = true;
			}
		}

		if (!idFound) {
			throw new IDNotRecognisedException();
		} else {
			Stage[] var10 = thisRace.getStages();

			for (Stage stage : var10) {
				totalLength += stage.getLength();
			}

			var6 = this.races.iterator();

			do {
				if (!var6.hasNext()) {
					return null;
				}

				element = (Race)var6.next();
			} while(element.getRaceId() != raceId);

			return  raceId + " " + element.getName() + " " + element.getDescription() + " " + element.getStageIds().length + " " + totalLength;
		}
	}

	@Override
	public void removeRaceById(int raceId) throws IDNotRecognisedException {
		boolean found = false;
		int[] stagesToRemove = this.getRaceStages(raceId);
		int[] checkpointsToRemove = new int[0];
		Iterator<Stage> var5 = this.stages.iterator();

		int checkpointId;
		while(var5.hasNext()) {
			Stage stage = (Stage) var5.next();
			checkpointId = stagesToRemove.length;

			for (int var9 = 0; var9 < checkpointId; ++var9) {
				int stageId = stagesToRemove[var9];
				if (stage.getStageId() == stageId) {
					checkpointsToRemove = this.getStageCheckpoints(stageId);
					this.stages.remove(stage);
					found = true;
				}
			}
			if (!found) {
				throw new IDNotRecognisedException();
			} else {

				for (int i : checkpointsToRemove) {
					checkpointId = i;
					int finalCheckpointId = checkpointId;
					this.checkpoints.removeIf((element) -> {
						return element.getCheckpointId() == finalCheckpointId;
					});
				}

				this.races.removeIf((element) -> {
					return element.getRaceId() == raceId;
				});
			}
		}
	}

	@Override
	public int getNumberOfStages(int raceId) throws IDNotRecognisedException {
		Iterator<Race> var2 = this.races.iterator();

		Race element;
		do {
			if (!var2.hasNext()) {
				throw new IDNotRecognisedException();
			}

			element = (Race)var2.next();
		} while(element.getRaceId() != raceId || element.getStageIds() == null);

		return element.getStageIds().length;
	}

	@Override
	public int addStageToRace(int raceId, String stageName, String description, double length, LocalDateTime startTime,
			StageType type)
			throws IDNotRecognisedException, IllegalNameException, InvalidNameException, InvalidLengthException {
		this.checkName(stageName);
		if (length < 5.0) {
			throw new InvalidLengthException();
		} else {
			Iterator<Race> var8 = this.races.iterator();

			Race element;
			do {
				if (!var8.hasNext()) {
					throw new IDNotRecognisedException();
				}

				element = (Race)var8.next();
				if (Objects.equals(element.getName(), stageName)) {
					throw new IllegalNameException();
				}
			} while(element.getRaceId() != raceId);

			Stage newStage = new Stage(stageName, description, length, startTime, type, "preparation");
			element.addStage(newStage);
			this.stages.add(newStage);
			return newStage.getStageId();
		}
	}

	@Override
	public int[] getRaceStages(int raceId) throws IDNotRecognisedException {
		this.sortStages();
		Iterator<Race> var2 = this.races.iterator();

		Race element;
		do {
			if (!var2.hasNext()) {
				throw new IDNotRecognisedException();
			}

			element = (Race)var2.next();
		} while(element.getRaceId() != raceId);

		return element.getStageIds();
	}

	@Override
	public double getStageLength(int stageId) throws IDNotRecognisedException {
		Iterator<Stage> var2 = this.stages.iterator();

		Stage element;
		do {
			if (!var2.hasNext()) {
				throw new IDNotRecognisedException();
			}

			element = (Stage)var2.next();
		} while(element.getStageId() != stageId);

		return element.getLength();
	}

	@Override
	public void removeStageById(int stageId) throws IDNotRecognisedException {
		int[] checkpointsToRemove = this.getStageCheckpoints(stageId);

		for (int checkpointId : checkpointsToRemove) {
			this.checkpoints.removeIf((element) -> {
				return element.getCheckpointId() == checkpointId;
			});
		}

		Stage stageToRemove = null;
		Iterator var11 = this.stages.iterator();

		while(var11.hasNext()) {
			Stage stage = (Stage)var11.next();
			if (stage.getStageId() == stageId) {
				stageToRemove = stage;
			}
		}

		var11 = this.races.iterator();

		while(var11.hasNext()) {
			Race race = (Race)var11.next();
			int[] var14 = race.getStageIds();

			for (int stage : var14) {
				if (stage == stageId) {
					race.removeStage(stageToRemove);
					this.stages.remove(stageToRemove);
				}
			}
		}

		throw new IDNotRecognisedException();
	}

	@Override
	public int addCategorizedClimbToStage(int stageId, Double location, CheckpointType type, Double averageGradient,
			Double length) throws IDNotRecognisedException, InvalidLocationException, InvalidStageStateException,
			InvalidStageTypeException {
		if (this.checkValidStage(stageId, location)) {
			Checkpoint newClimb = new Checkpoint(stageId, location, type, averageGradient, length);
			this.checkpoints.add(newClimb);
			this.sortCheckpoints();
			return newClimb.getCheckpointId();
		} else {
			throw new IDNotRecognisedException();
		}
	}

	public boolean checkValidStage(int stageId, double location) throws InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
		boolean valid = false;

		for (Stage stage : this.stages) {
			if (stage.getStageId() == stageId) {
				valid = true;
				if (location > stage.getLength()) {
					throw new InvalidLocationException();
				}

				if (Objects.equals(stage.getState(), "waiting for results")) {
					throw new InvalidStageStateException();
				}

				if (Objects.equals(stage.getType(), "TT")) {
					throw new InvalidStageTypeException();
				}
			}
		}

		return valid;
	}

	public void checkName(String name) throws InvalidNameException {
		if (name == null || name.isEmpty() || name.length() > 30 || name.contains(" ")) {
			throw new InvalidNameException();
		}
	}

	@Override
	public int addIntermediateSprintToStage(int stageId, double location) throws IDNotRecognisedException,
			InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
		if (this.checkValidStage(stageId, location)) {
			Checkpoint newIntermediateSprint = new Checkpoint(stageId, location, CheckpointType.SPRINT, 0.0, 0.0);
			this.checkpoints.add(newIntermediateSprint);
			this.sortCheckpoints();
			return newIntermediateSprint.getCheckpointId();
		} else {
			throw new IDNotRecognisedException();
		}
	}

	@Override
	public void removeCheckpoint(int checkpointId) throws IDNotRecognisedException, InvalidStageStateException {
		Iterator var2 = this.checkpoints.iterator();
		boolean b = true;

		while(b) {
			Checkpoint checkpoint;
			do {
				if (!var2.hasNext()) {
					throw new IDNotRecognisedException();
				}

				checkpoint = (Checkpoint)var2.next();
			} while(checkpoint.getCheckpointId() != checkpointId);

			int stageId = checkpoint.getStageId();

			for (Stage stage : this.stages) {
				if (stage.getStageId() == stageId && Objects.equals(stage.getState(), "waiting for results")) {
					throw new InvalidStageStateException();
				}
			}

			this.checkpoints.remove(checkpoint);
			b = false;
		}
	}

	@Override
	public void concludeStagePreparation(int stageId) throws IDNotRecognisedException, InvalidStageStateException {
		boolean idFound = false;

		for (Stage element : this.stages) {
			if (element.getStageId() == stageId) {
				if (Objects.equals(element.getState(), "waiting for results")) {
					throw new InvalidStageStateException();
				}

				idFound = true;
				element.setState("waiting for results");
			}
		}

		if (!idFound) {
			throw new IDNotRecognisedException();
		}
	}

	public void sortCheckpoints() {
		for(int i = 0; i < this.checkpoints.size(); ++i) {
			for(int j = i + 1; j < this.checkpoints.size(); ++j) {
				if (((Checkpoint)this.checkpoints.get(i)).getLocation() > ((Checkpoint)this.checkpoints.get(j)).getLocation()) {
					Checkpoint temp = (Checkpoint)this.checkpoints.get(i);
					this.checkpoints.set(i, (Checkpoint)this.checkpoints.get(j));
					this.checkpoints.set(j, temp);
				}
			}
		}
	}

	public void sortStages() {
		for(int i = 0; i < this.stages.size(); ++i) {
			for(int j = i + 1; j < this.stages.size(); ++j) {
				if (((Stage)this.stages.get(i)).getStartTime().isAfter(((Stage)this.stages.get(j)).getStartTime())) {
					Stage temp = (Stage)this.stages.get(i);
					this.stages.set(i, (Stage)this.stages.get(j));
					this.stages.set(j, temp);
				}
			}
		}
	}



	@Override
	public int[] getStageCheckpoints(int stageId) throws IDNotRecognisedException {
		boolean valid = false;
		this.sortCheckpoints();
		List<Integer> checkpointIds = new ArrayList<>();
		Iterator var4 = this.stages.iterator();

		while(var4.hasNext()) {
			Stage stage = (Stage)var4.next();
			if (stage.getStageId() == stageId) {
				valid = true;
				break;
			}
		}

		var4 = this.checkpoints.iterator();

		while(var4.hasNext()) {
			Checkpoint element = (Checkpoint)var4.next();
			if (element.getStageId() == stageId) {
				checkpointIds.add(element.getCheckpointId());
			}
		}

		int[] array = new int[checkpointIds.size()];

		for(int i = 0; i < checkpointIds.size(); ++i) {
			array[i] = (Integer)checkpointIds.get(i);
		}

		if (!valid) {
			throw new IDNotRecognisedException();
		} else {
			return array;
		}
	}

	@Override
	public int createTeam(String name, String description) throws IllegalNameException, InvalidNameException {
		this.checkName(name);
		Iterator<Team> var3 = this.teams.iterator();

		Team element;
		do {
			if (!var3.hasNext()) {
				Rider[] riders = new Rider[0];
				element = new Team(name, description, riders);
				this.teams.add(element);
				return element.getTeamId();
			}

			element = (Team)var3.next();
		} while(!Objects.equals(element.getName(), name));

		throw new IllegalNameException();
	}

	@Override
	public void removeTeam(int teamId) throws IDNotRecognisedException {
		this.teams.removeIf((element) -> {
			return element.getTeamId() == teamId;
		});
		throw new IDNotRecognisedException();
	}

	@Override
	public int[] getTeams() {
		List<Integer> teamIds = new ArrayList<>();

		for (Team element : this.teams) {
			teamIds.add(element.getTeamId());
		}

		int[] array = new int[teamIds.size()];

		for(int i = 0; i < teamIds.size(); ++i) {
			array[i] = (Integer)teamIds.get(i);
		}

		return array;
	}

	@Override
	public int[] getTeamRiders(int teamId) throws IDNotRecognisedException {
		Iterator<Team> var2 = this.teams.iterator();

		Team element;
		do {
			if (!var2.hasNext()) {
				throw new IDNotRecognisedException();
			}

			element = (Team)var2.next();
		} while(element.getTeamId() != teamId);

		List<Integer> riderIds = new ArrayList<>();
		Rider[] var5 = element.getRiders();
		int i = var5.length;

		for(int var7 = 0; var7 < i; ++var7) {
			Rider rider = var5[var7];
			riderIds.add(rider.getRiderId());
		}

		int[] array = new int[riderIds.size()];

		for(i = 0; i < riderIds.size(); ++i) {
			array[i] = (Integer)riderIds.get(i);
		}

		return array;
	}

	@Override
	public int createRider(int teamID, String name, int yearOfBirth)
			throws IDNotRecognisedException, IllegalArgumentException {
		if (!name.isEmpty() && yearOfBirth >= 1900) {
			Iterator<Team> var4 = this.teams.iterator();

			Team element;
			do {
				if (!var4.hasNext()) {
					throw new IDNotRecognisedException();
				}

				element = (Team)var4.next();
			} while(element.getTeamId() != teamID);

			Rider newRider = new Rider(name, teamID, yearOfBirth);
			element.addRider(newRider);
			return newRider.getRiderId();
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void removeRider(int riderId) throws IDNotRecognisedException {
		for (Team element : this.teams) {
			Rider[] var4 = element.getRiders();

			for (Rider rider : var4) {
				if (rider.getRiderId() == riderId) {
					element.removeRider(rider);
				}
			}
		}

		throw new IDNotRecognisedException();
	}

	@Override
	public void registerRiderResultsInStage(int stageId, int riderId, LocalTime... checkpoints)
			throws IDNotRecognisedException, DuplicatedResultException, InvalidCheckpointTimesException,
			InvalidStageStateException {
		List<CheckpointType> checkpointTypes = new ArrayList<>();

		for (Checkpoint checkpoint : this.checkpoints) {
			if (checkpoint.getStageId() == stageId) {
				checkpointTypes.add(checkpoint.getType());
			}
		}

		if (checkpoints.length != checkpointTypes.size() + 2) {
			throw new InvalidCheckpointTimesException();
		} else {
			CheckpointType[] checkpointsTypeArray = new CheckpointType[checkpointTypes.size()];

			for(int i = 0; i < checkpointTypes.size(); ++i) {
				checkpointsTypeArray[i] = (CheckpointType)checkpointTypes.get(i);
			}

			boolean idFound = false;
			Iterator var7 = this.teams.iterator();
			boolean b = true;

			while(b) {
				int var11;
				while(var7.hasNext()) {
					Team element = (Team)var7.next();
					Rider[] var9 = element.getRiders();
					int var10 = var9.length;

					for(var11 = 0; var11 < var10; ++var11) {
						Rider rider = var9[var11];
						if (rider.getRiderId() == riderId) {
							idFound = true;
							b = false;
							break;
						}
					}
				}

				if (idFound) {
					idFound = false;
					int count = 0;

					for (Stage stage : this.stages) {
						StageResult[] var20 = stage.getResults();
						var11 = var20.length;

						for (int var21 = 0; var21 < var11; ++var21) {
							StageResult result = var20[var21];
							if (result.getRiderId() == riderId) {
								++count;
							}
						}

						if (count > 1) {
							System.out.println(Arrays.toString(stage.getResults()) + " has already registered results for this stage");
						}

						if (stage.getStageId() == stageId) {
							if (!Objects.equals(stage.getState(), "waiting for results")) {
								throw new InvalidStageStateException();
							}

							stage.addResult(new StageResult(riderId, checkpointsTypeArray, checkpoints));
							idFound = true;
							break;
						}
					}
				}

				if (!idFound) {
					throw new IDNotRecognisedException();
				}

				return;
			}
		}
	}

	@Override
	public LocalTime[] getRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
		Iterator<Stage> var3 = this.stages.iterator();

		while(true) {
			Stage stage;
			do {
				if (!var3.hasNext()) {
					throw new IDNotRecognisedException();
				}

				stage = (Stage)var3.next();
			} while(stage.getStageId() != stageId);

			StageResult[] var5 = stage.getResults();

			for (StageResult result : var5) {
				if (result.getRiderId() == riderId) {
					LocalTime[] checkpointResults = result.getTimes();
					LocalTime[] newResults = new LocalTime[checkpointResults.length + 1];
					System.arraycopy(checkpointResults, 0, newResults, 0, checkpointResults.length);
					newResults[newResults.length - 1] = StageResult.getTotalTime(checkpointResults);
					return newResults;
				}
			}
		}
	}

	@Override
	public LocalTime getRiderAdjustedElapsedTimeInStage(int stageId, int riderId) throws IDNotRecognisedException {
		Iterator<Stage> var3 = this.stages.iterator();

		while(true) {
			Stage stage;
			do {
				if (!var3.hasNext()) {
					throw new IDNotRecognisedException();
				}

				stage = (Stage)var3.next();
			} while(stage.getStageId() != stageId);

			StageResult[] results = stage.getAdjustedResults();
			int var7 = results.length;

			for (StageResult result : results) {
				if (result.getRiderId() == riderId) {
					return result.getAdjustedTime();
				}
			}
		}
	}

	@Override
	public void deleteRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
		boolean idFound = false;
		Iterator var4 = this.stages.iterator();

		while(true) {
			Stage stage;
			do {
				if (!var4.hasNext()) {
					if (!idFound) {
						throw new IDNotRecognisedException();
					}

					return;
				}

				stage = (Stage)var4.next();
			} while(stage.getStageId() != stageId);

			StageResult[] var6 = stage.getResults();

			for (StageResult result : var6) {
				if (result.getRiderId() == riderId) {
					idFound = true;
					stage.removeResult(result);
				}
			}
		}
	}

	@Override
	public int[] getRidersRankInStage(int stageId) throws IDNotRecognisedException {
		boolean valid = false;
		StageResult[] results = new StageResult[0];
		List<Integer> riderRanks = new ArrayList<>();

		for (Stage stage : this.stages) {
			if (stage.getStageId() == stageId) {
				results = stage.getSortedResults();
				valid = true;
			}
		}

		StageResult[] var9 = results;
		int i = results.length;

		for (int var7 = 0; var7 < i; ++var7) {
			StageResult result = var9[var7];
			riderRanks.add(result.getRiderId());
		}

		return getInts(valid, riderRanks);
	}

	@Override
	public LocalTime[] getRankedAdjustedElapsedTimesInStage(int stageId) throws IDNotRecognisedException {
		boolean valid = false;
		List<LocalTime> totalTimes = new ArrayList<>();
		Iterator<Stage> var4 = this.stages.iterator();

		while(true) {
			Stage stage;
			do {
				if (!var4.hasNext()) {
					LocalTime[] array = new LocalTime[totalTimes.size()];

					for(int i = 0; i < totalTimes.size(); ++i) {
						array[i] = (LocalTime)totalTimes.get(i);
					}

					if (!valid) {
						throw new IDNotRecognisedException();
					}

					return array;
				}

				stage = (Stage)var4.next();
			} while(stage.getStageId() != stageId);

			StageResult[] results = stage.getAdjustedResults();
			valid = true;

			for (StageResult result : results) {
				totalTimes.add(result.getAdjustedTime());
			}
		}
	}

	@Override
	public int[] getRidersPointsInStage(int stageId) throws IDNotRecognisedException {
		boolean valid = false;
		StageResult[] results = new StageResult[0];

		for (Stage stage : this.stages) {
			if (stage.getStageId() == stageId) {
				results = stage.getSortedResults();
				stage.calculateGeneralPoints(this.getStageCheckpoints(stageId));
				valid = true;
			}
		}

		List<Integer> points = new ArrayList<>();
		StageResult[] var10 = results;
		int i = results.length;

		for(int var7 = 0; var7 < i; ++var7) {
			StageResult result = var10[var7];
			points.add(result.getPoints());
		}

		return getInts(valid, points);
	}

	@Override
	public int[] getRidersMountainPointsInStage(int stageId) throws IDNotRecognisedException {
		boolean valid = false;
		int index = 0;

		for (Stage stage : this.stages) {
			if (stage.getStageId() == stageId) {
				stage.calculatePoints(this.getStageCheckpoints(stageId), true);
				valid = true;
			}
		}

		List<Integer> points = new ArrayList<>();
		StageResult[] results = ((Stage)this.stages.get(index)).getSortedResults();
		int i = results.length;

		for (StageResult result : results) {
			points.add(result.getMountainPoints());
		}

		return getInts(valid, points);
	}

	public int[] getInts(boolean valid, List<Integer> points) throws IDNotRecognisedException {
		int i;
		int[] array = new int[points.size()];

		for(i = 0; i < points.size(); ++i) {
			array[i] = (Integer)points.get(i);
		}

		if (!valid) {
			throw new IDNotRecognisedException();
		} else {
			return array;
		}
	}

	@Override
	public void eraseCyclingPortal() {
		this.stages.clear();
		this.teams.clear();
		this.races.clear();
		this.checkpoints.clear();
		Race.lastRaceId = 0;
		Team.lastTeamId = 0;
		Rider.lastRiderId = 0;
		Stage.lastStageId = 0;
		Checkpoint.lastCheckpointId = 0;
	}

	@Override
	public void saveCyclingPortal(String filename) throws IOException {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(filename);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(this);
			objectOutputStream.close();
		} catch (IOException var4) {
			throw new IOException();
		}
	}

	@Override
	public void loadCyclingPortal(String filename) throws IOException, ClassNotFoundException {
		ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(filename));
		MiniCyclingPortalImpl tempPortal = (MiniCyclingPortalImpl) objectInputStream.readObject();
		this.stages = tempPortal.stages;
		this.teams = tempPortal.teams;
		this.checkpoints = tempPortal.checkpoints;
		this.races = tempPortal.races;
		Stage.lastStageId = tempPortal.stages.size();
		Team.lastTeamId = tempPortal.teams.size();
		Checkpoint.lastCheckpointId = tempPortal.checkpoints.size();
		Race.lastRaceId = tempPortal.races.size();
		int max = 0;

		for (Team team : tempPortal.teams) {
			Rider[] var7 = team.getRiders();

			for (Rider rider : var7) {
				if (rider.getRiderId() > max) {
					max = rider.getRiderId();
				}
			}
		}

		Rider.lastRiderId = max + 1;
		objectInputStream.close();
	}
}