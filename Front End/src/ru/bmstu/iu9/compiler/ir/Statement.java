package ru.bmstu.iu9.compiler.ir;

import ru.bmstu.iu9.compiler.cfg.BasicBlock;
import ru.bmstu.iu9.compiler.ir.type.PrimitiveType;
import ru.bmstu.iu9.compiler.syntax.tree.BinaryOperationNode;
import ru.bmstu.iu9.compiler.syntax.tree.UnaryOperationNode;

import java.util.*;

/**
 *
 * @author anton.bobukh
 */
abstract class Statement {
    public enum Operation { 
        PARAM, CALL, RETURN, GOTO, IF_GOTO, RUN, BARRIER, BINATY_OPERATION, 
        UNARY_OPERATION, ASSIGN, INDIRECT_ASSIGN, MEMBER_SELECT, INDEX,
        START_LOCK, END_LOCK, INDIRECT_RUN, INDIRECT_CALL, PHI_FUNCTION
    };
    
    protected Statement(Operation operation) {
        this.baseOperation = operation;
    }

    protected final Operation baseOperation;
    protected boolean ssaExists;
    protected List<SsaName> ssaList;

    public void SetSsa(SsaName ssa){
        ssaList.add(ssa);
        ssaExists = true;
    }

    public ListIterator<SsaName> GetDefSsaNames(){
        return this.ssaList.listIterator();
    };
    public int GetDefSsaNamesCount(){
        this.ssaList.size();
    };
   /* public SsaName GetDefSsaName(){
        return
    };*/
    public ListIterator<SsaName> GetUseSsaNames(){};
}

final class IndirectRunStatement extends Statement {
    public IndirectRunStatement(VariableOperand function, int argsNumber) {
        super(Operation.INDIRECT_RUN);
        this.function = function;
        this.argsNumber = 
            new ConstantOperand(
                new PrimitiveType(PrimitiveType.Type.INT),
                argsNumber
            );
    }
    
    @Override
    public String toString() {
        return "run " + function + " : " + argsNumber; 
    }
    
    public final VariableOperand function;
    public final ConstantOperand argsNumber;



}

final class RunStatement extends Statement {
    public RunStatement(VariableOperand function, int argsNumber) {
        super(Operation.RUN);
        this.function = function;
        this.argsNumber = 
            new ConstantOperand(
                new PrimitiveType(PrimitiveType.Type.INT),
                argsNumber
            );
    }
    
    @Override
    public String toString() {
        return "run " + function + " : " + argsNumber; 
    }
    
    public final VariableOperand function;
    public final ConstantOperand argsNumber;
}

final class ReturnStatement extends Statement {
    public ReturnStatement() {
        super(Operation.RETURN);
        value = null;
    }
    public ReturnStatement(Operand value) {
        super(Operation.RETURN);
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "return" + (value == null ? "" : " " + value);
    }

    public final Operand value;
}

final class StartLockStatement extends Statement {
    public StartLockStatement() {
        super(Operation.START_LOCK);
    }

    @Override
    public String toString() {
        return "start lock";
    }
}

final class EndLockStatement extends Statement {
    public EndLockStatement() {
        super(Operation.END_LOCK);
    }

    @Override
    public String toString() {
        return "end lock";
    }
}

final class BarrierStatement extends Statement {
    public BarrierStatement() {
        super(Operation.BARRIER);
    }

    @Override
    public String toString() {
        return "barrier";
    }
}

final class AssignmentStatement extends Statement {
    public AssignmentStatement(VariableOperand lhv, Operand rhv) {
        super(Operation.ASSIGN);
        this.lhv = lhv;
        this.rhv = rhv;
    }
    
    @Override
    public String toString() {
        return lhv + " = " + rhv;
    }

    public VariableOperand GetDefSsaName(){
        if (this.ssaExists)
            return lhv;
        else return null;
    };
    
    public final Operand rhv;
    public final VariableOperand lhv;
}


final class IndirectAssignmentStatement extends Statement {
    public IndirectAssignmentStatement(VariableOperand lhv, Operand rhv) {
        super(Operation.INDIRECT_ASSIGN);
        this.lhv = lhv;
        this.rhv = rhv;
    }
    
    @Override
    public String toString() {
        return "*" + lhv + " = " + rhv;
    }
    
    public final Operand rhv;
    public final VariableOperand lhv;
}


final class BinaryOperationStatement extends Statement {
    public enum Operation {
        MUL, MINUS, PLUS, ARRAY_ELEMENT, DIV, MOD, BITWISE_SHIFT_RIGHT,
        BITWISE_SHIFT_LEFT, GREATER, GREATER_OR_EQUAL, LESS, LESS_OR_EUQAL, 
        NOT_EQUAL, EQUAL, BITWISE_AND, BITWISE_XOR, BITWISE_OR, BOOL_AND, 
        BOOL_OR
    };
    public BinaryOperationStatement(
            Operand leftOperand, 
            Operand rightOperand, 
            Operand lhv,
            Operation operation) {
        
        super(Statement.Operation.BINATY_OPERATION);
        this.lhv = lhv;
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.operation = operation;
    }
    
