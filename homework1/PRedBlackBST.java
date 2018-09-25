// THIS CODE IS MY OWN WORK, IT WAS WRITTEN WITHOUT CONSULTING
// A TUTOR OR CODE WRITTEN BY OTHER STUDENTS - NANDAR SOE


public class PRedBlackBST<Key extends Comparable<Key>, Value>
    extends PBST<Key, Value>
{
    // Constructors: these just call the parent constructors.
    public PRedBlackBST() { super(); }
    private PRedBlackBST(Node r) { super(r); }
    
    public PBST<Key,Value> put(Key k, Value v) {
        Node newroot = put(root, k, v);

        	if(newroot.color == RED) {	
        		newroot = new Node(newroot.key, newroot.val, newroot.left, newroot.right, BLACK);
        	}
        	
        return new PRedBlackBST<Key, Value>(newroot);
    }
    
    Node put(Node h, Key key, Value val) { 
    	
        if (h == null)
            return new Node(key, val, null, null, RED);
        int cmp = key.compareTo(h.key);
        
        if (cmp<0) {
            // Return copy of h with revised h.left
            Node newleft = put(h.left, key, val);
            h = new Node(h.key, h.val, newleft, h.right, h.color);
            if (isRed(h.right) && !isRed(h.left))      h = rotateLeft(h);
            if (isRed(h.left)  &&  isRed(h.left.left)) h = rotateRight(h);
            if (isRed(h.left)  &&  isRed(h.right))     h = flipColors(h);
            return h;
        }
        if (cmp>0) {
            // Return copy of h with revised h.right
            Node newright = put(h.right, key, val);
            h = new Node(h.key, h.val, h.left, newright, h.color);
            if (isRed(h.right) && !isRed(h.left))      h = rotateLeft(h);
            if (isRed(h.left)  &&  isRed(h.left.left)) h = rotateRight(h);
            if (isRed(h.left)  &&  isRed(h.right))     h = flipColors(h);
            return h;
        }
        // Else cmp==0, return copy of h with revised h.val
        h = new Node(h.key, val, h.left, h.right, h.color);
        // if (isRed(h.right) && !isRed(h.left))      h = rotateLeft(h);
        // if (isRed(h.left)  &&  isRed(h.left.left)) h = rotateRight(h);
        // if (isRed(h.left)  &&  isRed(h.right))     h = flipColors(h);
        return h;
        
    }

    Node rotateRight(Node h) {
    	
    	Node newRightOfH = null;
    	
    	// if (h.right == null && h.left.right == null){
    	// 	newRightOfH = new Node(h.key, h.val, null, null, RED);
    	// }
    	// else if(h.right == null){
    	// 	newRightOfH = new Node(h.key, h.val, h.left.right, null, RED);
    	// }
    	// else if(h.left.right == null){
    	// 	newRightOfH = new Node(h.key, h.val, null, h.right, RED);
    	// }
    	// else{
    		newRightOfH = new Node(h.key, h.val, h.left.right, h.right, RED);
    	// }
    	
    	Node newH = new Node(h.left.key, h.left.val, h.left.left, newRightOfH, BLACK);
    	
    	return newH;
    }

    Node rotateLeft(Node h) {
    	Node newLeftOfH = null;
    	
    	if(h.left == null && h.right.left == null){
    		newLeftOfH = new Node(h.key, h.val, null, null, RED);
    	}
    	else if(h.left == null){
    		newLeftOfH = new Node(h.key, h.val, null, h.right.left, RED);
    	}
    	else if(h.right.left == null){
    		newLeftOfH = new Node(h.key, h.val, h.left, null, RED);
    	}
    	else newLeftOfH = new Node(h.key, h.val, h.left, h.right.left, RED);
    	
    	Node newH = new Node(h.right.key, h.right.val, newLeftOfH, h.right.right, h.color);
    	
    	return newH;
    }

    Node flipColors(Node h) {
    	Node newLeftOfH = new Node(h.left.key, h.left.val, h.left.left, h.left.right, BLACK);
    	Node newRightOfH = new Node(h.right.key, h.right.val, h.right.left, h.right.right, BLACK);
    	Node newH = new Node(h.key, h.val, newLeftOfH, newRightOfH, RED);
    	return newH;
	}

}
