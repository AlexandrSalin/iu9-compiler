package ru.bmstu.iu9.compiler.ir;

import ru.bmstu.iu9.compiler.ir.type.ArrayType;
import ru.bmstu.iu9.compiler.ir.type.BaseType;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by natalia on 22.03.14.
 */

public class PartRedundancy {
    CFG cfg;
    VariablesTable VarTable;

    List<Statement> allStatements = new LinkedList<Statement>();

    HashMap<Statement, Boolean> e_useB = new HashMap<Statement, Boolean>();
    //HashMap<Statement, Boolean> e_killB = new HashMap<Statement, Boolean>();

    HashMap<Statement, List<Statement>> expectedIn = new HashMap<Statement, List<Statement>>();
    HashMap<Statement, List<Statement>> expectedOut = new HashMap<Statement, List<Statement>>();

    HashMap<Statement, List<Statement>> availibleIn = new HashMap<Statement, List<Statement>>();
    HashMap<Statement, List<Statement>> availibleOut = new HashMap<Statement, List<Statement>>();

    HashMap<Statement, List<Statement>> earliest = new HashMap<Statement, List<Statement>>();

    HashMap<Statement, List<Statement>> postponableIn = new HashMap<Statement, List<Statement>>();
    HashMap<Statement, List<Statement>> postponableOut = new HashMap<Statement, List<Statement>>();

    HashMap<Statement, List<Statement>> usedIn = new HashMap<Statement, List<Statement>>();
    HashMap<Statement, List<Statement>> usedOut = new HashMap<Statement, List<Statement>>();

    HashMap<Statement, List<Statement>> latest = new HashMap<Statement, List<Statement>>();

    BaseBlock first;
    BaseBlock last;

    private void initStructs() {
        for (Statement s: allStatements) {
            e_useB.put(s, null);
            expectedIn.put(s, null);
            expectedOut.put(s, null);
        }
    }

    public PartRedundancy() { cfg = null; initStructs(); }

    public PartRedundancy(CFG c, VariablesTable vt) {
        cfg = c;
        VarTable = vt;
        initStructs();

        startReduce();
    }


    public void startReduce() {
        getAllStatements();

        BuildE_useB();

        countExpected();
        countAvailible();
        countPostponable();
        countUsed();

        //System.out.println("red created;");
        /*for(Statement s: e_useB.keySet()){
            System.out.println(s.toString() + ": ");

            //System.out.println("\t UNION: " + helpUnion(expectedIn.get(s), expectedOut.get(s)));

            System.out.println("\te in "+ expectedIn.get(s) + ", ");
            System.out.println("\te out "+ expectedOut.get(s) + ", ");

            System.out.println("\ta in "+ availibleIn.get(s) + ", ");
            System.out.println("\ta out "+ availibleOut.get(s) + ", ");

            System.out.println("\t!e!  "+ earliest.get(s) + ", ");

            System.out.println("\tp in " + postponableIn.get(s));
            System.out.println("\tp out " + postponableOut.get(s));

            System.out.println("\t!l! " + latest.get(s));

            System.out.println("\tu in " + usedIn.get(s));
            System.out.println("\tu out " + usedOut.get(s));
        }*/

        lastStep();
    }

    //----------------------help-----------------------------------

    private void getAllStatements() {
        List<BaseBlock> BBlocks = cfg.dfs();
        for(BaseBlock bb: BBlocks)
            for(Statement s: bb.getStatements()) {
               allStatements.add(s);
            }
    }

    //check if statement is expression (to be computed, that means contains binary or unary operation)
    private boolean isExpression(Statement s) {
        switch (s.baseOperation) {
            case BINATY_OPERATION:
            case UNARY_OPERATION:
                return true;
            default:
                return false;
        }
    }

    //union of two lists
    private List<Statement> helpUnion(List<Statement> a, List<Statement> b) {
        List<Statement> result = new LinkedList<Statement>();
        if(a != null)
            for (Statement l: a)
                result.add(l);
        if(b != null)
            for(Statement l: b)
                if(!result.contains(l))
                    result.add(l);
        return result;
    }

    //intersection of two lists
    private List<Statement> helpIntersect(List<Statement> a, List<Statement> b) {
        List<Statement> result = new LinkedList<Statement>();
        if(a == null || b == null)
            return result;
        else if(a.isEmpty() || b.isEmpty())
            return result;
        else {
            for(Statement e: a)
                result.add(e);
            for(Statement e: a)
                if(!b.contains(e))
                    result.remove(e);
        }
        return result;
    }

