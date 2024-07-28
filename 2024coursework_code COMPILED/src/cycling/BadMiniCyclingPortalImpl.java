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
 * BadMiniCyclingPortal is a minimally compiling and functioning implementor
 * of the MiniCyclingPortal interface.
 * 
 * @author Georgia Rennie
 * @version 2.0
 *
 */
// class to implement the MiniCyclingPortal interface
public class BadMiniCyclingPortalImpl implements MiniCyclingPortal {

	// initialising and declaring variables to store the data
	private List<Race> races = new ArrayList<>();
	private List<Team> teams = new ArrayList<>();
	private List<Stage> stages = new ArrayList<>();
	private List<Checkpoint> checkpoints = new ArrayList<>();

	// constructor method
	public BadMiniCyclingPortalImpl() {
	}

	// method to return an array of all the race ids
	@Override
	public int[] getRaceIds() {
		// initialises an integer list to then append
		List<Integer> raceIds = new ArrayList<>();

		// for each loop to iterate through the races
        for (Race element : this.races) {
            raceIds.add(element.getRaceId());
        }

		// initialises the list to the right size to then convert to an array and insert values
		int[] array = new int[raceIds.size()];

		// for loop to iterate through the list and add the values to the array
		for(int i = 0; i < raceIds.size(); ++i) {
			array[i] = (Integer)raceIds.get(i);
		}

		return array;
	}

	// method to create a race
	@Override
	public int createRace(String name, String description) throws IllegalNameException, InvalidNameException {
		Iterator<Race> var3 = this.races.iterator();

		// checks validity and adds an element to the list of races
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

	// method to view the race details
	@Override
	public String viewRaceDetails(int raceId) throws IDNotRecognisedException {
		// initialises a double variable to store the total length
		double totalLength = 0.0;
		// initialises an instance of race
		Race thisRace = null;
		// initialises a boolean variable to check if the id is found
		boolean idFound = false;
		Iterator<Race> var6 = this.races.iterator();

		Race element;
		while(var6.hasNext()) {
			element = (Race)var6.next();
			// checks if the id is found
			if (element.getRaceId() == raceId) {
				thisRace = element;
				idFound = true;
			}
		}

		if (!idFound) {
			throw new IDNotRecognisedException();
		} else {
			Stage[] var10 = thisRace.getStages();
			// for each loop to iterate through the stages
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

			// concatenating the values to return
			return  raceId + " " + element.getName() + " " + element.getDescription() + " " + element.getStageIds().length + " " + totalLength;
		}
	}

	// method to remove a race
	@Override
	public void removeRaceById(int raceId) throws IDNotRecognisedException {
		// initialises a boolean variable to check if the id is found
		boolean found = false;
		// initialises an array to store the stages to remove
		int[] stagesToRemove = this.getRaceStages(raceId);
		// initialises an array to store the checkpoints to remove
		int[] checkpointsToRemove = new int[0];
		Iterator<Stage> var5 = this.stages.iterator();

		int checkpointId;
		while(var5.hasNext()) {
			Stage stage = (Stage)var5.next();
            checkpointId = stagesToRemove.length;

			// for loop to iterate through the stages to remove
			for(int var9 = 0; var9 < checkpointId; ++var9) {
				int stageId = stagesToRemove[var9];
				if (stage.getStageId() == stageId) {
					checkpointsToRemove = this.getStageCheckpoints(stageId);
					this.stages.remove(stage);
					found = true;
				}
			}
		}

		if (!found) {
			throw new IDNotRecognisedException();
		} else {
			// for loop to iterate through the checkpoints to remove
            for (int i : checkpointsToRemove) {
                checkpointId = i;
                int finalCheckpointId = checkpointId;
                this.checkpoints.removeIf((element) -> {
                    return element.getCheckpointId() == finalCheckpointId;
                });
            }

			// for loop to iterate through the races and return the id if found
			this.races.removeIf((element) -> {
				return element.getRaceId() == raceId;
			});
		}
	}

	// method to return the number of stages
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

	// method to add a stage to a race
	@Override
	public int addStageToRace(int raceId, String stageName, String description, double length, LocalDateTime startTime,
			StageType type)
			throws IDNotRecognisedException, IllegalNameException, InvalidNameException, InvalidLengthException {
		// checks the name and length of the stage
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
			// initialises a new stage
			Stage newStage = new Stage(stageName, description, length, startTime, type, "preparation");
			element.addStage(newStage);
			this.stages.add(newStage);
			return newStage.getStageId();
		}
	}

