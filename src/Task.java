import java.util.*;

/**
 * A task is currently defined by its location in city, 
 * time available, if task was undertaken, and by when it was completed
 */
public class Task {
	String vertexLocation;
	int timeStep;
	int completedHour;
	int minutes;
	boolean taskTaken;

	public Task(String vertex, int time){
		vertexLocation = vertex;
		timeStep = time;
		taskTaken = false;
		completedHour = 0;
		minutes = 0;
	}

	/**
	 * Method gets time when task is available 
	 * @return time
	 */
	public int getTime() {
		return timeStep;
	}

	/**
	 * Methods gets the location of the task
	 * @return location
	 */
	public String getVertex() {
		return vertexLocation;
	}

	/**
	 * Method checks of task was completed
	 * @return boolean
	 */
	public boolean checkCompletion(){
		return taskTaken;
	}

	/**
	 * Method records the time that task was completed
	 * @param time
	 */
	public void completedTask(int time){
		taskTaken = true;
		this.completedHour = (time/60);
		this.minutes = (time%60);
	}

	/**
	 * Method clones the task
	 * @return cloned task
	 */
	public Task Clone(){
		Task clone = new Task(vertexLocation, timeStep);
		return clone;
	}

	/**
	 * Method resets the task to not being comleted
	 */
	public void ResetValues() {
		taskTaken = false;
	}

	/**
	 * Method returns a string representation of the task's
	 * location and time it starts to be available during simulation
	 */
	public String toString() {
		return "[" + vertexLocation + "," + timeStep + "]";
	}

	/**
	 * Method compares the available time of the task to another task
	 * @param otherTask
	 * @return
	 */
	public int compareTo(Task otherTask) {
        return (int)(this.timeStep - otherTask.timeStep);
    }
}