    //comparison of right parts of statements (for statements with unary and binary operations
    public boolean compareExpressions(Statement s1, Statement s2) {
        if(s1.baseOperation.equals(s2.baseOperation)) {
            switch (s1.baseOperation) {
                case BINATY_OPERATION: {
                    BinaryOperationStatement bos1 = (BinaryOperationStatement)s1;
                    BinaryOperationStatement bos2 = (BinaryOperationStatement)s2;
                    if(bos1.operation.toString().compareTo(bos2.operation.toString()) == 0
                            && bos1.rightOperand.toString().compareTo(bos2.rightOperand.toString()) == 0
                            && bos1.leftOperand.toString().compareTo(bos2.leftOperand.toString()) == 0) {
                        return true;
                    }
                    return false;
                }
                case UNARY_OPERATION: {
                    UnaryOperationStatement u1 = (UnaryOperationStatement)s1;
                    UnaryOperationStatement u2 = (UnaryOperationStatement)s2;
                    if(u1.rhv.toString().compareTo(u2.rhv.toString()) ==0 &&
                            u1.operation.toString().compareTo(u2.operation.toString()) ==0)
                        return true;
                    return false;
                }
                default:
                    return false;
            }
        }
        return false;
    }

    //-------------check-if-in-e_killB-
    private boolean helpIsKilled(Operand o,Statement ifKilled) {
        switch (ifKilled.baseOperation) {
            case BINATY_OPERATION:
                BinaryOperationStatement bin = (BinaryOperationStatement)ifKilled;
                if(bin.leftOperand.toString().compareTo(o.toString()) == 0
                        || bin.rightOperand.toString().compareTo(o.toString()) == 0)
                    return true;
                break;
            case UNARY_OPERATION:
                UnaryOperationStatement un = (UnaryOperationStatement)ifKilled;
                if(un.rhv.toString().compareTo(o.toString()) == 0)
                    return true;
                break;
            case ASSIGN:
                AssignmentStatement as = (AssignmentStatement)ifKilled;
                if(as.rhv.toString().compareTo(o.toString()) == 0)
                    return true;
                break;
            default:
        }
        return false;
    }

    private boolean IsKilled(Statement s,Statement ifKilled) {
         switch (s.baseOperation) {
            case BINATY_OPERATION:
                BinaryOperationStatement bin = (BinaryOperationStatement)s;
                return helpIsKilled(bin.lhv, ifKilled);
            case UNARY_OPERATION:
                UnaryOperationStatement u = (UnaryOperationStatement)s;
                return helpIsKilled(u.lhv, ifKilled);
            case ASSIGN:
                AssignmentStatement as = (AssignmentStatement)s;
                return helpIsKilled(as.lhv, ifKilled);
            default:
        }
        return false;
    }
    //---------not-needed------------
    /*private void BuildE_killB() {    //those, whose operands are calculated in B
        HashMap<Statement, List<Operand>> changedOperands = new HashMap<Statement, List<Operand>>();
        HashMap<Statement, Operand> changesOperand = new HashMap<Statement, Operand>();

        for (BaseBlock b1: cfg.getAllBaseBlocks())
            for(Statement s1: b1.getStatements())
                switch (s1.baseOperation) {
                    case BINATY_OPERATION:
                    case UNARY_OPERATION:
                    case ASSIGN:
                    case INDIRECT_ASSIGN:
                    case INDIRECT_CALL:
                    case CALL:
                        for (BaseBlock b: cfg.getAllBaseBlocks())
                            for(Statement s: b.getStatements()) {

                            }
                    default:
                }
    }*/
    //------------------e_useB-----------------------
    private void BuildE_useB() { //those, which are calculated in B, for each statement
            for (BaseBlock b: cfg.getAllBaseBlocks())
                for(Statement s: b.getStatements())
                    switch (s.baseOperation) {
                        case BINATY_OPERATION:
                        case UNARY_OPERATION:
                            e_useB.put(s, true);
                            break;
                        default:
                            e_useB.put(s, false);

                    }
    }

    //-----for list comparison----
    boolean isChanged = true;
    int numOfSt = 0;
    boolean firstTime = true;

    private boolean ifIsChanged(List<Statement> oldL, List<Statement> newL) {
        if(newL == null && oldL == null)
            return true;
        else if(newL == null)
            return false;
        //System.out.println("CHECK: " + oldL + "\n" + newL);
        for(Statement l: newL)
            if(!oldL.contains(l))
                return true;
        return false;
    }

    //------------------expectedIn------------
    private void funcExpectedIn(Statement s) {//fB(OUT[B]) = IN[B]
        expectedIn.put(s, new LinkedList<Statement>());
        if(e_useB.get(s))
            expectedIn.get(s).add(s);
        List<Statement> out = expectedOut.get(s);
        if(out != null)
            for (Statement st: out) {
                if(!IsKilled(s, st))
                    expectedIn.get(s).add(st);
            }
    }

