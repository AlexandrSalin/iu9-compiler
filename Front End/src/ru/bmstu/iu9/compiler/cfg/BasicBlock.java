package ru.bmstu.iu9.compiler.cfg;
import ru.bmstu.iu9.compiler.syntax.tree.Statement;

import java.util.LinkedList;

public class BasicBlock {
    private LinkedList<BasicBlock> predecessors;
    private LinkedList<BasicBlock> successors;
    private LinkedList<Edge> inputEdges;
    private LinkedList<Edge> outputEdges;
    private LinkedList<Statement> statements;
    private long label;


    private long Label() {
        return label;
    }
}
