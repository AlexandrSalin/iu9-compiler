/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.syntax.tree;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author maggot
 */
public class BlockNode<T> 
        extends BaseNode implements Iterable<T>, Statement {
    
    public BlockNode() {
        super(BaseNode.NodeType.BLOCK);
        this.children = new LinkedList<T>();
    }
    public BlockNode(List<T> block) {
        super(BaseNode.NodeType.BLOCK);
        this.children = block;
    }
    public BlockNode(T element) {
        super(BaseNode.NodeType.BLOCK);
        this.children = new LinkedList<T>();
        this.children.add(element);
    }
    protected BlockNode(BaseNode.NodeType type) {
        super(type);
        this.children = new LinkedList<T>();
    }
    protected BlockNode(BaseNode.NodeType type, List<T> block) {
        super(type);
        this.children = block;
    }
    
    public void addChild(T child) { this.children.add(child); }
    public List<T> children() { return this.children; }
    public void addChildren(List<T> children) {
        this.children.addAll(children);
    }

    @Override
    public Iterator<T> iterator() {
        return this.children.iterator();
    }
    
    @Override
    public String toString() {
        return children.toString();
    }
    
    private final List<T> children;
    
    
    public static class BlockNodeAdapter 
            implements JsonSerializer<BlockNode> {
        
        @Override
        public JsonElement serialize(
                BlockNode src, 
                Type type, 
                JsonSerializationContext context) {
            
            JsonObject result = new JsonObject();
            result.add("children", context.serialize(
                    src.children, 
                    new TypeToken<List<Object>>(){}.getType())
                );
            result.add("nodeType", new JsonPrimitive(src.nodeType));
            
            return result;
        }
    }
}
