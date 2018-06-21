package com.jaybaffoni.objects;

import java.awt.Color;
import java.awt.Graphics;

public abstract class Vehicle {
	
	public abstract void move();
	
	public abstract int getX();
	
	public abstract int getY();
	
	public abstract boolean isReady();
	
	//public abstract void setDestination(RoadTile road);
	public void paint(Graphics g) {
		g.setColor(Color.BLUE);
        g.fillRect((getX() * 10) + 2, (getY() * 10) + 2, 6, 6);
	}
}
