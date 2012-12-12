package edu.ucla.nesl.mca.classifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import edu.ucla.nesl.mca.feature.Feature;
import edu.ucla.nesl.mca.feature.Feature.OPType;
import edu.ucla.nesl.mca.xdr.XDRDataInput;
import edu.ucla.nesl.mca.xdr.XDRDataOutput;
import edu.ucla.nesl.mca.xdr.XDRSerializable;

public class DecisionTree extends Classifier implements XDRSerializable {

//    protected enum RealOperator {
//          LESSTHAN("<")  {
//              boolean evaluate(double featureValue, double threshold) {
//                  return featureValue < threshold;
//              }
//          },
//          LESSOREQUAL("<=") {
//              boolean evaluate(double featureValue, double threshold) {
//                  return featureValue <= threshold;
//              }
//          },
//          GREATERTHAN(">") {
//              boolean evaluate(double featureValue, double threshold) {
//                  return featureValue > threshold;
//              }
//          },
//          GREATEROREQUAL(">=") {
//              boolean evaluate(double featureValue, double threshold) {
//                  return featureValue >= threshold;
//              }
//          };
//          
//          abstract boolean evaluate(double featureValue, double threshold);
//          
//          private final String m_stringVal;
//          
//          RealOperator(String name) {
//              m_stringVal = name;
//          }
//                
//          public String toString() {
//              return m_stringVal;
//          }
//    }

    class TreeNode implements XDRSerializable {
        
        /** ID for this node */
        private int m_id = -1;

        /** Type of this node: NOMINAL or REAL */
        private Feature m_feature = null;
        
        /** Type of this node's feature: NOMINAL or REAL */
        private OPType m_type = null;
        
        /** Operator if type is REAL */
        private RealOperator m_realOp = null;
        
        /** Threshold if type is REAL */
        private double m_realThes = Double.NaN;

        /** Type of result: NOMINAL or REAL */
        private OPType m_resultType = null;

        /** result if resultType is REAL */
        private double m_realResult = Double.NaN;
        
        private String m_nominalResult = null;
        
        private int m_parameter = -1;

        public int getM_id() {
			return m_id;
		}

		public void setM_id(int m_id) {
			this.m_id = m_id;
		}

		public Feature getM_feature() {
			return m_feature;
		}

		public void setM_feature(Feature m_feature) {
			this.m_feature = m_feature;
		}

		public OPType getM_type() {
			return m_type;
		}

		public void setM_type(OPType m_type) {
			this.m_type = m_type;
		}

		public RealOperator getM_realOp() {
			return m_realOp;
		}

		public void setM_realOp(RealOperator m_realOp) {
			this.m_realOp = m_realOp;
		}

		public double getM_realThes() {
			return m_realThes;
		}

		public void setM_realThes(double m_realThes) {
			this.m_realThes = m_realThes;
		}

		public OPType getM_resultType() {
			return m_resultType;
		}

		public void setM_resultType(OPType m_resultType) {
			this.m_resultType = m_resultType;
		}

		public double getM_realResult() {
			return m_realResult;
		}

		public void setM_realResult(double m_realResult) {
			this.m_realResult = m_realResult;
		}

		public String getM_nominalResult() {
			return m_nominalResult;
		}

		public void setM_nominalResult(String m_nominalResult) {
			this.m_nominalResult = m_nominalResult;
		}

		public int getM_parameter() {
			return m_parameter;
		}

		public void setM_parameter(int m_parameter) {
			this.m_parameter = m_parameter;
		}

		public ArrayList<TreeNode> getM_childNodes() {
			return m_childNodes;
		}

		public void setM_childNodes(ArrayList<TreeNode> m_childNodes) {
			this.m_childNodes = m_childNodes;
		}

		/** Child nodes of this node */
        private ArrayList<TreeNode> m_childNodes = new ArrayList<TreeNode>();
        
        /** Temp array to store children IDs, not exported to XDR */
        private int[] childList;
        private int childCount;
        
