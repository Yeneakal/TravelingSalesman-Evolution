package GeneticObjects;



/*
 * Helps manage a population of candidate routes
 */
public class Population {

    // Holds population of routes
    Route[] routes;

    /*
     * Method Population helps generate our population
     */
    public Population(int populationSize, boolean initialise) {
    	//passing the size of the population
        routes = new Route[populationSize];
       
        //when true, we need to initialize a population of routes
        if (initialise) {
            
        	//for loop creating individuals, less than 
        	//populationSize parameter passed
            for (int i = 0; i < populationSize(); i++) {
                Route newRoute = new Route();
                newRoute.generateIndividual();
                saveRoute(i, newRoute);
            }
        }
    }
    
    // Access a route from the population
    public Route getRoute(int index) {
    	
        return routes[index];
    }
    
    // Allows us to save a route
    public void saveRoute(int index, Route route) {
    	
        routes[index] = route;
    }
    
   
 // return the length of the array which is the population size
    public int populationSize() {
        return routes.length;
    }

    // Gets the best route in the population
    public Route getFittest() {
        Route fittest = routes[0];
        // Loop through individuals to find fittest
        for (int i = 1; i < populationSize(); i++) {
        	
            if (fittest.getFitness() <= getRoute(i).getFitness()) {
            	//if the current fitness is less than the next fitness,
            	//update to return the best fitness so far
                fittest = getRoute(i);
            }
        }
        return fittest;
    }

    
}