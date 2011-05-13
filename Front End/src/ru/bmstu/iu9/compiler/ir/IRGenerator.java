/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.bmstu.iu9.compiler.ir;

import com.google.gson.*;
import java.io.*;
import ru.bmstu.iu9.compiler.*;
import ru.bmstu.iu9.compiler.semantics.*;
import ru.bmstu.iu9.compiler.syntax.tree.*;

/**
 *
 * @author maggot
 */
public class IRGenerator {
    public IRGenerator(BlockNode<BaseNode> parseTree) {
        this.parseTree = parseTree;
        this.code = new Code();
        this.varTable = new VariablesTable();
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
            
            IRGenerator generator = new IRGenerator(analyser.tree());
            generator.generate();
            
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
    
    public void generate() {
        generate(this.parseTree);
        return;
    }
    
    
    private VariableOperand generateArrayIndex(BinaryOperationNode node) {
        VariableOperand array = (VariableOperand) generate(node.leftChild());
        VariableOperand index = (VariableOperand) generate(node.rightChild());

        TmpVariableOperand address = 
            new TmpVariableOperand(new PointerType(node.realType()), varTable);

        code.addStatement(
            new ArrayIndexStatement(address, array, index)
        );

        return address;
    }
    
    private VariableOperand generateStructField(BinaryOperationNode node) {
        VariableOperand struct = (VariableOperand) generate(node.leftChild());
        VariableOperand field = (VariableOperand) generate(node.rightChild());

        TmpVariableOperand address = 
            new TmpVariableOperand(new PointerType(node.realType()), varTable);

        code.addStatement(
            new MemberSelectStatement(address, struct, field)
        );

        return address;
    }
    
    
    /**
     * Генерирует код для левой части операции присваивания.
     * 
     * Генерирует код для левой части операции присваивания. При этом возможны
     * два случая:
     * <dl>
     *   <dt>
     *     Присваивание происходит переменной
     *   </dt>
     *   <dd>
     *     Возвращается операнд, соответствующий этой переменной. Далее должна
     *     быть сгенерирована инструкция прямого присваивания.
     *   </dd>
     *   <dt>
     *     Присваивание происходит элементу массива, полю структуры или
     *     переменной по адресу
     *   </dt>
     *   <dd>
     *     Генерирует инструкцию индексации в массиве, доступа к полю структуры.
     *     Обе эти инструкции помещают во временную переменную tmp адрес в 
     *     памяти. Возвращает операнд, соответствующий tmp или адресу переменной
     *     в третьем случае. Далее должна быть сгенерирована инструкция 
     *     косвенного присваивания.
     *   </dd>
     * </dl>
     * 
     * @param node Узел, соответствующий левой части присваивания
     * @return Операнд, в который помещена переменная или адрес в памяти
     */
    private VariableOperand generateLeftHandValue(BaseNode node) {
        switch(node.nodeType()) {
            case VARIABLE:
            {
                VariableLeaf l = (VariableLeaf) node;
                
                return 
                    new NamedVariableOperand(varTable, varTable.get(l.name));
            }
            case BINARY_OPERATION:
            {
                BinaryOperationNode o = (BinaryOperationNode) node;
                
                switch(o.operation()) {
                    case ARRAY_ELEMENT:
                        return generateArrayIndex(o);
                    case MEMBER_SELECT:
                        return generateStructField(o);
                    default:
                        // @todo Report an error
                        break;
                }
                
                break;
            }
            case UNARY_OPERATION:
            {
                UnaryOperationNode o = (UnaryOperationNode) node;
                
                if(o.is(UnaryOperationNode.Operation.DEREF)) {
                    VariableOperand variable = 
                        (VariableOperand) generate(o.node);
                    
                    return variable;
                } else {
                    // @todo Report an error
                }
                
                break;
            }
            default:
                // @todo Report an error
                break;
        }
        return null;
    }
    
    private Operand generateRightHandValue(BaseNode node) {
        return generate(node);
    }
    
    
    private VariableOperand generateAssignment(BinaryOperationNode node) {
        VariableOperand lhv = generateLeftHandValue(node.leftChild());
        Operand rhv = generateRightHandValue(node.rightChild());
        
        if(node.leftChild() instanceof VariableLeaf) {
            code.addStatement(new AssignmentStatement(lhv, rhv));
            
            return lhv;
        } else {
            code.addStatement(new IndirectAssignmentStatement(lhv, rhv));
            
            TmpVariableOperand value = 
                new TmpVariableOperand(
                    ((PointerType)lhv.type()).pointerType,
                    varTable);
            
            code.addStatement(
                new UnaryOperationStatement(
                    value, 
                    lhv,
                    UnaryOperationStatement.Operation.DEREF)
            );
            
            return value;
        }       
    }
    
    
    
    private void generateReturn(ReturnNode node) {
        code.addStatement(new ReturnStatement());
    }
    private void generateFunction(FunctionDeclNode node) {
        for(ru.bmstu.iu9.compiler.syntax.tree.Statement n : node.block) {
            generate(n.getNode());
        }
    }
    private void generateBlock(BlockNode<BaseNode> node) {
        for(BaseNode n : (BlockNode<BaseNode>)node) {
            generate(n);
        }
    }
    
    private void declareVariable(VariableDeclNode node) {
        NamedVariable variable = new NamedVariable(node.name, node.realType());

        varTable.add(variable);

        if(node.value != null) {
            Operand value = generate(node.value);

            code.addStatement(
                new AssignmentStatement(
                    new NamedVariableOperand(varTable,
                        varTable.get(variable.name)),
                    value)
            );
        }
    }
    
    
    private Operand generate(BaseNode node) {
        switch(node.nodeType()) {
            case RETURN:
            {
                generateReturn((ReturnNode) node);
                break;
            }
            case FUNCTION_DECL:
            {
                generateFunction((FunctionDeclNode) node);
                
                break;
            }
            case BLOCK:
            {
                generateBlock((BlockNode<BaseNode>) node);
                break;
            }
            case BLOCK_DECL:
            {
                for(VariableDeclNode decl : 
                        (BlockDeclNode<VariableDeclNode>) node) {
                    
                    generate(decl);
                }
                break;
            }
            case VARIABLE:
            {
                return new NamedVariableOperand(
                    varTable, 
                    varTable.get(((VariableLeaf)node).name)
                );
            }
            case VAR_DECL:
            {
                declareVariable((VariableDeclNode) node);
                
                break;
            }
            case UNARY_OPERATION:
            {
                UnaryOperationNode u = (UnaryOperationNode)node;
                
                VariableOperand op = (VariableOperand)generate(u.node);
                
                UnaryOperationStatement.Operation operation = null;
                switch(u.operation()) {
                    case POST_INC:
                    {
                        operation = UnaryOperationStatement.Operation.POST_INC;
                        break;
                    }
                    case POST_DEC:
                    {
                        operation = UnaryOperationStatement.Operation.POST_DEC;
                        break;
                    }
                    case MINUS:
                    {
                        operation = UnaryOperationStatement.Operation.MINUS;
                        break;
                    }
                    case PLUS:
                    {
                        operation = UnaryOperationStatement.Operation.PLUS;
                        break;
                    }
                    case REF:
                    {
                        operation = UnaryOperationStatement.Operation.REF;
                        break;
                    }
                    case DEREF:
                    {
                        operation = UnaryOperationStatement.Operation.DEREF;
                        break;
                    }
                    case PRE_DEC:
                    {
                        operation = UnaryOperationStatement.Operation.PRE_DEC;
                        break;
                    }
                    case PRE_INC:
                    {
                        operation = UnaryOperationStatement.Operation.PRE_INC;
                        break;
                    }
                    case CAST:
                    {
                        operation = UnaryOperationStatement.Operation.CAST;
                        break;
                    }
                }
                
                TmpVariableOperand result = 
                    new TmpVariableOperand(
                        u.realType(),
                        varTable
                    );
                
                code.addStatement(
                    new UnaryOperationStatement(
                        result,
                        op,
                        operation)
                );
                
                break;
            }
            case BINARY_OPERATION:
            {
                BinaryOperationNode b = (BinaryOperationNode)node;
                
                VariableOperand left = (VariableOperand)generate(b.leftChild());
                VariableOperand right = 
                    (VariableOperand) generate(b.rightChild());
                
                switch(b.operation()) {
                    case ARRAY_ELEMENT:
                    {
                        TmpVariableOperand result = 
                            new TmpVariableOperand(
                                b.realType(),
                                varTable
                            );
                        
                        code.addStatement(
                            new ArrayIndexStatement(result, left, right)
                        );
                        
                        return result;
                    }
                    /**
                     * tmp1 = ((a.i).j);
                     * a - address or value?
                     * a [MEMBER_SELECT] i - address or value?
                     * (a.i) [MEMBER_SELECT] j - address or value?
                     */
                    case MEMBER_SELECT:
                    {
                        Operand struct = generate(b.leftChild());
                        //Code field = generate(b.rightChild());
                        
                        TmpVariable tmp = new TmpVariable(
                                new PointerType(b.leftChild().realType(), true)
                            );
                        
                        /*Statement stmt = 
                            new RefStatement(
                                tmp,
                                
                            );*/
                        break;
                    }
                    case MINUS: 
                    case DIV:
                    case MUL:
                    case MOD:
                    case PLUS: 
                    case BITWISE_SHIFT_RIGHT:
                    case BITWISE_SHIFT_LEFT:
                    case GREATER:
                    case GREATER_OR_EQUAL:
                    case LESS:
                    case LESS_OR_EUQAL:
                    case NOT_EQUAL:
                    case EQUAL:
                    case BITWISE_AND:
                    case BITWISE_XOR:
                    case BITWISE_OR:
                    case BOOL_AND:
                    case BOOL_OR:
                    {
                        BinaryOperationStatement.Operation operation = null;
                        switch(b.operation()) {
                            case MINUS: 
                                operation = 
                                    BinaryOperationStatement.Operation.MINUS;
                                break;
                            case DIV:
                                operation = 
                                    BinaryOperationStatement.Operation.DIV;
                                break;
                            case MUL:
                                operation = 
                                    BinaryOperationStatement.Operation.MUL;
                                break;
                            case MOD:
                                operation = 
                                    BinaryOperationStatement.Operation.MOD;
                                break;
                            case PLUS: 
                                operation = 
                                    BinaryOperationStatement.Operation.PLUS;
                                break;
                            case BITWISE_SHIFT_RIGHT:
                                operation = 
                                    BinaryOperationStatement.Operation.BITWISE_SHIFT_RIGHT;
                                break;
                            case BITWISE_SHIFT_LEFT:
                                operation = 
                                    BinaryOperationStatement.Operation.BITWISE_SHIFT_LEFT;
                                break;
                            case GREATER:
                                operation = 
                                    BinaryOperationStatement.Operation.GREATER;
                                break;
                            case GREATER_OR_EQUAL:
                                operation = 
                                    BinaryOperationStatement.Operation.GREATER_OR_EQUAL;
                                break;
                            case LESS:
                                operation = 
                                    BinaryOperationStatement.Operation.LESS;
                                break;
                            case LESS_OR_EUQAL:
                                operation = 
                                    BinaryOperationStatement.Operation.LESS_OR_EUQAL;
                                break;
                            case NOT_EQUAL:
                                operation = 
                                    BinaryOperationStatement.Operation.NOT_EQUAL;
                                break;
                            case EQUAL:
                                operation = 
                                    BinaryOperationStatement.Operation.EQUAL;
                                break;
                            case BITWISE_AND:
                                operation = 
                                    BinaryOperationStatement.Operation.BITWISE_AND;
                                break;
                            case BITWISE_XOR:
                                operation = 
                                    BinaryOperationStatement.Operation.BITWISE_XOR;
                                break;
                            case BITWISE_OR:
                                operation = 
                                    BinaryOperationStatement.Operation.BITWISE_OR;
                                break;
                            case BOOL_AND:
                                operation = 
                                    BinaryOperationStatement.Operation.BOOL_AND;
                                break;
                            case BOOL_OR:
                                operation = 
                                    BinaryOperationStatement.Operation.BOOL_OR;
                                break;
                        }
                        return binaryOperationStatement(
                                left,
                                right,
                                b.realType(),
                                operation);
                    }
                    case BITWISE_AND_ASSIGN:
                    case BITWISE_XOR_ASSIGN: 
                    case BITWISE_SHIFT_RIGHT_ASSIGN:
                    case BITWISE_SHIFT_LEFT_ASSIGN:
                    case BITWISE_OR_ASSIGN:
                    case MOD_ASSIGN:
                    case DIV_ASSIGN:
                    case MUL_ASSIGN:
                    case MINUS_ASSIGN: 
                    case PLUS_ASSIGN:
                    {
                        BinaryOperationStatement.Operation operation = null;
                        switch(b.operation()) {
                            case MINUS_ASSIGN: 
                                operation = 
                                    BinaryOperationStatement.Operation.MINUS;
                                break;
                            case DIV_ASSIGN:
                                operation = 
                                    BinaryOperationStatement.Operation.DIV;
                                break;
                            case MUL_ASSIGN:
                                operation = 
                                    BinaryOperationStatement.Operation.MUL;
                                break;
                            case MOD_ASSIGN:
                                operation = 
                                    BinaryOperationStatement.Operation.MOD;
                                break;
                            case PLUS_ASSIGN: 
                                operation = 
                                    BinaryOperationStatement.Operation.PLUS;
                                break;
                            case BITWISE_SHIFT_RIGHT_ASSIGN:
                                operation = 
                                    BinaryOperationStatement.Operation.BITWISE_SHIFT_RIGHT;
                                break;
                            case BITWISE_SHIFT_LEFT_ASSIGN:
                                operation = 
                                    BinaryOperationStatement.Operation.BITWISE_SHIFT_LEFT;
                                break;
                            case BITWISE_AND_ASSIGN:
                                operation = 
                                    BinaryOperationStatement.Operation.BITWISE_AND;
                                break;
                            case BITWISE_XOR_ASSIGN:
                                operation = 
                                    BinaryOperationStatement.Operation.BITWISE_XOR;
                                break;
                            case BITWISE_OR_ASSIGN:
                                operation = 
                                    BinaryOperationStatement.Operation.BITWISE_OR;
                                break;
                        }
                        
                        Operand result = binaryOperationStatement(
                                left,
                                right,
                                b.realType(),
                                operation);
                        
                        code.addStatement(
                            new AssignmentStatement(left, result)
                        );
                    }
                    case ASSIGN:
                    {
                        code.addStatement(new AssignmentStatement(left, right));
                        break;
                    }
                    
                }
                
                break;
            }
            case IF:
            {
                IfNode i = (IfNode) node;
                
                generateIf(i);
                
                break;
            }
            case ELSE:
            {
                ElseNode e = (ElseNode) node;
                for(ru.bmstu.iu9.compiler.syntax.tree.Statement stmt : 
                        e.block) {
                    
                    generate(stmt.getNode());
                }
                break;
            }
            case CONSTANT:
            {
                ConstantLeaf constant = (ConstantLeaf)node;
                
                switch(constant.constantType()) {
                    case INT:
                    {
                        return new ConstantOperand(
                            constant.realType(),
                            ((IntegerConstantLeaf)constant).value);
                    }
                    case DOUBLE:
                    {
                        return new ConstantOperand(
                            constant.realType(),
                            ((DoubleConstantLeaf)constant).value);
                    }
                    case CHAR:
                    {
                        return new ConstantOperand(
                            constant.realType(),
                            ((CharConstantLeaf)constant).value);
                    }
                    case BOOL:
                    {
                        return new ConstantOperand(
                            constant.realType(),
                            ((BoolConstantLeaf)constant).value);
                    }
                }
                
                break;
            }
            default:
            {
                break;
            }
        }
        return null;
    }
    
    private void generateIf(IfNode node) {
        VariableOperand condition = (VariableOperand) generate(node.condition);
        
        Label labelTrue = new Label();
        Label labelFalse = new Label();
        
        code.addStatement(new IfStatement(condition, labelTrue, labelFalse));
        
        labelTrue.setIndex(code.nextIndex());
        
        generate(node.block);
        
        if(node.elseNode != null) {
            Label endOfBlock = new Label();
            code.addStatement(new GoToStatement(endOfBlock));
            labelFalse.setIndex(code.nextIndex());
            
            generate(node.elseNode);
            endOfBlock.setIndex(code.nextIndex());
        } else {
            labelFalse.setIndex(code.nextIndex());
        }
        
        code.addStatement(new ReturnStatement());
    }
    
    private Operand binaryOperationStatement(
            Operand left,
            Operand right,
            BaseType type,
            BinaryOperationStatement.Operation operation) {
        
        Operand result = new TmpVariableOperand(type, varTable);
        code.addStatement(
            new BinaryOperationStatement(
                left,
                right,
                result,
                operation)
        );

        return result;
    }
    
    private VariablesTable varTable;
    private final BlockNode<BaseNode> parseTree;
    private Code code;
}