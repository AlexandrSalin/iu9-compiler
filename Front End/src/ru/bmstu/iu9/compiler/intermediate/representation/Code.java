/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.intermediate.representation;

import java.util.List;
import java.util.LinkedList;

/**
 *
 * @author maggot
 */
class Code {
    public Quadruple findLabel(String label) {
        for(Quadruple q : instructions) {
            if(q.label().equals(label))
                return q;
        }
        return null;
    }
    
    private List<Quadruple> instructions = new LinkedList<Quadruple>();
}
