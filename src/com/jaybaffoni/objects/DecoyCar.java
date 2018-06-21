package com.jaybaffoni.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import com.jaybaffoni.tiles.EntranceTile;
import com.jaybaffoni.tiles.RoadTile;
import com.jaybaffoni.tiles.Tile;

public class DecoyCar extends Vehicle{
	
	private RoadTile road;
	private RoadTile nextRoad = null;
	private int ticksToWait = 0;
	private String name;

	public DecoyCar(String name, Tile[][] roads, int x, int y) {
		this.name = name;
		this.road = (RoadTile) roads[x][y];
		ticksToWait = road.getWeight();
	}
	
	@Override
	public void move() {
		//System.out.println("move called");
		if(ticksToWait == 0) {
			//System.out.println("ready to move");
			//get neighbor list
			ArrayList<RoadTile> neighbors = road.getNeighbors();
			//pick random neighbor
			int nextIndex = ThreadLocalRandom.current().nextInt(0, neighbors.size());
			nextRoad = neighbors.get(nextIndex);
			
			while(nextRoad instanceof EntranceTile) {
				nextIndex = ThreadLocalRandom.current().nextInt(0, neighbors.size());
				nextRoad = neighbors.get(nextIndex);
			}
			
			if(!nextRoad.isOccupied()) {
				//if null, move and make current space null
				this.road.setOccupied(false);
				nextRoad.setOccupied(true);
				this.road = nextRoad;
				ticksToWait = this.road.getWeight();
			}
		} else {
			//System.out.println("ticks left: " + ticksToWait);
			ticksToWait--;
		}
		
	}

	public int getX() {
		return road.getCoordinates().x;
	}
	
	public int getY() {
		return road.getCoordinates().y;
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public String toString() {
		return name;
	}
	
	public void paint(Graphics g) {
		g.setColor(Color.BLUE);
        g.fillRect((getX() * 10) + 2, (getY() * 10) + 2, 6, 6);
	}

}
