/*
 * 
 * 
 * 
 * 
 * 
 */

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
		this.occupied = occupied;
	}
	
	public int getWeight() {
		return weight;
	}

	public Point getCoordinates() {
		return coordinates;
	}
	
	public int getX() {
		return coordinates.x;
	}
	
	public int getY() {
		return coordinates.y;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public abstract String toString();
	
	public abstract Color getColor();

	public abstract void paint(Graphics g);
	
	public void print() {
		System.out.println("X: " + getX() + ", Y: " + getY());
	}

}
