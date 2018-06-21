package com.jaybaffoni.objects;

import java.util.ArrayList;
import java.util.Arrays;

import com.jaybaffoni.structures.PriorityQueueMin;

public class Building {

	String id;
	EntranceTile entrance;
	ExitTile exit;
	int finalParkingIndex;
	ParkingTile[] lot;
	
	public Building(String id, EntranceTile entrance, ExitTile exit, int lotSize) {
		this.id = id;
		this.entrance = entrance;
		this.exit = exit;
		lot = new ParkingTile[lotSize];
	}
	
	/*public ArrayList<ParkingTile> getPathToExit(int index) {
		
		ArrayList<ParkingTile> path = new ArrayList<ParkingTile>();
		System.out.println("Finding all paths to index " + finalParkingIndex);
		int offset = lot[0].getId();
		index -= offset;
		System.out.println("From: " + (index));
		boolean[] visited = new boolean[lot.length];
		int[] prev = new int[lot.length];
		int[] toReturn = new int[lot.length];
		for(int i = 0; i < toReturn.length; i++) {
			toReturn[i] = -1;
			prev[i] = -1;
			visited[i] = false;
		}
		toReturn[index] = 0;
		int target = finalParkingIndex;
		PriorityQueueMin<Node> pq = new PriorityQueueMin<Node>();
		pq.add(new Node(index, 0));
		
		while(!pq.isEmpty()) {
			Node eval = pq.poll();
			if(eval.index == target) {
				break;
			}
			//System.out.println("Evaluating: " + eval.index);
			if(!visited[eval.index]) {
				//System.out.println("not visited");
				ParkingTile currentRoad = lot[eval.index];
				for(ParkingTile r: currentRoad.getParkingNeighbors()) {
					int newWeight = currentRoad.getWeight() + eval.combinedWeight;
					//System.out.println(newWeight);
					//check if there is a value yet in toReturn
					if(toReturn[r.getId() - offset] < 0) {
						//theres no value, just update it
						toReturn[r.getId() - offset] = newWeight;
						prev[r.getId() - offset] = eval.index;
					} else {
						//theres a value, compare
						if(newWeight < toReturn[r.getId() - offset]) {
							toReturn[r.getId() - offset] = newWeight;
							prev[r.getId() - offset] = eval.index;
						}
						
					}
					//this will add a new entry
					
					pq.add(new Node(r.getId() - offset, newWeight));
				}
				visited[eval.index] = true;
			} 
			
		}
		while(target != index) {
			//System.out.println(target);
			path.add(0, lot[target]);
			target = prev[target];
		}
		
		return path;
		
		
	}
	*/
	/*private class Node implements Comparable<Node>{
		
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
		
	}*/

	public void addParking(ParkingTile p, int offset) {
		lot[p.getId() - offset] = p;
	}
	
	public int getLotSize() {
		return lot.length;
	}
	
	public ParkingTile[] getLot() {
		return lot;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public EntranceTile getEntrance() {
		return entrance;
	}

	public void setEntrance(EntranceTile entrance) {
		this.entrance = entrance;
	}

	public void setFinalParkingIndex(int i) {
		this.finalParkingIndex = i;
		
	}
	
	
}
