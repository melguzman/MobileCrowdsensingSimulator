import java.util.*;
import java.io.*;

public class Analysis{
  public Analysis() {}

  /**
   * Method creates a csv file that contains the results based off of tasks
   * from running a scenario
   * @param tasks
   */
  public void tasksCompletedData(Vector<Task> tasks){
    try{
      FileWriter csvWriter = new FileWriter("tasksCompletedUpdated.csv"); 
      csvWriter.append("Location");
      csvWriter.append(",");
      csvWriter.append("Completed?");
      csvWriter.append(",");
      csvWriter.append("Hour");
      csvWriter.append(",");
      csvWriter.append("Minutes");
      csvWriter.append("\n");

      for (Task task : tasks) {
        csvWriter.append(String.join(",", task.getVertex(), Boolean.toString(task.checkCompletion()), Integer.toString(task.completedHour), Integer.toString(task.minutes)));
        csvWriter.append("\n");
      }

      csvWriter.flush();
      csvWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Method creates a csv file that contains information on where tasks
   * were placed in a city during a scenario. This data will highlight how
   * a large portion of tasks were placed in popular neighborhoods 
   * @param tasks
   * @param mappedNodes 
   */
  public void createVisual(Vector<Task> tasks, HashMap<String, String> mappedNodes){
    try{
      FileWriter csvWriter = new FileWriter("VeniceVisual.csv"); //rename to fit city name
      csvWriter.append("Time");
      csvWriter.append(",");
      csvWriter.append("Latitude");
      csvWriter.append(",");
      csvWriter.append("Longitude");
      csvWriter.append("\n");

      for (Task task : tasks) {
        System.out.println(mappedNodes.get(task.getVertex()));
        System.out.println("--->" + task.getVertex());
        String[] LatLon = mappedNodes.get(task.getVertex()).split(";"); //get location of task
        csvWriter.append(String.join(",", Integer.toString(task.getTime()), LatLon[0], LatLon[1]));
        csvWriter.append("\n");
      }

      csvWriter.flush();
      csvWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args){}
}