    @Override
    public String toString() {
        return lhv + " = " + leftOperand + " " + 
               operation.name() + " " + rightOperand;
    }
    
    public final Operand leftOperand;
    public final Operand rightOperand;
    public final Operand lhv;
    public final Operation operation;
    
    public static Operation operation(BinaryOperationNode.Operation operation) {
        return operations.get(operation);
    }
    
    private static final Map<BinaryOperationNode.Operation, Operation> operations;
    
    static {
        operations = 
            new EnumMap<BinaryOperationNode.Operation, Operation>(
                BinaryOperationNode.Operation.class
            );
        
        operations.put(BinaryOperationNode.Operation.MUL, Operation.MUL);
        operations.put(BinaryOperationNode.Operation.BOOL_OR, Operation.BOOL_OR);
        operations.put(BinaryOperationNode.Operation.BOOL_AND, Operation.BOOL_AND);
        operations.put(BinaryOperationNode.Operation.BITWISE_OR, Operation.BITWISE_OR);
        operations.put(BinaryOperationNode.Operation.BITWISE_XOR, Operation.BITWISE_XOR);
        operations.put(BinaryOperationNode.Operation.BITWISE_AND, Operation.BITWISE_AND);
        operations.put(BinaryOperationNode.Operation.EQUAL, Operation.EQUAL);
        operations.put(BinaryOperationNode.Operation.NOT_EQUAL, Operation.NOT_EQUAL);
        operations.put(BinaryOperationNode.Operation.LESS_OR_EUQAL, Operation.LESS_OR_EUQAL);
        operations.put(BinaryOperationNode.Operation.LESS, Operation.LESS);
        operations.put(BinaryOperationNode.Operation.GREATER_OR_EQUAL, Operation.GREATER_OR_EQUAL);
        operations.put(BinaryOperationNode.Operation.GREATER, Operation.GREATER);
        operations.put(BinaryOperationNode.Operation.BITWISE_SHIFT_LEFT, Operation.BITWISE_SHIFT_LEFT);
        operations.put(BinaryOperationNode.Operation.BITWISE_SHIFT_RIGHT, Operation.BITWISE_SHIFT_RIGHT);
        operations.put(BinaryOperationNode.Operation.DIV, Operation.DIV);
        operations.put(BinaryOperationNode.Operation.ARRAY_ELEMENT, Operation.ARRAY_ELEMENT);
        operations.put(BinaryOperationNode.Operation.PLUS, Operation.PLUS);
        operations.put(BinaryOperationNode.Operation.MINUS, Operation.MINUS);
    }
}


class Label {
    public Label() {
            this.index = (long)-1;
    }
    
    public void setIndex(long index) {
        this.index = index;
    }
    public long index() {
        return this.index;
    }
    
    @Override
    public String toString() {
        return index == null ? "" : index.toString();
    }
    
    private Long index;
}

final class GoToStatement extends Statement {
    public GoToStatement(Label label) {
        super(Operation.GOTO);
        this.label = label;
    }
    
    @Override
    public String toString() {
        return "goto " + label;
    }
    
    public final Label label;
}


final class IfGoToStatement extends Statement {
    public IfGoToStatement(
            Operand condition, 
            Label labelTrue,
            Label labelFalse) {
        
        super(Operation.IF_GOTO);
        this.condition = condition;
        this.labelTrue = labelTrue;
        this.labelFalse = labelFalse;
    }
    
    @Override
    public String toString() {
        return "if " + condition + " ? " + labelTrue + " : " + labelFalse;
    }
    
    public final Operand condition;
    public final Label labelTrue;
    public final Label labelFalse;
}


final class CallStatement extends Statement {
    public CallStatement(
            VariableOperand function,
            int argsNumber, 
            VariableOperand lhv) {
        
        super(Operation.CALL);
        this.function = function;
        this.argsNumber = 
            new ConstantOperand(
                new PrimitiveType(PrimitiveType.Type.INT, true),
                argsNumber
            );
        this.result = lhv;
    }
        
    public CallStatement(VariableOperand function, int argsNumber) {
        this(function, argsNumber, null);
    }

    @Override
    public String toString() {
        return (result == null ? "" : result + " = ") +
                "call " + function + " : " + argsNumber;
    }
    
    public final VariableOperand function;
    public final ConstantOperand argsNumber;
    public final VariableOperand result;
}

