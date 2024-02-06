import java.util.ArrayList;
import java.util.Comparator;

public class Airport implements Comparator<Airport>{
    String airportCode;
    String airfieldName;
    double latitude;
    double longitude;
    int parkingCost;
    public Airport path;
    public double cost;
    public double cost2;//newly added
    public Boolean fake;//newly added
    public int time;
    public Airport(){

    }
    public Airport(String airportCode,String airfieldName,double latitude, double longitude,int parkingCost){//initialization
        this.airportCode=airportCode;
        this.airfieldName=airfieldName;
        this.latitude=latitude;
        this.longitude=longitude;
        this.parkingCost=parkingCost;
        this.fake=false;

    }
    //priority queue
    public Airport(String airportCode,double cost,String airfieldName,double longitude,double latitude,Airport path){//task1
        this.airportCode=airportCode;
        this.cost=cost;
        this.airfieldName=airfieldName;
        this.longitude=longitude;
        this.latitude=latitude;
        this.path=path;//used in the first task
        this.fake=false;

    }
    public Airport(String airportCode,double cost,String airfieldName,double longitude,double latitude,Airport path,int time){//task2
        this.airportCode=airportCode;
        this.cost=cost;
        this.airfieldName=airfieldName;
        this.longitude=longitude;
        this.latitude=latitude;
        this.path=path;//used in the first task
        this.time = time;//second task
        this.fake=false;

    }
    //new method
    public Airport(String airportCode,double cost,double cost2,String airfieldName,double longitude,double latitude,Airport path,int time,Boolean fake){//task2
        this.airportCode=airportCode;
        this.cost=cost;
        this.airfieldName=airfieldName;
        this.longitude=longitude;
        this.latitude=latitude;
        this.path=path;//used in the first task
        this.time = time;//second task
        this.cost2=cost2;
        this.fake= true;
    }
    //
    public int compare(Airport o1,Airport o2){
        if(o1.fake){
            if (o1.cost2 < o2.cost2)
                return -1;

            if (o1.cost2 > o2.cost2)
                return 1;

            return 0;


        }else{
            if (o1.cost < o2.cost)
                return -1;

            if (o1.cost > o2.cost)
                return 1;

            return 0;

        }

    }




}
