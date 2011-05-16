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
    
    private Operand generateBoolExpression(BinaryOperationNode node) {
        if (node.is(BinaryOperationNode.Operation.Bool)) {
            Label rightCond = new Label();
            Label trueL = new Label();
            Label falseL = new Label();
            Label next = new Label();
                    
            switch(node.operation()) {
                /**
                 * a && b раскрывает в:
                 *  
                 *        tmp1 = generate(a);
                 *        tmp2 = generate(b);
                 *  
                 *        tmp3 = NOT tmp1;
                 *        tmp4 = NOT tmp2;
                 *  
                 *        if(tmp3) 
                 *          goto false;
                 *        else 
                 *          goto cond;
                 * cond:  if(tmp4) 
                 *          goto false;
                 *        else
                 *          goto true;
                 * true:  tmp5 = TRUE;
                 *        goto next;
                 * false: tmp5 = FALSE;
                 * next:  usage of tmp5
                 */
                case BOOL_AND:
                {
                    Operand left = generate(node.leftChild());   // tmp1
                    Operand right = generate(node.rightChild()); // tmp2
                    
                    // tmp3
                    TmpVariableOperand first = 
                        new TmpVariableOperand(
                            new PrimitiveType(
                                PrimitiveType.Type.BOOL, 
                                true
                            ),
                            varTable
                        );
                    
                    // tmp4
                    TmpVariableOperand second = 
                        new TmpVariableOperand(
                            new PrimitiveType(
                                PrimitiveType.Type.BOOL, 
                                true
                            ),
                            varTable
                        );
                    
                    // tmp3 = !a
                    code.addStatement(
                        new UnaryOperationStatement(
                            first, 
                            left,
                            UnaryOperationStatement.Operation.NOT)
                    );
                    // tmp4 = !b
                    code.addStatement(
                        new UnaryOperationStatement(
                            second, 
                            right,
                            UnaryOperationStatement.Operation.NOT)
                    );
                    
                    // !a ? goto false : goto nextCondition
                    code.addStatement(
                        new IfGoToStatement(first, falseL, rightCond)
                    );
                    // !b ? goto false : goto true
                    rightCond.setIndex(code.nextIndex());
                    code.addStatement(
                        new IfGoToStatement(second, falseL, trueL)
                    );
                    
                    // tmp5
                    TmpVariableOperand result = 
                        new TmpVariableOperand(
                            new PrimitiveType(PrimitiveType.Type.BOOL, true),
                            varTable
                        );
                    
                    // trueL:
                    trueL.setIndex(code.nextIndex());
                    
                    // trueL: tmp5 = true
                    code.addStatement(
                        new AssignmentStatement(
                            result,
                            new ConstantOperand(
                                new PrimitiveType(PrimitiveType.Type.BOOL),
                                Boolean.TRUE
                            )
                        )
                    );
                    
                    // goto next
                    code.addStatement(new GoToStatement(next));
                    
                    // falseL:
                    falseL.setIndex(code.nextIndex());
                    
                    // falseL: tmp5 = false
                    code.addStatement(
                        new AssignmentStatement(
                            result,
                            new ConstantOperand(
                                new PrimitiveType(PrimitiveType.Type.BOOL),
                                Boolean.FALSE
                            )
                        )
                    );
                    
                    // next:
                    next.setIndex(code.nextIndex());
                    
                    return result;
                }
                case BOOL_OR:
                {
                    Operand left = generate(node.leftChild());
                    Operand right = generate(node.rightChild());
                    
                    code.addStatement(
                        new IfGoToStatement(left, trueL, rightCond)
                    );
                    rightCond.setIndex(code.nextIndex());
                    code.addStatement(
                        new IfGoToStatement(right, trueL, falseL)
                    );
                    
                    TmpVariableOperand result = 
                        new TmpVariableOperand(
                            new PrimitiveType(PrimitiveType.Type.BOOL, true),
                            varTable
                        );
                    
                    trueL.setIndex(code.nextIndex());
                    
                    code.addStatement(
                        new AssignmentStatement(
                            result,
                            new ConstantOperand(
                                new PrimitiveType(PrimitiveType.Type.BOOL),
                                Boolean.TRUE
                            )
                        )
                    );
                    
                    code.addStatement(new GoToStatement(next));
                    
                    falseL.setIndex(code.nextIndex());
                    
                    code.addStatement(
                        new AssignmentStatement(
                            result,
                            new ConstantOperand(
                                new PrimitiveType(PrimitiveType.Type.BOOL),
                                Boolean.FALSE
                            )
                        )
                    );
                    
                    next.setIndex(code.nextIndex());
                    
                    return result;
                }
                default:
                    // @todo Report an error
                    return null;
            }
        } else {
            // @todo Report an error
            return null;
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
            case BREAK:
            {
                code.addStatement(
                    new GoToStatement(ControlStructureInfo.endOfBlockLabel)
                );
                break;
            }
            case CONTINUE:
            {
                code.addStatement(
                    new GoToStatement(ControlStructureInfo.conditionLabel)
                );
                break;
            }
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
                
                TmpVariableOperand result = 
                    new TmpVariableOperand(
                        u.realType(),
                        varTable
                    );
                
                code.addStatement(
                    new UnaryOperationStatement(
                        result,
                        op,
                        UnaryOperationStatement.operation(u.operation()))
                );
                
                break;
            }
            case BINARY_OPERATION:
            {
                BinaryOperationNode b = (BinaryOperationNode)node;
                
                switch(b.operation()) {
                    case ARRAY_ELEMENT:
                    {
                        return generateArrayIndex(b);
                    }
                    /**
                     * tmp1 = ((a.i).j);
                     * a - address or value?
                     * a [MEMBER_SELECT] i - address or value?
                     * (a.i) [MEMBER_SELECT] j - address or value?
                     */
                    case MEMBER_SELECT:
                    {
                        return generateStructField(b);
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
                    {
                        VariableOperand left =
                            (VariableOperand)generate(b.leftChild());
                        VariableOperand right = 
                            (VariableOperand) generate(b.rightChild());
                        
                        return generateBinaryOperation(
                            left,
                            right,
                            b.realType(),
                            BinaryOperationStatement.operation(b.operation()));
                    }
                    case BOOL_AND:
                    case BOOL_OR:
                    {
                        return generateBoolExpression(b);
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
                        VariableOperand lhv = 
                            generateLeftHandValue(b.leftChild());
                        
                        VariableOperand left =
                            (VariableOperand)generate(b.leftChild());
                        VariableOperand right = 
                            (VariableOperand) generate(b.rightChild());
                        
                        if (left.type().is(PrimitiveType.Type.POINTER)) {
                            TmpVariableOperand value = 
                                new TmpVariableOperand(
                                    ((PointerType)left.type()).pointerType,
                                    varTable);
                            
                            code.addStatement(
                                new UnaryOperationStatement(
                                    left, 
                                    value,
                                    UnaryOperationStatement.Operation.DEREF
                                )
                            );
                            
                            left = value;
                        }
                        
                        Operand result = generateBinaryOperation(
                            left,
                            right,
                            b.realType(),
                            BinaryOperationStatement.operation(b.operation())
                        );
                        
                        
                        if(b.leftChild() instanceof VariableLeaf) {
                            code.addStatement(
                                new AssignmentStatement(lhv, result)
                            );
                        } else {
                            code.addStatement(
                                new IndirectAssignmentStatement(lhv, result)
                            );
                        }
                        
                        break;
                    }
                    case ASSIGN:
                    {
                        generateAssignment(b);                        
                        break;
                    }
                    
                }
                
                break;
            }
            case IF:
                generateIf((IfNode) node);
                break;
            case WHILE:
                generateWhile((WhileNode) node);
                break;
            case DO_WHILE:
                generateDoWhile((DoWhileNode) node);
                break;
            case SWITCH:
                generateSwitch((SwitchNode) node);
                break;
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
    
    private void generateSwitch(SwitchNode node) {
        Operand expr = generate(node.expression);
        
        ControlStructureInfo.renew();
        Label defaultLabel = new Label();
        
        for(CaseNode c : node.cases) {
            Label caseL = new Label();
            
            Operand caseExpr = generate(c.expression);
            
            TmpVariableOperand condition = 
                new TmpVariableOperand(
                    new PrimitiveType(PrimitiveType.Type.BOOL),
                    varTable
                );
            
            code.addStatement(
                new BinaryOperationStatement(
                    expr,
                    caseExpr,
                    condition,
                    BinaryOperationStatement.Operation.EQUAL)
            );
            
            code.addStatement(
                new IfGoToStatement(condition, caseL, defaultLabel)
            );

            caseL.setIndex(code.nextIndex());

            generate(c.block);
            
            code.addStatement(new GoToStatement(defaultLabel));
        }
        
        if(node.defaultNode != null) {
            defaultLabel.setIndex(code.nextIndex());
            
            generate(node.defaultNode.block);
            ControlStructureInfo.endOfBlockLabel.setIndex(code.nextIndex());
        } else {
            ControlStructureInfo.endOfBlockLabel.setIndex(code.nextIndex());
            defaultLabel.setIndex(code.nextIndex());
        }
    }
    
    private void generateDoWhile(DoWhileNode node) {
        Operand condition = generate(node.expression);
        
        Label trueL = new Label();
        Label falseL = new Label();
        
        trueL.setIndex(code.nextIndex());
        
        generate(node.block);
        
        code.addStatement(
            new IfGoToStatement(condition, trueL, falseL)
        );
        
        falseL.setIndex(code.nextIndex());
    }
    
    private void generateWhile(WhileNode node) {
        Operand condition = generate(node.expression);
        
        ControlStructureInfo.renew();
        Label blockLabel = new Label();
        
        ControlStructureInfo.conditionLabel.setIndex(code.nextIndex());
        
        code.addStatement(
            new IfGoToStatement(
                condition, 
                blockLabel, 
                ControlStructureInfo.endOfBlockLabel)
        );
        
        blockLabel.setIndex(code.nextIndex());
        
        generate(node.block);
        
        code.addStatement(
            new GoToStatement(ControlStructureInfo.conditionLabel));
        
        ControlStructureInfo.endOfBlockLabel.setIndex(code.nextIndex());
    }
    
    private void generateIf(IfNode node) {
        Operand condition = generate(node.condition);
        
        Label trueL = new Label();
        Label falseL = new Label();
        
        code.addStatement(
            new IfGoToStatement(condition, trueL, falseL)
        );
        
        trueL.setIndex(code.nextIndex());
        
        generate(node.block);
        
        if(node.elseNode != null) {
            Label endOfBlock = new Label();
            code.addStatement(new GoToStatement(endOfBlock));
            falseL.setIndex(code.nextIndex());
            
            generate(node.elseNode);
            endOfBlock.setIndex(code.nextIndex());
        } else {
            falseL.setIndex(code.nextIndex());
        }
        
//        code.addStatement(new ReturnStatement());
    }
    
    private Operand generateBinaryOperation(
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
    
    
    private static class ControlStructureInfo {
        public static void renew() {
            conditionLabel = new Label();
            endOfBlockLabel = new Label();
        }
        
        public static Label conditionLabel;
        public static Label endOfBlockLabel;
    }
}