final class IndirectCallStatement extends Statement {
    public IndirectCallStatement(
            VariableOperand functionPointer,
            int argsNumber,
            VariableOperand lhv) {

        super(Operation.INDIRECT_CALL);
        this.functionPointer = functionPointer;
        this.argsNumber =
            new ConstantOperand(
                new PrimitiveType(PrimitiveType.Type.INT, true),
                argsNumber
            );
        this.result = lhv;
    }

    public IndirectCallStatement(
            VariableOperand functionPointer,
            int argsNumber) {

        this(functionPointer, argsNumber, null);
    }

    public final VariableOperand functionPointer;
    public final ConstantOperand argsNumber;
    public final VariableOperand result;
}

final class UnaryOperationStatement extends Statement {
    public enum Operation {
        POST_INC, POST_DEC, MINUS, PLUS, REF, DEREF, PRE_DEC, PRE_INC, 
        CAST, NOT, BITWISE_NOT
    };
    public UnaryOperationStatement(
            VariableOperand lhv, 
            Operand rhv,
            Operation operation) {
        
        super(Statement.Operation.UNARY_OPERATION);
        this.lhv = lhv;
        this.rhv = rhv;
        this.operation = operation;
    }
    
    @Override
    public String toString() {
        return lhv + " = " + operation + " " + rhv;
    }
    
    public final Operand rhv;
    public final VariableOperand lhv;
    public final Operation operation;
    
    public static Operation operation(UnaryOperationNode.Operation operation) {
        return operations.get(operation);
    }
    
    private static final Map<UnaryOperationNode.Operation, Operation> operations;
    
    static {
        operations = 
            new EnumMap<UnaryOperationNode.Operation, Operation>(
                UnaryOperationNode.Operation.class
            );
        operations.put(UnaryOperationNode.Operation.POST_INC, Operation.POST_INC);
        operations.put(UnaryOperationNode.Operation.POST_DEC, Operation.POST_DEC);
        operations.put(UnaryOperationNode.Operation.MINUS, Operation.MINUS);
        operations.put(UnaryOperationNode.Operation.PLUS, Operation.PLUS);
        operations.put(UnaryOperationNode.Operation.REF, Operation.REF);
        operations.put(UnaryOperationNode.Operation.DEREF, Operation.DEREF);
        operations.put(UnaryOperationNode.Operation.PRE_DEC, Operation.PRE_DEC);
        operations.put(UnaryOperationNode.Operation.PRE_INC, Operation.PRE_INC);
        operations.put(UnaryOperationNode.Operation.CAST, Operation.CAST);
        operations.put(UnaryOperationNode.Operation.NOT, Operation.NOT);
        operations.put(UnaryOperationNode.Operation.BITWISE_NOT, Operation.BITWISE_NOT);        
    }
}
/*
final class ArrayIndexStatement extends Statement {
    public ArrayIndexStatement(
            VariableOperand lhv,
            VariableOperand array, 
            Operand index
            ) {
        
        super(Operation.INDEX);
        this.array = array;
        this.index = index;
        this.lhv = lhv;
    }
    
    @Override
    public String toString() {
        return lhv + " = " + array + "[" + index + "]";
    }

    public final VariableOperand array;
    public final Operand index;
    public final VariableOperand lhv;
}
*/
final class ParamStatement extends Statement {
    public ParamStatement(Operand value) {
        super(Operation.PARAM);
        this.value = value;
    }

    @Override
    public String toString() {
        return "param " + value;
    }

    public final Operand value;
}
/*
final class MemberSelectStatement extends Statement {
    public MemberSelectStatement(
            VariableOperand lhv,
            VariableOperand struct, 
            VariableOperand field) {
        
        super(Operation.MEMBER_SELECT);
        this.field = field;
        this.struct = struct;
        this.lhv = lhv;
    }
    
    @Override
    public String toString() {
        return lhv + " = " + struct + "." + field;
    }
    
    public final VariableOperand struct;
    public final VariableOperand field;
    public final VariableOperand lhv;
}
*/

class PhiFunction extends Statement
{
    public PhiFunction()
    {
        super(Operation.PHI_FUNCTION);
    }

    public SsaName GetArgByEdge(Edge e)
    {

    };
    public Edge GetEdgeByArg(Iterator<SsaName> it){};
    public Edge GetEdgeByArg(SsaName ssaName){};
    public void DeleteEdgeByArg(Iterator<SsaName> it){};
    public void DeleteEdgeByArg(SsaName ssaName){};
    public void SetArgByEdge(Edge e, SsaName ssaName){};

    protected SsaName ssaNameLHS;
    protected SsaName ssaNamesRHS[]; // должно быть соответствие между входящими дугами и аргументами PHI
    protected BasicBlock bb;
}