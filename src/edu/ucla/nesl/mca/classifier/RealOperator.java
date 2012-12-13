package edu.ucla.nesl.mca.classifier;

public enum RealOperator {
    LESSTHAN("<")  {
        public boolean evaluate(double featureValue, double threshold) {
            return featureValue < threshold;
        }
    },
    LESSOREQUAL("<=") {
        public boolean evaluate(double featureValue, double threshold) {
            return featureValue <= threshold;
        }
    },
    GREATERTHAN(">") {
        public boolean evaluate(double featureValue, double threshold) {
            return featureValue > threshold;
        }
    },
    GREATEROREQUAL(">=") {
        public boolean evaluate(double featureValue, double threshold) {
            return featureValue >= threshold;
        }
    },
    EQUAL("=") {
            public boolean evaluate(double featureValue, double threshold) {
                return featureValue == threshold;
        }
    };
    
    public abstract boolean evaluate(double featureValue, double threshold);
    
    private final String m_stringVal;
    
    RealOperator(String name) {
        m_stringVal = name;
    }
          
    public String toString() {
        return m_stringVal;
    }
}
