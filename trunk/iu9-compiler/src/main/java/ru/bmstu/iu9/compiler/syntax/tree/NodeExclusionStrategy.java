package ru.bmstu.iu9.compiler.syntax.tree;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author maggot
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@interface Exclude {
    // Field tag only annotation
}

public class NodeExclusionStrategy implements ExclusionStrategy {
    public NodeExclusionStrategy() {
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(Exclude.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> type) {
        return false;
    }
}