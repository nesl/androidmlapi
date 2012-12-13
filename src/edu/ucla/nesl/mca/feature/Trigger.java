package edu.ucla.nesl.mca.feature;

import android.util.Log;
import edu.ucla.nesl.mca.classifier.RealOperator;
import edu.ucla.nesl.mca.feature.Feature.OPType;

public class Trigger {
	private int feature;
	private RealOperator realOp;
	private OPType type;
	private double threshold;
	private String value;
	private int duration;
	
	
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public OPType getType() {
		return type;
	}
	public void setType(OPType type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void setFeature(int feature) {
		this.feature = feature;
	}
	public int getFeature() {
		return feature;
	}
	public void setSensor(int feature) {
		this.feature = feature;
	}
	public RealOperator getRealOp() {
		return realOp;
	}
	public void setRealOp(RealOperator realOp) {
		this.realOp = realOp;
	}
	public double getThreshold() {
		return threshold;
	}
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	public Trigger(int feature, String operator, double threshold, int duration) {
		super();
		this.feature = feature;
		for (RealOperator o : RealOperator.values()) {
            if (o.toString().equals(operator)) {
            	this.realOp = o;
            	Log.i("Feature", "Operation=" + this.realOp + " find corresponds");
            	break;
              }
        }
		this.threshold = threshold;
		this.duration = duration;
		this.type = OPType.REAL;
	}
	
	public Trigger(int feature, String value, int duration) {
		this.type = OPType.NOMINAL;
		this.value = value;
		this.duration = duration;
	}
}