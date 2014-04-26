package ru.bmstu.iu9.compiler.ir;

import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 *
 * @author maggot
 */
public class Code implements Iterable<Statement>, Cloneable {
    CFG controlFlowGraph;

    public Code() {
        statements = new LinkedList<Statement>();
    }
    
    public void addStatement(Statement statement) {
        this.statements.add(statement);
        System.out.println(
                currentIndex() + " :\t" + statement.toString()
            );
    }
    
    public int nextIndex() {
        return statements.size();
    }
    public int currentIndex() {
        return nextIndex() - 1;
    }

    public ListIterator<Statement> iterator() {
        return this.statements.listIterator();
    }
    
    @Override
    public String toString() {
        return this.statements.toString();
    }
    
    private List<Statement> statements;


    public void print() {
        for(int i = 0; i < statements.size(); ++i) {
            System.out.println(
                i + " :\t" + statements.get(i).toString()
            );
        }
    }

    public Code clone() {
        Code clone = new Code();
        for (Statement st : statements) {
            clone.addStatement(st);
        }
        return clone;
    }

    public void buildCFG(){
        controlFlowGraph = new CFG(statements).build();
    }

    public CFG getCFG(){
        if (controlFlowGraph != null){
            return controlFlowGraph;
        }
        throw new RuntimeException("CFG is null for current code");
    }
}