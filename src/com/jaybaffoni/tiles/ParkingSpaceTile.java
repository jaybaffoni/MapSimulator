package com.jaybaffoni.tiles;

import java.awt.Color;
import java.awt.Graphics;

public class ParkingSpaceTile extends ParkingTile {
	
	Tile connector;

	public ParkingSpaceTile(int x, int y, int id, int speed) {
		super(x, y, id, speed);
		// TODO Auto-generated constructor stub
	}

	public void setConnector(Tile connect) {
		connector = connect;
	}
	
	
	public Tile getConnector() {
		return connector;
	}

	@Override
	public Color getColor() {
		return Color.decode("#ffff00");
	}
	
	public void paint(Graphics g) {
		g.setColor(Color.decode("#808080"));
        g.fillRect(getCoordinates().x * 10, getCoordinates().y * 10, 10, 10);
        g.setColor(Color.white);
        g.drawRect((getCoordinates().x * 10) + 1, (getCoordinates().y * 10) + 1, 8, 8);
	}

}
