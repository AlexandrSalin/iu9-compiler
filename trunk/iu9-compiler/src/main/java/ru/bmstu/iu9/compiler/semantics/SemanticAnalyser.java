package ru.bmstu.iu9.compiler.semantics;

import com.google.gson.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

import ru.bmstu.iu9.compiler.ir.type.*;
import ru.bmstu.iu9.compiler.syntax.tree.*;

/**
 * 
 * @author maggot
 */
public class SemanticAnalyser {
    public SemanticAnalyser(BlockNode<BaseNode> parseTree) {
        this.parseTree = parseTree;
    }
    public SemanticAnalyser(String filename) {
        BufferedReader reader = null;
        
        Gson gson = 
            new GsonBuilder().
                registerTypeHierarchyAdapter(
                    BaseNode.class, 
                    new BaseNode.BaseNodeAdapter()).
                registerTypeHierarchyAdapter(
                    BaseType.class, 
                    new BaseType.TypeAdapter()).
                create();
        
        try {
            reader = new BufferedReader(new FileReader(filename));
            this.parseTree = gson.fromJson(reader, BlockNode.class);
        } catch(java.io.IOException ex) {
//            this.parseTree = null;
        } finally {
            try {
                reader.close();
            } catch(java.io.IOException ex) {
//                ex.printStackTrace();
            }
        }
    }
    
    public void analyse() {
        try {
            resolveNames(parseTree);
            resolveTypes(parseTree);
            processNode(parseTree);
        } catch(SemanticException ex) {
            return;
        }
    }
    
    public static void main(String[] args) {
        SemanticAnalyser analyser = 
            new SemanticAnalyser("C:\\Users\\Bobukh\\Documents\\NetBeansProjects\\Front End\\src\\parse_tree.json");
        analyser.analyse();

        return;
    }
    
    
    
    private void resolveNames(BlockNode<BaseNode> tree) {
        for(BaseNode node : tree) {
            switch(node.nodeType()) {
                case STRUCT_DECL:
                {
                    StructDeclNode struct = (StructDeclNode)node;
                    
                    StructType realType = 
                            new StructType(struct.name, true);
                    
                    context.global().add(
                        new StructSymbol(
                            struct.name, 
                            realType,
                            new SymbolTable()
                        ));
                    
                    break;
                }
                case FUNCTION_DECL:
                {
                    FunctionDeclNode f = (FunctionDeclNode)node;
                    context.global().add(
                        new FunctionSymbol(
                            f.name, 
                            context.global(),
                            new SymbolTable()
                        ));
                    break;
                }
                default:
                {
                    break;
                }
            }
        }
    }
    
    private void resolveTypes(BlockNode<BaseNode> tree) {
        for(BaseNode node : tree) {
            switch(node.nodeType()) {
                case STRUCT_DECL:
                {
                    StructDeclNode struct = (StructDeclNode)node;
                    StructSymbol symbol = 
                        (StructSymbol)context.global().get(struct.name);
                    assert symbol == null;
                    
                    SymbolTable scope = symbol.scope;

                    StructType type = (StructType)symbol.type;

                    for(VariableDeclNode decl : struct.declarations) {
                        processType(decl.type);
                        scope.add(
                            new VariableSymbol(
                                decl.name, 
                                decl.type.realType()
                            ));
                        decl.setRealType(decl.type.realType());

                        type.addField(decl.name, decl.realType());
                    }

                    break;
                }
                case FUNCTION_DECL:
                {
                    FunctionDeclNode func = (FunctionDeclNode)node;
                    FunctionSymbol symbol = 
                        (FunctionSymbol)context.global().get(func.name);
                    assert symbol != null;
                    
                    SymbolTable scope = symbol.scope;
                    FunctionTypeNode type = (FunctionTypeNode)func.type;

                    for(FunctionTypeNode.ArgumentNode arg : type.arguments) {
                        processType(arg.type);
                        arg.setRealType(arg.type.realType());
                        scope.add(new VariableSymbol(arg.name, arg.realType()));
                    }
                    processType(type.returnValue);

                    List<FunctionType.Argument> arguments = 
                            new LinkedList<FunctionType.Argument>();

                    for(FunctionTypeNode.ArgumentNode arg : type.arguments) {
                        arguments.add(arg.toArgument());
                    }
                    
                    FunctionType realType = 
                        new FunctionType(
                            type.returnValue.realType(),
                            arguments,
                            type.constancy);

                    symbol.setType(realType);
                    type.setRealType(realType);
                    func.setRealType(realType);

                    break;
                }
                default:
                {
                    break;
                }
            }
        }
    }


