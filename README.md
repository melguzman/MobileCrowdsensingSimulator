# MobileCrowdsensingSimulator
In this research project, I created a Mobile Crowdsensing Simulator (MCS) in Java. I will provide some context and explain usage below.

## Introduction
With the growing availability of smartphones and other IoT devices, high speed wireless networks, and cloud computing, there has been a rising interest in smart city services. In smart cities, these technologies are used to improve city operations and enhance the lives of its residents. Mobile crowdsensing (MCS) is one paradigm that utilizes the sensing capabilities of the crowds in a smart city, thus adding the human-in-loop of the sensing process.

## Background
MCS systems use data shared by the mobile devices of a group of people in a city to measure and analyze any processes of a domain interest, like traffic jams, to introduce or improve on a smart city service. This data comes from embedded sensors such as microphones, cameras, and GPS in mobile phones. The model of data collection in MCS is either opportunistic or participatory. In an opportunistic MCS system, there is some central platform that tracks a user, but the user is rarely directly involved in data acquisition. Instead, the model relies on the sensors embedded in the user’s mobile devices automatically collecting and submitting data. In a participatory MCS system, users are actively involved in the acquisition of data, and it is the responsibility of the participant to complete the tasks and submit their data. 

As demonstrated in both models, MCS systems are reliant on large pools of participants to generate the necessary data. However, when it comes time for evaluating new MCS mechanisms, it is challenging to recruit the adequate set of participants needed for the evaluation. Moreover, providing financial incentives for such recruited participants adds extra burdens on developers. Simulators are therefore a useful and cost-effective tool for representing a domain of interest, and analyzing novel crowdsensing scenarios, along with the expected behavior of users in these scenarios. 

## Project
My MCS simulates a variety of mobile crowdsensing activities in an actual realistic urban environment using imported city map files and public GPS mobility traces of individuals. I focused on the realistic nature of the crowdsensing settings by modeling frequented locations (popular neighborhoods) of a city and creating heterogenous participants. Because these neighborhoods are popular, more people are likely to be located here and traveling. It could be advantageous to distribute a greater portion of tasks to these locations in hopes that more tasks are completed. As for simulating participants, I wanted to add some realistic factors. Not every person will want to complete 20 crowdsensing tasks in one day. People typically have varying daily schedules so I designed my participants to have different workloads (maximum tasks willing to complete). I also modeled participants so that they move at different speeds. One participant could be walking, another participant could be driving, and another participant could be biking. Because people travel in different modes, the completion of tasks is affected and a mobile crowdsensing simulator should have the capabilities to capture this fact.

