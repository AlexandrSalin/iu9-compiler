/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.syntax.tree;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author maggot
 */
final public class BlockNode extends Node implements Iterable<Node> {
    public BlockNode() {
        super(Node.NodeType.BLOCK, null);
        children = new LinkedList<Node>();
    }
    public BlockNode(Node[] nodes) {
        super(Node.NodeType.BLOCK, null);
        children = new LinkedList<Node>(Arrays.asList(nodes));
    }
    
    public void addChild(Node child) { this.children.add(child); }
    public List<Node> children() { return this.children; }
    public void addChildren(List<? extends Node> children) {
        this.children.addAll(children);
    }

    @Override
    public Iterator<Node> iterator() {
        return this.children.iterator();
    }
    
    @SerializedName("nodes")
    List<Node> children;
    
    public static class BlockNodeSerializer implements JsonSerializer<BlockNode> {
        @Override
        public JsonElement serialize(BlockNode src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("nodes", context.serialize(src.children, new TypeToken<List<Object>>(){}.getType()));
            result.add("nodeType", new JsonPrimitive(src.nodeType));
            
            return result;
        }
    }
}
