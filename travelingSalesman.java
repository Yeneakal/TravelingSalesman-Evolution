
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Scanner;

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
	private static final int STATION_SIZE = 5;


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

	/////////////////////////////////////////////////////////////
	// Visual components that are used throughout the code     //
	/////////////////////////////////////////////////////////////

	// Map of earth displayed with equi-rectangular coordinate projection
	private static GImage mapImage;
	// Text message indicating that data is loading
	private GLabel loadingMessage;
	//StationLabel variable used for mouse clicked.
	private GLabel stationLabel;

	/////////////////////////////////////////////////////////////
	// Values that change when the slider value changes        //
	/////////////////////////////////////////////////////////////

	// Indicates that new data needs to be plotted on the map in the run method
	private boolean loadData = false;

	/**
	 * In the previous project, you made use of the GOval class from acm.jar to draw circles and 
	 * ovals on the display. Full documentation for the acm.jar GOval class is here: 
	 * https://web.stanford.edu/class/cs106a/javataskforce/javadoc/student/acm/graphics/GOval.html
	 * 
	 * HOWEVER, in this program you should use the LabeledGOval class below instead of GOval.
	 * You construct a LabeledGOval just like a GOval, except you add a String label as the
	 * first parameter to the constructor. This will allow you to associate a String label with
	 * each oval that you draw to represent a temperature reading station. You will find it helpful
	 * to associate the station ID with each station that you draw on the map.
	 * 
	 * Note that the LabeledGOval class is an inner class, defined inside of the Climate class.
	 * You will learn more about defining and using inner classes in Computer Science II.
	 */
	private class LabeledGOval extends GOval { // LabeledGOval is just a GOval plus extra features

		/**
		 * Each LabeledGOval instance has its own String label.
		 * Note that the label is not automatically displayed.
		 * It is simply associated with the oval that contains it.
		 */
		String label;

		/**
		 * Create an oval of a given size with a given label.
		 * @param label String associated with oval (not displayed)
		 * @param width Width in pixels of box containing oval
		 * @param height Height in pixels of box containing oval
		 */
		public LabeledGOval(String label, double width, double height) {
			super(width,height);
			this.label = label;
		}

		/**
		 * Like the previous constructor, but also specifies location of oval.
		 * (could also be specified using the GOval setLocation method)
		 * @param label String associated with oval (not displayed)
		 * @param x X-coordinate of upper-left corner of bounding box
		 * @param y Y-coordinate of upper-left corner of bounding box
		 * @param width Width in pixels of box containing oval
		 * @param height Height in pixels of box containing oval
		 */
		public LabeledGOval(String label, double x, double y, double width, double height) {
			super(x,y,width,height);
			this.label = label;
		}
		/**
		 * Getter method that will return the label associated with 
		 * the oval 
		 * @return the label of the oval. 
		 */
		public String getLabel() {
			return label;
		}
	}

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
			loadStations();
			//loadTemperatures();
		} catch (FileNotFoundException e) {
			System.out.println("Cannot start program without appropriate data files");
			e.printStackTrace();
			System.exit(1);
		}

		// Creates a loading message to indicate when loading is occurring
		loadingMessage = new GLabel("Loading");
		loadingMessage.setColor(Color.BLACK);
		add(loadingMessage);

		// Start by loading data for most recent complete year
		//loadData = true;
		//yearToLoad = END_YEAR;
		//resetDisplay(yearToLoad);
		setLoading();		

		// Required to respond to mouse clicks and movement
		//addMouseListeners();
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
		for(int i = 0; i < numStations; i++) {
			String id = stations.next();
			String restOfLine = stations.nextLine(); // reads rest of line
			//System.out.println(restOfLine);
			String name = restOfLine.substring(1,31).trim(); // trim removes whitespace at edges
			Scanner latLonScanner = new Scanner(restOfLine.substring(31)); // scan the rest of the line
			double lat = latLonScanner.nextDouble();
			double lon = latLonScanner.nextDouble();
			latLonScanner.close();
			//System.out.println(name +":"+lat+":"+lon);

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
	 * Reset the entire display to the way it is at the start of the program,
	 * but with the year slider set to the designated year. Start by removing
	 * all objects from the window, and then proceed to fill it with all the
	 * components you need: map, heat scale, year slider (with specific year
	 * set), and empty station temperature plot in the upper right.
	 * 
	 * The method also places the "Loading" message on the screen, so that the
	 * temperature data for each station can be plotted within the run method.
	 * 
	 * @param sliderYear Year to set the slider to, and to load data for.
	 */
	private void resetDisplay(int sliderYear) {
		removeAll();
		plotMap();
		//plotHeatScale();
		//createYearSlider(sliderYear);
		//drawTemperatureScale(TOP_STATION_PLOT_EDGE);
		add(loadingMessage);
	}		

	/**
	 * Display the equi-rectangular projection of the earth at 1/2 its
	 * original size.
	 */
	private void plotMap() {
		mapImage = new GImage(MAP_IMAGE);
		mapImage.scale(0.5);
		add(mapImage);
	}

	/**
	 * Move the "Loading" message into view to indicate that data is loading
	 */
	private void setLoading() {
		loadingMessage.setLocation(10, MAP_HEIGHT+50);
	}

	/**
	 * Move the "Loading" message out of sight to indicate that loading is finished
	 */
	private void doneLoading() {
		loadingMessage.setLocation(-100, 0);
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
				plotTemperatureAnomalies(yearToLoad);
				doneLoading();
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
	private void plotTemperatureAnomalies(int year) {


		for(int i = 0; i < numTemperatureDataEntries; i ++ ) {
			if(year == years[i]) { // if parameter matches the data gathered in for-loop, execute the body. 

				String id = temperatureDataStationIDs[i];

				double count = 0;
				double total = 0;

				for(int month = 0; month < NUM_MONTHS; month++) {

					if( temperatures[month][i] != MISSING) {

						double monthAnom = temperatures[month][i] - baseLineTemperature(month, id);
						total += monthAnom;
						count++;
					}
				}
				double avgAnomaly = total/count;
				int ind = findIndex(id);

				String name = stationNames[ind];
				double latit = coordinates[INDEX_LATITUDE][ind];
				double longit = coordinates[INDEX_LONGITUDE][ind];

				//assigns heat scale color.
				GOval station = new LabeledGOval(name,STATION_SIZE,STATION_SIZE);
				station.setFilled(true);
				station.setColor(heatScale(avgAnomaly));

				//plots the data on the map.
				double x = mapX(longit);
				double y = mapY(latit); 

				station.setLocation(x, y);
				add(station);
			}
		}

	}




	/**
	 * Return the index in stationNames and coordinates 
	 * associated with a given String station ID.
	 * @param String a stationID from temperatureDataStationIDs
	 * @return an index in stationIDs such that 
	 *              stationIDs[index].equals(stationID)
	 **/
	public static int findIndex(String stationID) {

		for(int i = 0; i < numStations ; i++) {

			if(stationIDs[i].equals(stationID)) {
				return i;
			}
		}

		throw new IllegalArgumentException();
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