    private void processType(BaseTypeNode type) {
        switch(type.type()) {
            case ARRAY:
            {
                ArrayTypeNode a = (ArrayTypeNode)type;

                processType(a.element);
                try {
                    processNode(a.length);
                    a.setRealType(
                        new ArrayType(
                            a.element.realType(),
                            a.length.value,
                            a.constancy)
                        );
                } catch(SemanticException ex) {
                    a.setRealType(
                        new ArrayType(
                            a.element.realType(),
                            a.constancy)
                        );
                }/*
                if(a.length != null) {
                    processNode(a.length);

                    a.setRealType(
                        new ArrayType(
                            a.element.realType(),
                            a.length.value,
                            a.constancy)
                        );
                } else {
                    a.setRealType(
                        new ArrayType(
                            a.element.realType(),
                            a.constancy)
                        );
                }*/

                break;
            }
            case PRIMITIVE_TYPE:
            {
                PrimitiveTypeNode p = (PrimitiveTypeNode)type;

                switch(p.primitive()) {
                    case POINTER:
                    {
                        PointerTypeNode pointer = (PointerTypeNode)p;
                        processType(pointer.pointerType);

                        p.setRealType(
                            new PointerType(
                                pointer.pointerType.realType(), 
                                pointer.constancy)
                            );
                        break;
                    }
                    case INT:
                    {
                        p.setRealType(
                            new PrimitiveType(
                                PrimitiveType.Type.INT, 
                                p.constancy)
                            );
                        break;
                    }
                    case DOUBLE:
                    {
                        p.setRealType(
                            new PrimitiveType(
                                PrimitiveType.Type.DOUBLE, 
                                p.constancy)
                            );
                        break;
                    }
                    case FLOAT:
                    {
                        p.setRealType(
                            new PrimitiveType(
                                PrimitiveType.Type.FLOAT, 
                                p.constancy)
                            );
                        break;
                    }
                    case CHAR:
                    {
                        p.setRealType(
                            new PrimitiveType(
                                PrimitiveType.Type.CHAR, 
                                p.constancy)
                            );
                        break;
                    }
                    case VOID:
                    {
                        p.setRealType(
                            new PrimitiveType(
                                PrimitiveType.Type.VOID, 
                                p.constancy)
                            );
                        break;
                    }
                    case BOOL:
                    {
                        p.setRealType(
                            new PrimitiveType(
                                PrimitiveType.Type.BOOL, 
                                p.constancy)
                            );
                        break;
                    }
                    case LONG:
                    {
                        p.setRealType(
                            new PrimitiveType(
                                PrimitiveType.Type.LONG, 
                                p.constancy)
                            );
                        break;
                    }
                }

                break;
            }
            case STRUCT:
            {
                StructTypeNode s = (StructTypeNode)type;
                StructSymbol struct = (StructSymbol)context.global().get(s.name);
                assert struct == null;

                s.setRealType(struct.type);

                break;
            }
            case FUNCTION:
            {
                FunctionTypeNode f = (FunctionTypeNode)type;
                
                for(FunctionTypeNode.ArgumentNode arg : f.arguments) {
                    processType(arg.type);
                    arg.setRealType(arg.type.realType());
                }
                processType(f.returnValue);

                List<FunctionType.Argument> arguments = 
                        new LinkedList<FunctionType.Argument>();

                for(FunctionTypeNode.ArgumentNode arg : f.arguments) {
                    arguments.add(arg.toArgument());
                }

                f.setRealType(
                    new FunctionType(
                        f.returnValue.realType(),
                        arguments,
                        f.constancy)
                    );

                break;
            }
            case INVALID:
            {
                type.setRealType(new InvalidType());
                break;
            }
        }
    }
    
    
    
    
    
    
    
