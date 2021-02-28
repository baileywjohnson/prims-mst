import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Prims{
	
	public static void main(String[] args) throws FileNotFoundException{
		//Provide path to the target text file
		File infoFile = new File("");
		Scanner scan = new Scanner(infoFile);
		int vertMax = 0;
		ArrayList<ArrayList> graphFill = new ArrayList<ArrayList>();
		
		//Read in Each Line of the File
		while(scan.hasNextLine()) {
			String input = scan.nextLine();
			
			//Split Into Array on Spaces (https://stackoverflow.com/questions/7899525/how-to-split-a-string-by-space/7899558)
			String[] splitted = input.split("\\s+");
			int x = 0;
			int y = 0;
			int z = 0;
			
			//Set x (source), y (destination) and z (weight) equal to first 3 elements of array (parsed to integer)
			for(int i = 0; i < splitted.length; i++) {
				if(i == 0) {
					x = Integer.parseInt(splitted[i]);
				}
				if(i == 1) {
					y = Integer.parseInt(splitted[i]);
				}
				if(i == 2) {
					z = Integer.parseInt(splitted[i]);
				}
			}
			
			//store x, y and z into new data arraylist (representing one edge)
			ArrayList<Integer> data = new ArrayList<Integer>();
			data.add(x);
			data.add(y);
			data.add(z);
			
			//add data/edge arraylist to graphFill array list (contains all edges)
			graphFill.add(data);
			
			//find the highest value of the current source/destination
			int max = Math.max(x, y);
			
			//if the maximum of the current values is higher than the overall max for all lines, replace the overall (vertMax)
			if(max > vertMax) {
				vertMax = max;
			}
		}
		
		int vertices = vertMax + 1;
		Graph graph = new Graph(vertices);
		
		//Loop through the graphFill (edges) arrayList, and fill the graph object using the addEdge method
		for(int i = 0; i < graphFill.size(); i++) {
			int a = 0;
			int b = 0;
			int c = 0;
			for(int j = 0; j < graphFill.get(i).size(); j++) {
				if(j == 0) {
					a = (int) graphFill.get(i).get(j);
				}
				if(j == 1) {
					b = (int) graphFill.get(i).get(j);
				}
				if(j == 2) {
					c = (int) graphFill.get(i).get(j);
				}
			}
			graph.addEdge(a, b, c);
		}
		
		//Call primMST to build a minimum spanning tree out of the current graph object
		graph.primMST();
	}

	static class Edge{
		int source;
		int destination;
		int weight;
		
		public Edge(int source, int destination, int weight) {
			this.source = source;
			this.destination = destination;
			this.weight = weight;
		}
	}
	
	static class Graph{
		int vertices;
		//Represents all edges that belong to the graph
		ArrayList<Edge> allEdges = new ArrayList<>();
		
		Graph(int vertices){
			this.vertices = vertices;
		}
		
		//Adds an Edge to the Current Graph Object (allEdges)
		public void addEdge(int source, int destination, int weight) {
			Edge edge = new Edge(source, destination, weight);
			allEdges.add(edge);
		}
		
		//Builds a MST based on Prim's algorithm using the edges currently associated with the graph object
		public void primMST() {
			
			//'mst' represents the current minimum spanning tree at any point in the function
			ArrayList<Edge> mst = new ArrayList<>();
			
			//'pending' represents the frontier edges available to become part of the MST
			ArrayList<Edge> pending = new ArrayList<>();
			
			//Initially retrieve all edges of the graph connected to vertex 0 (starting vertex) and add to pending
			for(int i=0; i<allEdges.size(); i++) {
				if(allEdges.get(i).source == 0 || allEdges.get(i).destination == 0) {
					pending.add(allEdges.get(i));
				}
			}
			
			//Choose the edge in 'pending' with the least weight as the first connection, add to 'mst'
			mst.add(chooseEdge(pending));
			
			//Remove from allEdges the edge previously added to 'mst' 
			allEdges.remove(chooseEdge(pending));
			pending.clear();
			
			//Continue to loop through all other edges that connect, choosing the minimum to become part of MST
			boolean build_mst = true;
			while(build_mst) {
				
				//generates list of current "visited" vertices in MST (sources/destinations of all edges currently in list)
				ArrayList<Integer> visited = getVisited(mst);
				ArrayList<Edge> pending2 = new ArrayList<>();
				
				//loop through allEdges
				for(int i = 0; i < allEdges.size(); i++) {
					
					//if the source or destination of the current edge is in visited (a.k.a. if frontier edge)
					if(visited.contains(allEdges.get(i).source) || visited.contains(allEdges.get(i).destination)) {
						
						//if source and destination are not BOTH in visited (a.k.a. it won't create a cycle)
						if(!(visited.contains(allEdges.get(i).source) && visited.contains(allEdges.get(i).destination))) {
							//Add current edge to pending2 (elligible to be added to mst)
							pending2.add(allEdges.get(i));
						}
						//otherwise this edge is known to create a cycle, go ahead and remove from main edge list
						else {
							allEdges.remove(i);
						}
					}
				}
				
				//After all of the possible edges have been resolved (if there are any)
				if(pending2.size() != 0){
					//Choose the smallest weight edge (and maybe resolve ties) from pending2 and add to mst
					mst.add(chooseEdge(pending2));
					//Remove chosen edge from main edge list
					allEdges.remove(chooseEdge(pending2));
					pending2.clear();
				}
				
				//If the size of the main list of edges is 0 (we've gone through all)
				if(allEdges.size() == 0) {
					//Break Loop
					build_mst = false;
				}
			}
			System.out.println("Minimum Spanning Tree");
			printGraph(mst);
		}
		
		//Returns an ArrayList<Integer> that contains a list of the 'visited' vertices (number is either the source/destination of any edge in mst)
		public ArrayList<Integer> getVisited(ArrayList<Edge> mst){
			ArrayList<Integer> visited = new ArrayList<>();
			for(int i = 0; i < mst.size(); i++) {
				if(!visited.contains(mst.get(i).source)) {
					visited.add(mst.get(i).source);
				}
				if(!visited.contains(mst.get(i).destination)) {
					visited.add(mst.get(i).destination);
				}
			}
			return visited;
		}
		
		//Returns the edge from the given array with the lowest weight
		public Edge chooseEdge(ArrayList<Edge> pending){
			Edge temp = new Edge(0, 0, 5000);
			for(int i = 0; i < pending.size(); i++) {
				if(pending.get(i).weight < temp.weight) {
					temp = pending.get(i);
				}
				/**
				 * This provides some of the framework needed for resolving
				 * edge-weight ties 'alphabetically'.
				 * 
				else if(pending.get(i).weight == temp.weight) {
					if(pending.get(i).source < temp.source) {
						temp = pending.get(i);
					}
				}
				**/
			}
			return temp;
		}
		
		//Displays a Given Tree ('Adjacency List')
		public void printGraph(ArrayList<Edge> edgeList) {
			for (int i = 0; i <edgeList.size() ; i++) {
        			Edge edge = edgeList.get(i);
        			System.out.println("Edge " + i + "\nsource: " + edge.source +
        			", destination: " + edge.destination + ", weight: " + edge.weight + "\n");
			}
		}
	}
}