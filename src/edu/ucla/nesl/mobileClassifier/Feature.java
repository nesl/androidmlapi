package edu.ucla.nesl.mobileClassifier;

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
    
    public enum DataType {
        /*
    <xs:enumeration value="string"/>
    <xs:enumeration value="integer"/>
    <xs:enumeration value="float"/>
    <xs:enumeration value="double"/>
    <xs:enumeration value="boolean"/>
    <xs:enumeration value="date"/>
    <xs:enumeration value="time"/>
    <xs:enumeration value="dateTime"/>
    <xs:enumeration value="dateDaysSince[0]"/>
    <xs:enumeration value="dateDaysSince[1960]"/>
    <xs:enumeration value="dateDaysSince[1970]"/>
    <xs:enumeration value="dateDaysSince[1980]"/>
    <xs:enumeration value="timeSeconds"/>
    <xs:enumeration value="dateTimeSecondsSince[0]"/>
    <xs:enumeration value="dateTimeSecondsSince[1960]"/>
    <xs:enumeration value="dateTimeSecondsSince[1970]"/>
    <xs:enumeration value="dateTimeSecondsSince[1980]"/>
        */
    }
    
    public long GUID;
    public String name;
    public OPType opType;
    public DataType dataType;
    public ArrayList<String> dataSet;   // only used if NOMINAL
}
