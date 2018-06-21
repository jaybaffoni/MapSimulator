package com.jaybaffoni.tiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public abstract class Tile {
	
	int id;
	int weight;
	Point coordinates;
	boolean occupied;
	
	public int getId() {
		return id;
	}
	
	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean occupied) {
		if(coordinates.x == 24 && coordinates.y == 13) {
			//System.out.println("Changed target from " + occupied + " to " + !occupied);
		}
		this.occupied = occupied;
	}
	
	public int getWeight() {
		return weight;
	}

	public Point getCoordinates() {
		return coordinates;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}



	public String toString() {
		if(occupied) {
			return "(X)";
		}
		return "( )";
	}
	
	public abstract Color getColor();

	public void paint(Graphics g) {
		g.setColor(Color.decode("#000000"));
        g.fillRect(getCoordinates().x * 10, getCoordinates().y * 10, 10, 10);
	}

}
