package project5;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
* Disjoint set class, using union by rank and path compression.
* Elements in the set are numbered starting at 0.
* @author Mark Allen Weiss
*/
class DisjSets {
	/**
	 * Construct the disjoint sets object.
	 * @param numElements the initial number of disjoint sets.
	 */
	public DisjSets(int numElements) {
		s = new int [numElements];
		for (int i=0; i<s.length; i++)
			s[i] = -1;
	}
	
	/**
	 * Union two disjoint sets using the height heuristic.
	 * For simplicity, we assume root1 and root2 are distinct
	 * and represent set names.
	 * @param root1 the root of set 1.
	 * @param root2 the root of set 2.
	 */
	public void union(int root1, int root2) {
		//root2 is deeper
		if (s[root2] < s[root1]) {
			// Make root2 new root
			s[root1] = root2;   
		}
		else {
			//Update height if same
			if (s[root1] == s[root2])
				s[root1]--;         
			//Make root1 new root
			s[root2] = root1;     
		}
	}
	
	/**
	 * Perform a find with path compression.
	 * Error checks omitted again for simplicity.
	 * @param x the element being searched for.
	 * @return the set containing x.
	 */
	public int find(int x) {
		if (s[x] < 0)
			return x;
		else
			return s[x] = find(s[x]);
	}
	private int[] s;
}

class BinaryHeap<AnyType extends Comparable<? super AnyType>> {
    //Construct the binary heap.
    public BinaryHeap() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Construct the binary heap.
     * @param capacity the capacity of the binary heap.
     */
    public BinaryHeap(int capacity) {
        currentSize = 0;
        array = (AnyType[]) new Comparable[capacity + 1];
    }
    
    //Construct the binary heap given an array of items.
    public BinaryHeap(AnyType[] items) {
        currentSize = items.length;
        array = (AnyType[]) new Comparable[(currentSize + 2) * 11 / 10];

        int i = 1;
        for (AnyType item : items)
            array[i++] = item;
        buildHeap();
    }

    /**
     * Insert into the priority queue, maintaining heap order.
     * Duplicates are allowed.
     * @param x the item to insert.
     */
    public void insert(AnyType x) {
        if (currentSize == array.length - 1)
            enlargeArray(array.length * 2 + 1);

        // Percolate up
        int hole = ++currentSize;
        for (array[0] = x; x.compareTo( array[hole / 2] ) < 0; hole /= 2)
            array[hole] = array[hole / 2];
        array[hole] = x;
    }

    private void enlargeArray(int newSize) {
        AnyType [] old = array;
        array = (AnyType []) new Comparable[newSize];
        for (int i = 0; i < old.length; i++)
            array[i] = old[i];        
    }
    
    /**
     * Find the smallest item in the priority queue.
     * @return the smallest item, or throw an UnderflowException if empty.
     */
    public AnyType findMin() throws Exception {
        if (isEmpty())
            throw new Exception();
        return array[1];
    }

    /**
     * Remove the smallest item from the priority queue.
     * @return the smallest item, or throw an UnderflowException if empty.
     */
    public AnyType deleteMin() throws Exception {
        if (isEmpty())
            throw new Exception();

        AnyType minItem = findMin();
        array[1] = array[currentSize--];
        percolateDown(1);

        return minItem;
    }

    /**
     * Establish heap order property from an arbitrary
     * arrangement of items. Runs in linear time.
     */
    private void buildHeap() {
        for (int i = currentSize / 2; i > 0; i--)
            percolateDown(i);
    }

    /**
     * Test if the priority queue is logically empty.
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty() {
        return currentSize == 0;
    }

    //Make the priority queue logically empty.
    public void makeEmpty() {
        currentSize = 0;
    }
    private static final int DEFAULT_CAPACITY = 10;

    //Number of elements in heap
    private int currentSize;   
    //The heap array
    private AnyType[] array;

    /**
     * Internal method to percolate down in the heap.
     * @param hole the index at which the percolate begins.
     */
    private void percolateDown(int hole) {
        int child;
        AnyType tmp = array[hole];

        for (; hole * 2 <= currentSize; hole = child) {
            child = hole * 2;
            if (child != currentSize &&
                    array[child + 1].compareTo(array[ child ]) < 0)
                child++;
            if (array[child].compareTo(tmp) < 0)
                array[hole] = array[child];
            else
                break;
        }
        array[hole] = tmp;
    }
}

class Edge implements Comparable<Edge> {
	//Class variables
	private String start;
	private int distance;
	private String destination;
	
	//Constructor
	public Edge(String s, int dist, String dest) {
		start = s;
		distance = dist;
		destination = dest;
	}
	
	//Get distance
	public int getDistance() {
		return distance;
	}
	
	//Return the edge
	public String printEdge() {
		return start + " - " + distance + " - " + destination;
	}

	//compareTo if Edge objects
	@Override
	public int compareTo(Edge edge) {
		//Priorities of objects
		int distance = this.getDistance();
		int distance2 = edge.getDistance();
		
		//See if less than, greater than, or equal to
		if (distance < distance2)
			return -1;
		else if (distance == distance2)
			return 1;
		else
			return 0;
	}
}

public class Kruskals {
	public static void main(String[] args) throws Exception {
		BufferedReader br; 
		String line = "";
		
		try {
			//Open file and priority queue of edges
			br = new BufferedReader(new FileReader("assn9_data.csv"));
			BinaryHeap<Edge> binaryHeap = new BinaryHeap<Edge>();
			
			//Read each line and get starting points
			int size = 0;
			HashMap<String, Integer> starts = new HashMap<String, Integer>();
			
			while ((line = br.readLine()) != null) {
				//Put the start and its index in hash map 
				String start = line.split(",")[0];
				starts.put(start, size);
				
				for (int i=1; i<line.split(",").length; i+=2) {
					//Destinations and distances
					String destination = line.split(",")[i];
					int distance = Integer.parseInt(line.split(",")[i + 1]);
					
					//Make object to put in binary heap
					Edge edge = new Edge(start, distance, destination);
					binaryHeap.insert(edge);
				}
				++size;
			}
			//Disjoint set to make MST and list to store all edges
			DisjSets ds = new DisjSets(size);
			ArrayList<String> edgeList = new ArrayList<String>();
			
			while (!binaryHeap.isEmpty()) {
				//Get first edge in heap
				Edge edge = (Edge) binaryHeap.deleteMin();
				edgeList.add(edge.printEdge());
			}
			//Array list for paths of MST
			ArrayList<String> paths = new ArrayList<String>();
			
			for (int i=0; i<edgeList.size(); i++) {
				//Get start and destination of path
				String start = edgeList.get(i).split(" - ")[0];
				String dest = edgeList.get(i).split(" - ")[2];
				
				//Find the starting number and destination number
				int startNum = ds.find(starts.get(start));
				int destNum = ds.find(starts.get(dest));
				
				//If not equal, union and add edge to list of edges
				if (startNum != destNum) {
					ds.union(startNum, destNum);
					paths.add(edgeList.get(i));
				}
			}
			//Store sum of MST distances
			int sum = 0;
			
			//Show edges and sum
			for (String path: paths) {
				System.out.println(path);
				
				int distance = Integer.parseInt(path.split(" - ")[1]);
				sum += distance;
			}
			System.out.println("\nTotal MST distance: " + sum);
		}
		//Error if not found
		catch (FileNotFoundException e) {
			System.out.println("File not found!");
		}
	}
}