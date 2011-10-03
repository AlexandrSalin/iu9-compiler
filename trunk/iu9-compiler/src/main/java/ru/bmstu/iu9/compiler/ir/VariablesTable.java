package ru.bmstu.iu9.compiler.ir;

import java.util.*;

import ru.bmstu.iu9.compiler.ir.type.BaseType;


/**
 * Класс реализует хранилище для именованных и временных переменных. Доступ к
 * переменным осуществляется через ключ, представляемый целым числом, которое 
 * присваивается переменной при добавлении ее в таблицу.
 * 
 * @author anton.bobukh
 */
class VariablesTable {
    /**
     * Создает новую пустую таблицу переменных.
     */
    public VariablesTable() {
        vars = new HashMap<Long, Variable>();
    }
    
    /**
     * Получает переменную по имени.
     * 
     * @param name Имя переменной
     * @return Переменная с заданным именем
     * 
     * @tothink Действительно ли нужен этот медот?
     */
    public Long get(String name) {
        for(Map.Entry<Long, Variable> var : vars.entrySet()) {
            if (var.getValue() instanceof NamedVariable && 
                ((NamedVariable)var.getValue()).name.equals(name))
                return var.getKey();
        }
        return null;
    }
    /**
     * Возвращает переменную по ключу.
     * 
     * @param number Ключ, связанный с переменной
     * @return Переменная, связанная с ключом
     */
    public Variable get(long number) {
        return vars.get(number);
    }
    
    /**
     * Добавляет новую переменную в таблицу.
     * 
     * @param variable Переменная, которая должна быть добавлена в таблицу
     * @return Уникальный номер, присвоенный добавленной переменной
     */
    public long add(Variable variable) {
        long number = numerator.next();
        vars.put(number, variable);
        
        return number;
    }
    
    private final Iterator<Long> numerator = 
            new Iterator<Long>() {

                public boolean hasNext() {
                    return true;
                }


                public Long next() {
                    return ++counter;
                }


                public void remove() {
                    throw 
                        new UnsupportedOperationException("Not supported yet.");
                }
                
                private long counter = 0;
            };
    
    private final Map<Long, Variable> vars;
}



/**
 * Абстрактный базовый класс для всех перменных. Содержит в себе информацию о
 * типе переменной.
 * 
 * @author anton.bobukh
 */
abstract class Variable {
    /**
     * Класс предоставляет информацию об области видимости переменной. Область
     * видимости представляется парой <first, last>, где:
     * <dl>
     *   <dd>first</dd>
     *   <dt>
     *     Номер инструкции в IR, начиная с которой видна переменная
     *   </dt>
     *   <dd>last</dd>
     *   <dt>
     *     Номер инструкции в IR, после которой переменная не видна
     *   </dt>
     * </dl>
     */
    public class Scope {
        public Scope() { 
            this.first = this.last = null;
        }
        public Scope(long first, long last) {
            this.first = first;
            this.last = last;
        }
        
        public Long first() {
            return this.first;
        }
        public Long last() {
            return this.last;
        }
        public void setFirst(long first) {
            this.first = first;
        }
        public void setLast(long last) {
            this.last = last;
        }
        
        private Long first;
        private Long last;
    }
    
    /**
     * Класс предоставляет информацию о выравнивание переменной в памяти. Эта
     * информация представляется в виде пары <offset, n>, исходя из формулы 
     * <code>address % 2^n = o</code>, где:
     * <dl>
     *   <dt>address</dt>
     *   <dd>
     *     Адрес переменной в памяти
     *   </dd>
     *   <dt>n</dt>
     *   <dd>
     *     Значение, характеризующее границу, по которой выравнивается переменная
     *   </dd>
     *   <dt>o</dt>
     *   <dd>
     *     Смещение переменной относительно адреса address
     *   </dd>
     * </dl>
     */
    public class AlignmentInfo {
        public AlignmentInfo(long offset, int n) {
            this.offset = offset;
            this.n = n;
        }
        
        public final long offset;
        public final int n;
    }
    
    protected Variable(BaseType type) {
        this.type = type;
    }
    
    public final BaseType type;
}


/**
 * Класс представляет именованную переменную. Помимо информации о своем типе 
 * хранит имя и область видимости в IR.
 * 
 * @author anton.bobukh
 */
class NamedVariable extends Variable {
    /**
     * Создает именованную переменную с неопределенной областью видимости.
     * 
     * @param name Имя переменной
     * @param type Тип переменной
     */
    public NamedVariable(
            String name, 
            BaseType type) {
        
        super(type);
        this.name = name;
        scope = this.new Scope();
    }
    
    @Override
    public String toString() {
        return this.type + " " + this.name;
    }
    
    public final Scope scope;
    public final String name;
}


/**
 * Класс представляет безымянную временную переменную. Хранит только информацию
 * о своем типе.
 * 
 * @author anton.bobukh
 */
class TmpVariable extends Variable {
    /**
     * Создает безымянную временную переменную.
     * 
     * @param type Тип переменной
     */
    public TmpVariable(BaseType type) {
        super(type);
    }
    
    @Override
    public String toString() {
        return this.type + " tmp";
    }
}