package edu.ucla.nesl.mca.classifier;

public enum RealOperator {
    LESSTHAN("<")  {
        boolean evaluate(double featureValue, double threshold) {
            return featureValue < threshold;
        }
    },
    LESSOREQUAL("<=") {
        boolean evaluate(double featureValue, double threshold) {
            return featureValue <= threshold;
        }
    },
    GREATERTHAN(">") {
        boolean evaluate(double featureValue, double threshold) {
            return featureValue > threshold;
        }
    },
    GREATEROREQUAL(">=") {
        boolean evaluate(double featureValue, double threshold) {
            return featureValue >= threshold;
        }
    },
    EQUAL("=") {
            boolean evaluate(double featureValue, double threshold) {
                return featureValue == threshold;
        }
    };
    
    abstract boolean evaluate(double featureValue, double threshold);
    
    private final String m_stringVal;
    
    RealOperator(String name) {
        m_stringVal = name;
    }
          
    public String toString() {
        return m_stringVal;
    }
}
