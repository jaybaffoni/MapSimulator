package com.jaybaffoni.objects;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;

import org.omg.CORBA.Request;

import com.jaybaffoni.tiles.EntranceTile;
import com.jaybaffoni.tiles.ExitTile;

public class AutoShop extends Building{
	
	ArrayList<TowTruck> towTrucks;
	public int capacity = 10;
	ArrayList<Car> targetQueue;
	TowTruck nextInLine = null;
	ArrayList<Car> repairs;

	public AutoShop(String id, EntranceTile entrance, ExitTile exit, int lotSize) {
		super(id, entrance, exit, lotSize);
		towTrucks = new ArrayList<TowTruck>();
		targetQueue = new ArrayList<Car>();
		repairs = new ArrayList<Car>();
	}
	
	public void addTarget(Car c) {
		//System.out.println("Adding Target");
		targetQueue.add(c);
		capacity--;
		//System.out.println(capacity);
	}
	
	public void addTowTruck(TowTruck t) {
		towTrucks.add(t);
	}
	
	public TowTruck getTowTruck() {
		return towTrucks.remove(0);
	}
	
	public void setInternet(Internet net) {
		for(TowTruck tt: towTrucks) {
			tt.setInternet(net);
		}
	}
	
	public void addToRepairs(Car c) {
		repairs.add(c);
		c.beginRepairs();
	}
	
	public void move() {
		//iterate through repairing cars
		Iterator<Car> iterator = repairs.iterator();
		while (iterator.hasNext()) {
		   Car r = iterator.next();
		   if(!r.isRepairing()) {
			   iterator.remove();
			   capacity++;
		   }
		}
		if(!targetQueue.isEmpty() && nextInLine != null) {
			//there are calls in the queue and a tow truck ready
			nextInLine.setTarget(targetQueue.remove(0));
			nextInLine = null;
		}
		for(TowTruck t: towTrucks) {
			t.move();
		}
	}
	
	public boolean hasVacancy() {
		return capacity > 0;
	}
	
	public boolean hasNextInLine() {
		return nextInLine != null;
	}
	
	public void setNextInLine(TowTruck t) {
		nextInLine = t;
	}
	
	public void paint(Graphics g) {
		for(TowTruck t: towTrucks) {
			t.paint(g);
		}
	}

}