    private void funcExpectedOut(Statement s, List<Statement> succ) {
        if(succ == null)
            return;
        //expectedOut.put(s, new LinkedList<Statement>());

        List<Statement> f = new LinkedList<Statement>();
        if(expectedIn.get(succ.get(0)) != null)
            f.addAll(expectedIn.get(succ.get(0)));
        else
            return;
        if(f == null)       //if f is empty, then intersection is empty
            return;
        succ. remove(0);
        if(succ.isEmpty()) {    //if succ is now empty, then it consisted of only f and intersection equals to f
            expectedOut.put(s, new LinkedList<Statement>());
            expectedOut.get(s).addAll(f);
        } else {
            List<Statement> elementsToRemove = new LinkedList<Statement>();

            List <Statement> result = new LinkedList<Statement>();
            result.addAll(f);      //now result contains all statements from expectedIn for f
            for(Statement l: succ) {        //want to compare all in expectedIn for succ
                List<Statement> outNext = expectedIn.get(l);
                if(outNext == null)
                    return;
                for(Statement r: result) {
                    boolean ok = false;
                    for (Statement out : outNext)
                        if (compareExpressions(out, r))
                            ok = true;
                    if (!ok)
                        elementsToRemove.add(r);
                        //result.remove(r);
                }
            }
            for(Statement r: elementsToRemove)
                result.remove(r);
            expectedOut.put(s, result);
        }
    }

    HashMap<BaseBlock, Boolean> markBB = new HashMap<BaseBlock, Boolean>();

    private void recCountExpected(BaseBlock b, List<BaseBlock> next) {
        List<Statement> inBB = b.getStatements();
        numOfSt += inBB.size();
        Statement curS = inBB.get(inBB.size()-1);

        List<Statement> before = new LinkedList<Statement>();
        if(expectedOut.get(curS) != null) {
            before.addAll(expectedOut.get(curS));
            expectedOut.get(curS).clear();
        }

        if(next != null) {
            List<Statement> nextSts = new LinkedList<Statement>();//statements, which follow the last statement of b
            for (BaseBlock bb : next)
                nextSts.add(bb.getStatements().get(0));
            funcExpectedOut(curS, nextSts);//count OUT[B] = ^(s from succ(b)) IN[s]  ~ out[last st of b]
        } else funcExpectedOut(curS, null);

        if(ifIsChanged(before, expectedOut.get(curS)))
            isChanged = true;
        before.clear();
        if(expectedIn.get(curS) != null)
            before.addAll(expectedIn.get(curS));

        funcExpectedIn(curS);//IN[B] -fills expectedIn for curS

        if(ifIsChanged(before, expectedIn.get(curS)))
            isChanged = true;

        int i = 0;
        for (i = inBB.size()-2; i >= 0; i--) {
            Statement nextS = inBB.get(i);
            before.clear();
            if(expectedOut.get(nextS) != null)
                before.addAll(expectedOut.get(nextS));

            expectedOut.put(nextS, new LinkedList<Statement>());
            //--count-expOut-for-cur------


            if(expectedIn.get(curS) != null) {
                expectedOut.get(nextS).clear();
                expectedOut.get(nextS).addAll(expectedIn.get(curS));
            }

            if(ifIsChanged(before, expectedOut.get(nextS))) {
                //System.out.println("!!CHANGED");
                isChanged = true;
            }


            curS = nextS;
            //--count-expIn-for-cur-------
            before.clear();
            if(expectedIn.get(curS) != null)
                before.addAll(expectedIn.get(curS));

            funcExpectedIn(curS);//IN[B] -fills expectedIn for curS

            if(ifIsChanged(before, expectedIn.get(curS)))
                isChanged = true;
        }

        markBB.remove(b); markBB.put(b, true);
        for(BaseBlock c: cfg.prevBB(b)) {
            boolean count = true;

            List<BaseBlock> n = cfg.nextBB(c);
            for(BaseBlock nn: n)
               if(!markBB.get(nn))
                 count = false;

            //if(count) {//&& !markBB.get(b)
            //System.out.println("count="  + count +markBB.get(c) + " " + c.getStatements());
            if(!markBB.get(c)) {
                markBB.put(c, true);
                recCountExpected(c, n);         //!!!если в nextBB есть с непосчитанными Statements, то не выполняем
            }
        }
    }

    private void countExpected() {
        isChanged = true;
        firstTime = true;
        while (isChanged) {
            if(!firstTime)
                isChanged = false;
            BaseBlock last = null;
            for (BaseBlock b : cfg.dfs()) {
                markBB.put(b, false);
                for (Statement s : b.getStatements())
                    if (s.baseOperation == Statement.Operation.RETURN)
                        last = b;
            }
            this.last = last;

            recCountExpected(last, null);
            firstTime = false;
        }
    }

    //-------------availible-----and-earliest-------------------------------

    private void funcAvailibleIn(Statement s, List<Statement> prev) {
        if(prev == null)
            return;
        //availibleIn.put(s, new LinkedList<Statement>());
        List<Statement> first = availibleOut.get(prev.get(0));
        if (first == null)
            return;
        if (first.isEmpty())
            return;
        prev.remove(0);
        if(prev.isEmpty()) {
            availibleIn.put(s, new LinkedList<Statement>());
            for(Statement l: first)
                availibleIn.get(s).add(l);
        } else {
            List <Statement> result = new LinkedList<Statement>();
            for(Statement l: first)
                result.add(l);      //now result contains all statements from availibleOut for f
            for(Statement l: prev) {        //want to compare all in availibleOut for prev
                List<Statement> inNext = availibleOut.get(l);
                if(inNext == null)
                    return;
                for(Statement r: first) {
                    boolean ok = false;
                    for (Statement in : inNext)
                        if (compareExpressions(in, r))
                            ok = true;
                    if (!ok)
                        result.remove(r);
                }
            }
            availibleIn.put(s, result);
        }
    }

