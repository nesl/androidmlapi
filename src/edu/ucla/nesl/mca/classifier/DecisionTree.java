package edu.ucla.nesl.mca.classifier;

import java.io.*;
import java.util.*;
import org.json.*;

import edu.ucla.nesl.mca.feature.Feature;
import edu.ucla.nesl.mca.feature.Feature.OPType;
import edu.ucla.nesl.mca.xdr.XDRDataInput;
import edu.ucla.nesl.mca.xdr.XDRDataOutput;
import edu.ucla.nesl.mca.xdr.XDRSerializable;

public class DecisionTree extends Classifier implements XDRSerializable {

    protected enum RealOperator {
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

    protected class TreeNode implements XDRSerializable {
        
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
            
            if (nodeObj.has("FeatureID")) {
                int featureID = nodeObj.getInt("FeatureID");
                m_feature = parent.getInputs().getFeature(featureID);
                
                m_type = m_feature.opType;
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
                m_resultType = parent.getOutput().opType;
                if (m_resultType == OPType.REAL) {
                    m_realResult = nodeObj.getDouble("Result");
                }
                else if (m_resultType == OPType.NOMINAL) {
                	m_nominalResult = nodeObj.getString("Result");
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

    @Override
    protected void getModel(JSONObject modelObj) throws JSONException {       
        defaultResult = modelObj.getString("defaultResult");
        
        JSONArray nodeList = modelObj.getJSONArray("Nodes");
        TreeNode[] nodeArray = new TreeNode[nodeList.length()];
        HashMap<Integer, TreeNode> nodeDict = new HashMap<Integer, TreeNode>();
        
        // Read and build all the tree nodes
        for (int i = 0; i < nodeList.length(); i++) {
            nodeArray[i] = new TreeNode(nodeList.getJSONObject(i), this);
            nodeDict.put(nodeArray[i].getID(), nodeArray[i]);
        }
        
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

    /**
     * Main routine to parse a JSON file and generate a decision tree
     * 
     * @param args
     */
    public static void main(String[] args) {
        DecisionTree tree = new DecisionTree();
        try {
        	tree.parseJSON("JSON-Test.txt");
        	
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
}
