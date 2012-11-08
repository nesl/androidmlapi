package edu.ucla.nesl.mobileClassifier;

import java.io.*;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.ucla.nesl.mobileClassifier.Feature.OPType;
import edu.ucla.nesl.xdr.XDRDataInput;
import edu.ucla.nesl.xdr.XDRDataOutput;
import edu.ucla.nesl.xdr.XDROutputStream;
import edu.ucla.nesl.xdr.XDRSerializable;

public class DecisionTree extends Classifier implements XDRSerializable {

    protected class Spliter implements XDRSerializable {
        public int featureID;
        public double threshold = Double.NaN;
        public int[] indexSet = null;
        
        public Spliter (int feature) {
            this.featureID = feature;
        }
        
        @Override
        public void writeXDR(XDRDataOutput output) throws IOException {
            // TODO Auto-generated method stub
            // Write feature id
            output.writeInt(featureID);
            
            // Write threshold value (for numerical)
            output.writeDouble(threshold);
            
            // Write set memberships (for nominal)
            // first write the size
            output.writeInt(indexSet.length);
            for (int index:indexSet) {
                output.writeInt(index);
            }
        }
        @Override
        public void readXDR(XDRDataInput in) throws IOException {
            // TODO Auto-generated method stub
            
        }
    }

    class TreeNode {
        /** ID for this node */
        private int m_ID;
        
        /** Type of this node: NOMINAL or REAL */
        private int type;

        /** The index of this predicted value (if class is nominal) */
        private int m_scoreIndex = -1;

        /** The score as a number (if target is numeric) */
        private double m_scoreNumeric = Double.NaN;

        private Spliter m_split;

        private ArrayList<TreeNode> m_childNodes = new ArrayList<TreeNode>();
        
        public TreeNode(int id) {
            this.m_ID = id;
            this.m_childNodes = new ArrayList<TreeNode>();
        }

        protected TreeNode(Element nodeE, DecisionTree parent) throws Exception {
            // get the ID
            String id = nodeE.getAttribute("id");
            if (id != null && id.length() > 0) {
                m_ID = Integer.parseInt(id);
            }

            // get the score for this node
            String scoreS = nodeE.getAttribute("score");
            if (scoreS != null && scoreS.length() > 0) {
                if (parent.m_output.opType == OPType.NOMINAL) {
                    m_scoreIndex = parent.m_output.dataSet.indexOf(scoreS);
                    if (m_scoreIndex < 0) {
                        throw new Exception(
                                "Can't find match for predicted value "
                                        + scoreS + " in output feature!");
                    }
                } else {
                    try {
                        m_scoreNumeric = Double.parseDouble(scoreS);
                    } catch (NumberFormatException e) {
                        throw new Exception(
                                "Class is real but unable to parse score "
                                        + scoreS + " as a number!");
                    }
                }
            }

            // Now get the child Node(s)
            getChildNodes(nodeE, parent);

            // Get the default child (if applicable)
            String defaultC = nodeE.getAttribute("defaultChild");
            if (defaultC != null && defaultC.length() > 0) {
                for (TreeNode t : m_childNodes) {
                    if (t.getID() == Integer.parseInt(defaultC)) {
                        TreeNode m_defaultChild = t;
                        break;
                    }
                }
            }

            // Get the Predicates of childs, aggregate
            // m_predicate = Predicate.getPredicate(nodeE, miningSchema);
        }

        private void getChildNodes(Element nodeE, DecisionTree parent)
                throws Exception {
            NodeList children = nodeE.getChildNodes();

            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    String tagName = ((Element) child).getTagName();
                    if (tagName.equals("Node")) {
                        TreeNode tempN = new TreeNode((Element) child, parent);
                        m_childNodes.add(tempN);
                    }
                }
            }
        }

        public int getID() {
            return m_ID;
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

    public DecisionTree() {
        // Build an example tree
        m_root = new TreeNode(0);
        TreeNode n1 = new TreeNode(1);
        TreeNode n2 = new TreeNode(2);
        TreeNode n3 = new TreeNode(3);
        
        TreeNode n21 = new TreeNode(4);
        TreeNode n31 = new TreeNode(5);
        
        m_root.m_childNodes.add(n1);
        m_root.m_childNodes.add(n2);
        m_root.m_childNodes.add(n3);
        
        n2.m_childNodes.add(n21);
        n3.m_childNodes.add(n31);
    }


    @Override
    public void getModel(Element modelEl, FeaturePool m_featurePool)
            throws Exception {
        // find the root node of the tree
        NodeList children = modelEl.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String tagName = ((Element) child).getTagName();
                if (tagName.equals("Node")) {
                    m_root = new TreeNode((Element) child, this);
                    break;
                }
            }
        }
    }
 
    public ArrayList<TreeNode> preOrderTraversal(TreeNode node) {
        // Perform a pre-order traversal of the tree
        // return list of visited nodes
        ArrayList<TreeNode> r = new ArrayList<TreeNode>();
        r.add(node);
        for (TreeNode child:node.m_childNodes) {
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
        
        for (TreeNode node:nodeList) {
            // Write the id of the node
            output.writeInt(node.getID());
            
            // Write the type of the node
            output.writeShort(node.type);
            
            // Write the score of the node
            output.writeInt(node.type);
            output.writeDouble(node.m_scoreNumeric);
            
            // Write the id of all children
            // first write number of children
            output.writeInt(node.m_childNodes.size());
            for (TreeNode child:node.m_childNodes) {
                output.writeInt(child.m_ID);
            }
            
            // Write the split information of this node to XDR
            node.m_split.writeXDR(output);
        }
        output.close();
    }

    @Override
    public void readXDR(XDRDataInput in) throws IOException {
        // TODO Auto-generated method stub

    }
    
    /**
     * Main routine to test XDR write
     * @param args
     */
    public static void main(String[] args) {
        DecisionTree tree = new DecisionTree();
        ArrayList<TreeNode> result = tree.traversal();
        for(TreeNode node:result) {
            System.out.println(node.m_ID);
        }
        
        try {
            XDROutputStream output = new XDROutputStream(new FileOutputStream(new File("tree.xdr")));
            tree.writeXDR(output); 
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
