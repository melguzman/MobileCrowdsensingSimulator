import java.util.Collections;
import java.util.Vector;

/**
 * A trip represents a participant's route. It is currently defined by its
 * starting location, ending location, and whatever current location
 * the participant is at during the simulation
 */
public class Trip {
	String startLocationVertex;
	String endLocationVertex;
	String currentLocation;

	public Trip(String startLoc, String endLoc) {
		startLocationVertex = startLoc;
		endLocationVertex = endLoc;
		currentLocation = startLoc;
	}

}
