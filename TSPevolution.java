
public class TSPevolution {
	
	
	//fitness function that should give a score of our current 
	//distance from a city visited.
public static void fitnessFunction(cities[]) {
	double dist = 0;
	for(int i = 0; i < cities.length-2; i++)
		dist += distance(cities[i],cities[i+1]);
	return -distance
	}
}
