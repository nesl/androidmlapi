package edu.ucla.nesl.mca.feature;

import java.util.ArrayList;


public class Feature {
    public enum OPType {
        NOMINAL(1),
        REAL(2);
        
        /*
        CATEGORICAL(1),
        ORDINAL(2),
        CONTINUOUS(3);
        */
        
        private final int num;
        OPType(int value) {
            this.num = value;
        }
        public int num() {
            return this.num;
        }
    }
    
    public int id;
    public String name;
    public int sensor;
    public OPType opType;
    public double dataValue;
    public boolean isResult;
    public ArrayList<String> dataSet;   // only used if NOMINAL
    
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
	
	public double evaluate(String type, double value) {
		
		return 0.0;
	}
}
