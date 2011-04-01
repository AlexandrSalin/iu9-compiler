package ru.bmstu.iu9.compiler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;

/**
 *
 * @author maggot
 */
public class TypeFactory implements Iterable<Type> {
    public TypeFactory(String filename) {
        BufferedReader reader = null;
        
        try {
            Gson gson = 
                    new GsonBuilder().
                        registerTypeAdapter(
                            GeneralizedType.class, 
                            new GeneralizedType.TypeInstanceCreator()).
                        create();
            
            reader = new BufferedReader(
                        new FileReader(filename));
            
            types = gson.fromJson(reader, GeneralizedType[].class);
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
    
    @Override
    public Iterator<Type> iterator() {
        return new Iterator<Type>() {
                @Override
                public boolean hasNext() {
                    return counter < types.length;
                }
                @Override
                public Type next() {
                    ++counter;
                    GeneralizedType type = types[counter];
                    switch (type.typename) {
                        case INT:
                            BOOL:
                            DOUBLE:
                            FLOAT:
                            CHAR:
                            VOID:
                            return new PrimitiveType(type.typename, type.isConstant);
                        case STRUCT:
                            return new StructType(type.name);
                        case FUNCTION:
                            return new FunctionType(type.type, type.arguments);
                        case ARRAY:
                            return new ArrayType(type.type, type.length);
                        case POINTER:
                            return new PointerType(type.type, type.isConstant);
                        default:
                            return null;
                    }
                }
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
                
                int counter = -1;
            };
    }
    
    private static class GeneralizedType {
        private GeneralizedType() { }
        
        private Type.Typename typename;
        private boolean isConstant;
        private Type type;
        private Integer length;
        private Type[] arguments;
        private String name;
        
        public static class TypeInstanceCreator implements InstanceCreator<GeneralizedType> {
            @Override
            public GeneralizedType createInstance(java.lang.reflect.Type type) {
                return new GeneralizedType();
            }
        }
    }
    
    private GeneralizedType[] types;
}