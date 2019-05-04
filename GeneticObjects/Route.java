package GeneticObjects;

import java.util.ArrayList;
import java.util.Collections;

/*
 * stores our candidate route
 */
public class Route{

    // Holds our route of cities
	//ArrayList of points type double
    private ArrayList route = new ArrayList<City>();
    private int distance = 0;
    private double fitness = 0;
  
    
    // Constructs a blank route
    public Route(){
        for (int i = 0; i < CityRoute.numberOfCities(); i++) {
            route.add(null);
        }
    }
    
    public Route(ArrayList route){
        this.route = route;
    }

    

    // Access a city from the given route
    public City getCity(int routePosition) {
        return (City)route.get(routePosition);
    }


    // Allows us to set a city in a certain position in the route
    public void setCity(int routePosition, City city) {
        route.set(routePosition, city);
       
        distance = 0;
        fitness = 0;
        
    }
    
 // Creates a random individual
    public void generateIndividual() {
        // Loop through all our destination cities and add them to our route
        for (int cityIndex = 0; cityIndex < CityRoute.numberOfCities(); cityIndex++) {
          setCity(cityIndex, CityRoute.getCity(cityIndex));
        }
        // Randomly reorder the route
        Collections.shuffle(route);
    }
    
    // Gets the routes fitness
    public double getFitness() {
        if (fitness == 0) {
            fitness = 1/(double)getDistance();
        }
        return fitness;
    }
    
    // Gets the total distance of the route
    public int getDistance(){
        if (distance == 0) {
            int routeDistance = 0;
            // Loop through our route's cities
            for (int cityIndex=0; cityIndex < routeSize(); cityIndex++) {
                // Get city we're travelling from
                City fromCity = getCity(cityIndex);
                // City we're travelling to
                City destinationCity;
                // Check we're not on our route's last city, if we are set our 
                // route's final destination city to our starting city
                if(cityIndex+1 < routeSize()){
                    destinationCity = getCity(cityIndex+1);
                }
                else{
                    destinationCity = getCity(0);
                }
                // Get the distance between the two cities
                routeDistance += fromCity.distanceTo(destinationCity);
            }
            distance = routeDistance;
        }
        return distance;
    }

    
    //returns a boolean checking to see if our
    //route contains a city
    public boolean containsCity(City city){
        return route.contains(city);
    }
   
    //returns the number of cities in the route
    public int routeSize() {
        return route.size();
    }
    
   
   
    
    @Override
    public String toString() {
        String stringGene = "|";
        for (int i = 0; i < routeSize(); i++) {
        	
            stringGene += getCity(i)+"|";
        }
        return stringGene;
    }
}