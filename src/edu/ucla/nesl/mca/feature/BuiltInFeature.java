/**
 * Class to provide information about features and sensors
 * Need to be finished
 */
package edu.ucla.nesl.mca.feature;


public class BuiltInFeature {
	
	public static Feature.OPType getOPType(int id) {
		if (id == 1 || id == 2) {
			return Feature.OPType.REAL;
		}
		else {
			return Feature.OPType.NOMINAL;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
