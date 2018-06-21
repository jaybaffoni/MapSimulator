package com.jaybaffoni.objects;

import java.util.ArrayList;

import com.jaybaffoni.structures.PriorityQueueMin;
import com.jaybaffoni.tiles.RoadTile;

public class Navigator {
	
	//acts as a navigation system for vehicles
	//takes an input road and a target road
	//calculates best path between them, keeping list of roads
	//might need to run asynchronously if the graph search takes too long
	
	RoadTile[] graph;
	int[] delays;
	
	
	public Navigator(RoadTile[] graph) {
		super();
		this.graph = graph;
		delays = new int[graph.length];
		for(int x = 0; x < delays.length; x++) {
			delays[x] = 0;
		}
	}
	
	public ArrayList<RoadTile> findShortestPath(int index, int target) {
		ArrayList<RoadTile> path = new ArrayList<RoadTile>();
		//System.out.println("Finding all paths from: " + index);
		boolean[] visited = new boolean[graph.length];
		int[] prev = new int[graph.length];
		int[] toReturn = new int[graph.length];
		for(int i = 0; i < toReturn.length; i++) {
			toReturn[i] = -1;
			prev[i] = -1;
			visited[i] = false;
		}
		toReturn[index] = 0;
		//visited[index] = true;
		PriorityQueueMin<Node> pq = new PriorityQueueMin<Node>();
		pq.add(new Node(index, 0));
		//System.out.println("PQ empty? " + pq.isEmpty());
		while(!pq.isEmpty()) {
			Node eval = pq.poll();
			if(eval.index == target) {
				break;
			}
			//System.out.println("Evaluating: " + eval.index);
			// TODO has it been visited?
			if(!visited[eval.index]) {
				//System.out.println("not visited");
				RoadTile currentRoad = graph[eval.index];
				for(RoadTile r: currentRoad.getNeighbors()) {
					int newWeight = (currentRoad.getWeight() + delays[eval.index]) + eval.combinedWeight;
					//System.out.println(newWeight);
					//check if there is a value yet in toReturn
					if(toReturn[r.getId()] < 0) {
						//theres no value, just update it
						toReturn[r.getId()] = newWeight;
						prev[r.getId()] = eval.index;
					} else {
						//theres a value, compare
						if(newWeight < toReturn[r.getId()]) {
							toReturn[r.getId()] = newWeight;
							prev[r.getId()] = eval.index;
						}
						
					}
					//this will add a new entry
					
					pq.add(new Node(r.getId(), newWeight));
				}
				visited[eval.index] = true;
			} 
			
		}
		while(target != index) {
			path.add(0, graph[target]);
			target = prev[target];
		}
		
		return path;
	}
	
	/*public int getDistanceToTarget(int target) {
		return shortestPaths[target];
	}*/
	public void updateDelay(int id, int delay, boolean strong) {
		int currentDelay = delays[id];
		if(delay > currentDelay || strong) {
			delays[id] = delay;
		}
	}
	
	private class Node implements Comparable<Node>{
		
		int index;
		int combinedWeight;

		public Node(int index, int combinedWeight) {
			super();
			this.index = index;
			this.combinedWeight = combinedWeight;
		}

		@Override
		public int compareTo(Node other) {
			return this.combinedWeight - other.combinedWeight;
		}
		
		public String toString() {
			return index + "," + combinedWeight;
		}
		
	}
	

}
