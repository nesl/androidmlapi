package edu.ucla.nesl.mca.classifier;

import java.io.IOException;

import org.json.*;

import edu.ucla.nesl.mca.xdr.XDRDataInput;
import edu.ucla.nesl.mca.xdr.XDRDataOutput;
import edu.ucla.nesl.mca.xdr.XDRSerializable;

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
    protected void getModel(JSONObject modelObj) throws JSONException {
        // TODO Auto-generated method stub
        
    }

	@Override
	public Object evaluate() {
		// TODO Auto-generated method stub
		return null;
	}

}
