package ru.bmstu.iu9.compiler.ir;

import ru.bmstu.iu9.compiler.ir.type.BaseType;

/**
 * Интерфейс для всех инструкций в трехадресном коде.
 * 
 * @author anton.bobukh
 */
interface Operand extends Cloneable {    
    BaseType type();
}

/**
 * Абстрактный базовый класс для операндов типа переменная. Связан с 
 * {@link ru.bmstu.iu9.compiler.ir.Variable переменной} через таблицу, в которой
 * она расположена, и ключ в этой таблице.
 * 
 * @author anton.bobukh
 */
abstract class VariableOperand implements Operand {
    protected VariableOperand(VariablesTable table, long number) {
        this.table = table;
        this.number = number;
    }

    /**
     * Открывает доступ к типу переменной, с которой связан операнд.
     * 
     * @return Тип переменной
     */
    public
    BaseType type() {
        return this.table.get(number).type; 
    }

    protected final VariablesTable table;
    protected final long number;
}

/**
 * Операнд, представленный именованной переменной.
 * 
 * @author anton.bobukh
 */
class NamedVariableOperand extends VariableOperand {
    /**
     * Добавляет новую 
     * {@link ru.bmstu.iu9.compiler.ir.NamedVariable именованную переменную} в 
     * таблицу и создает связанный с ней операнд.
     * 
     * @param name Имя переменной
     * @param type Тип переменной
     * @param table Таблица, в которую будет добавлена именованая переменная
     */
    public NamedVariableOperand(
            String name, 
            BaseType type, 
            VariablesTable table) {
        
        super(table, table.add(new NamedVariable(name, type)));
    }
    /**
     * Создает операнд, связанный с 
     * {@link ru.bmstu.iu9.compiler.ir.NamedVariable именованной переменной}.
     * 
     * @param table Таблица, в которой расположена переменная
     * @param number Ключ, связанный с переменной
     */
    public NamedVariableOperand(VariablesTable table, long number) {
        super(table, number);
    }

    /**
     * Предоставляет доступ к имени переменной.
     * 
     * @return Имя переменной
     */
    public String name() { 
        return ((NamedVariable)this.table.get(number)).name;
    }

    /**
     * Создает копию операдна для использования в другой инструкции.
     * 
     * @return Новый операнд, связанный с той же переменной
     */
    public Object clone() {
        return new NamedVariableOperand(this.table, this.number);
    }
    
    @Override
    public String toString() {
        return name();
    }
}

/**
 * Операнд, представленный безымянной временной переменной.
 * 
 * @author anton.bobukh
 */
class TmpVariableOperand extends VariableOperand {
    /**
     * Добавляет новую 
     * {@link ru.bmstu.iu9.compiler.ir.TmpVariable временную переменную} в 
     * таблицу и создает связанный с ней операнд.
     * 
     * @param type Тип переменной
     * @param table Таблица, в которую будет добавлена переменная
     */
    public TmpVariableOperand(BaseType type, VariablesTable table) {
        super(table, table.add(new TmpVariable(type)));
    }
    /**
     * Создает операнд, связанный с 
     * {@link ru.bmstu.iu9.compiler.ir.TmpVariable временной переменной}.
     * 
     * @param table Таблица, в которой расположена переменная
     * @param number Ключ, связанный с переменной
     */
    private TmpVariableOperand(VariablesTable table, long number) {
        super(table, number);
    }

    /**
     * Создает копию операдна для использования в другой инструкции.
     * 
     * @return Новый операнд, связанный с той же переменной
     */
    public Object clone() {
        return new TmpVariableOperand(this.table, this.number);
    }
    
    @Override
    public String toString() {
        return "tmp" + number;
    }
}


/**
 * Операнд, представленный константой.
 * 
 * @author anton.bobukh
 */
class ConstantOperand implements Operand {
    /**
     * Создает операнд для константы.
     * 
     * @param type Тип константы
     * @param value Значение константы
     */
    public ConstantOperand(BaseType type, Object value) {
        this.type = type;
        this.value = value;
    }
    
    /**
     * Предоставляет доступ к типу константы
     * 
     * @return Тип константы
     */
    public BaseType type() {
        return this.type;
    }
    
    /**
     * Создает копию операдна для использования в другой инструкции.
     * 
     * @return Новый операнд, связанный с той же переменной
     */
    public Object clone() {
        return new ConstantOperand(this.type, this.value);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
    
    /**
     * Значение константы
     */
    public final Object value;
    private final BaseType type;
}