        public TreeNode() {
            // Used for readXDR
        }

        public TreeNode(JSONObject nodeObj, DecisionTree parent) throws JSONException {
            m_id = nodeObj.getInt("ID");
            Log.i("DecisionTree", "Node ID=" + m_id);
            if (nodeObj.has("FeatureID")) {
                int featureID = nodeObj.getInt("FeatureID");
                Log.i("DecisionTree", "FeatureID=" + featureID);
                m_feature = parent.getInputs().getFeature(featureID);
                Log.i("DecisionTree", "Feature=" + m_feature.getName());
                if (nodeObj.has("Parameter")) {
                	m_parameter = nodeObj.getInt("Parameter");
                }
                m_type = m_feature.getOpType();
                if (m_type == OPType.REAL) {
                    String op = nodeObj.getString("Operator");
                    for (RealOperator o : RealOperator.values()) {
                        if (o.toString().equals(op)) {
                            m_realOp = o;
                            break;
                          }
                    }
                    m_realThes = nodeObj.getDouble("Value");
                }
                
                JSONArray childNodeList = nodeObj.getJSONArray("ChildNode");
                childCount = childNodeList.length();
                childList = new int[childCount];
                for (int i = 0; i < childCount; i++) {
                    childList[i] = childNodeList.getInt(i);
                }
            } 
            else if (nodeObj.has("Result")) {
                m_resultType = parent.getOutput().getOpType();
                if (m_resultType == OPType.REAL) {
                    m_realResult = nodeObj.getDouble("Result");
                }
                else if (m_resultType == OPType.NOMINAL) {
                	m_nominalResult = nodeObj.getString("Result");
                	Log.i("DecisionTree", "Result=" + m_nominalResult);
                }
            } 
            else {
                throw new JSONException("Cannot have a node with no Feature nor Result defined.");
            }
        }

        public int getID() {
            return m_id;
        }
        
        public void updateChild(HashMap<Integer, TreeNode> nodeDict) {
            for (int i = 0; i < childCount; i++) {
                m_childNodes.add(nodeDict.get(childList[i]));
            }
        }
        
        public void evaluate() {
        	
        }

        @Override
        public void writeXDR(XDRDataOutput output) throws IOException {

        }

        @Override
        public void readXDR(XDRDataInput input) throws IOException {
            
        }
    }

    // Basic assumptions
    // This is always a Classification
    // NO MissingValueStrategy implemented
    // NO MissingValuePenalty implemented
    // NoTrueChild NOT allowed
    // SplitCharacteristic treat binarySplit as MultiSplit

    /** The root of the tree */
    protected TreeNode m_root = null;
    
    /** Default evaluation result */
    protected String defaultResult;
    
    public DecisionTree() {
    }

    public TreeNode getM_root() {
		return m_root;
	}

	public void setM_root(TreeNode m_root) {
		this.m_root = m_root;
	}
	
	@Override
    protected void getModel(JSONObject modelObj) throws JSONException {       
        defaultResult = modelObj.getString("Default Result");
        
        JSONArray nodeList = modelObj.getJSONArray("Nodes");
        TreeNode[] nodeArray = new TreeNode[nodeList.length()];
        HashMap<Integer, TreeNode> nodeDict = new HashMap<Integer, TreeNode>();
        Log.i("DecisionTree", "node length=" + nodeList.length());
        // Read and build all the tree nodes
        for (int i = 0; i < nodeList.length(); i++) {
            nodeArray[i] = new TreeNode(nodeList.getJSONObject(i), this);
            nodeDict.put(nodeArray[i].getID(), nodeArray[i]);
        }
        m_root = nodeArray[0];
        Log.i("DecisionTree", "Root Node ID=" + m_root.m_id);
        // Need to loop the node list once more to construct node hierarchy
        for (int i = 0; i < nodeList.length(); i++) {
            nodeArray[i].updateChild(nodeDict);
        }
    }

