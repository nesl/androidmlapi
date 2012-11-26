//package edu.ucla.nesl.mca.pmml;
//
//import java.io.*;
//import java.util.*;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//
//import edu.ucla.nesl.mca.classifier.Classifier;
//import edu.ucla.nesl.mca.feature.Feature;
//import edu.ucla.nesl.mca.feature.FeaturePool;
//import edu.ucla.nesl.mca.feature.Feature.OPType;
//import edu.ucla.nesl.mca.xdr.XDRDataInput;
//import edu.ucla.nesl.mca.xdr.XDRDataOutput;
//import edu.ucla.nesl.mca.xdr.XDRInputStream;
//import edu.ucla.nesl.mca.xdr.XDRSerializable;
//
//public class DecisionTreePMML extends Classifier implements XDRSerializable {
//
//    protected class Spliter implements XDRSerializable {
//        public int featureID;
//        public double threshold = Double.NaN;
//        public int[] indexSet = null;
//
//        public Spliter() {
//
//        }
//
//        public Spliter(int feature) {
//            this.featureID = feature;
//        }
//
//        @Override
//        public void writeXDR(XDRDataOutput output) throws IOException {
//            // Write feature id
//            output.writeInt(featureID);
//
//            // Write threshold value (for numerical)
//            output.writeDouble(threshold);
//
//            // Write set memberships (for nominal)
//            // first write the size
//            if (indexSet != null) {
//                output.writeInt(indexSet.length);
//                for (int index : indexSet) {
//                    output.writeInt(index);
//                }
//            } else {
//                output.writeInt(0);
//            }
//        }
//
//        @Override
//        public void readXDR(XDRDataInput input) throws IOException {
//            this.featureID = input.readInt();
//            this.threshold = input.readDouble();
//            int n = input.readInt();
//            if (n > 0) {
//                indexSet = new int[n];
//                for (int i = 0; i < n; i++) {
//                    indexSet[i] = input.readInt();
//                }
//            }
//        }
//    }
//    
//    // helper class to build split
//    protected class Predicate {
//        public String type;
//        public long featureGUID;
//        public String operator;
//        public double value;
//        public Array valueSet;
//        
//        public Predicate(String typename) {
//            type = typename;
//        }
//    }
//
//    class TreeNode implements XDRSerializable{
//        /** ID for this node */
//        private int m_ID = -1;
// //       private String m_IDString = null;   // No Export
//
//        /** Type of this node: NOMINAL or REAL */
//        //private int type;
//
//        /** The index of this predicted value (if class is nominal) */
//        private int m_scoreIndex = -1;
//
//        /** The score as a number (if target is numeric) */
//        private double m_scoreNumeric = Double.NaN;
//
//        private Spliter m_split = null;
//        
//        private Predicate m_predicate = null;   // No Export
//
//        private ArrayList<TreeNode> m_childNodes = new ArrayList<TreeNode>();
//        
//        /** Temp array to store children IDs, not exported to XDR */
//        private int[] childList;
//        
//        public TreeNode() {
//            
//        }
//
//        public TreeNode(int id) {
//            this.m_ID = id;
//            this.m_childNodes = new ArrayList<TreeNode>();
//        }
//
//        protected TreeNode(Element nodeE, DecisionTreePMML parent) throws Exception {
//            // get the ID
//            String id = nodeE.getAttribute("id");
//            if (id != null && id.length() > 0) {
//                m_ID = Integer.parseInt(id);    //!!!!
//            }
//
//            // get the score for this node
//            String scoreS = nodeE.getAttribute("score");
//            if (scoreS != null && scoreS.length() > 0) {
//                if (parent.m_output.opType == OPType.NOMINAL) {
//                    m_scoreIndex = parent.m_output.dataSet.indexOf(scoreS);
//                    if (m_scoreIndex < 0) {
//                        throw new Exception(
//                                "Can't find match for predicted value "
//                                        + scoreS + " in output feature!");
//                    }
//                } else {
//                    try {
//                        m_scoreNumeric = Double.parseDouble(scoreS);
//                    } catch (NumberFormatException e) {
//                        throw new Exception(
//                                "Class is real but unable to parse score "
//                                        + scoreS + " as a number!");
//                    }
//                }
//            }
//            
//            // Get predicate
//            NodeList children = nodeE.getChildNodes();
//            for (int i = 0; i < children.getLength(); i++) {
//                Node child = children.item(i);
//                if (child.getNodeType() == Node.ELEMENT_NODE) {
//                    String tagName = ((Element)child).getTagName();
//                    if (tagName.equals("True")) {
//                        m_predicate = new Predicate("True");
//                        break;
//                    } else if (tagName.equals("False")) {
//                        m_predicate = new Predicate("False");
//                        break;
//                    } else if (tagName.equals("SimplePredicate")) {
//                        m_predicate = getSimplePredicate((Element)child, parent.m_inputs);
//                        break;
//                    } else if (tagName.equals("SimpleSetPredicate")) {
//                        m_predicate = getSimpleSetPredicate((Element)child, parent.m_inputs);
//                        break;
//                    } else if (tagName.equals("CompoundPredicate")) {
//                        throw new Exception("CompoundPredicate is not supported!");
//                    } 
//                }
//            }
//
//            if (m_predicate == null) {
//              throw new Exception("unknown or missing predicate type in node");
//            }
//
//            // Now get the child Node(s)
//            getChildNodes(nodeE, parent);
//
//            // Get the default child (if applicable)
//            String defaultC = nodeE.getAttribute("defaultChild");
//            if (defaultC != null && defaultC.length() > 0) {
//                for (TreeNode t : m_childNodes) {
//                    if (t.getID() == Integer.parseInt(defaultC)) {
//                        TreeNode m_defaultChild = t;
//                        m_defaultChild.getID();
//                        break;
//                    }
//                }
//            }
//
//            // Get the Predicates of childs, aggregate
//            // m_predicate = Predicate.getPredicate(nodeE, miningSchema);
//        }
//        
//        public Predicate getSimplePredicate(Element simpleP, ArrayList<Feature> featureList) 
//                throws Exception {
//            Predicate predicate = new Predicate("SimplePredicate");
//            
//            // get the field name and set up the index
//            String fieldS = simpleP.getAttribute("field");
//            if (!featureList.contains(fieldS)) {
//              throw new Exception("[SimplePredicate] unable to find field " + fieldS
//                  + " in the incoming instance structure!");
//            }
//            
//            for (Feature f : featureList) {
//                if (f.name.equals(fieldS)) {
//                    predicate.featureGUID = f.GUID;
//                    break;
//                  }
//            }
//            
//            // get the operator
//            String oppS = simpleP.getAttribute("operator");
//            predicate.operator = oppS;
//
//            // get value: assume SimplePredicate = numberic value!!!!
//            String valueS = simpleP.getAttribute("value");
//            predicate.value = Double.parseDouble(valueS);
//            
//            return predicate;
//        }
//        
//        public Predicate getSimpleSetPredicate(Element setP, ArrayList<Feature> featureList) 
//                throws Exception {
//            Predicate predicate = new Predicate("SimplePredicate");
//            
//            // get the field name and set up the index
//            String fieldS = setP.getAttribute("field");
//            if (!featureList.contains(fieldS)) {
//              throw new Exception("[SimplePredicate] unable to find field " + fieldS
//                  + " in the incoming instance structure!");
//            }
//            
//            for (Feature f : featureList) {
//                if (f.name.equals(fieldS)) {
//                    predicate.featureGUID = f.GUID;
//                    break;
//                  }
//            }
//            
//            // get the operator
//            String oppS = setP.getAttribute("operator");
//            predicate.operator = oppS;
//
//            // get value: assume SimpleSetPredicate = nominal value!!!!
//            // need to scan the children looking for an array type
//            NodeList children = setP.getChildNodes();
//            for (int i = 0; i < children.getLength(); i++) {
//                Node child = children.item(i);
//                if (child.getNodeType() == Node.ELEMENT_NODE) {
//                    if (Array.isArray((Element)child)) {
//                        // found the array
//                        predicate.valueSet = Array.create((Element)child);
//                        break;
//                      }
//                }
//            }
//
//            if (predicate.valueSet == null) {
//              throw new Exception("[SimpleSetPredictate] couldn't find an " +
//              "array containing the set values!");
//            }
//
//            return predicate;
//        }
//
//        private void getChildNodes(Element nodeE, DecisionTreePMML parent)
//                throws Exception {
//            NodeList children = nodeE.getChildNodes();
//
//            for (int i = 0; i < children.getLength(); i++) {
//                Node child = children.item(i);
//                if (child.getNodeType() == Node.ELEMENT_NODE) {
//                    String tagName = ((Element) child).getTagName();
//                    if (tagName.equals("Node")) {
//                        TreeNode tempN = new TreeNode((Element) child, parent);
//                        m_childNodes.add(tempN);
//                    }
//                }
//            }
//        }
//
//        public int getID() {
//            return m_ID;
//        }
//
//        @Override
//        public void writeXDR(XDRDataOutput output) throws IOException {
//         // Write the id of the node
//            output.writeInt(this.getID());
//
//            // Write the type of the node
//            //output.writeShort(this.type);
//
//            // Write the score of the node
//            //output.writeInt(this.type);
//            output.writeDouble(this.m_scoreNumeric);
//
//            // Write the id of all children
//            // first write number of children
//            output.writeInt(this.m_childNodes.size());
//            for (TreeNode child : this.m_childNodes) {
//                output.writeInt(child.m_ID);
//            }
//
//            // Write the split information of this node to XDR
//            this.m_split.writeXDR(output);
//            
//        }
//
//        @Override
//        public void readXDR(XDRDataInput input) throws IOException {
//            // Read the node
//            int id = input.readInt();
//            this.m_ID = id;
//
//            //int type = input.readShort();
//            //this.type = type;
//
//            int score1 = input.readInt();
//            this.m_scoreIndex = score1;
//
//            double score2 = input.readDouble();
//            this.m_scoreNumeric = score2;
//
//            int nChild = input.readInt();
//            this.childList = new int[nChild];
//            for (int i = 0; i < nChild; i++) {
//                childList[i] = input.readInt();
//            }
//
//            Spliter spliter = new Spliter();
//            spliter.readXDR(input);
//            this.m_split = spliter;
//        }
//    }
//
//    // Basic assumptions
//    // This is always a Classification
//    // NO MissingValueStrategy implemented
//    // NO MissingValuePenalty implemented
//    // NoTrueChild NOT allowed
//    // SplitCharacteristic treat binarySplit as MultiSplit
//
//    /** The root of the tree */
//    protected TreeNode m_root = null;
//
//    public DecisionTreePMML() {
//
//    }
//
//    public DecisionTreePMML(boolean example) {
//        // Build an example tree
//        m_root = new TreeNode(0);
//        m_root.m_split = new Spliter(1);
//        m_root.m_split.indexSet = new int[3];
//        m_root.m_split.indexSet[0] = 0;
//        m_root.m_split.indexSet[0] = 1;
//        m_root.m_split.indexSet[0] = 2;
//        //m_root.type = 0;
//
//        TreeNode n1 = new TreeNode(1);
//        n1.m_scoreIndex = 0;
//        n1.m_split = new Spliter(0);
//        TreeNode n2 = new TreeNode(2);
//        n2.m_scoreIndex = 1;
//        n2.m_split = new Spliter(0);
//        TreeNode n3 = new TreeNode(3);
//        n3.m_split = new Spliter(0);
//        n2.m_scoreIndex = 2;
//
//        // TreeNode n21 = new TreeNode(4);
//        // TreeNode n31 = new TreeNode(5);
//
//        m_root.m_childNodes.add(n1);
//        m_root.m_childNodes.add(n2);
//        m_root.m_childNodes.add(n3);
//
//        // n2.m_childNodes.add(n21);
//        // n3.m_childNodes.add(n31);
//    }
//
//    @Override
//    public void getModel(Element modelEl, FeaturePool m_featurePool)
//            throws Exception {
//        // find the root node of the tree
//        NodeList children = modelEl.getChildNodes();
//        for (int i = 0; i < children.getLength(); i++) {
//            Node child = children.item(i);
//            if (child.getNodeType() == Node.ELEMENT_NODE) {
//                String tagName = ((Element) child).getTagName();
//                if (tagName.equals("Node")) {
//                    m_root = new TreeNode((Element) child, this);
//                    break;
//                }
//            }
//        }
//    }
//
//    public ArrayList<TreeNode> preOrderTraversal(TreeNode node) {
//        // Perform a pre-order traversal of the tree
//        // return list of visited nodes
//        ArrayList<TreeNode> r = new ArrayList<TreeNode>();
//        r.add(node);
//        for (TreeNode child : node.m_childNodes) {
//            r.addAll(preOrderTraversal(child));
//        }
//        return r;
//    }
//
//    public ArrayList<TreeNode> traversal() {
//        return preOrderTraversal(m_root);
//    }
//
//    @Override
//    public void writeXDR(XDRDataOutput output) throws IOException {
//        // Write the name of the classifier
//        // Note: the length of the classifier name must be unified
//        output.writeString("TREE");
//
//        // Perform an pre-order traversal
//        ArrayList<TreeNode> nodeList = traversal();
//        // Write the number of node to XDR
//        output.writeInt(nodeList.size());
//
//        for (TreeNode node : nodeList) {
//            node.writeXDR(output);
//        }
//        output.close();
//    }
//
//    @Override
//    public void readXDR(XDRDataInput input) throws IOException {
//        // Check classifier type
//        String classifier = input.readString();
//        if (classifier.equals("TREE")) {
//            // read number of nodes
//            int n = input.readInt();
//            // System.out.println(n);
//            // map the IDs to each node
//            ArrayList<TreeNode> list = new ArrayList<TreeNode>();
//            HashMap<Integer, TreeNode> map = new HashMap<Integer, TreeNode>();
//            // read all nodes
//            for (int i = 0; i < n; i++) {
//                TreeNode node = new TreeNode();
//                node.readXDR(input);
//                map.put(node.m_ID, node);
//                list.add(node);
//            }
//
//            // build the hierarchy of the tree
//            m_root = list.get(0);
//            for (int i = 0; i < n; i++) {
//                TreeNode node = list.get(i);
//                int[] children = node.childList;
//                for (int j = 0; j < children.length; j++) {
//                    //System.out.print(children[j] + " ");
//                    node.m_childNodes.add(map.get(children[j]));
//
//                }
//                //System.out.println();
//            }
//        }
//    }
//
//    /**
//     * Main routine to test XDR write
//     * 
//     * @param args
//     */
//    public static void main(String[] args) {
//        DecisionTreePMML tree = new DecisionTreePMML();
//        // ArrayList<TreeNode> result = tree.traversal();
//        // for(TreeNode node:result) {
//        // System.out.println(node.m_ID);
//        // }
//
//        try {
//            // XDROutputStream output = new XDROutputStream(new
//            // FileOutputStream(new File("tree.xdr")));
//            // tree.writeXDR(output);
//            XDRInputStream input = new XDRInputStream(new FileInputStream(
//                    new File("tree.xdr")));
//            tree.readXDR(input);
//            ArrayList<TreeNode> result = tree.traversal();
//            for (TreeNode node : result) {
//                System.out.println(node.m_ID);
//            }
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//	@Override
//	protected void getModel(JSONObject modelObj) throws JSONException {
//		// TODO Auto-generated method stub
//		
//	}
//}