    private boolean helpContains(List<Statement> list, Statement s) {//true - if list doesn't contain s
        if(list == null)
            return true;
        else if(list.isEmpty())
            return true;
        else {
            for(Statement l: list)
                if(l.toString().compareTo(s.toString()) == 0)
                    return false;
            return true;
        }
    }

    private void funcAvailibleOut(Statement s) {
        availibleOut.put(s, new LinkedList<Statement>());
        //List<Statement> in = availibleIn.get(s);
        if(availibleIn.get(s) != null) {
            for (Statement st : availibleIn.get(s))
                if (!IsKilled(s, st))
                    availibleOut.get(s).add(st);
            //in.clear();
        }
        //in = expectedIn.get(s);
        //System.out.println("avOut: s=" + s.toString());
        if(expectedIn.get(s) != null)
            for(Statement st: expectedIn.get(s)) {
                //System.out.println("avOut: s=" + s.toString() +" st=" + st + " c:" + IsKilled(s, st));
                if (!IsKilled(s, st) && helpContains(availibleOut.get(s), st))
                    availibleOut.get(s).add(st);
            }

        //System.out.println("avOut: result=" + availibleOut.get(s));
    }

    private void recCountAvailible(BaseBlock b, List<BaseBlock> prev) {
        List<Statement> stmts = b.getStatements();
        Statement curS = stmts.get(0);
        numOfSt += stmts.size();
        //stmts.remove(0);
        //availibleIn.put(curS, new LinkedList<Statement>());
        List<Statement> before  = new LinkedList<Statement>();
        if(availibleIn.get(curS) != null)
            before.addAll(availibleIn.get(curS));

        if(prev != null) {
            List<Statement> prevS = new LinkedList<Statement>();
            for (BaseBlock bb : prev)
                prevS.add(bb.getStatements().get(bb.getStatements().size()-1));
            funcAvailibleIn(curS, prevS);
        } else funcAvailibleIn(curS, null);       //intersect of null lists is null

        if(ifIsChanged(before, availibleIn.get(curS)))
            isChanged = true;
        before.clear();
        if(availibleOut.get(curS) != null)
            before.addAll(availibleOut.get(curS));

        //System.out.println("recavOut: curs=" + curS.toString());
        funcAvailibleOut(curS);

        if(ifIsChanged(before, availibleOut.get(curS)))
            isChanged = true;

        for(int i = 1; i < stmts.size(); i++) {
        //for(Statement st: stmts) {
            Statement nextS = stmts.get(i);
            //--count-avOut-for-cur------
    //added
            before.clear();
            if(availibleIn.get(nextS) == null)
                availibleIn.put(nextS, new LinkedList<Statement>());
            else
                before.addAll(availibleIn.get(nextS));

            if(availibleOut.get(curS) != null) {
                availibleIn.get(nextS).addAll(availibleOut.get(curS));
                if (ifIsChanged(before, availibleIn.get(nextS)))
                    isChanged = true;
            }
            //System.out.println("afterAOut: result=" + availibleOut.get(curS) + "\n\t" + availibleIn.get(nextS));
            curS = nextS;
            //--count-avIn-for-cur-------
            before.clear();
            if(availibleOut.get(curS) != null)
                before.addAll(availibleOut.get(curS));
            funcAvailibleOut(curS);//IN[B] -fills expectedIn for curS
            if(ifIsChanged(before, availibleOut.get(curS)))
                isChanged = true;
        }
        //for(int i = 0; i < stmts.size(); i++)
        //    System.out.println("afterAOut: result=" + availibleOut.get(stmts.get(i)) + " " + availibleIn.get(stmts.get(i)));
        markBB.remove(b); markBB.put(b, true);
        for(BaseBlock c: cfg.nextBB(b)) {
            boolean count = true;

            List<BaseBlock> n = cfg.prevBB(c);
            for(BaseBlock nn: n)
                if(!markBB.get(nn))
                    count = false;

            //if(count)//&& !markBB.get(b)
            if(!markBB.get(c))
                recCountAvailible(c, n);         //!!!если в nextBB есть с непосчитанными Statements, то не выполняем
        }
    }

