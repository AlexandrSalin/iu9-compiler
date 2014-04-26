package ru.bmstu.iu9.compiler;

import com.google.gson.InstanceCreator;

/**
 *
 * @author maggot
 */
public final class Fragment {
    /**
     * Координаты фрагмента текста программы
     * @param startingPosition позиция первого символа фрагмента
     * @param endingPosition позиция последнего символа фрагмента
     */
    private Fragment() { }
    public Fragment(Position starting, Position ending) {
        this.starting = starting;
        this.ending = ending;
    }
    
    public Position starting() { return starting; }
    public Position ending() { return ending; }
    
    private Position starting;
    private Position ending;
    
    public static class FragmentInstanceCreator 
        implements InstanceCreator<Fragment> {
        
        @Override
        public Fragment createInstance(java.lang.reflect.Type type) {
            return new Fragment();
        }
    }
    
    @Override
    public String toString() {
        return "from " + starting + " to " + ending;
    }
}