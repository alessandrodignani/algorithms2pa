import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;

public class SAP {
    private final Digraph digraph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("Graph is null.");

        digraph = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        int[] result = shortest(v, w);
        return result[0];
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        int[] result = shortest(v, w);
        return result[0];
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        int[] result = shortest(v, w);
        return result[1];
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        int[] result = shortest(v, w);
        return result[1];
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        if (v < 0 || v >= digraph.V())
            throw new IllegalArgumentException("vertex " + v + " is out of bounds: not between 0 and " + (digraph.V() - 1));
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertices(Iterable<Integer> vertices) {
        if (vertices == null) throw new IllegalArgumentException("vertices is null");
        for (int v : vertices)
            validateVertex(v);
    }

    // return shortest length and corresponding ancestor
    private int[] shortest(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        BreadthFirstDirectedPaths vBfs = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths wBfs = new BreadthFirstDirectedPaths(digraph, w);

        return getDistanceAndAncestor(vBfs, wBfs);
    }

    // return shortest length and corresponding ancestor
    private int[] shortest(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertices(v);
        validateVertices(w);
        BreadthFirstDirectedPaths vBfs = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths wBfs = new BreadthFirstDirectedPaths(digraph, w);

        return getDistanceAndAncestor(vBfs, wBfs);
    }

    private int[] getDistanceAndAncestor(BreadthFirstDirectedPaths vBfs, BreadthFirstDirectedPaths wBfs) {
        int shortestLen = Integer.MAX_VALUE;
        int shortestAncestor = -1;
        for (int i = 0; i < digraph.V(); i++) {
            if (vBfs.hasPathTo(i) && wBfs.hasPathTo(i)) {
                int len = vBfs.distTo(i) + wBfs.distTo(i);
                if (len < shortestLen) {
                    shortestLen = len;
                    shortestAncestor = i;
                }
            }
        }

        if (shortestAncestor == -1) return new int[]{-1, -1};
        else return new int[]{shortestLen, shortestAncestor};
    }
}