    private void countEarliest() {
        for (Statement s: expectedIn.keySet()) {
            earliest.put(s, new LinkedList<Statement>());
            if(availibleIn.get(s) == null) {
                for (Statement e : expectedIn.get(s))
                    earliest.get(s).add(e);
            } else if(availibleIn.get(s).isEmpty()) {
                for (Statement e : expectedIn.get(s))
                    earliest.get(s).add(e);
            } else {
                for (Statement e: expectedIn.get(s)) {
                    boolean add = true;
                    for (Statement a: availibleIn.get(s))
                        if (compareExpressions(a, e))
                            add = false;
                    if (add)
                        earliest.get(s).add(e);
                }
            }
        }
    }

    private void countAvailible() {
        isChanged = true;
        boolean firstTime = true;
        while (isChanged) {
            if(firstTime)
                firstTime = false;
            else
                isChanged = false;
            BaseBlock first = null;
            markBB.clear();
            for (BaseBlock b : cfg.dfs()) {
                markBB.put(b, false);
                if (cfg.prevBB(b) == null)
                    first = b;
                else if (cfg.prevBB(b).isEmpty())
                    first = b;
            }
            this.first = first;

            recCountAvailible(first, null);
        }
            countEarliest();
    }

    //----------count-postponable-----------------------------

    private void funcPostponableIn(Statement s, List<Statement> prev) {
        if(prev == null)
            return;
        //availibleIn.put(s, new LinkedList<Statement>());
        List<Statement> first = postponableOut.get(prev.get(0));
        if (first == null)
            return;
        if (first.isEmpty())
            return;
        prev.remove(0);
        if(prev.isEmpty()) {
            for(Statement l: first)
                postponableIn.get(s).add(l);
        } else {
            List <Statement> result = new LinkedList<Statement>();
            for(Statement l: first)
                result.add(l);      //now result contains all statements from availibleOut for f
            for(Statement l: prev) {        //want to compare all in availibleOut for prev
                List<Statement> inNext = postponableOut.get(l);
                if(inNext == null)
                    return;
                for(Statement r: first) {
                    boolean ok = false;
                    for (Statement in : inNext)
                        if (compareExpressions(in, r))
                            ok = true;
                    if (!ok)
                        result.remove(r);
                }
            }
            postponableIn.put(s, result);
        }
    }

    private void funcPostponableOut(Statement s) {
        postponableOut.put(s, new LinkedList<Statement>());
        if(earliest.get(s) != null)
            for (Statement e: earliest.get(s))
                if (!compareExpressions(e, s)) {//
                    postponableOut.get(s).add(e);
                }
        if (postponableIn.get(s) != null)
            for (Statement e: postponableIn.get(s))
                if (!compareExpressions(s, e) && !postponableOut.get(s).contains(e))
                    postponableOut.get(s).add(e);//!!!wrong - (-euseb)
    }

    private void recCountPostponable(BaseBlock b, List<BaseBlock> prev) {
        List<Statement> stmts = b.getStatements();
        Statement curS = stmts.get(0);

        List<Statement> before  = new LinkedList<Statement>();
        if(postponableIn.get(curS) != null)
            before.addAll(postponableIn.get(curS));
        postponableIn.put(curS, new LinkedList<Statement>());

        if(prev != null) {
            List<Statement> prevS = new LinkedList<Statement>();
            for (BaseBlock bb : prev)
                prevS.add(bb.getStatements().get(bb.getStatements().size()-1));
            funcPostponableIn(curS, prevS);
        } else funcPostponableIn(curS, null);       //intersect of null lists is null

        if(ifIsChanged(before, postponableIn.get(curS)))
            isChanged = true;
        before.clear();
        if(postponableOut.get(curS) != null)
            before.addAll(postponableOut.get(curS));
        postponableOut.put(curS, new LinkedList<Statement>());

        funcPostponableOut(curS);

        if(ifIsChanged(before, postponableOut.get(curS)))
            isChanged = true;

        for(int i = 1; i < stmts.size(); i++) {
            Statement nextS = stmts.get(i);
            //--count-pOut-for-cur------
            before.clear();
            if(postponableIn.get(nextS) != null) {
                before.addAll(postponableIn.get(nextS));
            }
            postponableIn.put(nextS, new LinkedList<Statement>());

            if(postponableOut.get(curS) != null)
                postponableIn.get(nextS).addAll(postponableOut.get(curS));

            if(ifIsChanged(before, postponableIn.get(nextS)))
                isChanged = true;

            curS = nextS;
            //--count-pIn-for-cur-------
            before.clear();
            if(postponableOut.get(curS) != null) {
                before.addAll(postponableOut.get(curS));
                //postponableOut.get(curS).clear();
            }
            postponableOut.put(curS, new LinkedList<Statement>());

            funcPostponableOut(curS);//IN[B] -fills postponableIn for curS

            if(ifIsChanged(before, postponableOut.get(curS)))
                isChanged = true;
        }
        markBB.remove(b); markBB.put(b, true);
        for(BaseBlock c: cfg.nextBB(b)) {
            boolean count = true;

            List<BaseBlock> n = cfg.prevBB(c);
            for(BaseBlock nn: n)
                if(!markBB.get(nn))
                    count = false;
            //System.out.println(markBB.get(c).toString()+c);
            if(!markBB.get(c))//&& !markBB.get(b)
                recCountPostponable(c, n);         //!!!если в nextBB есть с непосчитанными Statements, то не выполняем
        }
    }

