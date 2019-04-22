
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JSlider;


import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GLine;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GPoint;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;


/**
 * Achilles Armendariz
 * armendaa@southwestern.edu
 * Artificial Intelligence
 * 04/14/2019
 * @author achilles armendariz
 *
 */
public class travelingSalesman extends GraphicsProgram{

	/////////////////////////////////////////////////////////////
	// Constants determining the layout of the window contents //
	/////////////////////////////////////////////////////////////
	
	// Width of the map image in pixels
	private static final int MAP_WIDTH = 1024;
	// Height of the map image in pixels
	private static final int MAP_HEIGHT = 513;
	// Height and width of a point depicting a temperature station
	private static final int STATION_SIZE = 8;

	/////////////////////////////////////////////////////////////
	// File names                                              //
	/////////////////////////////////////////////////////////////

	// Path to file with information about temperature reading stations (ID, name, latitude, longitude)
	private static final String STATION_FILE = "../station_list.txt";
	// Path to file with map image
	private static final String MAP_IMAGE = "../Equirectangular_projection_SW.jpg";

	/////////////////////////////////////////////////////////////
	// Data from the STATION_FILE                              //
	/////////////////////////////////////////////////////////////

	// Will store the number of lines in the STATION_FILE
	private static int numStations;
	// Will store list of station IDs in the order they are listed in the STATION_FILE
	private static String[] stationIDs;
	// Will store list of station names in the order they are listed in the STATION_FILE
	private String[] stationNames;
	// Latitude and longitude of each station in the STATION_FILE
	private double[][] coordinates = new double[2][]; // 0 = Latitude, 1 = Longitude
	// Latitude index in coordinates. Example: coordinates[INDEX_LATITUDE][i] is the latitude of station i
	private static final int INDEX_LATITUDE = 0;
	// Longitude index in coordinates. Example: coordinates[INDEX_LONGITUDE][i] is the longitude of station i
	private static final int INDEX_LONGITUDE = 1;	
	
	////////////////////////////////////////////////
	//Data for the Salesman						 //
	//////////////////////////////////////////////
	
	// Will store the Points of cities plotted
	private ArrayList<PointDouble> cities = new ArrayList<PointDouble>();
	//Number of cities the salesman will travel
	private static int citiesToTravel = 25;

	/////////////////////////////////////////////////////////////
	// Visual components that are used throughout the code     //
	/////////////////////////////////////////////////////////////

	// Map of earth displayed with equi-rectangular coordinate projection
	private static GImage mapImage;
	// Text message indicating that data is loading
	private GLabel loadingMessage;
	//StationLabel variable used for mouse clicked.
	private GLabel stationLabel;
	private boolean loadData = false;
	

	

	/**
	 * This method is automatically called once at the start of the program,
	 * and then the run method is called.
	 */
	public void init() {
		resize(MAP_WIDTH, MAP_HEIGHT); // set the window size

		/**
		 * A try/catch block is an alternative approach to handling exceptions that you
		 * will learn more about in Computer Science II. The short summary of how they
		 * work is as follows: run the code in the first set of curly braces, but if an
		 * exception of the type designated in the catch parentheses is thrown, execute
		 * the code in the second set of curly braces. For the code below, the program
		 * will exit with an error if a FileNotFoundException occurs.
		 */
		try {
			//plot cities is called here
			loadStations();
			plotMap();
			plotCities();
		} catch (FileNotFoundException e) {
			System.out.println("Cannot start program without appropriate data files");
			e.printStackTrace();
			System.exit(1);
		}

		// Creates a loading message to indicate when loading is occurring
		loadingMessage = new GLabel("Loading");
		loadingMessage.setColor(Color.BLACK);
		add(loadingMessage);

			

	}

	/////////////////////////////////////////////////////////////
	// Methods for loading data from text files                //
	/////////////////////////////////////////////////////////////

	/**
	 * Counts the number of lines contained within a text file.
	 * 
	 * @param filename Name of file
	 * @return Number of lines in file
	 * @throws FileNotFoundException thrown if file not found
	 */
	private int countLines(String filename) throws FileNotFoundException {
		Scanner temp = new Scanner(new File(filename));
		int count = 0;
		while(temp.hasNextLine()) {
			count++;
			temp.nextLine(); // discard each line we count
		}
		return count;

	}	