    private void processNode(BaseNode node) throws SemanticException {
        try {
            switch (node.nodeType()) {
                case BINARY_OPERATION:
                {
                    BinaryOperationNode bNode = (BinaryOperationNode)node;

                    switch(bNode.operation()) {
                        case ARRAY_ELEMENT:
                        {
                            processNode(bNode.leftChild());
                            processNode(bNode.rightChild());

                            TypeChecker.check(
                                    bNode.leftChild().realType(), 
                                    BaseType.Type.ARRAY,
                                    bNode.dInfo);

                            ArrayType array =
                                    (ArrayType)bNode.leftChild().realType();
                            bNode.setRealType(array.element);

                            break;
                        }
                        case MEMBER_SELECT:
                        {
                            processNode(bNode.leftChild());

                            BaseType type = bNode.leftChild().realType();

                            while(type.is(PrimitiveType.Type.POINTER)) {

                                type = ((PointerType)type).pointerType;
                            }

                            if(TypeChecker.check(
                                    type, 
                                    BaseType.Type.STRUCT, 
                                    bNode.dInfo)
                            ) {

                                context.enterStruct((StructType) type);

                                processNode(bNode.rightChild());

                                VariableLeaf fieldName = 
                                    (VariableLeaf) bNode.rightChild();

                                Symbol field = context.get(fieldName.name);
                                if (field == null) {
                                    Logger.logUndeclaredVarialbe(
                                            fieldName.name, 
                                            bNode.rightChild().dInfo.position);
                                    bNode.setRealType(new InvalidType());
                                } else {
                                    bNode.setRealType(field.type);
                                }

                                context.leaveStruct();
                            } else {
                                Logger.logIncompatibleTypes(
                                        type.toString(), 
                                        BaseType.Type.STRUCT.toString(), 
                                        bNode.dInfo.position);
                            }

                            break;
                        }
                        case ASSIGN:
                        {
                            processNode(bNode.leftChild());
                            processNode(bNode.rightChild());
                            
                            ExpressionNode lhv = bNode.leftChild();

                            // lhv check
                            if (!lhv.is(BaseNode.NodeType.VARIABLE) &&
                                !lhv.is(BinaryOperationNode.Operation.MEMBER_SELECT) &&
                                !lhv.is(BinaryOperationNode.Operation.ARRAY_ELEMENT) &&
                                !lhv.is(UnaryOperationNode.Operation.DEREF)) {

                                throw (SemanticException)new InvalidLeftHandValueException(bNode)
                                        .initPosition(bNode.dInfo.position)
                                        .Log("ru.bmstu.iu9.compiler.semantics");
                            }

                            break;
                        }
                        default:
                        {
                            processNode(bNode.leftChild());
                            processNode(bNode.rightChild());

                            TypeChecker.check(
                                    bNode.leftChild().realType(), 
                                    bNode.rightChild().realType(),
                                    bNode.dInfo);

                            if (bNode.is(BinaryOperationNode.Operation.Bitwise)) {

                                TypeChecker.check(
                                    bNode.realType(), 
                                    PrimitiveType.Type.INT, 
                                    bNode.dInfo);
                            }

                            if (bNode.is(BinaryOperationNode.Operation.Comparison)) {

                                bNode.setRealType(
                                    new PrimitiveType(
                                        PrimitiveType.Type.BOOL, 
                                        true)
                                    );
                            } else {
                                bNode.setRealType(bNode.leftChild().realType());
                            }
                            break;
                        }
                    }

                    break;

                }
                case UNARY_OPERATION:
                {
                    UnaryOperationNode uNode = ((UnaryOperationNode) node);

                    processNode(uNode.node);
                    switch (uNode.operation()) {
                        case NOT:
                        {
                            TypeChecker.check(
                                    uNode.node.realType(), 
                                    PrimitiveType.Type.BOOL, 
                                    uNode.dInfo);

                            uNode.setRealType(uNode.node.realType());
                            break;
                        }
                        case POST_INC:
                        case POST_DEC:
                        case PRE_INC:
                        case PRE_DEC:
                        case BITWISE_NOT:
                            TypeChecker.check(
                                    uNode.node.realType(), 
                                    new PrimitiveType.Type[] {
                                        PrimitiveType.Type.INT, 
                                        PrimitiveType.Type.CHAR
                                    }, 
                                    uNode.dInfo);

                            uNode.setRealType(uNode.node.realType());
                            break;
                        case MINUS:
                        case PLUS:
                            TypeChecker.check(uNode.node.realType(),
                                    new PrimitiveType.Type[] {
                                        PrimitiveType.Type.INT, 
                                        PrimitiveType.Type.DOUBLE,
                                        PrimitiveType.Type.FLOAT
                                    }, uNode.dInfo);

                            uNode.setRealType(uNode.node.realType());
                            break;
                        case DEREF:
                            TypeChecker.check(
                                    uNode.node.realType(), 
                                    PrimitiveType.Type.POINTER, 
                                    uNode.dInfo);

                            uNode.setRealType(
                                ((PointerType)uNode.node.realType()).pointerType);
                            break;
                        case CAST:
                            CastNode c = (CastNode) node;

                            processNode(c.castingType);
                            uNode.setRealType(c.castingType.realType());

                            break;
                        case REF:
                            uNode.setRealType(
                                new PointerType(uNode.node.realType(), false));
                            break;
                    }
                    break;

                }
                case VAR_DECL:
                {
                    VariableDeclNode leaf = (VariableDeclNode)node;
                    processNode(leaf.type);
                    BaseType t = leaf.type.realType();

                    VariableSymbol symbol = new VariableSymbol(leaf.name, t);

                    Symbol declaredSymbol = context.get(leaf.name);
                    if (declaredSymbol != null) {
                        throw (SemanticException)new VariableRedefinitionException(
                                    leaf.name
                                )
                                .initPosition(leaf.dInfo.position)
                                .Log("ru.bmstu.iu9.semantics");
                    }

                    context.add(symbol);

                    leaf.setRealType(t);

                    if(leaf.value != null) {
                        processNode(leaf.value);

                        TypeChecker.check(t, leaf.value.realType(), leaf.dInfo);
                    }

                    break;
                }
                case FOR:
                {
                    ForNode forNode = (ForNode)node;

                    context.pushScope();
                    processNode(forNode.initialization);
                    processNode(forNode.expression);
                    processNode(forNode.step);
                    processNode(forNode.block);
                    context.popScope();

                    TypeChecker.check(
                            forNode.expression.realType(), 
                            PrimitiveType.Type.BOOL,
                            ((ExpressionNode)forNode.expression).dInfo);
                    break;
                }
                case IF:
                {
                    boolean returnsBefore = context.returns;
                    context.returns = false;

                    IfNode ifNode = (IfNode)node;

                    context.pushScope();
                    processNode(ifNode.condition);
                    processNode(ifNode.block);
                    context.popScope();

                    if(ifNode.elseNode != null) {
                        boolean returns = context.returns;
                        context.returns = false;

                        processNode(ifNode.elseNode);

                        context.returns = context.returns && returns;
                    } else {
                        context.returns = false;
                    }

                    TypeChecker.check(
                            ifNode.condition.realType(), 
                            PrimitiveType.Type.BOOL, 
                            ((ExpressionNode)ifNode.condition).dInfo);

                    context.returns = context.returns || returnsBefore;

                    break;
                }
                // @todo Протестировать
                case SWITCH:
                {
                    boolean returnsBefore = context.returns;
                    context.returns = false;

                    context.enterSwitch();

                    SwitchNode switchNode = ((SwitchNode)node);

                    context.pushScope();
                    processNode(switchNode.expression);

                    boolean returns = true;

                    for(CaseNode child : switchNode.cases) {
                        context.returns = false;
                        processNode(child);
                        returns = context.returns && returns;
                    }
                    if(switchNode.defaultNode != null) {
                        context.returns = false;
                        processNode(switchNode.defaultNode);
                        context.returns = context.returns && returns;
                    } else {
                        context.returns = false;
                    }
                    context.popScope();

                    for (CaseNode caseNode : switchNode.cases) {
                        TypeChecker.check(
                                switchNode.expression.realType(), 
                                caseNode.expression.realType(), 
                                ((ExpressionNode)switchNode.expression).dInfo);
                    }

                    context.leaveSwitch();

                    context.returns = returnsBefore || context.returns;
                    break;
                }
                case CASE:
                {
                    context.pushScope();
                    processNode(((CaseNode)node).expression);
                    processNode(((CaseNode)node).block);
                    context.popScope();
                    break;
                }
                case WHILE:
                {
                    context.enterLoop();
                    WhileNode cbNode = (WhileNode)node;

                    context.pushScope();
                    processNode(cbNode.expression);
                    processNode(cbNode.block);
                    context.popScope();

                    TypeChecker.check(
                            cbNode.expression.realType(), 
                            PrimitiveType.Type.BOOL,
                            ((ExpressionNode)cbNode.expression).dInfo);

                    context.leaveLoop();
                    break;
                }
                case DO_WHILE:
                {
                    boolean returnsBefore = context.returns;
                    context.returns = false;
                    context.enterLoop();

                    DoWhileNode cbNode = (DoWhileNode)node;

                    context.pushScope();
                    processNode(cbNode.block);
                    processNode(cbNode.expression);
                    context.popScope();

                    TypeChecker.check(
                            cbNode.expression.realType(), 
                            PrimitiveType.Type.BOOL,
                            ((ExpressionNode)cbNode.expression).dInfo);

                    context.leaveLoop();

                    context.returns = context.returns || returnsBefore;
                    break;
                }
                    // @todo unstable. test
                case BLOCK:
                {
                    BlockNode<BaseNode> block = (BlockNode<BaseNode>) node;
                    for(BaseNode child : block) {
                        try {
                            processNode(child);
                        } catch(SemanticException ex) {
                            // block.removeNode(child);
                            continue;
                        }
                    }
                    break;
                }
                    // @todo unstable. test
                case BLOCK_DECL:
                {
                    context.pushScope();

                    BlockDeclNode<DeclNode> declarations = 
                        (BlockDeclNode<DeclNode>) node;

                    for(DeclNode child : declarations) {
                        try {
                            processNode(child);
                        } catch(SemanticException ex) {
                            // declarations.removeNode(child);
                            continue;
                        }
                    }
                    break;
                }
                case VARIABLE:
                {
                    VariableLeaf var = (VariableLeaf)node;
                    Symbol symbol = context.get(var.name);

                    if (symbol != null) {
                        var.setRealType(symbol.type());
                    } else {
                        //Logger.logUndeclaredVarialbe(var.name, var.dInfo.position);
                        throw new UseOfUndeclaredVariableException(var.name)
                                .initPosition(var.dInfo.position)
                                .Log("ru.bmstu.iu9.compiler.semantics");
                    }
                    break;
                }
                case CONSTANT:
                {
                    ConstantLeaf c = (ConstantLeaf)node;

                    switch(c.constantType()) {
                        case INT:
                            c.setRealType(
                                new PrimitiveType(
                                    PrimitiveType.Type.INT, 
                                    true)
                                );
                            break;
                        case DOUBLE:
                            c.setRealType(
                                new PrimitiveType(
                                    PrimitiveType.Type.DOUBLE, 
                                    true)
                                );
                            break;
                        case BOOL:
                            c.setRealType(
                                new PrimitiveType(
                                    PrimitiveType.Type.BOOL, 
                                    true)
                                );
                            break;
                        case CHAR:
                            c.setRealType(
                                new PrimitiveType(
                                    PrimitiveType.Type.CHAR, 
                                    true)
                                );
                            break;
                    }

                    break;
                }
                case FUNCTION_DECL:
                {
                    FunctionDeclNode function = (FunctionDeclNode)node;

                    context.enterFunction(function);

                    boolean returns = context.returns = false;

                    for(Statement child : function.block) {
                        processNode(child.getNode());
 //                       returns = returns || context.returns;
                    }

                    if(!(context.returnValue().is(PrimitiveType.Type.VOID))/* &&
                       !returns*/) {
                        
                        context.leaveFunction();
                        throw new MissingReturnStatementException()
                                .Log("ru.bmstu.iu9.compiler.semantics");
                    }

                    context.leaveFunction();
                    break;
                }
                case RETURN:
                {
                    ReturnNode ret = (ReturnNode)node;

                    context.returns = true;

                    if(ret.returnExpr != null) {
                        processNode(ret.returnExpr);
                        TypeChecker.check(
                                ret.returnExpr.realType(), 
                                context.returnValue(), 
                                ret.dInfo);
                    } else {
                        TypeChecker.check(
                                PrimitiveType.Type.VOID, 
                                context.returnValue(), 
                                ret.dInfo);
                    }

                    break;
                }
                case CALL:
                {
                    CallNode call = (CallNode)node;

                    processNode(call.function);
                    processNode(call.arguments);

                    if (TypeChecker.check(
                            call.function.realType(), 
                            BaseType.Type.FUNCTION, 
                            call.dInfo)) {

                        FunctionType func = (FunctionType)call.function.realType();

                        if (func.arguments.size() ==
                                call.arguments.children().size()) {

                            for (int i = 0; i < func.arguments.size(); ++i) {
                                TypeChecker.check(
                                        func.arguments.get(i).type,
                                        call.arguments.children().get(i).realType(), 
                                        call.dInfo);
                            }
                        }
                    }

                    call.setRealType(
                        ((FunctionType)call.function.realType()).returnValue);
                    break;
                }
                case ELSE:
                {
                    ElseNode n = (ElseNode)node;

                    if(n.block != null) {
                        context.pushScope();
                        processNode(n.block);
                        context.popScope();
                    }

                    break;
                }
                case DEFAULT:
                {
                    DefaultNode n = (DefaultNode)node;

                    processNode(n.block);

                    break;
                }
                case RUN:
                {
                    RunNode n = (RunNode)node;

                    processNode(n.expression);
                    TypeChecker.check(
                            n.expression.realType(), 
                            BaseType.Type.FUNCTION, 
                            n.dInfo);

                    break;
                }
                case INVALID:
                {
                    break;
                }
                case LOCK:
                {
                    LockNode n = (LockNode)node;

                    processNode(n.block);

                    break;
                }
                case TYPE:
                {
                    processType((BaseTypeNode)node);
                    break;
                }
                case BREAK:
                {
                    if (!context.isInSwitch() && !context.isInLoop()) {
                        BreakNode b = (BreakNode) node;

                        throw new InvalidJumpStatementException()
                                .initPosition(b.dInfo.position)
                                .Log("ru.bmstu.iu9.compiler.semantics");
                    }
                    break;
                }
                case CONTINUE:
                    if (!context.isInLoop()) {
                        ContinueNode b = (ContinueNode) node;

                        throw new InvalidJumpStatementException()
                                .initPosition(b.dInfo.position)
                                .Log("ru.bmstu.iu9.compiler.semantics");
                    }
                    break;
                case BARRIER:
                    break;
            }
        } catch (SemanticException ex) {
            throw ex;
        } catch (Exception ex) {
            throw (SemanticException)new NonAnalysisErrorException(ex)
                    .Log("ru.bmstu.iu9.compiler.semantics");
        }
    }  
    