    private void countPostponable() {
        isChanged = true;
        boolean firstTime = true;
        while (isChanged) {
            //System.out.println("!!!AGAIN!!!");
            if (firstTime)
                firstTime = false;
            else
                isChanged = false;
            for (BaseBlock b : cfg.dfs())
                markBB.put(b, false);
            for (Statement s : expectedOut.keySet()) {
                //postponableIn.put(s, new LinkedList<Statement>());
                //postponableOut.put(s, new LinkedList<Statement>());
            }
            recCountPostponable(first, null);
        }
    }

    //----------------------used----------and-latest-----------

    private void funcUsedOut(Statement s, List<Statement> succ) {
        if(succ == null)
            return;
        if(succ.isEmpty())
            return;
        //expectedOut.put(s, new LinkedList<Statement>());

        List<Statement> f = expectedIn.get(succ.get(0));
        if(f == null)       //if f is empty, then intersection is empty
            return;
        succ. remove(0);
        if(succ.isEmpty()) {    //if succ is now empty, then it consisted of only f and intersection equals to f
            for (Statement l: f)
                usedOut.get(s).add(l);
        } else {
            List <Statement> result = new LinkedList<Statement>();
            for(Statement l: f)
                result.add(l);      //now result contains all statements from expectedIn for f
            for(Statement l: succ) {        //want to compare all in expectedIn for succ
                List<Statement> outNext = usedIn.get(l);
                if(outNext == null)
                    return;
                List<Statement> elementsToRemove = new LinkedList<Statement>();
                for(Statement r: result) {
                    boolean ok = false;
                    for (Statement out : outNext)
                        if (compareExpressions(out, r))
                            ok = true;
                    if (!ok)
                        elementsToRemove.add(r);
                }
                for(Statement r: elementsToRemove)
                    result.remove(r);

            }
            usedOut.put(s, result);
        }
    }

    private void funcUsedIn(Statement s) {
        usedIn.put(s, new LinkedList<Statement>());
        if(latest.get(s) != null)
            if(!latest.get(s).isEmpty()) {
                if (!latest.get(s).contains(s) && isExpression(s))
                    usedIn.get(s).add(s);
                if(usedOut.get(s) != null)
                    for (Statement l : usedOut.get(s))
                        if (!latest.get(s).contains(l) && !usedIn.get(s).contains(l) && isExpression(l))
                            usedIn.get(s).add(l);//добавляем только выражения - т.е. содержащие унарные и бинарные операцие
                return;
            }
        if(isExpression(s))
            usedIn.get(s).add(s);
        if(usedOut.get(s) != null)
            for (Statement l : usedOut.get(s))
                if (!usedIn.get(s).contains(l) && isExpression(l))
                    usedIn.get(s).add(l);
    }

    private void recCountUsed(BaseBlock b, List<BaseBlock> next) {
        List<Statement> inBB = b.getStatements();
        Statement curS = inBB.get(inBB.size()-1);

        List<Statement> before  = new LinkedList<Statement>();
        if(usedOut.get(curS) != null)
            before.addAll(usedOut.get(curS));
        usedOut.put(curS, new LinkedList<Statement>());

        if(next != null) {
            List<Statement> nextSts = new LinkedList<Statement>();//statements, which follow the last statement of b
            for (BaseBlock bb : next)
                nextSts.add(bb.getStatements().get(0));

            //---------latest--------------------
            countLatest(curS, nextSts);

            funcUsedOut(curS, nextSts);//count OUT[B] = ^(s from succ(b)) IN[s]  ~ out[last st of b]
        } else funcUsedOut(curS, null);

        if(ifIsChanged(before, usedOut.get(curS)))
            isChanged = true;
        before.clear();
        if(usedIn.get(curS) != null)
            before.addAll(usedIn.get(curS));
        usedIn.put(curS, new LinkedList<Statement>());

        funcUsedIn(curS);//IN[B] -fills expectedIn for curS

        if(ifIsChanged(before, usedIn.get(curS)))
            isChanged = true;

        int i = 0;
        for (i = inBB.size()-2; i >= 0; i--) {
            Statement nextS = inBB.get(i);

            //------latest-----------------
            countLatest(nextS, curS);

            before.clear();
            if(usedOut.get(nextS) != null)
                before.addAll(usedOut.get(nextS));

            usedOut.put(nextS, new LinkedList<Statement>());
            //--count-expOut-for-cur------
            if(usedIn.get(curS) != null)
                for(Statement l: usedIn.get(curS))
                    usedOut.get(nextS).add(l);


            if(ifIsChanged(before, usedOut.get(nextS)))
                isChanged = true;

            curS = nextS;
            //--count-expIn-for-cur-------

            if(usedIn.get(curS) != null)
                before.addAll(usedIn.get(curS));
            usedIn.put(curS, new LinkedList<Statement>());

            funcUsedIn(curS);//IN[B] -fills expectedIn for curS

            if(ifIsChanged(before, usedIn.get(curS)))
                isChanged = true;
        }

        markBB.remove(b); markBB.put(b, true);
        for(BaseBlock c: cfg.prevBB(b)) {
            if(!markBB.get(c))//count)//&& !markBB.get(b)
                recCountUsed(c, cfg.nextBB(c));         //!!!если в nextBB есть с непосчитанными Statements, то не выполняем
        }
    }
    //latest-beginning
    private void countLatest(Statement s, Statement succ) {
        List<Statement> a = new LinkedList<Statement>();
        a.add(succ);
        countLatest(s, a);
    }

