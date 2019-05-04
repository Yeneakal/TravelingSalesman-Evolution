package GeneticObjects;

import java.util.ArrayList;


/*
* 
* Holds the cities of the route
*/
public class CityRoute {

    // private arrayList meant to store our cites 
	//cities are points of type double
    private static ArrayList destinationCities = new ArrayList<City>();

    // adds a distination city to the Arraylist
    public static void addCity(City city) {
    	
        destinationCities.add(city);
    }
    
    // Getter. allows access of a city in the ArrayList destinationCities
    public static City getCity(int index){
    	
        return (City)destinationCities.get(index);
    }
    
    // return the size of the ArrayList destinationCities
    //this will tell us the number of cities
    public static int numberOfCities(){
    	
        return destinationCities.size();
    }
}