	// method to return the stage ids of the races
	@Override
	public int[] getRaceStages(int raceId) throws IDNotRecognisedException {
		// sorts the stages
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

	// method to return the length of a stage
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

	// method to remove a stage
	@Override
	public void removeStageById(int stageId) throws IDNotRecognisedException {
		int[] checkpointsToRemove = this.getStageCheckpoints(stageId);

		// for loop to iterate through the checkpoints to remove
        for (int checkpointId : checkpointsToRemove) {
            this.checkpoints.removeIf((element) -> {
                return element.getCheckpointId() == checkpointId;
            });
        }

		// initialises a stage to remove
		Stage stageToRemove = null;
		Iterator<Stage> var11 = this.stages.iterator();
        while(var11.hasNext()) {
			Stage stage = (Stage)var11.next();
			if (stage.getStageId() == stageId) {
				stageToRemove = stage;
			}
		}

		Iterator<Race> var20 = this.races.iterator();
		var20 = this.races.iterator();

		// for loop to iterate through the races and remove the stage
		while(var11.hasNext()) {
			Race race = (Race)var20.next();
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

	// method to add a categorized climb to a stage
	@Override
	public int addCategorizedClimbToStage(int stageId, Double location, CheckpointType type, Double averageGradient,
			Double length) throws IDNotRecognisedException, InvalidLocationException, InvalidStageStateException,
			InvalidStageTypeException {
		// checks the validity of the stage
		if (this.checkValidStage(stageId, location)) {
			// initialises a new climb
			Checkpoint newClimb = new Checkpoint(stageId, location, type, averageGradient, length);
			// adds the climb to the list of checkpoints
			this.checkpoints.add(newClimb);
			this.sortCheckpoints();
			return newClimb.getCheckpointId();
		} else {
			throw new IDNotRecognisedException();
		}
	}

	// method to check the validity of a stage
	public boolean checkValidStage(int stageId, double location) throws InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
		boolean valid = false;

		// for loop to iterate through the stages and check the validity
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

	// method to check the name
	public void checkName(String name) throws InvalidNameException {
		if (name == null || name.isEmpty() || name.length() > 30 || name.contains(" ")) {
			throw new InvalidNameException();
		}
	}

	// method to add an intermediate sprint to a stage
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
		Iterator<Checkpoint> var2 = this.checkpoints.iterator();
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

	// method to conclude the stage preparation
	@Override
	public void concludeStagePreparation(int stageId) throws IDNotRecognisedException, InvalidStageStateException {
		boolean idFound = false;

		// for loop to iterate through the stages and check the validity
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

	// method to sort the checkpoints
	public void sortCheckpoints() {
		for(int i = 0; i < this.checkpoints.size(); ++i) {
			for(int j = i + 1; j < this.checkpoints.size(); ++j) {
				if (((Checkpoint)this.checkpoints.get(i)).getLocation() > ((Checkpoint)this.checkpoints.get(j)).getLocation()) {
					// swaps the values
					Checkpoint temp = (Checkpoint)this.checkpoints.get(i);
					this.checkpoints.set(i, (Checkpoint)this.checkpoints.get(j));
					this.checkpoints.set(j, temp);
				}
			}
		}
	}

	// method to sort the stages
	public void sortStages() {
		for(int i = 0; i < this.stages.size(); ++i) {
			for(int j = i + 1; j < this.stages.size(); ++j) {
				if (((Stage)this.stages.get(i)).getStartTime().isAfter(((Stage)this.stages.get(j)).getStartTime())) {
					// swaps the values
					Stage temp = (Stage)this.stages.get(i);
					this.stages.set(i, (Stage)this.stages.get(j));
					this.stages.set(j, temp);
				}
			}
		}
	}

	// method to get the checkpoints
	@Override
	public int[] getStageCheckpoints(int stageId) throws IDNotRecognisedException {
		// initialises a boolean variable to check if the id is found
		boolean valid = false;
		this.sortCheckpoints();
		// initialises a list to store the checkpoint ids
		List<Integer> checkpointIds = new ArrayList<>();
		Iterator<Stage> var4 = this.stages.iterator();

		while(var4.hasNext()) {
			Stage stage = (Stage)var4.next();
			if (stage.getStageId() == stageId) {
				valid = true;
				break;
			}
		}

		Iterator<Checkpoint> var21 = this.checkpoints.iterator();

		// for loop to iterate through the checkpoints and add the ids to the list
		while(var4.hasNext()) {
			Checkpoint element = (Checkpoint)var21.next();
			if (element.getStageId() == stageId) {
				checkpointIds.add(element.getCheckpointId());
			}
		}

		int[] array = new int[checkpointIds.size()];

		// for loop to iterate through the list and add the values to the array
		for(int i = 0; i < checkpointIds.size(); ++i) {
			array[i] = (Integer)checkpointIds.get(i);
		}

		if (!valid) {
			throw new IDNotRecognisedException();
		} else {
			return array;
		}
	}

	// method to create a team
	@Override
	public int createTeam(String name, String description) throws IllegalNameException, InvalidNameException {
		this.checkName(name);
		Iterator<Team> var3 = this.teams.iterator();

		// checks the validity of the name and adds the team to the list
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

	// method to remove a team
	@Override
	public void removeTeam(int teamId) throws IDNotRecognisedException {
		this.teams.removeIf((element) -> {
			return element.getTeamId() == teamId;
		});
		throw new IDNotRecognisedException();
	}

	// method to return a list of the teams
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

	// method to return the riders in a team
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

		// initialises a list to store the rider ids
		List<Integer> riderIds = new ArrayList<>();
		Rider[] var5 = element.getRiders();
		int i = var5.length;

		// for loop to iterate through the riders and add the ids to the list
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
		// checks the validity of the name and year of birth
		if (!name.isEmpty() && yearOfBirth >= 1900) {
			Iterator<Team> var4 = this.teams.iterator();

			Team element;
			do {
				if (!var4.hasNext()) {
					throw new IDNotRecognisedException();
				}

				element = (Team)var4.next();
			} while(element.getTeamId() != teamID);

			// initialises a new rider
			Rider newRider = new Rider(name, teamID, yearOfBirth);
			element.addRider(newRider);
			return newRider.getRiderId();
		} else {
			throw new IllegalArgumentException();
		}
	}

	// method to remove a rider
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

	// method to register the rider results in a stage
	@Override
	public void registerRiderResultsInStage(int stageId, int riderId, LocalTime... checkpoints)
			throws IDNotRecognisedException, DuplicatedResultException, InvalidCheckpointTimesException,
			InvalidStageStateException {
		List<CheckpointType> checkpointTypes = new ArrayList<>();

		// for loop to iterate through the checkpoints and add the types to the list
        for (Checkpoint checkpoint : this.checkpoints) {
            if (checkpoint.getStageId() == stageId) {
                checkpointTypes.add(checkpoint.getType());
            }
        }

		// checks the validity of the checkpoints
		if (checkpoints.length != checkpointTypes.size() + 2) {
			throw new InvalidCheckpointTimesException();
		} else {
			CheckpointType[] checkpointsTypeArray = new CheckpointType[checkpointTypes.size()];

			// for loop to iterate through the checkpoint types and add them to the array
			for(int i = 0; i < checkpointTypes.size(); ++i) {
				checkpointsTypeArray[i] = (CheckpointType)checkpointTypes.get(i);
			}

			// initialises a boolean variable to check if the id is found
			boolean idFound = false;
			Iterator<Team> var7 = this.teams.iterator();
			boolean b = true;

			// for loop to iterate through the teams and check the validity of the rider
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

			// for loop to iterate through the stages and add the results
			if (idFound) {
				idFound = false;
				int count = 0;

				for (Stage stage : this.stages) {
					StageResult[] var20 = stage.getResults();
					var11 = var20.length;

					// for loop to iterate through the results and check if the rider has already registered
					for (int var21 = 0; var21 < var11; ++var21) {
						StageResult result = var20[var21];
						if (result.getRiderId() == riderId) {
							++count;
						}
					}

					// checks the validity of the stage
					if (count > 1) {
						System.out.println(Arrays.toString(stage.getResults()) + " has already registered results for this stage");
					}

					// for loop to iterate through the stages and add the results
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
		}
	}

	// method to get the rider results in a stage
	@Override
	public LocalTime[] getRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
		Iterator<Stage> var3 = this.stages.iterator();

		// loop to iterate through the stages and return the results
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

	// method to get the rider adjusted elapsed time in a stage
	@Override
	public LocalTime getRiderAdjustedElapsedTimeInStage(int stageId, int riderId) throws IDNotRecognisedException {
		Iterator<Stage> var3 = this.stages.iterator();

		// loop to iterate through the stages
		while(true) {
			Stage stage;
			do {
				if (!var3.hasNext()) {
					throw new IDNotRecognisedException();
				}

				stage = (Stage)var3.next();
			} while(stage.getStageId() != stageId);

			StageResult[] results = stage.getAdjustedResults();

			// returns the adjusted time
            for (StageResult result : results) {
                if (result.getRiderId() == riderId) {
                    return result.getAdjustedTime();
                }
            }
		}
	}

	// method to delete the rider results in a stage
	@Override
	public void deleteRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
		boolean idFound = false;
		Iterator<Stage> var4 = this.stages.iterator();

		// loop to iterate through the stages and delete the results
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

	// method to get the riders rank in a stage
	@Override
	public int[] getRidersRankInStage(int stageId) throws IDNotRecognisedException {
		boolean valid = false;
		StageResult[] results = new StageResult[0];
		List<Integer> riderRanks = new ArrayList<>();

		// loop to iterate through the stages and get the results
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

	// method to get the riders elapsed time in a stage
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

	// method to get the riders points in a stage
	@Override
	public int[] getRidersPointsInStage(int stageId) throws IDNotRecognisedException {
		boolean valid = false;
		StageResult[] results = new StageResult[0];

		// loop to iterate through the stages and get the results
        for (Stage stage : this.stages) {
            if (stage.getStageId() == stageId) {
                results = stage.getSortedResults();
                stage.calculateGeneralPoints(this.getStageCheckpoints(stageId));
                valid = true;
            }
        }

		// initialises a list to store the points
		List<Integer> points = new ArrayList<>();
		StageResult[] var10 = results;
		int i = results.length;

		// for loop to iterate through the results and add the points to the list
		for(int var7 = 0; var7 < i; ++var7) {
			StageResult result = var10[var7];
			points.add(result.getPoints());
		}

		return getInts(valid, points);
	}

	// method to get the riders sprint points in a stage
	@Override
	public int[] getRidersMountainPointsInStage(int stageId) throws IDNotRecognisedException {
		boolean valid = false;
		int index = 0;

		// loop to iterate through the stages and get the results
        for (Stage stage : this.stages) {
            if (stage.getStageId() == stageId) {
                stage.calculatePoints(this.getStageCheckpoints(stageId), true);
                valid = true;
            }
        }

		List<Integer> points = new ArrayList<>();
		StageResult[] results = ((Stage)this.stages.get(index)).getSortedResults();

        for (StageResult result : results) {
            points.add(result.getMountainPoints());
        }

		return getInts(valid, points);
	}

	// method to get the integer points
	private int[] getInts(boolean valid, List<Integer> points) throws IDNotRecognisedException {
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

	// method to erase all the data in the lists and resets the ids
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

	// method to save the data to a file
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

	// method to load the data from a file
	@Override
	public void loadCyclingPortal(String filename) throws IOException, ClassNotFoundException {
		ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(filename));
		BadMiniCyclingPortalImpl tempPortal = (BadMiniCyclingPortalImpl)objectInputStream.readObject();
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