    private void countLatest(Statement s, List<Statement> succ) {
        //a = earliest + postponableIn
        //b = intersect (nextEarliest + nextPostponableIn) for every successor
        //c = result = intersect of a with union of e_use and !b
        List<Statement> a = new LinkedList<Statement>();
        for (Statement l: helpUnion(earliest.get(s), postponableIn.get(s)))
            a.add(l);

        List <Statement> b = new LinkedList<Statement>();
        if (!(succ == null))
            if(!succ.isEmpty()) {
                List<Statement> newsucc = new LinkedList<Statement>();
                for(Statement l: succ)
                    newsucc.add(l);
                Statement first = newsucc.get(0);
                newsucc.remove(first);
                for(Statement l: helpUnion(earliest.get(first), postponableIn.get(first)))
                    b.add(l);

                if(!newsucc.isEmpty())
                    for(Statement l: newsucc) {
                        List<Statement> t = helpIntersect(b, helpUnion(earliest.get(l), postponableIn.get(l)));
                        //b.clear();
                        //for(Statement e: t)
                        //    b.add(e);
                        for(Statement e: b)
                            if(!t.contains(e))
                                b.remove(e);
                    }
            }//found intersect of unions for successors

        List<Statement> c = new LinkedList<Statement>();
        for (Statement l: a)
            c.add(l);
        for (Statement l: a)
            if(!(compareExpressions(s, l) || !b.contains(l)))
                c.remove(l);

        latest.put(s, new LinkedList<Statement>());
        latest.get(s).addAll(c);
    }
    //latest-end

    private void countUsed() {
        isChanged = true;
        boolean firstTime = true;
        while (isChanged) {
            //System.out.println("AGAIN!!!");
            if (firstTime)
                firstTime = false;
            else
                isChanged = false;
            for (BaseBlock b : cfg.dfs())
                markBB.put(b, false);

            //countLatest(); -at  the beginning of every rec fo fo every statement
            recCountUsed(last, null);
        }
    }

    //---------------finishing---creating-new-variables,-inserting-new-blocks-and-replacing-expressions-----
    private List<Statement> deleteTheSame(List<Statement> a) {
        if (a == null)
            return a;
        if (a.isEmpty())
            return a;
        List<Statement> elementsToRemove = new LinkedList<Statement>();
        for(int i = 0; i < a.size(); i++)
            for(int j = i+1; j < a.size(); j++)
                if(compareExpressions(a.get(i), a.get(j)))
                    elementsToRemove.add(a.get(j));
        for(Statement s: elementsToRemove)
            a.remove(s);
        return a;
    }

    List<Statement> newStatements = new LinkedList<Statement>();
    HashMap<Statement, Boolean> whereToReplace = new HashMap<Statement, Boolean>();

    private Operand findOperand(Statement s) {
        if(newStatements.isEmpty())
            return null;

        for(Statement n: newStatements)
            if(compareExpressions(s, n)) {
                switch (n.baseOperation) {
                    case BINATY_OPERATION:
                        BinaryOperationStatement b = (BinaryOperationStatement)n;
                        return b.lhv;
                    case UNARY_OPERATION:
                        UnaryOperationStatement u = (UnaryOperationStatement)n;
                        return u.lhv;
                }
            }//!!!иначе найти первое вхождение этого выражения в проге
        for(Statement e: expectedIn.keySet()) {
            if(compareExpressions(s, e) && !whereToReplace.get(e))
                switch (s.baseOperation) {
                    case BINATY_OPERATION:
                        BinaryOperationStatement b = (BinaryOperationStatement)e;
                        return b.lhv;
                    case UNARY_OPERATION:
                        UnaryOperationStatement u = (UnaryOperationStatement)e;
                        return u.lhv;
                }
        }
        return null;
    }

