package ru.bmstu.iu9.compiler.semantics;

import com.google.gson.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import ru.bmstu.iu9.compiler.*;
import ru.bmstu.iu9.compiler.syntax.tree.*;

/**
 *
 * @author maggot
 */
class SemanticAnalyser {
    public SemanticAnalyser(BlockNode<BaseNode> parseTree) {
        this.parseTree = parseTree;
    }
    
    public void Analyse() {
        resolveNames(parseTree);
        resolveTypes(parseTree);
        processNode(parseTree);
    }
    public static void main(String[] args) {
        BufferedReader reader = null;
        
        try {
            Gson gson = 
                    new GsonBuilder().
                        registerTypeHierarchyAdapter(
                            BaseNode.class, 
                            new BaseNode.BaseNodeAdapter()).
                        registerTypeHierarchyAdapter(
                            BaseType.class, 
                            new BaseType.TypeAdapter()).
                        create();
            
            reader = new BufferedReader(
                        new FileReader("C:\\Users\\maggot\\Documents\\NetBeansProjects\\ru.bmstu.iu9.compiler\\Front End\\src\\parse_tree.json"));
            
            BlockNode<BaseNode> tree = gson.fromJson(reader, BlockNode.class);
            SemanticAnalyser analyser = new SemanticAnalyser(tree);
            analyser.Analyse();
            
            return;
        } catch(java.io.IOException ex) {
//            ex.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch(java.io.IOException ex) {
//                ex.printStackTrace();
            }
        }
    }
    
    
    
    private void resolveNames(BlockNode<BaseNode> tree) {
        for(BaseNode node : tree) {
            switch(node.nodeType()) {
                case STRUCT_DECL:
                {
                    StructDeclNode s = (StructDeclNode)node;
                    context.global().add(
                        new StructSymbol(
                            s.name, 
                            context.global(), 
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
                    

                    for(VariableDeclNode decl : struct.declarations) {
                        processType(decl.type);
                        scope.add(
                            new VariableSymbol(
                                decl.name, 
                                decl.type.realType()
                            ));
                        decl.setRealType(decl.type.realType());
                    }
                    
                    long size = 0;
                    for(Symbol s : scope) {
                        size += s.type().size;
                    }
                    
                    StructType realType = 
                            new StructType(struct.name, true, size);

                    symbol.setType(realType);
                    
                    break;
                }
                case FUNCTION_DECL:
                {
                    FunctionDeclNode func = (FunctionDeclNode)node;
                    FunctionSymbol symbol = 
                        (FunctionSymbol)context.global().get(func.name);
                    assert symbol == null;
                    
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
                }

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
//                SymbolTable scope = 
//                    ((FunctionSymbol)context.get(f.name)).scope;
                
                for(FunctionTypeNode.ArgumentNode arg : f.arguments) {
                    processType(arg.type);
                    arg.setRealType(arg.type.realType());
//                    scope.add(new VariableSymbol(arg.name, arg.realType()));
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
    
    
    
    
    
    
    
    private void processNode(BaseNode node) {
        switch (node.nodeType()) {
            case BINARY_OPERATION:
            {
                BinaryOperationNode bNode = (BinaryOperationNode)node;
                
                processNode(bNode.leftChild());
                processNode(bNode.rightChild());
                
                TypeChecker.check(
                        bNode.leftChild().realType(), 
                        bNode.rightChild().realType(),
                        bNode.dInfo);
                
                if (bNode.operation().is(
                        BinaryOperationNode.Operation.Bitwise)) {
                    
                    TypeChecker.check(
                        bNode.realType(), 
                        PrimitiveType.Type.INT, 
                        bNode.dInfo);
                }
                
                if (bNode.operation().is(
                        BinaryOperationNode.Operation.Comparison)) {
                    
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
            case UNARY_OPERATION:
            {
                UnaryOperationNode uNode = ((UnaryOperationNode)node);
                
                processNode(uNode.node);
                switch (uNode.operation()) {
                    case POST_INC:
                    case POST_DEC:
                    case PRE_INC:
                    case PRE_DEC:
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
                        uNode.setRealType(
                            ((CastNode)uNode).castingType.realType());
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
                IfNode ifNode = (IfNode)node;
                
                context.pushScope();
                processNode(ifNode.condition);
                processNode(ifNode.block);
                context.popScope();
                if(ifNode.elseNode != null)
                    processNode(ifNode.elseNode);
                
                TypeChecker.check(
                        ifNode.condition.realType(), 
                        PrimitiveType.Type.BOOL, 
                        ((ExpressionNode)ifNode.condition).dInfo);
                break;
            }
            case SWITCH:
            {
                SwitchNode switchNode = ((SwitchNode)node);
                        
                context.pushScope();
                processNode(switchNode.expression);
                processNode(switchNode.cases);
                if(switchNode.defaultNode != null)
                    processNode(switchNode.defaultNode);
                context.popScope();
                
                for (CaseNode caseNode : ((SwitchNode)node).cases) {
                    TypeChecker.check(
                            switchNode.expression.realType(), 
                            caseNode.expression.realType(), 
                            ((ExpressionNode)switchNode.expression).dInfo);
                }
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
                WhileNode cbNode = (WhileNode)node;
                
                context.pushScope();
                processNode(cbNode.expression);
                processNode(cbNode.block);
                context.popScope();
                
                TypeChecker.check(
                        cbNode.expression.realType(), 
                        PrimitiveType.Type.BOOL,
                        ((ExpressionNode)cbNode.expression).dInfo);
                break;
            }
            case DO_WHILE:
            {
                DoWhileNode cbNode = (DoWhileNode)node;
                
                context.pushScope();
                processNode(cbNode.block);
                processNode(cbNode.expression);
                context.popScope();
                
                TypeChecker.check(
                        cbNode.expression.realType(), 
                        PrimitiveType.Type.BOOL,
                        ((ExpressionNode)cbNode.expression).dInfo);
                break;
            }
            case BLOCK:
            {
//                context.pushScope();
                for(BaseNode child : (BlockNode<BaseNode>)node) {
                    processNode(child);
                }
//                context.popScope();
                break;
            }
            case BLOCK_DECL:
            {
                context.pushScope();
                for(DeclNode child : (BlockDeclNode<?>)node) {
                    processNode(child);
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
                    Logger.logUndeclaredVarialbe(var.name, var.dInfo.position);
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
              
                processNode(function.block);

                context.leaveFunction();
                break;
            }
            case RETURN:
            {
                ReturnNode ret = (ReturnNode)node;
                
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
                    
                    if (func.arguments().size() == call.arguments.children().size()) {
                        for (int i = 0; i < func.arguments().size(); ++i) {
                            TypeChecker.check(
                                    func.arguments().get(i).type, 
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
                
                context.pushScope();
                processNode(n.block);
                context.popScope();
                
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
            /*
            case ARGUMENT:
            {
                FunctionTypeNode.ArgumentNode a = 
                        (FunctionTypeNode.ArgumentNode)node;
                
                processNode(a.type);
                a.setRealType(a.type.realType());
                
                context.add(new VariableSymbol(a.name, a.realType()));
                
                break;
            }
            */
            case BREAK:
            case CONTINUE:
            case BARRIER:
                break;
        }
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

    private SymbolTable global;
    private FunctionTypeNode function;
    private Stack<SymbolTable> scopes;
    private boolean inFunction;
}