## General Design
### Simulating a City 
- I use geodata of city maps obtained from OpenStreetMap (https://www.openstreetmap.org/#map=4/38.01/-95.84) to create the layout of a specific city. 
- The layout is represented by a graph of nodes. A node consists of a latitude and longitude coordinate. 

### Tasks
- Tasks are currently defined by their location and starting time that specifies when they are available for users to complete, their status of completion, and the time when they were actually completed.

### Participants
- A participant is currently defined by their arrival time (randomized by their trip), workload (amount of tasks willing to complete), and speed of travel. 
- The speed of travel is currently designed as just inconsistent so the speed of a participant is randomized and constantly varying as they travel along their route. 

### Trips
- A trip is defined by a start location and an end location.
- Each participant is taking a trip based on their locations and destinations. These locations are randomly selected, but they are based on four types of trips: traveling from a regular node to a node in a popular neighborhood, traveling from a node in a popular neighborhood to a regular node, traveling from one node in a popular neighborhood to another node in a popular neighborhood, and traveling from one regular node to another regular node. 
- This simulates participants traveling from a non-popular area like their home to a popular area in a city where it's congested with people and traffic and vice versa or traveling solely within a popular area in the city or traveling solely within a non popular area.

### Popular Neighborhoods
- I use public GPS mobility traces of actual individuals, which just contains the paths/routes in a city that an individual walked, biked, ran, or drove. With this information, I am able to determine what locations were frequently visited and from there are able to identify what are popular neighborhoods in the city.
Obtains from OpenStreetMap
- There is a popularity index (settable by the user) that indicates how many people have to frequent a location for that location to be considered popular

### Distributing Tasks and Participants
- Tasks and Participants were randomly assigned to locations, but they are particularly placed in popular neighborhoods
- I use a modified version of BFS to establish popular neighborhoods by placing more tasks and participants in these areas “layer by layer” 

### Routes
- I use the shortest path algorithm based off the start location and destination of participants

### Simulating Movement of Participants 
- I stimulate the steps that a participant takes as they travel on their route. With each step, the internal clock is updated and the participant checks the available tasks at each node/location and marks some tasks as complete and continues until the very end of their trip.
- A step does not necessarily mean a physical step. A step in this simulator just means moving from one point to another. It is defined so to account for participants that may be traveling a different speeds or different modes (driving versus walking)
- Inconsistent speed of participant implemented here so a participate does not always complete tasks at every node in a consecutive time

### Testing/Analysis
- Able to produce a csv file that can be imported to http://www.heatmapper.ca/ create a heap map visual on the distribution of tasks
- Simple unmodified BFS that checks if a graph is connected (likely to never be connected)
- A very small graph that checks logic of algorithms like shortest path
- Able to produce csv file that contains information about all of the tasks 

### Allocation algorithm 
- Currently, participants just complete a random amount of available tasks at the current node they stepped to in their path depending on their limitations like speed and workload.

## Working with OpenStreetMap Data
My MCS uses imported city map files and public GPS mobility traces of actual individuals from Open Street Map using their API (https://wiki.openstreetmap.org/wiki/API_v0.6#Get_GPS_Points:_Get_.2Fapi.2F0.6.2Ftrackpoints.3Fbbox.3Dleft.2Cbottom.2Cright.2Ctop.26page.3DpageNumber). I used the BasicOSMParser to convert the OSM files, which were the city map files, into CSV files. The BasicOSMParser (https://github.com/PanierAvide/BasicOSMParser/) is a free software that can be found on GitHub. It parses the OpenStreetMap data into 3 neat csv files: one file for nodes, one file for relations, and one file for ways. The file for relations is irrelevant for my MCS. The wiki page for OpenStreetMap does a great job on explaining these elements nodes, relations, and ways (https://wiki.openstreetmap.org/wiki/Elements). It also has many other resources and helpful frameworks. To use the BasicOSMParser, the creator explains:
> In order to use BasicOSMParser, you can download the BasicOSMParser.jar file. Alternatively, you can put the content of src/main/ folder in the source directory of your project. Then, add this code in your classes to import the parser:

```
import info.pavie.basicosmparser.controller.*;
import info.pavie.basicosmparser.model.*; 
```
To then convert to csv files, just type in your termianl: 
```
java -jar BasicOSMParser.jar /path/to/data.osm /path/to/output/folder/
```
I also used an online converter MyGeodata Converter (https://mygeodata.cloud/converter/gpx-to-csv) to convert the public GPS mobility traces that were GPX files from OpenStreetMap into CSV files. 

## Usage of MCS
With the three csv files: nodes file, ways file, and the public GPS mobility traces file, you can then run the program with the files after compiling the program. Just type in your terminal: 
```
java srpGraph /path/to/nodes.csv /path/to/ways.csv /path/to/mobilityTrace.csv
```
To see an example, I included existing csv files on the area of Venice in Los Angeles: nodesVenice.csv, waysVenice.csv, and tracksVeniceCombined.csv. Just type in your terminal:
```
java srpGraph /path/to/mcsData/nodesVenice.csv /path/to/mcsData/waysVenice.csv /path/to/mcsData/tracksVeniceCombined.csv  
```
In the terminal, a lot of information will get printed. At the end, you should see 
```
=============================Scenario================================
Simulation finished. Tasks completed: 477
impossiblePaths: 228
```
Note #1: There is a lot of randomization involved in how tasks and participants are created and distributed so the numbers above will be different each time you run the program despite using the same files.
Note #2: Impossible Paths is natural due to the nature of city maps. A city is represented as a graph in the code and the graph may contain some cycles or may not always be connected leading to some impossible paths.

## Modification 
In the srpGraph.java file under the main method where method parseTrackPtCSV is called, you can modify how popular nodes are selected. A popularity index is used to determine how many times a location must be visited for it to be considered popular. The popularity index must be between 0 and 1. The default is 0.98. You can also modify how tasks and participants are distributed throughout the graph by changing the values of the distributeTasks method. 
- You can change the number of layers, which is basically how much area around the frequented GPS coordinate/point on the graph you want to define as the popular area. The default is 15 layers. 
- You can change the number of tasks to distribute. The default is 1,000
- You can change the number of participants. The default is 3,000
- You can change the start time of the simulation. The default is 7 (7 AM). All time is in military time. 
- You can change the end time of the simulation. The default is 19 (7 PM).
- You can change the maximum number of tasks allowed to be assigned to one point of the graph (location point in city). The default is 20.
- You can change the maximum number of participants allowed to be placed to one point of the graph (location point in city). The default is 30.

You can also obtain information about the results by making an instance of the Analysis class and calling its functions tasksCompletedData and createVisual. The tasksCompleteData method creates a csv file that contains information about all of the tasks from the crowdsensing scenario. This data says which tasks were completed, not completed, at what time during the simulation was the task completed, and where were the locations of the tasks. The createVisual method creates a csv file that contains information on where tasks were placed in the city. You can then upload this file to an online heat map generated like (http://www.heatmapper.ca/) to create a heatmap. This is very useful when you want to see how a large portion of tasks were assigned to points inside popular neighborhoods and how their completion rate was affected by also looking at the data returned by the tasksCompletedData method.


