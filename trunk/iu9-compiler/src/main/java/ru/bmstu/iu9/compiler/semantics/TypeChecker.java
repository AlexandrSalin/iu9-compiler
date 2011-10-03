/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.bmstu.iu9.compiler.semantics;

import ru.bmstu.iu9.compiler.*;
import ru.bmstu.iu9.compiler.ir.type.BaseType;
import ru.bmstu.iu9.compiler.ir.type.PrimitiveType;

/**
 *
 * @author maggot
 */
public class TypeChecker {
    
    public static boolean check(
            BaseType found,
            BaseType required, 
            DebugInfo dInfo) {
        
        boolean result;
        if (result = (found == null || !found.equals(required)))
            Logger.logIncompatibleTypes(
                    (found == null) ? "UNKNOWN TYPE" : found.toString(), 
                    required.toString(), 
                    dInfo.position);
        return !result;
    }
    
    public static boolean check(
            BaseType found, 
            BaseType.Type required, 
            DebugInfo dInfo) {
        
        boolean result;
        if (result = (found == null || !found.is(required)))
            Logger.logIncompatibleTypes(
                    (found == null) ? "UNKNOWN TYPE" : found.toString(),
                    required.name(), 
                    dInfo.position);
        return !result;
    }
    
    public static boolean check(
            BaseType found, 
            PrimitiveType.Type required, 
            DebugInfo dInfo) {
        
        boolean result;
        if (result = (found == null || /*!(found instanceof PrimitiveType) || */
                !(((PrimitiveType)found).primitive() == required)))
            Logger.logIncompatibleTypes(
                    (found == null) ? "UNKNOWN TYPE" : found.toString(),
                    required.name(), 
                    dInfo.position);
        return !result;
    }
    
    public static boolean check(
            PrimitiveType.Type found,
            BaseType required, 
            DebugInfo dInfo) {
        
        boolean result;
        if (result = (found == null || /*!(required instanceof PrimitiveType) ||*/ 
                !(((PrimitiveType)required).primitive() == found)))
            Logger.logIncompatibleTypes(
                    (found == null) ? "UNKNOWN TYPE" : found.toString(),
                    required.toString(), 
                    dInfo.position);
        return !result;
    }
    
    public static boolean check(
            BaseType found, 
            BaseType.Type[] required, 
            DebugInfo dInfo) {
        boolean result = (found == null || !found.is(required));
        if (result) {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < required.length; ++i) {
                str.append(required[i]);
                str.append(" or ");
            }
            str.delete(str.length() - 4, str.length());
            Logger.logIncompatibleTypes(
                    (found == null) ? "UNKNOWN TYPE" : found.toString(),
                    str.toString(), 
                    dInfo.position);
        }
        
        return !result;
    }
    
    public static boolean check(
            BaseType found, 
            PrimitiveType.Type[] required,
            DebugInfo dInfo) {

        boolean result = (found == null || /*!(found instanceof PrimitiveType) ||*/
                !found.is(required));
        if (result) {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < required.length; ++i) {
                str.append(required[i]);
                str.append(" or ");
            }
            str.delete(str.length() - 4, str.length());
            Logger.logIncompatibleTypes(
                    (found == null) ? "UNKNOWN TYPE" : found.toString(),
                    str.toString(), 
                    dInfo.position);
        }
        
        return !result;
    }
    
}