    public ArrayList<TreeNode> preOrderTraversal(TreeNode node) {
        // Perform a pre-order traversal of the tree
        // return list of visited nodes
        ArrayList<TreeNode> r = new ArrayList<TreeNode>();
        r.add(node);
        for (TreeNode child : node.m_childNodes) {
            r.addAll(preOrderTraversal(child));
        }
        return r;
    }

    public ArrayList<TreeNode> traversal() {
        return preOrderTraversal(m_root);
    }
    
    
    public Feature getRootFeature () {
    	return m_root.m_feature;
    }

    @Override
    public void writeXDR(XDRDataOutput output) throws IOException {
        // Write the name of the classifier
        // Note: the length of the classifier name must be unified
        output.writeString("TREE");

        // Perform an pre-order traversal
        ArrayList<TreeNode> nodeList = traversal();
        // Write the number of node to XDR
        output.writeInt(nodeList.size());

        for (TreeNode node : nodeList) {
            node.writeXDR(output);
        }
        output.close();
    }

    @Override
    public void readXDR(XDRDataInput input) throws IOException {
        // Check classifier type
        String classifier = input.readString();
        if (classifier.equals("TREE")) {
            // read number of nodes
            int n = input.readInt();
            // System.out.println(n);
            // map the IDs to each node
            ArrayList<TreeNode> list = new ArrayList<TreeNode>();
            HashMap<Integer, TreeNode> map = new HashMap<Integer, TreeNode>();
            // read all nodes
            for (int i = 0; i < n; i++) {
                TreeNode node = new TreeNode();
                node.readXDR(input);
                map.put(node.m_id, node);
                list.add(node);
            }

            // build the hierarchy of the tree
            m_root = list.get(0);
            for (int i = 0; i < n; i++) {
                TreeNode node = list.get(i);
                int[] children = node.childList;
                for (int j = 0; j < children.length; j++) {
                    //System.out.print(children[j] + " ");
                    node.m_childNodes.add(map.get(children[j]));
                }
                //System.out.println();
            }
        }
    }

	@Override
	public Object evaluate() {
		// TODO Auto-generated method stub
		TreeNode cur = m_root;
		Log.i("DecisionTree", "root value = " + cur.m_feature.evaluate(cur.m_parameter));
		while (true) {
			// do the evaluation in decision tree
			if (cur.getM_resultType() != null) {
				if (cur.getM_resultType() == OPType.REAL) {
					return Double.valueOf(cur.getM_realResult());
				}
				if (cur.getM_resultType() == OPType.NOMINAL) {
					Log.i("DecisionTreeEvaluate", "Reach leaf node, result=" + cur.getM_nominalResult());
					String res = new String(cur.getM_nominalResult());
					String features = "";
					for (Feature fea:LogUtil.features) {
						features = features + fea.getName() + "(" + fea.getParameter() + ")=" + fea.getDataValue() + "; ";
					}
					Log.i("DecisionTreeEvaluate", "Result done: " + features + res);
					LogUtil.features.clear();
					return res;
				}
			}
			else {
				if (cur.getM_type() == OPType.REAL) {
					double var = (Double)cur.m_feature.evaluate(cur.m_parameter);
					Log.i("DecisionTreeEvaluate", "value=" + var + " threshold=" + cur.getM_realThes());
					if (cur.getM_realOp().evaluate(var, cur.getM_realThes())) {
						Log.i("DecisionTreeEvaluate", "go to left child");
						cur = cur.getM_childNodes().get(0);
					}
					else {
						Log.i("DecisionTreeEvaluate", "go to right child");
						cur = cur.getM_childNodes().get(1);
					}
				}
				else if (cur.getM_type() == OPType.NOMINAL) {
					int sel = (int) Math.round((Double)cur.m_feature.evaluate(cur.m_parameter));
					cur = cur.getM_childNodes().get(sel);
				}
			}
		}
	}
}
