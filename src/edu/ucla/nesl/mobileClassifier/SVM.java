package edu.ucla.nesl.mobileClassifier;

import java.io.IOException;

import org.w3c.dom.Element;

import edu.ucla.nesl.xdr.XDRDataInput;
import edu.ucla.nesl.xdr.XDRDataOutput;
import edu.ucla.nesl.xdr.XDRSerializable;

public class SVM extends Classifier implements XDRSerializable {
    protected int[][] w;
    protected double b;

    @Override
    public void writeXDR(XDRDataOutput out) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void readXDR(XDRDataInput in) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void getModel(Element modelEl, FeaturePool m_featurePool)
            throws Exception {
        // TODO Auto-generated method stub

    }

}
