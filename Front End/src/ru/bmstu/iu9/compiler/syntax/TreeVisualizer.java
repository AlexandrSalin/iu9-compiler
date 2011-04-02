package ru.bmstu.iu9.compiler.syntax;

import com.mxgraph.layout.mxCompactTreeLayout;
import javax.swing.JFrame;


import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author maggot
 */
class TreeVisualizer extends JFrame {
    public TreeVisualizer(TreeNode tree) {
        super("Parse Tree");
        
        graph = new mxGraph();
        
        root = graph.getDefaultParent();

        graph.getModel().beginUpdate();
        try {
            processNode(tree, root);
        } finally {
            graph.getModel().endUpdate();
        }

        mxCompactTreeLayout layout = new mxCompactTreeLayout(graph, false);
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        getContentPane().add(graphComponent);

        layout.execute(root);
        graph.setCellsLocked(true);
/*
        mxStylesheet stylesheet = graph.getStylesheet();
        Map<String, Object> style = new HashMap<String, Object>();
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        style.put(mxConstants.STYLE_OPACITY, 50);
        stylesheet.putCellStyle("ROUNDED", style);
*/
    }
    
    private void processNode(TreeNode node, Object parent) {
        Object nodeObj = null;
        if (node instanceof CompositeNode) {
            nodeObj = graph.insertVertex(
                    root, null, ((CompositeNode)node).operation(), 
                    10, 10, 90, 40);
            
            for (TreeNode child : ((CompositeNode)node).children()) {
                processNode(child, nodeObj);
            }
        } else if (node instanceof ConstantLeaf) {
            nodeObj = graph.insertVertex(
                    root, null, ((ConstantLeaf)node).value(), 
                    10, 10, 90, 40);
        } else if (node instanceof VariableLeaf) {
            nodeObj = graph.insertVertex(
                    root, null, 
                    ((VariableLeaf)node).type().toString() + " " + ((VariableLeaf)node).name().toString(), 
                    10, 10, 90, 40);
        } else {
            nodeObj = graph.insertVertex(
                    root, null, "InvalidNode", 
                    10, 10, 90, 40);
        }
        
        graph.insertEdge(root, null, null, parent, nodeObj);
    }
    
    private Object root;
    private mxGraph graph;
}
