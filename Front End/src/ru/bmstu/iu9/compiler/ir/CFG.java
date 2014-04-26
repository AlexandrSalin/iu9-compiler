package ru.bmstu.iu9.compiler.ir;

import java.util.*;


class Edge implements AbstractEdge<BaseBlock> {
    int trueFalse;//1 -true, 0 -false, -1 -none
    BaseBlock inVertex, outVertex;

    public Edge(int i, BaseBlock in, BaseBlock out) {
        trueFalse = i;
        inVertex = in;
        outVertex = out;
    }

    public int getTrueFalse() { return trueFalse; }
    public BaseBlock InVertex() { return inVertex; }
    public BaseBlock OutVertex() { return outVertex; }
}

class BaseBlock {
    List<Statement> statements;
    long numOfFirstStatement;
    protected List<PhiFunction> phis;

    public BaseBlock() {
        statements = new LinkedList<Statement>();
    }

    public BaseBlock(Statement s) {
        statements = new LinkedList<Statement>();
        statements.add(s);
    }

    public void AddStatement(Statement s) {
        statements.add(s);
    }

    public void setNumOfFirstStatement(long numOfFirestStatement){
        this.numOfFirstStatement = numOfFirestStatement;
    }

    public List<Statement> getStatements() { return statements; }

    @Override
    public String toString() {
        String result = "next BB (" + this.numOfFirstStatement + "): \n";
        int i = 0;
        for(Statement s: statements) {
            result += ("\t" + i + ": " + s.toString() + "\n");
            i++;
        }
        return result;
    }


    public ListIterator<PhiFunction> GetPhiFunctions(){
        return this.phis.listIterator();
    };
    public int GetPhiFunctionsCount(){
        return this.phis.size();
    };
    public void AddPhiFunction(PhiFunction phi){
        this.phis.add(phi);
    };
    public void DeletePhiFunction(Iterator<PhiFunction> it){
        it.remove();
    };
    public void DeletePhiFunction(PhiFunction phi){
        this.phis.remove(phi);
    };
}


public class CFG {
    Digraph<BaseBlock, Edge> G = new Digraph<BaseBlock, Edge>();
    List<Statement> statements;

    public CFG(List<Statement> statements) {
        this.statements = statements;
    }

    public CFG build(){
        Hashtable<Long, Integer> StToBB = new Hashtable<Long, Integer>();//match index of statement to index of base block in vBB
        List<BaseBlock> vBB = new LinkedList<BaseBlock>();

        StToBB.put((long) 0, 0);//нулевому statement соответствует нулевой base block в vBB
        BaseBlock bb = new BaseBlock();
        vBB.add(bb);
        G.add(bb);
        long i = 0;
        for (Statement s: statements) {
            if(s.baseOperation == Statement.Operation.IF_GOTO)  {
                IfGoToStatement a = (IfGoToStatement)s;
                long ltrue = a.labelTrue.index();
                long lfalse = a.labelFalse.index();
                if(!StToBB.containsKey(ltrue)) {
                    StToBB.put(ltrue, vBB.size());
                    BaseBlock b = new BaseBlock();
                    b.setNumOfFirstStatement(i);
                    vBB.add(b);
                    G.add(b);
                }
                if(!StToBB.containsKey(lfalse)) {
                    StToBB.put(lfalse, vBB.size());
                    BaseBlock b = new BaseBlock();
                    b.setNumOfFirstStatement(i);
                    vBB.add(b);
                    G.add(b);
                }
            } else if (s.baseOperation == Statement.Operation.GOTO) {
                GoToStatement a = (GoToStatement)s;

                if (a.label.index() == -1) {
                    Label label = new Label();
                    label.setIndex(i + 1);
                    a = new GoToStatement(label);
                    statements.set((int) i, a);
                }
                long l = a.label.index();
                if(!StToBB.containsKey(l)) {
                    StToBB.put(l, vBB.size());
                    BaseBlock b = new BaseBlock();
                    b.setNumOfFirstStatement(i);
                    //System.out.println("got found: l=" + l + "; vBB.size()=" + vBB.size() + ";\n");
                    vBB.add(b);
                    //System.out.println("got found: l=" + l + "; vBB.size()=" + vBB.size() + ";\n");
                    G.add(b);
                }
            }
            i++;
        }

        //System.out.println("Started\n");

        i = 0;
        BaseBlock curBB = vBB.get(0);
        //System.out.println("contains: " + G.contains(curBB));
        for (Statement s: statements) {
            curBB.AddStatement(s);
            if(s.baseOperation == Statement.Operation.IF_GOTO)  {
                IfGoToStatement igt = (IfGoToStatement)s;
                G.add(new Edge(1, curBB, vBB.get(StToBB.get(igt.labelTrue.index()))));
                G.add(new Edge(0, curBB, vBB.get(StToBB.get(igt.labelFalse.index()))));
                //System.out.println("contains ifgoto: " + G.contains(curBB) + " and i+1=" + (i+1));
                if(StToBB.containsKey(i)) {
                    curBB = vBB.get(StToBB.get(i));
                } else {
                    BaseBlock b = new BaseBlock();
                    b.setNumOfFirstStatement(i);
                    vBB.add(b);
                    G.add(curBB);
                    curBB = b;
                }
            } else if (s.baseOperation == Statement.Operation.GOTO) {
                GoToStatement gt = (GoToStatement)s;
                //System.out.println("contains goto: " + vBB.get(StToBB.get(gt.label.index())).toString() + " and i+1=" + (i+1));
                G.add(new Edge(-1, curBB, vBB.get(StToBB.get(gt.label.index()))));
                if(StToBB.containsKey(i)) {
                    curBB = vBB.get(StToBB.get(i));
                } else {
                    BaseBlock b = new BaseBlock();
                    b.setNumOfFirstStatement(i);
                    vBB.add(b);
                    G.add(curBB);
                    curBB = b;
                }
            } else if(StToBB.containsKey(i)) {
                G.add(new Edge(-1, curBB, vBB.get(StToBB.get(i))));
                curBB = vBB.get(StToBB.get(i));
            }
            i++;
        }
        return this;
    }

    public List<BaseBlock> prevBB(BaseBlock b) { return G.prevBB(b); }
    public List<BaseBlock> nextBB(BaseBlock b) { return G.nextBB(b); }
    public List<Statement> getBBStatements(BaseBlock b) { return b.getStatements(); }

    public BaseBlock beginBB(Edge e) { return e.InVertex(); }
    public BaseBlock endBB(Edge e) { return e.OutVertex(); }
    public int trueOrFalse(Edge e) { return e.getTrueFalse(); } //1 -true, 0 -false, -1 -none
    public int trueOrFalse(BaseBlock a, BaseBlock b) { return G.getEdge(a, b).getTrueFalse(); } //1 -true, 0 -false, -1 -none

    public Set<BaseBlock> getAllBaseBlocks() { return G.getAllVertexes(); }
    public List<Edge> getAllEdges() { return G.getAllEdges(); }

    public List<BaseBlock> dfs() { return G.dfs(); }
    public List<BaseBlock> bfs() { return G.bfs(); }

    @Override
    public String toString() {
        return G.toString();
    }
}
