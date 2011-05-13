package ru.bmstu.iu9.compiler;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.util.List;

/**
 *
 * @author maggot
 */
abstract public class BaseType {
    public enum Type { 
        ARRAY, STRUCT, FUNCTION, PRIMITIVE_TYPE, INVALID;
        
        private Type() {
            this.value = 1 << this.ordinal();
        }
        
        private boolean is(Type type) {
            return (this.value & type.value) != 0;
        }
        private boolean is(Type[] types) {
            for (int i = 0; i < types.length; ++i) {
                if ((this.value & types[i].value) != 0)
                    return true;
            }
            return false;
        }
        
        private int value = 0;
    };
    
    protected BaseType(Type type, long size) {
        this(type, false, size);
    }
    protected BaseType(Type type, boolean constancy, long size) {
        this.type = type.ordinal();
        this.constancy = constancy;
        this.size = size;
    }
    
    public boolean is(Type type) {
        return this.type().is(type);
    }
    public boolean is(Type[] types) {
        return this.type().is(types);
    }
    
    public boolean is(PrimitiveType.Type type) {
        return this.type().is(Type.PRIMITIVE_TYPE);
    }
    public boolean is(PrimitiveType.Type[] types) {
        return this.type().is(Type.PRIMITIVE_TYPE);
    }
    
    public Type type() {
        return Type.values()[this.type];
    }
    
    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass()) && 
                (this.type == (((BaseType)obj).type));
    }
   /* @Override
    public String toString() {
        return ((constancy) ? "CONST " : "") + 
                Type.values()[this.type].toString();
    }*/
    
    public long size() {
        return this.size;
    }
    
    private final int type;
    public boolean constancy;
    protected long size;
    
    
    public static class TypeAdapter implements JsonDeserializer<BaseType> {

        public BaseType deserialize(
                JsonElement src, 
                java.lang.reflect.Type type, 
                JsonDeserializationContext context) throws JsonParseException {
            
            JsonObject object = src.getAsJsonObject();
            BaseType result = null;
            
            BaseType.Type typeType = 
                    BaseType.Type.values()[(Integer)context.deserialize(object.get("typeType"), Integer.class)];
            
            boolean constancy = context.deserialize(object.get("constancy"), Boolean.class);
            
            JsonPrimitive primitive = null;
            JsonObject obj = null;
            switch(typeType) {
                case ARRAY:
                {
                    obj = object.getAsJsonObject("elementType");
                    assert obj == null;
                    BaseType elementType = context.deserialize(obj, BaseType.class);
                    
                    primitive = object.getAsJsonPrimitive("lenght");
                    if(primitive == null) {
                        result = new ArrayType(elementType, constancy);
                    } else {
                        int lenght = context.deserialize(primitive, Integer.class);
                        result = new ArrayType(elementType, lenght, constancy);
                    }
                    
                    break;
                }
                case STRUCT:
                {
                    primitive = object.getAsJsonPrimitive("name");
                    assert primitive == null;
                    String name = context.deserialize(primitive, String.class);
                    
                    result = new StructType(name, constancy);
                    
                    break;
                }
                case FUNCTION:
                {
                    obj = object.getAsJsonObject("returnValue");
                    assert obj == null;
                    BaseType returnValue = context.deserialize(obj, BaseType.class);
                    
                    JsonArray a = object.getAsJsonArray("arguments");
                    assert a == null;
                    List<FunctionType.Argument> args = context.deserialize(
                        a, 
                        new TypeToken<List<FunctionType.Argument>>(){}.getType());
                    
                    result = new FunctionType(returnValue, args, constancy);
                    
                    break;
                }
                case PRIMITIVE_TYPE:
                {
                    primitive = object.getAsJsonPrimitive("primitive");
                    assert primitive == null;
                    PrimitiveType.Type t = PrimitiveType.Type.values()[
                            (Integer)context.deserialize(primitive, Integer.class)];
                    
                    switch(t) {
                        case POINTER:
                        {
                            obj = object.getAsJsonObject("type");
                            assert obj == null;
                            BaseType pType = context.deserialize(obj, BaseType.class);
                            
                            result = new PointerType(pType, constancy);
                            
                            break;
                        }
                        default:
                        {
                            result = new PrimitiveType(t, constancy);
                            break;
                        }
                    }
                    
                    break;
                }
                case INVALID:
                {
                    result = new InvalidType();
                    
                    break;
                }
            }
            
            return result;
        }
    }
}
