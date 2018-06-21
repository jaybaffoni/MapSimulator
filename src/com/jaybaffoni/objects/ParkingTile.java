package com.jaybaffoni.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

public class ParkingTile extends Tile{

	String buildingId;
	private ArrayList<ParkingTile> neighbors;
	private ExitTile exit;
	private ParkingTile towardExitTile;
	
	public ParkingTile(int x, int y, int id,int speed) {
		this.coordinates = new Point(x, y);
		this.neighbors = new ArrayList<ParkingTile>();
		this.id = id;
		occupied = false;
		this.weight = speed;
	}
	
	public ParkingTile getTowardExitTile() {
		return towardExitTile;
	}

	public void setTowardExitTile(ParkingTile towardExitTile) {
		this.towardExitTile = towardExitTile;
	}

	public void addNeighbor(ParkingTile road) {
		neighbors.add(road);
	}
	
	public void addExit(ExitTile exit) {
		this.exit = exit;
	}
	
	public ExitTile getExitTile() {
		return exit;
	}
	
	public ArrayList<ParkingTile> getParkingNeighbors(){
		if(neighbors == null) {
			System.out.println("guess its null");
		}
		return neighbors;
	}
	
	@Override
	public Color getColor() {
		return Color.decode("#808080");
	}
	
	public String toString() {
		return "(" + id + ")";
	}

	public void paint(Graphics g) {
		g.setColor(Color.decode("#808080"));
        g.fillRect(getCoordinates().x * 10, getCoordinates().y * 10, 10, 10);
	}
}
