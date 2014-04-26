package ru.bmstu.iu9.compiler.ir;

/**
 * Created by natalia on 20.03.14.
 */

import java.util.*;

/**
 * An example class for directed graphs.  The vertex type can be specified.
 * There are no edge costs/weights.
 *
 * Written for CS211, Nov 2006.
 *
 * @author Paul Chew
 */

interface AbstractEdge<V> {
    public V InVertex();
    public V OutVertex();
}

public class Digraph<V, E extends AbstractEdge<V>> {

    /**
     * The implementation here is basically an adjacency list, but instead
     * of an array of lists, a Map is used to map each vertex to its list of
     * adjacent vertices.
     */
    private Map<V,List<V>> neighbors = new HashMap<V,List<V>>();

    private  Map<V, List<V>> parent_V = new HashMap<V, List<V>>();
    List<E> edges = new LinkedList<E>();

    /**
     * String representation of graph.
     */
    @Override
    public String toString () {
        StringBuffer s = new StringBuffer();
        for (V v: neighbors.keySet()) s.append("\n    " + parent_V.get(v) + " -> " + v.toString() + " -> " + neighbors.get(v));
        return s.toString();
    }

    /**
     * Add a vertex to the graph.  Nothing happens if vertex is already in graph.
     */
    public void add (V vertex) {
        if (neighbors.containsKey(vertex)) return;
        neighbors.put(vertex, new ArrayList<V>());

        parent_V.put(vertex, new ArrayList<V>());
    }

    /**
     * True iff graph contains vertex.
     */
    public boolean contains (V vertex) {
        return neighbors.containsKey(vertex);
    }

    public boolean contains (V from, V to) { return neighbors.get(from).contains(to);}
    public boolean contains (E e) { return neighbors.get(e.InVertex()).contains(e.OutVertex());}

    public E getEdge(V from, V to) {
        for (E e: edges)
            if(e.OutVertex().equals(to) && e.InVertex().equals(from))
                return e;
        return null;
    }
    /**
     * Add an edge to the graph; if either vertex does not exist, it's added.
     * This implementation allows the creation of multi-edges and self-loops.
     */
    public void add (V from, V to) {
        this.add(from); this.add(to);
        neighbors.get(from).add(to);

        parent_V.get(to).add(from);
    }

    public void add(E e) {
        this.add(e.InVertex()); this.add(e.OutVertex());
        neighbors.get(e.InVertex()).add(e.OutVertex());

        parent_V.get(e.OutVertex()).add(e.InVertex());
        edges.add(e);
    }

    /**
     * Remove an edge from the graph.  Nothing happens if no such edge.
     * @throws IllegalArgumentException if either vertex doesn't exist.
     */
    public void remove (V from, V to) {
        if (!(this.contains(from) && this.contains(to)))
            throw new IllegalArgumentException("Nonexistent vertex");
        neighbors.get(from).remove(to);
    }

    public void removeVE (V from, V to) {
        if (!(this.contains(from) && this.contains(to)))
            throw new IllegalArgumentException("Nonexistent vertex");
        neighbors.get(from).remove(to);

        parent_V.get(to).remove(from);
        edges.remove(getEdge(from, to));
    }

    public List<V> prevBB(V v) {
        return parent_V.get(v);
    }
    public List<V> nextBB(V v) {
        return  neighbors.get(v);
    }

    public V beginBB(E e) { return e.InVertex(); }
    public V endBB(E e) { return e.OutVertex(); }

    public Set<V> getAllVertexes() { return neighbors.keySet(); }
    public List<E> getAllEdges() { return edges; }

