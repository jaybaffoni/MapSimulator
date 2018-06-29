package com.jaybaffoni.objects;

import java.util.ArrayList;
import java.util.HashMap;

public class Internet {

	Navigator GPS;
	HashMap<String, ArrayList<Building>> directory;
	
	public Internet() {
		directory = new HashMap<String, ArrayList<Building>>();
	}
	
	public void setGPS(Navigator GPS) {
		this.GPS = GPS;
	}
	
	public Navigator getGPS() {
		return GPS;
	}
	
	public void addBuilding(String s, Building b) {
		if(directory.containsKey(s)) {
			ArrayList<Building> buildings = directory.get(s);
			buildings.add(b);
		} else {
			ArrayList<Building> buildings = new ArrayList<Building>();
			buildings.add(b);
			directory.put(s, buildings);
		}
	}
	
	public AutoShop getAutoShop() {
		ArrayList<Building> buildings = directory.get("autoShop");
		//System.out.println("SHOPS: " + buildings.size());
		for(Building b: buildings) {
			AutoShop a = (AutoShop) b;
			if(a.hasNextInLine()) {
				if(a.hasVacancy()) {
					return a;
				}
			}
		}
		for(Building b: buildings) {
			AutoShop a = (AutoShop) b;
			if(a.hasVacancy()) {
				return a;
			}
		}
		return null;
	}
	
}
