import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Graph {
    private double dist[];
    private Set<Airport> settled;
    private HashMap<String,HashMap<Integer,Boolean>> settled_task2= new HashMap<>();
    private PriorityQueue<Airport> pq;
    private final int V;
    public Map<String, List<Airport>> adjacencyList;
    public HashMap<String,Integer> weatherHashMap;
    public HashMap<String,Airport> airportHashMap;
    public Map<String, Integer> airportIndices ;
    public String timeOrigin;
    public List<String> airportNames;
    public int start;//task2
    public int deadline;//task2
    public String aircraft;
    public FileWriter fw1;
    public FileWriter fw2;

    public Graph(int v,List<String> airportNames,FileWriter fw1,FileWriter fw2) {
        this.V = v;
        dist = new double[V];
        settled = new HashSet<>();
        pq = new PriorityQueue<>(V, new Airport());
        adjacencyList=new HashMap<>();
        weatherHashMap = new HashMap<>();
        airportHashMap=new HashMap<>();
        this.airportNames=airportNames;
        airportIndices=new HashMap<>();
        this.fw1=fw1;
        this.fw2=fw2;
    }

    public void addAirport(Airport airport) {
        adjacencyList.putIfAbsent(airport.airportCode, new ArrayList<>());
    }

    public void addConnection(Airport sourceAirport, Airport destinationAirport) {
        adjacencyList.get(sourceAirport.airportCode).add(destinationAirport);
    }

    public void setPq(PriorityQueue<Airport> pq) {
        this.pq = pq;
    }

    public void setSettled(Set<Airport> settled) {
        this.settled = settled;
    }
    public void setSettled_task2(HashMap<String, HashMap<Integer, Boolean>> settled_task2) {
        this.settled_task2 = settled_task2;
    }

    public List<Airport> getConnectedAirports(Airport airport) {
        return adjacencyList.getOrDefault(airport.airportCode, new ArrayList<>());
    }

    public Set<String> getAllAirportNames() {
        return adjacencyList.keySet();
    }


    public void dijkstra(HashMap<String,Airport> airportHashMap, Airport airportOrigin, Airport airportDestination, String timeOrigin) throws IOException {
        this.airportHashMap=airportHashMap;
        this.timeOrigin=timeOrigin;

        for (int i = 0; i < V; i++){
            String airportName = airportNames.get(i);
            airportIndices.put(airportName, i);
            dist[i] = Integer.MAX_VALUE;
        }
        //initialization
        pq.add(new Airport(airportOrigin.airportCode, 0,airportOrigin.airfieldName,airportOrigin.longitude,airportOrigin.latitude,null));
        dist[airportIndices.get(airportOrigin.airportCode)] = 0;

        while (!pq.isEmpty()) {

            Airport u = pq.remove();

            if(u.airportCode.equals(airportDestination.airportCode)){
                String output = airportOrigin.airportCode+" "+print(u) +String.format("%.5f", u.cost);
                //System.out.println(output);
                fw1.append(output).append("\n");
                break;
            }
            if (settled.contains(u))
                continue;

            settled.add(u);

            e_Neighbours(u);
        }
    }
    private void e_Neighbours(Airport u)
    {

        double edgeDistance = -1;
        double newDistance = -1;
        for (int i = 0; i < adjacencyList.get(u.airportCode).size(); i++) {
            Airport v = adjacencyList.get(u.airportCode).get(i);
            if (!settled.contains(v)) {
                edgeDistance = costCalculator(u,v);
                newDistance = dist[airportIndices.get(u.airportCode)] + edgeDistance;

                if (newDistance < dist[airportIndices.get(v.airportCode)]){
                    dist[airportIndices.get(v.airportCode)] = newDistance;
                    v.path=u;
                    pq.add(new Airport(v.airportCode, dist[airportIndices.get(v.airportCode)],v.airfieldName,v.longitude,v.latitude,u));
                }

            }
        }
    }

    static double haversine(double lat1, double lon1,
                            double lat2, double lon2)
    {
        // distance between latitudes and longitudes
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // apply formulae
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(lat1) *
                        Math.cos(lat2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        return rad * c;
    }
    static double weatherMultiplier(int weatherCode){
        String binary = String.format("%5s", Integer.toBinaryString(weatherCode)).replace(' ', '0');
        String[] b =binary.split("");
        int Bw=Integer.parseInt(b[0]);
        int Br=Integer.parseInt(b[1]);
        int Bs= Integer.parseInt(b[2]);
        int Bh= Integer.parseInt(b[3]);
        int Bb= Integer.parseInt(b[4]);
        return (Bw*1.05 + (1-Bw))*(Br*1.05 + (1-Br))*(Bs*1.1 + (1-Bs))*(Bh*1.15 + (1-Bh))*(Bb*1.20 + (1-Bb));
    }

    public double costCalculator(Airport from, Airport to){
        double d = haversine(from.latitude,from.longitude,to.latitude,to.longitude);
        double Wfrom = weatherMultiplier(weatherHashMap.get(from.airfieldName+timeOrigin));
        double Wto = weatherMultiplier(weatherHashMap.get(to.airfieldName+timeOrigin));
        return 300*Wfrom*Wto+d;
    }
    public double costCalculator2(Airport from,String to,double d){
        double Wfrom = weatherMultiplier(weatherHashMap.get(from.airfieldName+from.time));
        double Wto = weatherMultiplier(weatherHashMap.get(to));
        return 300*Wfrom*Wto+d;
    }

    public String print(Airport u){
        Stack<Airport> toPrint = new Stack<>();
        while(u.path!=null){//without starting node
            toPrint.push(u);
            u=u.path;
        }
        StringBuilder sb = new StringBuilder();
        while(!toPrint.isEmpty()){
            sb.append(toPrint.pop().airportCode).append(" ");
        }
        return sb.toString();

    }
    public String print2(Airport u){
        Stack<String> toPrint = new Stack<>();
        while(u.path!=null){
            if(u.path.airportCode.equals(u.airportCode)){
                toPrint.push("PARK");
            }else{
                toPrint.push(u.airportCode);
            }
            u=u.path;
        }
        StringBuilder sb = new StringBuilder();
        while(!toPrint.isEmpty()){
            sb.append(toPrint.pop()).append(" ");
        }
        return sb.toString();
    }

    public void dijkstra_task2(Airport airportOrigin, Airport airportDestination ,int timeOrigin,int deadline ,String aircraft) throws IOException {
        this.aircraft = aircraft;
        this.start = timeOrigin;
        this.deadline=deadline;


        //initialization
        pq.add(new Airport(airportOrigin.airportCode, 0,airportOrigin.airfieldName,airportOrigin.longitude,airportOrigin.latitude,null,timeOrigin));

        for( String name: airportNames){
            settled_task2.put(name,new HashMap<Integer,Boolean>());
        }


        while (!pq.isEmpty()) {// while condition has changed

            Airport u = pq.remove();
            if(settled_task2.get(u.airportCode).containsKey(u.time)){
                continue;
            }
            HashMap<Integer,Boolean> current =settled_task2.get(u.airportCode);
            current.put(u.time,true);

            int parkTime= u.time+6*3600;
            double cost = u.cost+airportHashMap.get(u.airportCode).parkingCost;
            if(parkTime<=deadline)
                pq.add(new Airport(u.airportCode,cost,u.airfieldName,u.longitude,u.latitude,u,parkTime));


            if(u.airportCode.equals(airportDestination.airportCode)){
                String output = airportOrigin.airportCode+" "+print2(u) + String.format("%.5f", u.cost);
                //System.out.println(u.path.airportCode);
                //System.out.println(output);
                fw2.append(output).append("\n");
                return;
            }

            e_Neighbours_task2(u);
        }
        //System.out.println("No possible solution.");
        fw2.append("No possible solution.").append("\n");
    }
    private void e_Neighbours_task2(Airport u)
    {
        int t;
        double edgeDistance = -1;
        double newDistance = -1;
        for (int i = 0; i < adjacencyList.get(u.airportCode).size(); i++) {
            Airport v = adjacencyList.get(u.airportCode).get(i);
            double d = haversine(u.latitude,u.longitude,v.latitude,v.longitude);
            int flightTime;
            if(aircraft.equals("Orion III"))
                flightTime = orion(d);
            else if(aircraft.equals("Skyfleet S570"))
                flightTime= skyfleet(d);
            else if(aircraft.equals("T-16 Skyhopper"))
                flightTime=skyhopper(d);
            else
                flightTime=carreidas(d);
            if(u.time+flightTime<=deadline){
                t = u.time+flightTime;
                edgeDistance=costCalculator2(u,v.airfieldName+t,d);
                newDistance = u.cost + edgeDistance;
                pq.add(new Airport(v.airportCode,newDistance,v.airfieldName,v.longitude,v.latitude,u,(u.time)+flightTime));
            }

        }
    }
    public int carreidas(double d){
        if(d<=175){
            return 6*3600;
        }
        else if(d<=350){
            return 12*3600;
        }
        return 18*3600;
    }
    public int orion(double d){
        if(d<=1500){
            return 6*3600;
        }
        else if(d<=3000){
            return 12*3600;
        }
        return 18*3600;

    }
    public int skyfleet(double d){
        if(d<=500){
            return 6*3600;
        }
        else if(d<=1000){
            return 12*3600;
        }
        return 18*3600;

    }
    public int skyhopper(double d){
        if(d<=2500){
            return 6*3600;
        }
        else if(d<=5000){
            return 12*3600;
        }
        return 18*3600;

    }

    public void dijkstra_task3(Airport airportOrigin, Airport airportDestination ,int timeOrigin,int deadline ,String aircraft) throws IOException {
        this.aircraft = aircraft;
        this.start = timeOrigin;
        this.deadline=deadline;


        //initialization
        //pq.add(new Airport(airportOrigin.airportCode, 0,airportOrigin.airfieldName,airportOrigin.longitude,airportOrigin.latitude,null,timeOrigin));
        pq.add(new Airport(airportOrigin.airportCode, 0,0+haversine(airportOrigin.latitude,airportOrigin.longitude,airportDestination.latitude,airportDestination.longitude),airportOrigin.airfieldName,airportOrigin.longitude,airportOrigin.latitude,null,timeOrigin,true));


        for( String name: airportNames){
            settled_task2.put(name,new HashMap<Integer,Boolean>());
        }


        while (!pq.isEmpty()) {// while condition has changed

            Airport u = pq.remove();
            if(settled_task2.get(u.airportCode).containsKey(u.time)){
                continue;
            }
            HashMap<Integer,Boolean> current =settled_task2.get(u.airportCode);
            current.put(u.time,true);

            int parkTime= u.time+6*3600;
            double cost = u.cost+airportHashMap.get(u.airportCode).parkingCost;

            if(parkTime<=deadline)
                //pq.add(new Airport(u.airportCode,cost,u.airfieldName,u.longitude,u.latitude,u,parkTime));
                pq.add(new Airport(u.airportCode,cost,cost+haversine(u.latitude,u.longitude,airportDestination.latitude,airportDestination.longitude),u.airfieldName,u.longitude,u.latitude,u,parkTime,true));



            if(u.airportCode.equals(airportDestination.airportCode)){
                String output = airportOrigin.airportCode+" "+print2(u) + String.format("%.5f", u.cost);
                //System.out.println(u.path.airportCode);
                //System.out.println(output);
                fw2.append(output).append("\n");
                return;
            }

            e_Neighbours_task3(u,airportDestination);
        }
        //System.out.println("No possible solution.");
        fw2.append("No possible solution.").append("\n");
    }
    private void e_Neighbours_task3(Airport u,Airport dest)
    {
        int t;
        double edgeDistance = -1;
        double newDistance = -1;
        for (int i = 0; i < adjacencyList.get(u.airportCode).size(); i++) {
            Airport v = adjacencyList.get(u.airportCode).get(i);
            double d = haversine(u.latitude,u.longitude,v.latitude,v.longitude);
            int flightTime;
            if(aircraft.equals("Orion III"))
                flightTime = orion(d);
            else if(aircraft.equals("Skyfleet S570"))
                flightTime= skyfleet(d);
            else if(aircraft.equals("T-16 Skyhopper"))
                flightTime=skyhopper(d);
            else
                flightTime=carreidas(d);
            if(u.time+flightTime<=deadline){
                t = u.time+flightTime;
                edgeDistance=costCalculator2(u,v.airfieldName+t,d);
                newDistance = u.cost + edgeDistance;
                //pq.add(new Airport(v.airportCode,newDistance,v.airfieldName,v.longitude,v.latitude,u,(u.time)+flightTime));
                if (!settled_task2.get(v.airportCode).containsKey(t) || !settled_task2.get(v.airportCode).get(t))
                    pq.add(new Airport(v.airportCode,newDistance,newDistance+haversine(v.latitude,v.longitude,dest.latitude,dest.longitude),v.airfieldName,v.longitude,v.latitude,u,(u.time)+flightTime,true));
            }

        }
    }
}

