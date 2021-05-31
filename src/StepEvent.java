import java.util.*;

public class StepEvent implements IEvent {
	int moveTime; //participant arrived at location/node
	Participant theParticipant;
	int workload; //participant's tasks limit
	//int speedParticipant;

	/**
	 * Constructor
	 * @param time
	 * @param theParticipant
	 */
	public StepEvent(int time, Participant theParticipant) {
		moveTime = time; 
		this.theParticipant = theParticipant;
		this.workload = theParticipant.workload;
		//this.speedParticipant = theParticipant.speed;
	}

	/**
	 * Method gets time that participant was at location/node
	 * @return
	 */
	@Override
	public int getEventTime() {
		return moveTime;
	}

	/**
	 * Method simulates the action of a participant completing tasks
	 * @param shortestPath participant's route
	 * @param tasks
	 * @param nodesTracked
	 * @param trackLatLon
	 * @return the number of tasks completed 
	 */
	public int execute(LinkedList<Integer> shortestPath, Vector<Task> tasks, HashMap<Integer, String> nodesTracked, HashMap<String, String> trackLatLon){
		Random rand = new Random();
		int tasksCompleted = 0;
		boolean ranOutofTime = false; //simulation is not over yet

		for (int point : shortestPath){
			int index = 0;
			String node = nodesTracked.get(point);
			if (!ranOutofTime){
				for (Task task : tasks){ 
					//System.out.println(index + " task.getVertex() " + task.getVertex());
					index++;
					//check if task is available at location and at current time
					//check if task has not been completed
					//check if participant still has room to complete tasks based of their workload
					if (trackLatLon.get(task.getVertex()).equals(node) && task.getTime() == this.moveTime && !task.checkCompletion() && workload > 0){ 
						workload--;
						task.completedTask(this.moveTime);
						tasksCompleted++;
						break; //completes only one task from each point in path for now
					}
				}
			}
			this.moveTime += 1;

			int speedParticipant = rand.nextInt(60) + 1; //participant moving at inconsistent speed
			if (this.moveTime == 1140){ //1140 should be changed to when simulation ends
				ranOutofTime = true;
				//System.out.println("Ran out of time");
			} else if (this.moveTime + speedParticipant > 1140){ //slow down, simulation close to ending
				this.moveTime += 1; // 1 step forward
				//System.out.println("Slow Down");
			} else {
				this.moveTime += speedParticipant; // safe to move
				//System.out.println("Good");
			}
		}
		return tasksCompleted;
	}

	@Override
	public IEvent executeEvent() {
		return null;
	}

}
