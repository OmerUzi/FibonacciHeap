/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 */

public class FibonacciHeap
{
	private HeapNode min;
	private HeapNode first;
    private int size = 0;
    private int numOfMarkedNodes = 0;
    public int numOfTrees = 0;
    
    private static int numOfCuts = 0; 
    private static int numOfLinks = 0;
	
	public void print() { //prints roots
    	HeapNode n = this.first;
    	if(n==null) {
    		System.out.println("(empty)");
    		return;
    	}
    	for(int i=0; i<this.numOfTrees; i++) {
    		System.out.print("("+n.getKey()+")-");
    		n=n.next;
    	}
    	System.out.println();
    }


   /**
    * public boolean isEmpty()
    *
    * precondition: none
    * 
    * The method returns true if and only if the heap
    * is empty.
    *   
    */
    public boolean isEmpty()
    {
    	return (this.size == 0); 
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * 
    * Returns the new node created. 
    */
    public HeapNode insert(int key)
    {    
    	//creating new HeapNode
    	HeapNode newNode = new HeapNode(key);
    
    	if(this.isEmpty()) { 
    		this.min = newNode;
    		this.min.next = newNode; this.min.prev = newNode; //making it a circular doubly linked list
    		
    	}else {
    		this.first.setSibling(newNode);		
    	}
    	this.first = newNode;			//updating first
    	
    	//checking if the new node is also the new min
    	if(newNode.key < this.min.key) {
    		this.min = newNode;
    	}
    	
    	this.size++;
    	this.numOfTrees++; 
    	return newNode; 
    }

   /**
    * public void deleteMin()
    *
    * Delete the node containing the minimum key.
    *
    */
public void deleteMin()
    {
     	if (this.isEmpty()) { // we can't delete the min of an empty heap
     		return;
     	}
     	// we are going to delete something
     	this.size --; // updating the size
     	HeapNode node2delete = this.min;   
     	
     	if(node2delete.rank==0) { // min is a one noded tree
     		this.numOfTrees--; // because wer'e just removing a node, the number of trees will decrease
     		if(this.numOfTrees==0) { //our heap has only one tree that wer'e going to delete now
     			this.min=null;
         		this.first=null;
         		return;		// our heap is now empty
     		}else {	//our heap has more than one tree
     			if(this.getFirst()==node2delete) { // our first pointer is our min as well
     				this.first = node2delete.getNext();
     				this.getFirst().prev = node2delete.getPrev();
     				this.getFirst().getPrev().next = this.getFirst();
     			}else {
     				node2delete.getPrev().next=node2delete.getNext(); //updating the sibling nodes in the DLL
                 	node2delete.getNext().prev=node2delete.getPrev();
     			}
     		}
     	}else {
         	// else, our node has kids!
     		if(this.first.next==this.first) { //our heap has only one tree
     			this.first=this.first.getChild();
     				// updating node2delete's children- their parent should be none and they shouldn't be marked
         		int numofsons= updatesons(this.first);
         		this.numOfTrees+= numofsons-1; // adding all of the sons as root, -1 for the root we removed
     		}else {	//our heap has more than one tree
     			HeapNode after = node2delete.getNext();
         		HeapNode before = node2delete.getPrev();
         		HeapNode son = node2delete.getChild();
         			// updating node2delete's children- their parent should be none and they shouldn't be marked
         		int numofsons= updatesons(son);
         		this.numOfTrees+= numofsons-1; // adding all of the sons as root, -1 for the root we removed
         				// now add the sons of the min in it's place
         		before.next=son; after.prev = son.prev;
         		son.prev.next= after; son.prev = before;
         		if(this.first==node2delete) {
         			this.first=son;
         		}
         		// now wer'e ready to successive link
     		}
     	}
     	// now, we need to successive link our heap if it has more than one root
         successive_link(this);
     	// now we need to update min
     	this.calcmin();
    }

   private int updatesons(HeapNode n) {
	   if (n==null) {
		   return 0;
	   }
	   int key = n.getKey();
	   int count=0;
	   do {
		   n.parent = null;
		   if (n.marked) {
			   this.numOfMarkedNodes--;
			   n.marked = false;
		   }
		   count++;
		   n = n.getNext();
	   }
	   while (n.getKey()!=key); 
	   return count;
	   
}

private void successive_link(FibonacciHeap fibonacciHeap) {
	// storing our current nodes
	HeapNode[] arr = new HeapNode[this.numOfTrees];
	   // creating the array we'll export from. it needs to be around log(n)+1 long.
	int log2 = (int) (Math.ceil(Math.log(this.size)/ Math.log(2))+1);
	HeapNode[] arroftrees = new HeapNode[log2];
	HeapNode node = this.first;
	for (int i = 0; i < arr.length; i++) {
		arr[i] = node;
		node = node.getNext();
	}
	for (HeapNode n : arr) {
		n.next=null;
		n.prev =null;
		int rank = n.getRank();
		// we need to meld the trees together until we reach an empty slot
		while(rank<arroftrees.length && arroftrees[rank]!= null) {
			n = link(n, arroftrees[rank]);
			arroftrees[rank]=null;
			rank++;
			}
		arroftrees[rank]=n;
		
		
	}
	// now, we need to change our heap to match the array.
	this.first=null;
	this.numOfTrees=0;
	for (int i = 0; i < arroftrees.length; i++) {
		HeapNode root = arroftrees[i];
		if(root!= null) {
			 numOfTrees++;
			if (this.first==null) {
				this.first = root;
				root.next = root; root.prev=root; // creating a DLL with our first node
			}else{ // we already inserted a tree to our heap
				HeapNode last = this.first.prev;
				last.next = root; root.prev = last;
				root.next = this.first; this.first.prev = root;
				this.first = root;
			}
		}
	}
}

private void calcmin() {
	this.min = this.first; // first we delete our old min
	HeapNode node = this.first;
	for (int i=0; i<this.numOfTrees; i++) { // working all the roots in our heap, and updating the min accordingly
		if(this.findMin().getKey()>node.getKey()) {
			this.min = node;
		}
		node=node.getNext();
	}
}

/**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal. 
    *
    */
    public HeapNode findMin()
    {
    	return this.min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    *
    * our notes:
    * meld this => x-y-z with heap2 => a-b-c
    * before meld -> this.first.prev = z, heap2.first.prev = c
    * after meld -> this.first.prev = c, no heap2
    */
    public void meld (FibonacciHeap heap2)
    {
	   if(this.isEmpty()) {
		   if(heap2.isEmpty()) {
			   return; //no need to change anything
		   }else { //turn our heap to be heap2
			   this.first = heap2.first;
			   this.min = heap2.min;
			   this.numOfMarkedNodes = heap2.numOfMarkedNodes;
			   this.numOfTrees = heap2.numOfTrees;
			   this.size = heap2.size;
		   }
	   }
	   if(heap2.isEmpty()) { //no need to change anything, heap stays the same
		   return;
	   }
	   
    	//connecting c with x
    	heap2.first.prev.next = this.first; 
    	//connecting z with a
    	this.first.prev.next = heap2.first; 
    	//connecting x with c
    	this.first.prev = heap2.first.prev;
    	//connecting a with z
    	heap2.first.prev = this.first.prev;
    	
    	if(heap2.min.getKey() < this.min.getKey()) { //updating new min
    		this.min = heap2.min;
    		heap2.min = null;
    	}
    	this.size += heap2.size();
    	this.numOfMarkedNodes += heap2.numOfMarkedNodes;
    	this.numOfTrees += heap2.numOfTrees;
    }

   /**
    * public int size()
    *
    * Return the number of elements in the heap
    *   
    */
    public int size()
    {
    	return this.size;
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * 
    */
    public int[] countersRep()
    {
    	int[] arr = new int[this.maxRank() + 1];
    	arr[this.first.rank]++; 
    	
    	HeapNode curr = this.first.next;
    	while(curr != this.first) { //same as in maxRank
    		arr[curr.rank]++;
    		curr = curr.next;
    	}
        return arr; 
    }
	
    /**
     * public static int maxRank()
     * iterating over the ranks of each root, and returning the max rank
     */
    public int maxRank() {
    	int maxRank = this.first.rank;
    	HeapNode curr = this.first.next;
    	while(curr != this.first) { //circular linked list, eventually it will go back to the first one.
    		if(curr.rank > maxRank) {
    			maxRank = curr.rank;
    		}
    		curr = curr.next;
    	}
    	return maxRank;
    }
    
    
    
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap. 
    *
    */
    public void delete(HeapNode x) 
    {    
    	int minkey = this.min.getKey();
    	
    	decreaseKey(x, x.getKey()+minkey-1); // now x will have the lowest key in the heap
    	this.deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	// first we need to change the key
    	x.key -= delta;
    	// we need to check if we need to update the min
    	updatemin(x);
    	if (x.getParent()!=null) {
    		if (x.getKey()< x.getParent().getKey()) {
        		// we need to cut this branch
        		cascadingCut(x);
        	}
    	}    	
    }

   private void updatemin(HeapNode n) {
	if(this.min==null || n.getKey()<this.min.getKey()) {
		this.min = n;
	}
	
}




private void cut(HeapNode x) { 
	   numOfCuts++;
//	   numOfTrees++;
	   HeapNode xp = x.getParent();
	   x.parent = null;
	   if (x.marked) {
		   this.numOfMarkedNodes--;
		   x.marked = false;
	   }
	   if (x.getNext()==x) { // n has only one child
		   xp.child = null;
   		}else {
   			if(xp.getChild()==x) {
   	   			xp.child = x.next;
   			}
   			x.getNext().prev = x.getPrev(); x.getPrev().next = x.getNext(); // removing x from the DLL
   		}
	   xp.rank--;
	   insertTree(x);

}
 
   private void cascadingCut(HeapNode x) {
       HeapNode xp = x.getParent();
       cut(x);
       if (xp.getParent() != null) {
           if (!xp.marked) {
        	   numOfMarkedNodes ++;
               xp.marked = true;
               return;
           } else {
               cascadingCut(xp);
           }
       }
   }


private void insertTree(HeapNode tree) {
	//sanity check
	if (tree==null) {
		return;
	}
	// inserting this
	numOfTrees++;
	HeapNode first = this.first;
	HeapNode last = first.getPrev();
	this.first = tree;
	tree.next = first; first.prev = tree;
	tree.prev = last; last.next = tree;

	//update marks:
	if(tree.marked) {
		tree.marked=false;
		numOfMarkedNodes--;
	}
	// updating min
	if (tree.getKey()<=this.min.getKey()) {
		this.min=tree;
	}
}

/**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
    	return this.numOfTrees + 2*this.numOfMarkedNodes;
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    */
    public static int totalLinks()
    {    
    	return numOfLinks;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return numOfCuts;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k minimal elements in a binomial tree H.
    * The function should run in O(k*deg(H)). 
    * You are not allowed to change H.
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {    
    	int[] arr = new int[k];
    	FibonacciHeap fibHeap = new FibonacciHeap();
        HeapNode currNode = H.findMin(); //this is the first min key
        
        // k iterations for k min keys
        for(int i = 0; i < k; i++) { 
            if(currNode != null) { //the node has a son
            	//inserting the first node 
            	HeapNode iNode = fibHeap.insert(currNode.getKey()); // iNode = inserted Node
                iNode.setInfo(currNode); 							//connecting a node from fibHeap it's origin in H
                int currKey = currNode.getKey();
                currNode = currNode.next;
                while(currNode != null) {	//we always start from the first because we have pointer to the left most child
                	if(currNode.getKey() == currKey) {
                		break;
                	}
                	iNode = fibHeap.insert(currNode.getKey());
                    iNode.setInfo(currNode); 						
                    currNode = currNode.next;
                }
            } 
            currNode = fibHeap.findMin().getInfo(); //finding the min and it's origin to insert
            arr[i] = currNode.getKey();
            fibHeap.deleteMin(); 
            currNode = currNode.child; //continuing to the next level
        }
        return arr;
    }
    
    
    /**
     * HeapNode link(HeapNode a, HeapNode b)
     * link 2 trees with the rank (k) in the heap to one tree with rank k+1
     * returns the root of the new tree
     */
    public HeapNode link(HeapNode a, HeapNode b) {
        HeapNode parent;
        HeapNode child;
        if (a.getKey() < b.getKey()) { //the smaller will be the root
            parent = a;
            child = b;
        }else {
            parent = b;
            child = a;
        }
        parent.setChild(child);
        
        if(this.first == child) { //update first field
        	this.first = parent;
        }
        numOfLinks++;
        return parent;
    }
    
    
    /**
     * public HeapNode getFirst()
     * return the first HeapNode in the heap
     */
    public HeapNode getFirst() {
    	return this.first;
    }
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode{
    	public int key;
    	private int rank;
    	private boolean marked; //if marked than true, else false
    	private HeapNode child; //left child
    	private HeapNode next;
    	private HeapNode prev;
    	private HeapNode parent;
    	private HeapNode info; //only used for kMin

  	public HeapNode(int key) {
	    this.key = key;
	    this.rank = 0; //when created, heapNode has no children
	    this.child = null;
	    this.next = null;
	    this.prev = null;
	    this.parent = null;
	    this.info = null;
      }

  	public int getKey() {
	    return this.key;
      }
  	
  	public int getRank() {
  		return this.rank;
  	}
  	
  	public HeapNode getChild() {
  		return this.child;
  	}
  	
  	public HeapNode getNext() {
  		return this.next;
  	}
  	
  	public HeapNode getPrev() {
  		return this.prev;
  	}

  	public HeapNode getParent() {
  		return this.parent;
  	}
  	
  	public HeapNode getInfo() {
  		return this.info;
  	}
  	
  	public void setInfo(HeapNode node) {
  		this.info = node;
  	}
  	
  	public void setChild(HeapNode node) { //will always set it as leftMost child
  		node.parent = this; //updating node's parent
  		if(this.child == null) { //parent has no child
  			this.child = node;
  			node.next = node;
  			node.prev = node;
  			
  		}else {
  			this.child.setSibling(node);
  		}
  		this.child = node;
  		this.rank++;
  	}
  	
  	//add node to the beginning of the sibling's circular linked list
  	public void setSibling(HeapNode node) { 
  		this.prev.next = node; //closing the circle including the new sibling
		node.prev = this.prev; //like wise
		this.prev = node;		
		node.next = this;	
  	}

    }
}



