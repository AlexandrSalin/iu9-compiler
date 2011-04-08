package ru.bmstu.iu9.compiler.intermediate.representation;

import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 *
 * @author maggot
 */
class Code implements Iterable<Statement> {
    public void addStatement(Statement statement) {
        this.statements.add(statement);
    }
    
    public Statement[] statements() { return this.statements.toArray(new Statement[0]); }
    
    @Override
    public ListIterator<Statement> iterator() {
        return this.statements.listIterator();
    }
    
    private List<Statement> statements = new LinkedList<Statement>();
}