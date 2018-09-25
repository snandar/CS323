import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.Queue; // Iterable

public class CC2
{
    private int bridges;     // bridge counter
    private int cc;          // connected component counter
    private int cnt;         // dfs pre-order counter
    private int idCount = 0;
    private int counter; 
    private int ccID = 0;
    private int[] pre;       // pre[v] = order in which dfs discovered v
    private int[] low;       // low[v] = minimum DFS preorder number of v
    private int[] id2;         // id[v] = id of component containing v
    private int[] id;
    private int[] size;       // size[id[v]] = size of component
    private int[] parent; 	 //parent[v] = edgeTo
    private int[] subtreesize; 
    private int[] connectedcomponent;
    private int[] connectedcomponentID;
    private Queue<Edge> q = new Queue<Edge>();
    
    //Private Methods:
    private void dfs(Graph G, int u, int v) {
        // Note: u is parent of v, or u==v when v is root
    	if(u == v)
    		parent[v] = v; 
    	else
    		parent[v] = u;
    	
        pre[v] = cnt++;
        low[v] = pre[v];
        for (int w : G.adj(v)) {
            if (pre[w] == -1) {
                dfs(G, v, w);
                low[v] = Math.min(low[v], low[w]);
                if (low[w] == pre[w]) {
                    bridges++;
                    Edge ed = new Edge(v, w);
                    q.enqueue(ed);
                }
            }
            else {
                // w was already marked, but we can use pre[w]
                // to update low[v], if w is not our parent.
                if (w != u)
                    low[v] = Math.min(low[v], pre[w]);
            }
        }
    }
        
    private void dfs2(Graph G, int u, int v){
    	
    	if(low[v] == pre[v]){
    		id[v] = idCount++;
    		id2[v] = v;
    		size[v] = 1;
    	}
    	else{
    		id[v] = id[u];
    		id2[v] = id2[u];
    		size[id2[v]]++;
    	}
    	
    	counter++;
    	subtreesize[v]++;
    	connectedcomponentID[v] = ccID;
    	
    	for (int w : G.adj(v)) {
    		if(id2[w] == -1){
    			dfs2(G, v, w);
    			subtreesize[v] += subtreesize[w];
    		}
    	}
    	
    		
    }
    
    // Public methods:
    public CC2(Graph G)
    {
        // TODO: do most of your work here!
        // This should run in O(V+E) time, probably requiring a couple
        // of DFS traversals of G to compute everything you need.
        // See Bridge.java and CC1.java for ideas.
        // Note you should NOT print bridge edges (or anything else)
        // to System.out.  Rather, they are available via the
        // bridges() method, below.

        low = new int[G.V()];
        pre = new int[G.V()];
        parent = new int[G.V()];
        id2 = new int[G.V()];
        id = new int[G.V()];
        size = new int[G.V()];       // all 0 
        subtreesize = new int[G.V()];
        connectedcomponentID = new int[G.V()];

        for (int v = 0; v < G.V(); v++){
            low[v] = -1;
            pre[v] = -1;
            id2[v] = -1;
            connectedcomponentID[v] = -1;
        }

        // full dfs #1 traversal of G: // computes the pre and low
        for (int v = 0; v < G.V(); v++) {
            if (pre[v] == -1) {
                cc++;
                dfs(G, v, v);
            }
           
        }
        
        connectedcomponent = new int[cc];
        
        //full dfs #2 traversal of G 
        //to compute id and size
        for (int v = 0; v < G.V(); v++) {
        	if (id2[v] == -1){
        		counter = 0;
        		dfs2(G, v, v);
        		connectedcomponent[ccID] = counter;
        		ccID++;
        	}
        	size[v] = size[id2[v]];  
        }
    }

    // Return id of the two-edge-connected component containing v.
    // The id's should be consecutive integers.  For example, if there
    // are 4 two-edge-connected components, then their ids should be
    // 0, 1, 2, and 3.
    public int id(int v) { return id[v]; } // TODO

    // Are v and w in the same two-edge-connected component? (done!)
    public boolean connected(int v, int w) { return id(v)==id(w); }

    // Return size of the two-edge-connected component containing v
    public int size(int v) { return size[v]; } // TODO

    // Return the number of two-edge-connected components
    public int count() { return bridges + cc; } // TODO

    // This returns an Iterable, which (via iteration) lists all
    // bridge edges in the graph.  The bridges should be listed
    // in the order they were discovered (or printed by Bridges.java)
    public Iterable<Edge> bridges() {
        // TODO
        // In your working code, you'll need to create and populate a
        // Queue as part of your constuctor.  Here, we just return an
        // empty queue.
        return q;
    }

    // This last method, below, requires that you (internally) also
    // keep track of connected components, their sizes, and (maybe)
    // dfs subtree sizes.  If you like you may use a CC1 object
    // internally, or just copy some code from CC1.java

    // Suppose b is a bridge edge.  It is part of some connected
    // component with s vertices. If we remove b, the component falls
    // apart into two components, of sizes k and s-k.  The "value" of
    // bridge b is defined to be min(k, s-k)

    // Given edge b, this method should return its value if b is a
    // bridge of G, or 0 if b is not a bridge.
    public int bridgeValue(Edge b) { 
    	
    	int v = b.either();
    	int w = b.other(v);
    	int cmpOne = 0;
    	int cmpTwo = 0;
    	
    	if(parent[w] == v){
    		if (low[w] == pre[w]){
    			cmpOne = subtreesize[w];
    			cmpTwo = connectedcomponent[connectedcomponentID[w]] - cmpOne;
    		}
    	}
    	else if(parent[v] == w){
    		if(low[v] == pre[v]){
    			cmpOne = subtreesize[v];
    			cmpTwo = connectedcomponent[connectedcomponentID[v]] - cmpOne;
    		}
    	}
    	
    return Math.min(cmpOne, cmpTwo);
    }
//    return Math.min(subtreeSize[b.either()], b.other(b.either())); } // TODO
}