    private void lastStep() {

        System.out.println("\n\nbefore: "+cfg);

        HashMap<Statement, List<Statement>> intersectLatestAndUsedOut = new HashMap<Statement, List<Statement>>();
        for(BaseBlock bb: cfg.getAllBaseBlocks()) {
            List<Statement> bbS = bb.getStatements();
            List<Statement> newListStatements = new LinkedList<Statement>();
            newListStatements.addAll(bbS);

            for (Statement s : bbS) {
                intersectLatestAndUsedOut.put(s, new LinkedList<Statement>());
                intersectLatestAndUsedOut.get(s).addAll(deleteTheSame(helpIntersect(latest.get(s), usedOut.get(s))));


                for (Statement n : intersectLatestAndUsedOut.get(s)) {
                    switch (n.baseOperation) {
                        case BINATY_OPERATION:
                            BinaryOperationStatement b = (BinaryOperationStatement) n;
                            Operand newOp = new TmpVariableOperand(b.lhv.type(), VarTable);//new TmpVariableOperand(VariableOperand, )
                            Statement newSt = new BinaryOperationStatement(b.leftOperand, b.rightOperand, newOp, b.operation);
                            System.out.println("new statement to insert: " + newSt);
                            newListStatements.add(newListStatements.indexOf(s), newSt);
                            newStatements.add(newSt);
                            //System.out.println("changed BB: "+ newListStatements);
                            whereToReplace.put(newSt, false);
                            break;
                        case UNARY_OPERATION:
                            UnaryOperationStatement u = (UnaryOperationStatement) n;
                            Operand newO = new TmpVariableOperand(u.lhv.type(), VarTable);//new TmpVariableOperand(VariableOperand, )
                            Statement newS = new UnaryOperationStatement((VariableOperand)newO, u.rhv, u.operation);
                            System.out.println("new statement to insert: " + newS);
                            newListStatements.add(newListStatements.indexOf(s), newS);
                            newStatements.add(newS);

                            whereToReplace.put(newS, false);
                            break;
                        default:
                            System.out.println("expression expected, but " + n + " found");
                    }
                }

                whereToReplace.put(s, false);
                if (usedOut.get(s) != null) {
                    boolean add = false;
                    for (Statement l : usedOut.get(s))
                        if (compareExpressions(s, l))
                            add = true;
                    if (add)
                        whereToReplace.put(s, true);
                }
                if (latest.get(s) == null) {
                    if (isExpression(s))
                        whereToReplace.put(s, true);
                } else {
                    boolean add = true;
                    for (Statement l : latest.get(s))
                        if (compareExpressions(s, l))
                            add = false;
                    if (add && isExpression(s))
                        whereToReplace.put(s, true);
                }


            }
            bb.statements.clear(); bb.statements.addAll(newListStatements);
            //System.out.println("changed BB: "+ bb);
        }


        for(BaseBlock bb: cfg.getAllBaseBlocks())
            for(Statement s: bb.getStatements()) {
                //System.out.println("insert before: " + s + "\t\t ex: " + intersectLatestAndUsedOut.get(s));
            }
        for(BaseBlock bb: cfg.getAllBaseBlocks())
            for(Statement s: bb.getStatements()) {
                //System.out.println("\t\t\t\t\t\t\treplace: " + s + "\t- "+whereToReplace.get(s));
            }



        HashMap<BaseBlock, List<Statement>> bbToStatements = new HashMap<BaseBlock, List<Statement>>();
        for(BaseBlock bb: cfg.getAllBaseBlocks()) {
            List<Statement> bbS = bb.getStatements();
            List<Statement> newListStatements = new LinkedList<Statement>();
            newListStatements.addAll(bbS);

            for (Statement s : bbS) {
                if (whereToReplace.get(s)) {
                    switch (s.baseOperation) {
                        case BINATY_OPERATION:
                            BinaryOperationStatement bin = (BinaryOperationStatement) s;
                            Statement as = new AssignmentStatement((VariableOperand) bin.lhv, findOperand(s));
                            newListStatements.add(newListStatements.indexOf(s), as);
                            newListStatements.remove(s);
                            break;
                        case UNARY_OPERATION:
                            UnaryOperationStatement u = (UnaryOperationStatement) s;
                            Statement ass = new AssignmentStatement((VariableOperand) u.lhv, findOperand(s));
                            newListStatements.add(newListStatements.indexOf(s), ass);
                            newListStatements.remove(s);
                            break;
                    }
                }
                bbToStatements.put(bb, newListStatements);
                //bb.statements.clear(); bb.statements.addAll(newListStatements);
            }
        }
        for(BaseBlock bb: bbToStatements.keySet()) {
            bb.getStatements().clear(); bb.getStatements().addAll(bbToStatements.get(bb));
            //System.out.println("after replace BB: "+ bb);
        }

        //for(BaseBlock b: cfg.getAllBaseBlocks())
            System.out.println("\n\nafter: "+cfg);
    }
}

/*
выражения,в ычисляемые в бб, т.е. в Statement - это то выражение, из которого базовый блок и состоит
т.е. e_useB - это сам бб, т.е. Statement

выражения, операнды которых изменяются в бб - это все то из всех выражений в программе, которые содержат
первое поле текущего Statement(перед "="), т.е. кому присваивается новое значение

 */