package ru.bmstu.iu9.compiler.ir;

import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 *
 * @author maggot
 */
class Code implements Iterable<Statement> {
    public Code() {
        statements = new LinkedList<Statement>();
    }
    
    public void addStatement(Statement statement) {
        this.statements.add(statement);
        System.out.println(
                currentIndex() + " :\t" + statement.toString()
            );
    }
    
    public Statement[] statements() { 
        return this.statements.toArray(new Statement[0]); 
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
}