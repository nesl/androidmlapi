/**
 * Class to provide information about features and sensors
 * Need to be finished
 */
package edu.ucla.nesl.mca.feature;


public class BuiltInFeature {
	
	public static Feature.OPType getOPType(int id) {
		if (id == 1 || id == 2 || id == 3) {
			return Feature.OPType.REAL;
		}
		else if (id == 0) {
			return Feature.OPType.NOMINAL;
		}
		else {
			return Feature.OPType.NOMINAL;
		}
	}
}
