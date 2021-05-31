import java.util.Vector;
import java.util.Random;

/**
 * A participant represents an actual person.
 * Their traits are currently workload, arrivalTime, and their trips
 */
public class Participant {
	Random rand = new Random();
	int workload; //mask tasks willing to complete
	//int speed; //speed of travel
	int arrivalTime; // Arrival time to the system
	Vector<Trip> participantTrips; // The trips that the participant will make

	/**
	 * Constructor
	 * @param arrivalTime
	 * @param theTrip
	 */
	public Participant(int arrivalTime, Trip theTrip) {
		this.arrivalTime = arrivalTime;
		workload = rand.nextInt(50) + 1; //up to 50 tasks possible
		//speed = rand.nextInt(60) + 1; //can take either a minute or up to an hour
										//to get to one point from another

		participantTrips = new Vector<Trip>();
		participantTrips.add(theTrip);
	}

	/**
	 * Method returns arrival time of participant
	 * @return start time
	 */
	public int getTime(){
		return this.arrivalTime;
	}

	/**
	 * Method gets the trip of the participtant.
	 * Currently, a participtant only takes one trip.
	 * @return one trip
	 */
	public Trip getTrip(){ 
		return this.participantTrips.get(0);
	}

	/**
	 * Method adds trips to the participant's list of trips.
	 * A single participant can have 0 or more trips
	 * @param theTrip: The information of a single trip
	 * @return The number of trips added so far
	 */
	public int AddTrip(Trip theTrip) {
		participantTrips.add(theTrip);
		return participantTrips.size()-1;
	}
}