    public BlockNode<BaseNode> tree() {
        return parseTree;
    }
    
    private BlockNode<BaseNode> parseTree;
    private Context context = new Context();
}


final class Context {
    public Context() {
        scopes = new Stack<SymbolTable>();
        global = new SymbolTable();
        scopes.add(global);
    }
    public void enterFunction(FunctionDeclNode function) {
        this.function = (FunctionTypeNode)function.type;
        FunctionSymbol symbol = (FunctionSymbol)get(function.name);
        assert symbol == null;
        
        global = scopes.peek();
        scopes.add(symbol.scope);
        this.inFunction = true;
    }
    public void leaveFunction() {
        assert !this.inFunction;
        
        scopes.clear();
        scopes.add(global);
        this.inFunction = false;
    }
    public BaseType returnValue() {
        return this.inFunction ? 
            ((FunctionType)function.realType()).returnValue : 
            null;
    }
    
    
    public void enterStruct(StructType type) {
        scopes.add(((StructSymbol)get(type.name)).scope);
    }
    public void leaveStruct() {
        popScope();
    }

    
    
    public void add(Symbol symbol) {
        this.scopes.peek().add(symbol);
    }
    public Symbol get(String name) {
        return this.scopes.peek().get(name);
    }
    public void pushScope() {
        SymbolTable tmp = new SymbolTable();
        tmp.setOpenScope(this.scopes.peek());
        
        this.scopes.push(tmp);
    }
    public SymbolTable popScope() {
        return this.scopes.pop();
    }
    public SymbolTable peekScope() {
        return this.scopes.peek();
    }
    public SymbolTable global() {
        return this.global;
    }
    
    public void enterLoop() {
        inLoop = true;
    }
    public void leaveLoop() {
        inLoop = false;
    }
    public void enterSwitch() {
        inSwitch = true;
    }
    public void leaveSwitch() {
        inSwitch = false;
    }
    public boolean isInLoop() {
        return inLoop;
    }
    public boolean isInSwitch() {
        return inSwitch;
    }

    private SymbolTable global;
    private FunctionTypeNode function;
    private Stack<SymbolTable> scopes;
    private boolean inFunction;
    
    public boolean returns = false;
    
    
    private boolean inLoop = false;
    private boolean inSwitch = false;
}