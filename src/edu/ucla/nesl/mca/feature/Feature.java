package edu.ucla.nesl.mca.feature;

import java.util.ArrayList;

import android.os.Bundle;


public class Feature {
    public enum OPType {
        NOMINAL(1),
        REAL(2);
 
        private final int num;
        OPType(int value) {
            this.num = value;
        }
        public int num() {
            return this.num;
        }
    }
    
    private int id;
    private String name;
    private int sensor;
    private OPType opType;
    private double dataValue;
    private boolean isResult;
    private ArrayList<String> dataSet;   // only used if NOMINAL
    private Bundle data;
    
    public Feature() {
    	dataSet = new ArrayList<String>();
    }

	public boolean isResult() {
		return isResult;
	}

	public void setResult(boolean isResult) {
		this.isResult = isResult;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getSensor() {
		return sensor;
	}

	public void setSensor(int sensor) {
		this.sensor = sensor;
	}

	public OPType getOpType() {
		return opType;
	}

	public void setOpType(OPType opType) {
		this.opType = opType;
	}
	
	public void addMembership(String mem) {
		dataSet.add(mem);
	}

	public double getDataValue() {
		return dataValue;
	}

	public void setDataValue(double dataValue) {
		this.dataValue = dataValue;
	}

	public Bundle getData() {
		return data;
	}

	public void setData(Bundle data) {
		this.data = data;
	}
	
	public double evaluate(double parameter) {
		// compute the current value using parameter
		return 0.0;
	}
}
