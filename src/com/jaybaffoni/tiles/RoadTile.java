package com.jaybaffoni.tiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

public class RoadTile extends Tile{
	
	private ArrayList<RoadTile> neighbors;
	//weights are: 60:3, 45:4, 30:6, 15:12
	//private int id;
	
	public RoadTile(int x, int y, int id,int speed) {
		this.coordinates = new Point(x, y);
		this.neighbors = new ArrayList<RoadTile>();
		this.id = id;
		occupied = false;
		this.weight = speed;
	}
	
	public void addNeighbor(RoadTile road) {
		neighbors.add(road);
	}
	
	public int getNeighborCount() {
		return neighbors.size();
	}
	
	public ArrayList<RoadTile> getNeighbors(){
		return neighbors;
	}
	
	public void printNeighbors() {
		for(RoadTile r: neighbors) {
			System.out.println(r.getId());
		}
	}
	
	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	public Color getColor() {
		if(weight == 6) {
			return Color.decode("#808080");
		} else if(weight == 4) {
			return Color.decode("#aaaaaa");
		} else {
			return Color.decode("#ffffff");
		}
	}

	public String toString() {
		return coordinates.x + "," + coordinates.y + "," + weight;
	}
	
	public void paint(Graphics g) {
		g.setColor(getColor());
        g.fillRect(getCoordinates().x * 10, getCoordinates().y * 10, 10, 10);
	}

}
