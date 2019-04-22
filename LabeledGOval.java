import acm.graphics.GOval;

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
	public class LabeledGOval extends GOval { // LabeledGOval is just a GOval plus extra features

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
