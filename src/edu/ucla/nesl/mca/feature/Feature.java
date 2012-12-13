package edu.ucla.nesl.mca.feature;

import java.util.ArrayList;

import android.os.Bundle;
import edu.ucla.nesl.mca.classifier.LogUtil;

public class Feature {
	public enum OPType {
		NOMINAL(1), REAL(2);

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
	private int windowSize;
	private ArrayList<String> dataSet; // only used if NOMINAL
	private Bundle data;
	private int parameter;
	private Trigger triggerOn = null;
	private Trigger triggerOff = null;
	
//	public Trigger getTrigger() {
//		return trigger;
//	}
//
//	public void setTrigger(int feature, String operator, double threshold) {
//		this.trigger = new Trigger(feature, operator, threshold);
//	}
//	
//	public int getTriggerFeature() {
//		return this.trigger.feature;
//	}
//	
//	public RealOperator getTriggerRealOperator() {
//		return this.trigger.realOp;
//	}
//	
//	public double getTriggerThreshold() {
//		return this.trigger.threshold;
//	}
	
	

	public Trigger getTriggerOn() {
		return triggerOn;
	}

	public void setTriggerOn(Trigger triggerOn) {
		this.triggerOn = triggerOn;
	}

	public Trigger getTriggerOff() {
		return triggerOff;
	}

	public void setTriggerOff(Trigger triggerOff) {
		this.triggerOff = triggerOff;
	}

	public Feature() {
		dataSet = new ArrayList<String>();
		windowSize = 100;
	}
	
	public int getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
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

	public Object evaluate(double parameter) {
		// compute the current value using parameter
		if (this.sensor == SensorProfile.GPS) {

		} 
		else if (this.sensor == SensorProfile.ACCELEROMETER) {
			double[] accData = AlgorithmUtil.magnititute(
					data.getDoubleArray("AccX"), data.getDoubleArray("AccY"),
					data.getDoubleArray("AccZ"));
			int dataSize = accData.length;

//			double sum = 0.0, avg = 0.0, var = 0.0;
//			for (int i = 0; i < dataSize; i++) {
//				sum += accData[i];
//			}
//
//			avg = sum / dataSize;
//			sum = 0.0;
//			for (int i = 0; i < dataSize; i++) {
//				sum += Math.pow((accData[i] - avg), 2.0);
//			}
//			var = sum / dataSize;
			if (!AlgorithmUtil.getOutdoor()) {
				double accFft[] = new double[5];
				accFft[0] = AlgorithmUtil.goertzel(accData, 1., dataSize);
				accFft[1] = AlgorithmUtil.goertzel(accData, 2., dataSize);
				accFft[2] = AlgorithmUtil.goertzel(accData, 3., dataSize);
				accFft[3] = AlgorithmUtil.goertzel(accData, 4., dataSize);
				accFft[4] = AlgorithmUtil.goertzel(accData, 5., dataSize);
				
				int newVal = 0;
				if (accFft[0] + accFft[1] + accFft[2] + accFft[3] + accFft[4] < AlgorithmUtil.INDOOR_THRESHOLD) {
					newVal = 0;
				}
				else {
					newVal = 1;
				}
				if (AlgorithmUtil.ioq.size() == AlgorithmUtil.QUEUE_SIZE){
					AlgorithmUtil.ioScore -= AlgorithmUtil.ioq.removeLast();
				}
				AlgorithmUtil.ioq.addFirst(newVal);
				AlgorithmUtil.ioScore += newVal;
				if (AlgorithmUtil.ioScore > AlgorithmUtil.QUEUE_SIZE / 2) {
					AlgorithmUtil.setOutdoor();
					AlgorithmUtil.ioq.clear();
					AlgorithmUtil.ioScore = 0;
				}
			}
			
			double s = 0.0, a0 = 0.0, v = 0.0;
			double a[] = new double[10];
			for (int i = 0; i < dataSize; i++) {
				accData[i] = accData[i] / 310.;
				s += accData[i];
			}
			a0 = s / dataSize;
			s = 0.0;

			a[1] = AlgorithmUtil.goertzel(accData, 1., dataSize);
			a[2] = AlgorithmUtil.goertzel(accData, 2., dataSize);
			a[3] = AlgorithmUtil.goertzel(accData, 3., dataSize);
			a[4] = AlgorithmUtil.goertzel(accData, 4., dataSize);
			a[5] = AlgorithmUtil.goertzel(accData, 5., dataSize);
			a[6] = AlgorithmUtil.goertzel(accData, 6., dataSize);
			a[7] = AlgorithmUtil.goertzel(accData, 7., dataSize);
			a[8] = AlgorithmUtil.goertzel(accData, 8., dataSize);
			a[9] = AlgorithmUtil.goertzel(accData, 9., dataSize);
			a[0] = AlgorithmUtil.goertzel(accData, 10., dataSize);

			for (int i = 0; i < dataSize; i++) {
				s += Math.pow((accData[i] - a0), 2.0);
			}
			v = s / dataSize;
			
			if (this.name.equals(SensorProfile.VARIANCE)) {
				this.dataValue = v;
				LogUtil.features.add(this);
				return this.dataValue;
			}
			else if (this.name.equals(SensorProfile.ENERGYCOEFFICIENT)) {
				this.parameter = (int)parameter;
				this.dataValue = a[(int)parameter];
				LogUtil.features.add(this);
				return this.dataValue;
			}
			else if (this.name.equals(SensorProfile.INDOOR)) {
				if (AlgorithmUtil.getOutdoor()) {
					this.dataValue = 1.0;
				}
				else {
					this.dataValue = 0.0;
				}
				return this.dataValue;
			}
		}
		return Double.MIN_VALUE;
	}

	public int getParameter() {
		return parameter;
	}

	public void setParameter(int parameter) {
		this.parameter = parameter;
	}
}
