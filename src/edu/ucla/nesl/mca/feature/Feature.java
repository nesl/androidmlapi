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
    public int[] sensors;
    public OPType opType;
    public double dataValue;
    public ArrayList<String> dataSet;   // only used if NOMINAL
    
    public Feature() {
    	
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

	public int[] getSensors() {
		return sensors;
	}

	public void setSensors(int[] sensors) {
		this.sensors = sensors;
	}

	public OPType getOpType() {
		return opType;
	}

	public void setOpType(OPType opType) {
		this.opType = opType;
	}
	
	public double evaluate(String type, double value) {
		
		return 0.0;
	}
}
