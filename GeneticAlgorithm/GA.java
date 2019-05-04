package GeneticAlgorithm;

import GeneticObjects.City;
import GeneticObjects.Population;
import GeneticObjects.Route;



/*
 * algorithm for evolving population
 */
public class GA {

    
   //Parameters for our genetic algorithm
    private static final int tourneySize = 5;
    private static final double mutationRate = 0.015;
    private static final boolean elitism = true;

  
    //over one generation we are evolving our population
    public static Population evolvePopulation(Population pop) {
        Population newPopulation = new Population(pop.populationSize(), false);

       
        //when elitism is enabled, keep best
        int elitismOffset = 0;
        if (elitism) {
            newPopulation.saveRoute(0, pop.getFittest());
            elitismOffset = 1;
        }

        // Crossover population
        // Loop over the new population's size and create individuals from
        // Current population
        for (int i = elitismOffset; i < newPopulation.populationSize(); i++) {
            // Select parents
            Route parent1 = Tournament(pop);
            Route parent2 = Tournament(pop);
            // Crossover parents
            Route child = crossover(parent1, parent2);
            // Add child to new population
            newPopulation.saveRoute(i, child);
        }
        
        //adding new genetic material to the popuation
        for (int i = elitismOffset; i < newPopulation.populationSize(); i++) {
            mutate(newPopulation.getRoute(i));
        }

        return newPopulation;
    }
    
    // Mutate a route using swap mutation
    private static void mutate(Route route) {
        // Loop through route cities
        for(int routePos1=0; routePos1 < route.routeSize(); routePos1++){
            // Apply mutation rate
            if(Math.random() < mutationRate){
                // Get a second random position in the route
                int routePos2 = (int) (route.routeSize() * Math.random());

                // Get the cities at target position in route
                City city1 = route.getCity(routePos1);
                City city2 = route.getCity(routePos2);

                // Swap them around
                route.setCity(routePos2, city1);
                route.setCity(routePos1, city2);
            }
        }
    }

    // Applies crossover to a set of parents and creates offspring
    public static Route crossover(Route parent1, Route parent2) {
        // Create new child route
        Route child = new Route();

        // Get start and end sub route positions for parent1's route
        int startPos = (int) (Math.random() * parent1.routeSize());
        int endPos = (int) (Math.random() * parent1.routeSize());

        // Loop and add the sub route from parent1 to our child
        for (int i = 0; i < child.routeSize(); i++) {
            // If our start position is less than the end position
            if (startPos < endPos && i > startPos && i < endPos) {
                child.setCity(i, parent1.getCity(i));
            } // If our start position is larger
            else if (startPos > endPos) {
                if (!(i < startPos && i > endPos)) {
                    child.setCity(i, parent1.getCity(i));
                }
            }
        }

        // Loop through parent2's city route
        for (int i = 0; i < parent2.routeSize(); i++) {
            // If child doesn't have the city add it
            if (!child.containsCity(parent2.getCity(i))) {
                // Loop to find a spare position in the child's route
                for (int ii = 0; ii < child.routeSize(); ii++) {
                    // Spare position found, add city
                    if (child.getCity(ii) == null) {
                        child.setCity(ii, parent2.getCity(i));
                        break;
                    }
                }
            }
        }
        return child;
    }

 

    // Selects candidate route for crossover
    private static Route Tournament(Population pop) {
        // Create a tournament population
        Population tournament = new Population(tourneySize, false);
        

        //get a random candidate and add it.
        for (int i = 0; i < tourneySize; i++) {
            int randomId = (int) (Math.random() * pop.populationSize());
            tournament.saveRoute(i, pop.getRoute(randomId));
        }
        
        //return the fittest route
        Route fittest = tournament.getFittest();
        return fittest;
    }
}