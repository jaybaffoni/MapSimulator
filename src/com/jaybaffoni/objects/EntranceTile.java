package com.jaybaffoni.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class EntranceTile extends RoadTile{
	ArrayList<ParkingTile> parkingNeighbors;
	ArrayList<ParkingSpaceTile> spaceNeighbors;

	public EntranceTile(int x, int y, int id, int speed) {
		super(x, y, id, speed);
		parkingNeighbors = new ArrayList<ParkingTile>();
		spaceNeighbors = new ArrayList<ParkingSpaceTile>();
		
	}
	
	public ArrayList<ParkingTile> getParkingNeighbors(){
		return parkingNeighbors;
	}
	
	public void addParkingNeighbor(ParkingTile parking) {
		parkingNeighbors.add(parking);
	}
	
	public void addParkingSpaceNeighbor(ParkingSpaceTile space) {
		spaceNeighbors.add(space);
	}

	public Color getColor() {
		return Color.decode("#00ff00");
	}
	
	public void paint(Graphics g) {
		g.setColor(Color.decode("#808080"));
        g.fillRect(getCoordinates().x * 10, getCoordinates().y * 10, 10, 10);
	}
	
}
