package edu.ucla.nesl.mca.feature;

import java.util.LinkedList;

import android.hardware.SensorManager;

public class AlgorithmUtil {
	public static final int INDOOR_THRESHOLD = 5654;
	public static final int QUEUE_SIZE = 90;
	public static boolean outdoors = false;
    public static LinkedList<Integer> ioq = new LinkedList<Integer>();
    public static int ioScore = 0;
    
    public static synchronized void setIndoor() {
        outdoors = false;
    }
			
    public static synchronized void setOutdoor() {
        outdoors = true;
    }
    
    public static synchronized boolean getOutdoor() {
        return outdoors;
    }

    public static double goertzel(double [] data, double freq, double sr){
		double s_prev = 0;
        double s_prev2 = 0;
        double coeff = 2 * Math.cos( (2*Math.PI*freq) / sr);
        double s;
        for (int i = 0; i < data.length; i++)
        {
        	double sample = data[i];
            s = sample + coeff*s_prev  - s_prev2;
            s_prev2 = s_prev;
            s_prev = s;
        }
        double power = s_prev2*s_prev2 + s_prev*s_prev - coeff*s_prev2*s_prev;

        return power;
	}
	
	public static double[] magnititute(double accx[], double accy[], double accz[]) {
		if (!((accx.length == accy.length) && (accy.length == accz.length))) {
			return null;
		}
		double result[] = new double[accx.length];
		for (int i = 0; i < accx.length; i++) {
			result[i] = 0.0;
			double grav = SensorManager.GRAVITY_EARTH;
			result[i] += Math.pow(accx[i] / grav, 2.0); 
	        result[i] += Math.pow(accy[i] / grav, 2.0); 
	        result[i] += Math.pow(accz[i] / grav, 2.0); 
	        result[i] = Math.sqrt(result[i]);
	        result[i] *= 310;
		}
		
        return result;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
