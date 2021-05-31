import java.util.*;
import java.io.*;

public class srpGraph{
  int numVertices;
  ArrayList<ArrayList<Integer>> adj;
  protected boolean[][] edges;
  HashMap<String, Integer> trackNodes;
  HashMap<Integer, String> reverseTrackNodes;
  HashMap<String, String> trackLatLon;
  HashMap<String, String> nodesToLatLon; 
  Vector<Participant> allParticipants;

  public srpGraph(){
    this.numVertices = 0;
    this.trackNodes = new HashMap<String, Integer>(); //("N1", 1)
    this.reverseTrackNodes = new HashMap<Integer, String>(); //(1, "N1")
    this.nodesToLatLon = new HashMap<String, String>(); //("N1", ("33.45656", "181.634398"))
    this.trackLatLon = new  HashMap<String, String>(); // (("33.45656", "181.634398"), N1)
    this.allParticipants = new Vector<Participant>();
  }

  /**
   * Helper method to get information about nodes and their cooresponding locations
   * @return mapped nodes to locations
   */
  public HashMap<String, String> getMappedLocations(){
    return this.nodesToLatLon;
  }

  /**
   * Method parses through Node data set to map a node's ID to a number to later 
   * be mapped to a graph that represents a city
   * @param path file
   * @return hashMap of Node IDs and their corresponding number 
   */
  public HashMap<String, Integer> parseNodeCSV(String path){
    try {
      BufferedReader csvReader = new BufferedReader(new FileReader(path));
      String ignoreHeader = csvReader.readLine();
      String row = "";
      int index = 1;

      while ((row = csvReader.readLine()) != null){
        String[] data = row.split(";");
        this.trackNodes.put(data[0], index);
        this.reverseTrackNodes.put(index, data[0]);
        this.nodesToLatLon.put(data[0], data[6] + ";" + data[7]); 
        this.trackLatLon.put(data[6] + ";" + data[7], data[0]);
        index++;
      }

      csvReader.close(); 
    } catch (IOException e) {
      e.printStackTrace();
    }
    return trackNodes;
  }

