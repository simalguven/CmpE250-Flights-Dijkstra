import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        FileWriter filewriter = new FileWriter(args[4],true);
        FileWriter filewriter2 = new FileWriter(args[5],true);
        HashMap<String,Airport> airportHashMap = new HashMap<>();
        List<String> airportNames = new ArrayList<>();
        //String filePath = "/Users/simalguven/IdeaProjects/Project4/src/airports/INTER-2.csv";
        File file = new File(args[0]);
        Scanner scanner = new Scanner (file);
        scanner.nextLine();
        int airportNum=0;
        while(scanner.hasNextLine()){
            String[] line = scanner.nextLine().split(",");
            Airport airport = new Airport(line[0],line[1],Double.parseDouble(line[2]),Double.parseDouble(line[3]),Integer.parseInt(line[4]));
            airportHashMap.put(line[0],airport);
            airportNames.add(line[0]);
            airportNum++;

        }
        scanner.close();

        Graph graph = new Graph(airportNum,airportNames,filewriter,filewriter2);//graph is constructed with number of nodes (airports) in the network
        //String filePath2 = "/Users/simalguven/IdeaProjects/Project4/src/directions/INTER-2.csv";
        File file2 = new File (args[1]);
        Scanner scanner2 = new Scanner(file2);
        scanner2.nextLine();
        while(scanner2.hasNextLine()){
            String[] line = scanner2.nextLine().split(",");
            Airport from = airportHashMap.get(line[0]);
            Airport to = airportHashMap.get(line[1]);
            graph.addAirport(from);
            graph.addConnection(from,to);

        }
        scanner2.close();

        //String filePath3 = "/Users/simalguven/IdeaProjects/Project4/src/weather.csv";
        File file3 = new File(args[2]);
        Scanner scanner3 = new Scanner(file3);
        scanner3.nextLine();
        while(scanner3.hasNextLine()){
            String[] line = scanner3.nextLine().split(",");
            String key = line[0]+line[1];
            int value = Integer.parseInt(line[2]);
            graph.weatherHashMap.put(key,value);
        }
        scanner3.close();

        //String filePath4 = "/Users/simalguven/IdeaProjects/Project4/src/missions/INTER-2.in";
        File file4 = new File(args[3]);
        Scanner scanner4 = new Scanner(file4);
        Scanner scanner5 = new Scanner(file4);
        String aircraft = scanner5.nextLine();
        scanner4.nextLine();
        while(scanner4.hasNextLine()){
            String[] line = scanner4.nextLine().split(" ");
            String airportOrigin = line[0];
            String airportDestination = line[1];
            String timeOrigin = line[2];
            graph.dijkstra(airportHashMap,airportHashMap.get(airportOrigin),airportHashMap.get(airportDestination),timeOrigin);
            graph.setPq(new PriorityQueue<>(airportNum, new Airport()));
            graph.setSettled(new HashSet<>());
        }
        scanner4.close();
        filewriter.close();

        while(scanner5.hasNextLine()){
            String[] line = scanner5.nextLine().split(" ");
            String airportOrigin = line[0];
            String airportDestination = line[1];
            int timeOrigin = Integer.parseInt(line[2]);
            int deadline = Integer.parseInt(line[3]);
            graph.dijkstra_task3(airportHashMap.get(airportOrigin),airportHashMap.get(airportDestination),timeOrigin,deadline,aircraft);
            graph.setPq(new PriorityQueue<>(airportNum,new Airport()));
            graph.setSettled_task2(new HashMap<>());
        }
        scanner5.close();
        filewriter2.close();
    }

}

