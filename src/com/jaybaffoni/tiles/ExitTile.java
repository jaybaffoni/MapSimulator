package com.jaybaffoni.tiles;

import java.awt.Color;
import java.awt.Graphics;

public class ExitTile extends ParkingTile {
	
	RoadTile exit;

	public ExitTile(int x, int y, int id, int speed) {
		super(x, y, id, speed);
	}

	public RoadTile getExit() {
		return exit;
	}

	public void setExit(RoadTile exit) {
		this.exit = exit;
	}
	
	@Override
	public Color getColor() {
		return Color.decode("#ff0000");
	}
	
	public void paint(Graphics g) {
		g.setColor(Color.decode("#808080"));
        g.fillRect(getCoordinates().x * 10, getCoordinates().y * 10, 10, 10);
	}

}