    public List<V> dfs() {
        Map<V, Boolean> Vmarks = new HashMap<V, Boolean>();
        for(V v: neighbors.keySet())
            Vmarks.put(v, false);

        Stack<V> stack = new Stack<V>();
        LinkedList<V> vertexes = new LinkedList<V>();
        for (V vertex : neighbors.keySet()) {
            stack.push(vertex);
            while (!stack.isEmpty()) {
                V curVertex = stack.pop();
                if (!Vmarks.get(curVertex)) {
                    Vmarks.put(curVertex, true);
                    vertexes.add(curVertex);
                    List<E> outEdges = new LinkedList<E>();
                    for(E e: edges)
                        if(e.InVertex().equals(curVertex))
                            stack.push(e.OutVertex());
                }
            }
        }
        return vertexes;
    }
    public List<V> bfs() //– возвращает последовательность вершин, полученную путём обхода графа в ширину;
    {
        List<V> vertexes = new LinkedList<V>();
        HashMap<V, Boolean> vMarks = new HashMap<V, Boolean>();
        for(V v: neighbors.keySet())
            vMarks.put(v, false);

        LinkedList<V> q = new LinkedList<V>();
        for (V vertex : neighbors.keySet()) {
            if(vMarks.get(vertex) == false) {
                q.addFirst(vertex);
                while(!q.isEmpty()) {
                    V v = q.removeLast();
                    vMarks.put(v, true);
                    vertexes.add(v);
                    for(E e: edges)
                        if(e.InVertex().equals(v))
                            if(vMarks.get(e.OutVertex()) == false)
                                q.addFirst(e.OutVertex());

                }
            }
        }
        return vertexes;
    }













//=============================================================================
    /**
     * Report (as a Map) the out-degree of each vertex.
     */
    public Map<V,Integer> outDegree () {
        Map<V,Integer> result = new HashMap<V,Integer>();
        for (V v: neighbors.keySet()) result.put(v, neighbors.get(v).size());
        return result;
    }

    /**
     * Report (as a Map) the in-degree of each vertex.
     */
    public Map<V,Integer> inDegree () {
        Map<V,Integer> result = new HashMap<V,Integer>();
        for (V v: neighbors.keySet()) result.put(v, 0);       // All in-degrees are 0
        for (V from: neighbors.keySet()) {
            for (V to: neighbors.get(from)) {
                result.put(to, result.get(to) + 1);           // Increment in-degree
            }
        }
        return result;
    }

    /**
     * Report (as a List) the topological sort of the vertices; null for no such sort.
     */
    public List<V> topSort () {
        Map<V, Integer> degree = inDegree();
        // Determine all vertices with zero in-degree
        Stack<V> zeroVerts = new Stack<V>();        // Stack as good as any here
        for (V v: degree.keySet()) {
            if (degree.get(v) == 0) zeroVerts.push(v);
        }
        // Determine the topological order
        List<V> result = new ArrayList<V>();
        while (!zeroVerts.isEmpty()) {
            V v = zeroVerts.pop();                  // Choose a vertex with zero in-degree
            result.add(v);                          // Vertex v is next in topol order
            // "Remove" vertex v by updating its neighbors
            for (V neighbor: neighbors.get(v)) {
                degree.put(neighbor, degree.get(neighbor) - 1);
                // Remember any vertices that now have zero in-degree
                if (degree.get(neighbor) == 0) zeroVerts.push(neighbor);
            }
        }
        // Check that we have used the entire graph (if not, there was a cycle)
        if (result.size() != neighbors.size()) return null;
        return result;
    }

    /**
     * True iff graph is a dag (directed acyclic graph).
     */
    public boolean isDag () {
        return topSort() != null;
    }

    /**
     * Report (as a Map) the bfs distance to each vertex from the start vertex.
     * The distance is an Integer; the value null is used to represent infinity
     * (implying that the corresponding node cannot be reached).
     */
    public Map bfsDistance (V start) {
        Map<V,Integer> distance = new HashMap<V,Integer>();
        // Initially, all distance are infinity, except start node
        for (V v: neighbors.keySet()) distance.put(v, null);
        distance.put(start, 0);
        // Process nodes in queue order
        Queue<V> queue = new LinkedList<V>();
        queue.offer(start);                                    // Place start node in queue
        while (!queue.isEmpty()) {
            V v = queue.remove();
            int vDist = distance.get(v);
            // Update neighbors
            for (V neighbor: neighbors.get(v)) {
                if (distance.get(neighbor) != null) continue;  // Ignore if already done
                distance.put(neighbor, vDist + 1);
                queue.offer(neighbor);
            }
        }
        return distance;
    }
}