package mobileClassifier;

import java.util.ArrayList;

import mobileClassifier.Feature.OPType;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DecisionTree extends Classifier {
    
    protected class Spliter {
        public double threshold = Double.NaN;
        public int[] indexSet = null;
    }
    
    protected class TreeNode {
        /** ID for this node */
        private String m_ID;
        
        /** The index of this predicted value (if class is nominal) */
        private int m_scoreIndex = -1;
        
        /** The score as a number (if target is numeric) */
        private double m_scoreNumeric = Double.NaN;
        
        private Spliter m_split;
        
        private ArrayList<TreeNode> m_childNodes = new ArrayList<TreeNode>();

        protected TreeNode(Element nodeE, DecisionTree parent) throws Exception {
            // get the ID
            String id = nodeE.getAttribute("id");
            if (id != null && id.length() > 0) {
                m_ID = id;
            }

            // get the score for this node
            String scoreS = nodeE.getAttribute("score");
            if (scoreS != null && scoreS.length() > 0) {
                if (parent.m_output.opType == OPType.NOMINAL) {
                    m_scoreIndex = parent.m_output.dataSet.indexOf(scoreS);
                    if (m_scoreIndex < 0) {
                        throw new Exception("Can't find match for predicted value " 
                            + scoreS + " in output feature!");
                    }
                }
                else {
                    try {
                        m_scoreNumeric = Double.parseDouble(scoreS);
                    } catch (NumberFormatException e) {
                        throw new Exception("Class is real but unable to parse score " 
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
                    if (t.getID().equals(defaultC)) {
                        TreeNode m_defaultChild = t;
                        break;
                    }
                }
            }
            
            // Get the Predicates of childs, aggregate
            m_predicate = Predicate.getPredicate(nodeE, miningSchema);
        }

        private void getChildNodes(Element nodeE, DecisionTree parent) 
                throws Exception {
            NodeList children = nodeE.getChildNodes();
            
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    String tagName = ((Element)child).getTagName();
                    if (tagName.equals("Node")) {
                        TreeNode tempN = new TreeNode((Element)child, parent);
                        m_childNodes.add(tempN);
                    }
                }
            }
        }
        
        private String getID() {
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
    }


    @Override
    public void getModel(Element modelEl, FeaturePool m_featurePool) 
            throws Exception {
        // find the root node of the tree
        NodeList children = modelEl.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String tagName = ((Element)child).getTagName();
                if (tagName.equals("Node")) {
                  m_root = new TreeNode((Element)child, this);          
                  break;
                }
            }
        }    
        
    }
      

}
