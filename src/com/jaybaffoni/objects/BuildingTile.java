package com.jaybaffoni.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class BuildingTile extends Tile{
	
	public BuildingTile(int id, int x, int y) {
		this.id = id;
		this.coordinates = new Point();
		this.coordinates.x = x;
		this.coordinates.y = y;
	}
	
	public void paint(Graphics g) {
		g.setColor(Color.decode("#000000"));
        g.fillRect(getCoordinates().x * 10, getCoordinates().y * 10, 10, 10);
	}

	@Override
	public Color getColor() {
		// TODO Auto-generated method stub
		return null;
	}

}
