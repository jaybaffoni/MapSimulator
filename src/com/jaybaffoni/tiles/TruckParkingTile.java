package com.jaybaffoni.tiles;

import java.awt.Color;
import java.awt.Graphics;

public class TruckParkingTile extends ParkingTile{
	
	private TruckParkingTile neighbor;
	private ParkingTile parking;

	public TruckParkingTile(int x, int y, int id, int speed) {
		super(x, y, id, speed);
		// TODO Auto-generated constructor stub
	}

	
	
	public TruckParkingTile getNeighbor() {
		return neighbor;
	}



	public void setNeighbor(TruckParkingTile neighbor) {
		this.neighbor = neighbor;
	}



	public ParkingTile getParking() {
		return parking;
	}



	public void setParking(ParkingTile parking) {
		this.parking = parking;
	}



	public void paint(Graphics g) {
		g.setColor(Color.decode("#808080"));
        g.fillRect(getCoordinates().x * 10, getCoordinates().y * 10, 10, 10);
        g.setColor(Color.CYAN);
        g.drawRect((getCoordinates().x * 10) + 1, (getCoordinates().y * 10) + 1, 8, 8);
	}
	
}