	/**
	 * Loads all data from the STATION_FILE. You must first assign the
	 * correct value to numStations by counting the number of entries
	 * in the file. Note that the first line is not an entry, but consists
	 * of column headers.
	 * 
	 * This method should only be called once, and afterward stationIDs,
	 * stationNames, and coordinates will all be filled with data.
	 * 
	 * @throws FileNotFoundException 
	 * 				this exception will be thrown if you name your 
	 * 				station file incorrectly or store it in the 
	 * 				wrong location.
	 */
	private void loadStations() throws FileNotFoundException {
		// Count the stations
		numStations = countLines(STATION_FILE) - 1;
		// Go through file again to save station data
		stationIDs = new String[numStations];
		stationNames = new String[numStations];
		coordinates = new double[2][numStations]; // 2 is for x and y coordinates
		Scanner stations = new Scanner(new File(STATION_FILE)); // reinitialize Scanner from start
		stations.nextLine(); // Discard the column headers
		System.out.println("number of stations" + numStations);
		for(int i = 0; i < numStations; i++) {
			String id = stations.next();
			String restOfLine = stations.nextLine(); // reads rest of line
			String name = restOfLine.substring(1,31).trim(); // trim removes whitespace at edges
			Scanner latLonScanner = new Scanner(restOfLine.substring(31)); // scan the rest of the line
			double lat = latLonScanner.nextDouble();
			double lon = latLonScanner.nextDouble();
			latLonScanner.close();

			stationIDs[i] = id;
			stationNames[i] = name;
			coordinates[INDEX_LATITUDE][i] = lat;
			coordinates[INDEX_LONGITUDE][i] = lon;

		}
		stations.close();
	}

	/////////////////////////////////////////////////////////////
	// Methods dealing with the initial layout of the window   //
	/////////////////////////////////////////////////////////////

	/**
	 * Display the equi-rectangular projection of the earth at 1/2 its
	 * original size.
	 */
	private void plotMap() {
		mapImage = new GImage(MAP_IMAGE);
		mapImage.scale(0.5);
		add(mapImage);
	}



	/////////////////////////////////////////////////////////////
	// Methods dealing with plotting stations on the map       //
	/////////////////////////////////////////////////////////////

	/**
	 * The run method starts executing at the start of the program.
	 * Its purpose is to continue looping forever, waiting for the
	 * loadData variable to be true. When this happens, the
	 * temperature anomalies for the yearToLoad will be plotted
	 * at the geographic coordinates corresponding to the station
	 * where the reading occurred.
	 */
	public void run() {		
		while(true) { // Loop as long as program is running
			pause(1); // Allows program to respond to clicks and other events
			if(loadData) {
				/*System.out.println(" we are in this loop");*/
				try {
					plotCities();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				loadData  = false;
			}
		}
	}


	/**
	 * Goes through the years array to find every entry matching the designated year.
	 * The temperature anomaly for that year at that station is then calculated, and 
	 * that anomaly is used to determine the color of a dot (using the heat scale) 
	 * that is placed on the map to represent the temperature anomaly at the given 
	 * reading station. The dot is place on that map according to the station's 
	 * latitude and longitude, which must be recovered from the coordinates array by 
	 * finding the index in stationIDs that matches a given entry in temperatureDataStationIDs.
	 * 
	 * The main loop performs a calculation for every station that had readings in the
	 * given year. After each such calculation, the result should be added to a sum
	 * that is used at the end of the method to calculate the global average temperature
	 * anomaly for the given year. This value is displayed under the right edge of the map.
	 * 
	 * @param year Year to plot data from
	 */
	
	/**
	 * at this point I need to generate random 10 cities to plot on the map.
	 * $ Get 10 random cities plotted on the map with ovals
	 */
	private void plotCities()throws FileNotFoundException {

		
		//Agent will travel to 25 of the 5,978 cities in stations list
		Random rand = new Random();
		numStations = countLines(STATION_FILE) - 1;
		
		for (int i=0; i<=citiesToTravel; i++ ) {
			//grabs a random index from the range of numStations
			int ind = rand.nextInt(numStations);
			ind +=1;
			
			System.out.println("num station" + ""+ ind);

			String name = stationNames[ind];
			double latit = coordinates[INDEX_LATITUDE][ind];
			double longit = coordinates[INDEX_LONGITUDE][ind];
			
			//System.out.println("name: " + name);

			//assigns color to a city
			GOval station = new LabeledGOval(name,STATION_SIZE,STATION_SIZE);
			station.setFilled(true);
			station.setFillColor(Color.RED);

			//plots the data on the map.
			double x = mapX(longit);
			double y = mapY(latit); 
			
			//add new Points to ArrayList of type point
			cities.add(new PointDouble(x,y));
	
			//plot station
			station.setLocation(x, y);
			add(station);
		}
		System.out.println(" size of the list: " + cities.size());
		System.out.println("point 0 " + cities.get(0));
		System.out.println("point 1 " + cities.get(1));

		
		System.out.println(cities.toString());
		System.out.println("distance from 0 to 1 index: " + cities.get(0).distanceTo(cities.get(1)));
		

		}
		
		



	//Take the map longitude and convert it to a x pixel coordinate on the applet.
	private double mapX(double longitude) {
		return (MAP_WIDTH/2.0)*(longitude/180) + (MAP_WIDTH/2.0);
	}

	//Taking the map latitude and convert it to a y pixel coordinate on the applet
	private double mapY(double latitude) {
		return (-MAP_HEIGHT/2.0)* (latitude/90) + (MAP_HEIGHT/2.0);
	}



}