  /**
   * Method parses through Way dataset to extract nodes and their neighbors 
   * and map them to a graph
   * @param path file
   * @param nodes
   * @return graph of nodes that represents the city
   */
  public ArrayList<ArrayList<Integer>> parseWayCSV(String path, HashMap<String, Integer> nodes){
    int noBuilding = 0; //just for testing to see amount of roads
    this.edges = new boolean[nodes.size()+1][nodes.size()+1];
    this.adj = new ArrayList<ArrayList<Integer>>(nodes.size()+2); //tracks neighbors
    for (int i = 1; i < nodes.size()+2; i++){
      adj.add(new ArrayList<Integer>());
    }

    try{
    BufferedReader csvReader = new BufferedReader(new FileReader(path));
    String ignoreHeader = csvReader.readLine();
    String row = "";

    while ((row = csvReader.readLine()) != null){
      int checkfirst = 0; //count being bad = starts at 10
      boolean foundBuilding = true;
      String[] data = row.split(","); //might have to move count here

      for(int z = 0; z < data.length; z++){
        if (data[z].contains("highway=")){
          foundBuilding = false;
          break;
        }
      }

      if (!foundBuilding){
        noBuilding++;
        String firstNode = data[0].split("\\[")[1]; //first node is tangled up due to csv format issue from framework. Fixed here!

        for (int i=0; i < data.length; i++){
          if (checkfirst == 0 && data[i].startsWith("N") && Character.isDigit(data[i].charAt(1)) && !data[i].contains("]")){ //handles very first node with csv issue
            if (!nodes.containsKey(firstNode) || !nodes.containsKey(data[i])){ //when graph has to update to remove buildings
              break;
            }
            this.edges[nodes.get(firstNode)][nodes.get(data[i])] = true;
            this.edges[nodes.get(data[i])][nodes.get(firstNode)] = true;
            checkfirst++; //no longer dealing with data[i] being a regular formatted node that's "first"
                     //and data[i-1] being actual firsNode from data[0] which has some csv format issue
            //keeping track of neighbors through hashmap
            adj.get(nodes.get(firstNode)).add(nodes.get(data[i]));
            adj.get(nodes.get(data[i])).add(nodes.get(firstNode));
          } else if (data[i].startsWith("N") && Character.isDigit(data[i].charAt(1))){
            if (data[i].contains("]")){ // special case in which data[i] is lastNode so csv format issue also here
              String lastNode = data[i].split("\\]")[0];
              if (data[i-1].contains("[")){ //special case in which only two nodes make up the way & includes both csv format issue
                if (!nodes.containsKey(lastNode) || !nodes.containsKey(firstNode)){ //when graph has to update to remove buildings
                  break;
                }
                this.edges[nodes.get(lastNode)][nodes.get(firstNode)] = true;
                this.edges[nodes.get(firstNode)][nodes.get(lastNode)] = true;
                //keeping track of neighbors through hashmap
                adj.get(nodes.get(lastNode)).add(nodes.get(firstNode));
                adj.get(nodes.get(firstNode)).add(nodes.get(lastNode));
              } else { //lastNode just data[i] and regular node with data[i-1]
                if (!nodes.containsKey(lastNode) || !nodes.containsKey(data[i-1])){ //when graph has to update to remove buildings
                  break;
                }
                this.edges[nodes.get(lastNode)][nodes.get(data[i-1])] = true;
                this.edges[nodes.get(data[i-1])][nodes.get(lastNode)] = true;
                //keeping track of neighbors through hashmap
                adj.get(nodes.get(lastNode)).add(nodes.get(data[i-1]));
                adj.get(nodes.get(data[i-1])).add(nodes.get(lastNode));
              }
            } else if (data[i-1].contains("[")) { //when data[i-1] constains the issue in csv format (firstNode) but data[i] regular
              if (!nodes.containsKey(firstNode) || !nodes.containsKey(data[i])){ //when graph has to update to remove buildings
                break;
              }
              this.edges[nodes.get(data[i])][nodes.get(firstNode)] = true;
              this.edges[nodes.get(firstNode)][nodes.get(data[i])] = true;
              //keeping track of neighbors through hashmap
              adj.get(nodes.get(data[i])).add(nodes.get(firstNode));
              adj.get(nodes.get(firstNode)).add(nodes.get(data[i]));
            } else { //regular nodes with no csv format issues!!!!
              if (!nodes.containsKey(data[i]) || !nodes.containsKey(data[i-1])){ //when graph has to update to remove buildings
                break;
              }
              this.edges[nodes.get(data[i])][nodes.get(data[i-1])] = true;
              this.edges[nodes.get(data[i-1])][nodes.get(data[i])] = true;
              //keeping track of neighbors through hashmap
              adj.get(nodes.get(data[i])).add(nodes.get(data[i-1]));
              adj.get(nodes.get(data[i-1])).add(nodes.get(data[i]));
            }
          }
        } //end of for loop
      }
      //now remove any empty array lists since those building nodes are not inlcuded in graph + edges
      //can set their value to -1 instead and later check for -1 to know not to use that?

    } //end of while
    csvReader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("Roads: " + noBuilding);
    //return edges;
    return this.adj; 
  }

  /**
   * Method parses the graph to remove all nodes that describe anything 
   * that is not a road like a building. This is meant to make the graph complete 
   * or as close to complete as possible.
   * @param graph
   * @return hashMap of nodes that has been updated
   */
  public HashMap<String, Integer> parseNodesNoBuilding(ArrayList<ArrayList<Integer>> graph){ 
    ArrayList<Integer> noBuildings = new ArrayList<Integer>();
    for (int i = 1; i < graph.size(); i++){
      if (graph.get(i).size() > 0){ //only add nodes that describe roads
        if(!noBuildings.contains(i)){ //handles duplicates
          noBuildings.add(i);
        }
        for(int j = 0; j < graph.get(i).size(); j++){ //will be small
          if (!noBuildings.contains(graph.get(i).get(j))){ //handles duplicates
            noBuildings.add(graph.get(i).get(j)); //only add nodes that describe roads
          }
        }
      }
    }
    int newIndex = 1;
    HashMap<String, Integer> tempTrackNodes = new HashMap<String, Integer>();
    HashMap<Integer, String> tempReverseTrackNodes = new HashMap<Integer, String>();
    HashMap<String, String> tempNodesToLatLon = new HashMap<String, String>();
    HashMap<String, String> tempTrackLatLon = new HashMap<String, String>();

    for (int k = 0; k < noBuildings.size(); k++){ //have to update nodes and lat/lon
      String nodeName = this.reverseTrackNodes.get(noBuildings.get(k));
      tempTrackNodes.put(nodeName, newIndex); //("N1", 1)
      tempReverseTrackNodes.put(newIndex, nodeName); //(1, "N1")

      String LatLon = this.nodesToLatLon.get(nodeName);
      tempNodesToLatLon.put(nodeName, LatLon);
      tempTrackLatLon.put(LatLon, nodeName);
      newIndex++;
    }

    this.trackNodes = tempTrackNodes;
    this.reverseTrackNodes = tempReverseTrackNodes;
    this.nodesToLatLon = tempNodesToLatLon;
    this.trackLatLon = tempTrackLatLon;
    return trackNodes;
  }

  /**
   * Method that implements the haversine formula which determines the great-circle distance 
   * between two points on a sphere given their longitudes and latitudes.
   * @param lat1 latitude of first point
   * @param lon1 longitude of first point
   * @param lat2 latitude of second point
   * @param lon2 longitude of second point
   * @return the distance (double) between the two points in KM
   */
  static double haversine(double lat1, double lon1, double lat2, double lon2)
  {
      // distance between latitudes and longitudes
      double dLat = Math.toRadians(lat2 - lat1);
      double dLon = Math.toRadians(lon2 - lon1);
      lat1 = Math.toRadians(lat1);
      lat2 = Math.toRadians(lat2);

      double a = Math.pow(Math.sin(dLat / 2), 2) +
                 Math.pow(Math.sin(dLon / 2), 2) *
                 Math.cos(lat1) *
                 Math.cos(lat2);
      double rad = 6371;
      double c = 2 * Math.asin(Math.sqrt(a));
      return rad * c; //in KM
  }
  
  /**
   * Method parses through the mobility trace data set to determine most frequented locations
   * in a city using the popularity index as specified by the user.
   * @param path file
   * @param nodes
   * @param popularityRank
   * @return List of most frequented locations in a city
   */
  public List<String> parseTrackPtCSV(String path, HashMap<String, Integer> nodes, Double popularityRank){
    HashMap<String, Integer> popularPlaces = new HashMap<String, Integer>(); // (latLon, 26)
    HashMap<Integer, String> trackPop = new HashMap<Integer, String>(); // (26, LatLon)
    HashMap<String, Double> popularityConv = new HashMap<String, Double>();
    List<String> popularNodes = new ArrayList<String>();
    int index = 0;

    try{
      BufferedReader csvReader = new BufferedReader(new FileReader(path));
      String ignoreHeader = csvReader.readLine();
      String row = "";
      int maxPop = 1;
      String popularPoint = "";
      List<String> popularPoints = new ArrayList<String>(); //final list of most freq locations

      while ((row = csvReader.readLine()) != null){
        String[] data = row.split(",");
        int year = Integer.parseInt(data[6].split("/")[0]);
        if (year >= 2011){ // not interested in old data
          String node = trackLatLon.get(data[1] + ";" + data[0]);
          String latLon = data[1] + ";" + data[0]; //works here without using info from map

          if (popularPlaces.containsKey(latLon)){ //increment
            popularPlaces.put(latLon, popularPlaces.get(latLon) + 1);
            if (popularPlaces.get(latLon) > maxPop){ //keep track of max popularity
              maxPop = popularPlaces.get(latLon);
            }
          } else {
            popularPlaces.put(latLon, 1);
            trackPop.put(index, latLon);
            index++;
          }
        }
      }
      csvReader.close(); // close buffer!

      //find popularPoint and convert popularity rank to 0-1 range
      for (int j = 0; j < trackPop.size(); j++){
        if (popularPlaces.get(trackPop.get(j)) == maxPop){
          popularPoint = trackPop.get(j);
        }
        popularityConv.put(trackPop.get(j), popularPlaces.get(trackPop.get(j))/ (double)10);
      }
      System.out.println("Most Popular point (not node): " + popularPoint);

      //collect set of popular points
      for (int k = 0; k < trackPop.size(); k++){
        if(popularityConv.get(trackPop.get(k)) >= popularityRank){
          popularPoints.add(trackPop.get(k)); //add to collection of popular points
        }
      }
      System.out.println("Amount of popular points: " + popularPoints.size());
      System.out.println("Popular points: " + popularPoints);

      //find nodes in map near popular popularPoints
      for (int n = 0; n < popularPoints.size(); n++){
        double minDist = 6371; //radius of earth in KM
        String saveNode = "";
        String pPoint = popularPoints.get(n); //popular point
        double lat1 = Double.parseDouble(pPoint.split(";")[0]);
        double lon1 = Double.parseDouble(pPoint.split(";")[1]);
        for (int i = 1; i < nodes.size(); i++){
          String latLon2 = this.nodesToLatLon.get(this.reverseTrackNodes.get(i));
          double lat2 = Double.parseDouble(latLon2.split(";")[0]);
          double lon2 = Double.parseDouble(latLon2.split(";")[1]);
          if(this.haversine(lat1, lon1, lat2, lon2) < minDist){
            minDist = this.haversine(lat1, lon1, lat2, lon2);
            saveNode = Double.toString(lat2) + ";" + Double.toString(lon2);
          }
        }
        popularNodes.add(this.trackLatLon.get(saveNode));
      }

      //check if popular nodes repeat
      List<String> replacePopNodes = new ArrayList<String>();
      for (String node : popularNodes){
        if (!replacePopNodes.contains(node)){
          replacePopNodes.add(node);
        }
      }
      popularNodes = replacePopNodes; //got rid of repeated popular nodes
      System.out.println("Most popular nodes: " + popularNodes);
      return popularNodes;

    } catch (IOException e){
      e.printStackTrace();
    }
    return popularNodes;
  }

  /**
   * Method distributes tasks and participants throughought the graph. More tasks and 
   * participants are placed around popular locations through the use of a modified 
   * BFS and some randomization. The rest of tasks and participants are randomly 
   * assigned to locations without consideration of popular locations. 
   * @param nodeNeighbors the graph
   * @param popNodes list of popular nodes/locations
   * @param neighborPopLayers how large the popular neighborhood is
   * @param numTasks total amount of tasks
   * @param numParticipants total amount of participants
   * @param start start time of simulation based on 24-hour clock
   * @param end end time of simulation based on 24-hour clock
   * @param maxTasksToNodePA max amount of tasks that can be placed in a popular node/location
   * @param capParticipants max amount of participants that can be placed in a popular node/location
   * @return vector of complied tasks
   */
  public Vector<Task> distributeTasks(ArrayList<ArrayList<Integer>> nodeNeighbors, List<String> popNodes, int neighborPopLayers, int numTasks, int numParticipants, int start, int end, int maxTasksToNodePA, int capParticipants){
    Vector<Task> results = new Vector<Task>();
    Random rand = new Random();
    boolean visited[] = new boolean[nodeNeighbors.size()];
    LinkedList<Integer> queue = new LinkedList<Integer>();
    visited[this.trackNodes.get(popNodes.get(0))]=true;
    queue.add(this.trackNodes.get(popNodes.get(0)));

    for (int i = 0; i < popNodes.size(); i++){
    	int depth = 0;
      int check = 0;
      System.out.println("========Looking at index "+ i +" of list of popular nodes========");
      queue.add(this.trackNodes.get(popNodes.get(i)));
      while (depth  < neighborPopLayers){ // BFS approach so that way can go by layer
         System.out.println("At Layer: " + depth);
         if (queue.size() == 0){ //handles special case
           System.out.println("Queue is empty so move onto next popular node if available.");
           break; //break out of while loop
         }
    	   int s = queue.poll();
         List<Integer> neighbors = new ArrayList<Integer>();
         if (check == 0){ //grab adjacent nodes of popular node
           if (this.trackNodes.get(popNodes.get(i)) == 0){ //check for when 0 index -> avoid null issue
             break;
           }
           neighbors = nodeNeighbors.get(this.trackNodes.get(popNodes.get(i)));
           System.out.println("Node neighbors: " + neighbors);
           check++;
           if (neighbors.size() == 0){
             break;
           }
         } else { //grab adjacent nodes of adjacent node
           if (s == 0){ //check for when 0 index -> avoid null issue
             break;
           }
           neighbors =  nodeNeighbors.get(s);
           System.out.println("Node neighbors: " + neighbors);
           if (neighbors.size() == 0){
             break;
           }
         }
    		 //randomness in amount of nodes in neighbors that will have tasks
    		 int nodeAmount = rand.nextInt(neighbors.size()) + 1;
         System.out.println("Random amount of neighbors that will have tasks: " + nodeAmount);
         for (int k = 0; k < nodeAmount; k++){ //randomness in which nodes have tasks
    	      int node = rand.nextInt(neighbors.size());

            //participants to a popular area
            int maxParticipants = rand.nextInt(capParticipants); //randomness in amount of participants assigned to a PA neighborhood
            if (numParticipants > maxParticipants){
              for (int y = 0; y < maxParticipants; y++){
                int pathDirection = rand.nextInt(3); //0 or 1 or 2
                int startTime = (rand.nextInt((end - start)+1) + start)*60; //minutes

                if (pathDirection == 0){ //from a popular place
                  String particiPANodeStart = this.reverseTrackNodes.get(neighbors.get(rand.nextInt(neighbors.size())));
                  String notPopEnd = this.reverseTrackNodes.get(rand.nextInt(nodeNeighbors.size()-1) + 1);
                  allParticipants.add(new Participant(startTime, new Trip(particiPANodeStart, notPopEnd)));
                  numParticipants--; //participants left to add?
                } else if (pathDirection == 1){ //to a popular place
                  String notPopStart = this.reverseTrackNodes.get(rand.nextInt(nodeNeighbors.size()-1) + 1);
                  String particiPANodeEnd = this.reverseTrackNodes.get(neighbors.get(rand.nextInt(neighbors.size())));
                  allParticipants.add(new Participant(startTime, new Trip(notPopStart, particiPANodeEnd)));
                  numParticipants--;
                } else {
                  String popStart = this.reverseTrackNodes.get(neighbors.get(rand.nextInt(neighbors.size())));
                  String endStart = this.reverseTrackNodes.get(neighbors.get(rand.nextInt(neighbors.size())));
                  allParticipants.add(new Participant(startTime, new Trip(popStart, endStart)));
                  numParticipants--;
                }
              }
            }

            //tasks to a popular area
            int amountOfTasks = rand.nextInt(maxTasksToNodePA) + 1; //randomness in amount of tasks assigned to each node
            if (numTasks > amountOfTasks){
              for (int n = 0; n < amountOfTasks; n++){
                int timeStep = (rand.nextInt((end - start)+1) + start)*60;
        	      results.add(new Task(this.nodesToLatLon.get(this.reverseTrackNodes.get(neighbors.get(node))), timeStep)); //this task at this time available
              }
              numTasks -= amountOfTasks;
            }
         }
         System.out.println("Tasks added so far to popular areas: " + results.size());
         //System.out.println("neighbors size: " + neighbors.size());
    		 for (int j = 0; j < neighbors.size(); j++){
             //System.out.println("visited already: " +visited[neighbors.get(j)]);
    		     if (!visited[neighbors.get(j)]){
               //System.out.println("Inside if: " + !visited[neighbors.get(j)]);
               //visited[neighbors.get(j)] = true;
               queue.add(neighbors.get(j));
               //System.out.println("queue size: " + queue.size());
             }
         }
         //System.out.println("queue size: " + queue.size());
         depth++; //this layer is done
       }
       queue = new LinkedList<Integer>(); //rest queue for next popular node
       //System.out.println("Q size should be zero: " + queue.size());
     }
     for (int m = 0; m < numTasks; m++){ //randomness for remaining tasks for rest of regular nodes
       int regularNode = rand.nextInt(nodeNeighbors.size()-1)+1; // sizes of both off by 1
       int timeStep = (rand.nextInt((end - start)+1) + start)*60; // randomness in timeStep:7 AM-7PM
       results.add(new Task(this.nodesToLatLon.get(reverseTrackNodes.get(regularNode)), timeStep)); //add one task at a time for regular node
     }
     System.out.println("Total tasks added to regular nodes: " + numTasks);

     System.out.println("Total participants added to poopular areas: " + allParticipants.size());
     for (int x = 0; x < numParticipants; x++){
       String notPopStart = this.reverseTrackNodes.get(rand.nextInt(nodeNeighbors.size()-1) + 1);
       String notPopEnd = this.reverseTrackNodes.get(rand.nextInt(nodeNeighbors.size()-1) + 1);
       int startTime = (rand.nextInt((end - start)+1) + start)*60; //minutes
       allParticipants.add(new Participant(startTime, new Trip(notPopStart, notPopEnd))); //participant traveling in nonpopular areas
     }
     System.out.println("Total participants added to regular nodes: " + numParticipants);
     System.out.println("Check total participants in vector: " + allParticipants.size());
     return results;
  }

  /**
   * Method that returns a list of all nodes that make up the shortest path between two nodes on a graph
   * @param adj the graph
   * @param start location
   * @param end location
   * @param numNodes total amount of nodes in graph
   * @return the shortest path
   */
  public LinkedList<Integer> shortestPath(ArrayList<ArrayList<Integer>> adj, String start, String end, int numNodes){
      int s = this.trackNodes.get(start);
      int dest = this.trackNodes.get(end);
      int pred[] = new int[numNodes];
      double dist[] = new double[numNodes];

      if (findPath(adj, s, dest, numNodes, pred, dist) == false) {
          //System.out.println("Impossible to reach. Not connected");
          return null;
      }
      LinkedList<Integer> rPath = new LinkedList<Integer>();
      int back = dest;
      rPath.add(back);
      while (pred[back] != -1) {
          rPath.add(pred[back]);
          back = pred[back];
      }
      LinkedList<Integer> path = new LinkedList<Integer>(); //flip
      for (int i = rPath.size() - 1; i >= 0; i--) {
          path.add(rPath.get(i));
      }
      return path;
  }

  /**
   * Method is a modified version of BFS that stores predecessor of each vertex 
   * in array pred to keep track of nodes in a would be path and its distance 
   * from source in array dist
   * @param adj the graph
   * @param src start location of a participtant
   * @param dest end location of a participtant
   * @param numVertices total amount of nodes
   * @param pred array of predecessor
   * @param dist array of distances
   * @return boolean on if path exists between the two points/locations
   */
  public boolean findPath(ArrayList<ArrayList<Integer>> adj, int src, int dest, int numVertices, int[] pred, double[] dist){
      LinkedList<Integer> queue = new LinkedList<Integer>();
      boolean[] visited = new boolean[numVertices]; //fixed brackets

      for (int i = 0; i < numVertices; i++) {
          visited[i] = false;
          dist[i] = Integer.MAX_VALUE; // dist[i] for all i set to infinity
          pred[i] = -1;
      }
      visited[src] = true;
      dist[src] = 0.0;
      queue.add(src); // distance from source to itself should be 0

      while (!queue.isEmpty()) {
          int u = queue.remove();
          for (int i = 0; i < adj.get(u).size(); i++) { //looking through neighbors
              if (visited[adj.get(u).get(i)] == false) {
                  visited[adj.get(u).get(i)] = true;
                  String[] latLon1 = this.nodesToLatLon.get(this.reverseTrackNodes.get(u)).split(";");
                  String[] latLon2 = this.nodesToLatLon.get(this.reverseTrackNodes.get(adj.get(u).get(i))).split(";");
                  double distance = this.haversine(Double.parseDouble(latLon1[0]), Double.parseDouble(latLon1[1]), Double.parseDouble(latLon2[0]), Double.parseDouble(latLon2[1]));
                  dist[adj.get(u).get(i)] = dist[u] + distance;
                  pred[adj.get(u).get(i)] = u;
                  queue.add(adj.get(u).get(i));

                  if (adj.get(u).get(i) == dest) // stopping condition, at destination node
                      return true;
              }
          }
      }
      return false;
  }

  /**
   * Method runs simulation using the compiled tasks and participants using the graph
   * @param tasks vector of all tasks
   * @param graph the graph that represents the city
   */
  public void scenario(Vector<Task> tasks, ArrayList<ArrayList<Integer>> graph){
    int impossiblePaths = 0; //should always be 0 = complete graph
    int tasksDone = 0;

    for (Participant p : this.allParticipants){
      Trip pTrip = p.getTrip();
      LinkedList<Integer> path = this.shortestPath(graph, pTrip.startLocationVertex, pTrip.endLocationVertex, graph.size());
      if (path != null){
        StepEvent event = new StepEvent(p.getTime(), p); //rn int change to double in participant and here
        tasksDone += event.execute(path, tasks, this.reverseTrackNodes, this.trackLatLon);
      } else {
        impossiblePaths++;
      }
    }
    System.out.println("Simulation finished. Tasks completed: " + tasksDone);
    System.out.println("impossiblePaths: " + impossiblePaths);

  }

  /**
   * Method checks if graph is connected
   * @param startNode
   * @param numNodes total nodes in graph
   * @param graph
   */
  public void BFS(int startNode, int numNodes, ArrayList<ArrayList<Integer>> graph) {
      int count = 0;
      boolean visited[] = new boolean[numNodes + 1];
      LinkedList<Integer> queue = new LinkedList<Integer>();
      visited[startNode]=true;
      queue.add(startNode);

      while (queue.size() != 0){
          startNode = queue.poll(); // Dequeue a vertex from queue and print it

          ArrayList<Integer> neighbors = graph.get(startNode);
          for (int i = 0; i < neighbors.size(); i++){
              if (!visited[neighbors.get(i)]){
                  visited[neighbors.get(i)] = true;
                  queue.add(neighbors.get(i));
                  count += 1;
              }
          }
      }
      System.out.println("count: " + count);
  }

  //tests shortest path algorithm
  public void testSmallGraph(ArrayList<ArrayList<Integer>> graph, String start, String dest, int numNodes){
    LinkedList<Integer> path = this.shortestPath(graph, start, dest, numNodes);
    System.out.println("Path length: " + path.size());
    for (int i = 0; i < path.size(); i++){
      System.out.println(path.get(i));
    }
  }

  public static void main(String[] args) {
    srpGraph test = new srpGraph();
    HashMap<String, Integer> initialNodes = test.parseNodeCSV(args[0]);
    System.out.println("Total Nodes: " + initialNodes.size());

    //boolean[][] graph = test.parseWayCSV(args[1], nodes);
    //sets up graph/city
    ArrayList<ArrayList<Integer>> nodeNeighbors = test.parseWayCSV(args[1], initialNodes); 
    //update nodes for no buildings
    HashMap<String, Integer> nodes = test.parseNodesNoBuilding(nodeNeighbors); 
    // have to update graph for no buildings
    ArrayList<ArrayList<Integer>> updatedGraph = test.parseWayCSV(args[1], nodes); 
    //finds most frequented locations using popularity index, here 0.98
    //popularity index must be between 0 and 1
    List<String> popularNodes = test.parseTrackPtCSV(args[2], nodes, 0.98);
    //layers,tasks, part, st, end,max task, max part
    Vector<Task> tasks = test.distributeTasks(updatedGraph, popularNodes, 15, 1000, 3000, 7, 19, 20, 30); 
    System.out.println("Tasks added in total: " + tasks.size());

    System.out.println("=============================Scenario================================");
    test.scenario(tasks, updatedGraph);

    //Now collect data from scenario
    //Analysis results = new Analysis();
    //results.tasksCompletedData(tasks);
    //System.out.println("Tasks placed in csv file!");

    //Collect data to later create visual
    //HashMap<String, String> mappedNodes = test.getMappedLocations();
    //results.createVisual(tasks, mappedNodes); 
    //System.out.println("Data in csv file ready for visual!");
  }

}