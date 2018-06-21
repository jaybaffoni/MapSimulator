package com.jaybaffoni.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Car extends Vehicle{

	private Tile currentTile;
	private RoadTile nextRoad = null;
	private Building currentBuilding = null;
	private Building destination = null;
	private int ticksToWait = 0;
	private String name;
	private ArrayList<RoadTile> path;
	Navigator navSystem;
	Tile[][] landMap;
	enum State {
		READY, MOVING, PARKING, PARKED, EXITING;
	}
	State state;
	int[] xCheck = {1,-1,0,0};
	int[] yCheck = {0,0,1,-1};
	//ArrayList<ParkingTile> pathToExit;
	int currentDelay = 0;
	int newRouteDelay = 0;
	int maxDelay = 0;
	
	public Car(String name, Tile[][] landMap, int x, int y) {
		this.name = name;
		this.currentTile = landMap[x][y];
		this.currentTile.setOccupied(true);
		ticksToWait = currentTile.getWeight();
		state = State.PARKED;
		this.landMap = landMap;
	}
	
	public void setNavSystem(Navigator navSystem) {
		this.navSystem = navSystem;
	}
	
	public boolean isReady() {
		return destination == null;
	}
	
	public int getX() {
		return currentTile.getCoordinates().x;
	}
	
	public int getY() {
		return currentTile.getCoordinates().y;
	}
	
	public String toString() {
		return state.toString();
	}
	
	public void setDestination(Building building) {
		//System.out.println(name + " destination set to " + building.getId());
		this.destination = building;
		path = null;
		//System.out.println(destination.toString());
		if(state == State.READY) {
			state = State.MOVING;
		} else if(state == State.PARKED) {
			state = State.EXITING;
		}
		
	}

	@Override
	public void move() {
		//System.out.println(this.toString());
		//System.out.println("move called");
		//check if the car is going anywhere
		
		if(state == State.PARKING) {
			if(ticksToWait == 0) {
				if(!checkSpaces()) {
					//System.out.println("No spaces here");
					//find next parking road
					ArrayList<ParkingTile> neighbors = null;
					ExitTile parkExit = null;
					if(currentTile instanceof ExitTile) {
						RoadTile next = ((ExitTile) currentTile).getExit();
						if(!next.isOccupied()) {
							currentTile.setOccupied(false);
							next.setOccupied(true);
							currentTile = next;
							ticksToWait = currentTile.getWeight();
							state = State.MOVING;
							destination = currentBuilding;
						}
						return;
					} else if(currentTile instanceof ParkingTile) {
						ParkingTile loc = (ParkingTile)currentTile;
						neighbors = loc.getParkingNeighbors();
						System.out.println("from parking");
						parkExit = loc.getExitTile();
					} else if(currentTile instanceof EntranceTile){
						EntranceTile loc = (EntranceTile)currentTile;
						neighbors = loc.getParkingNeighbors();
						//System.out.println("from entrance");
						//System.out.println(neighbors.size());
					}
					ParkingTile nextPark;
					if(neighbors == null || neighbors.size() == 0) {
						//System.out.println(currentTile.getCoordinates().x + "," + currentTile.getCoordinates().y);
						//System.out.println(currentTile.getId());
					}
					if(neighbors.size() > 1) {
						int nextIndex = ThreadLocalRandom.current().nextInt(0, neighbors.size());
						nextPark = neighbors.get(nextIndex);
						while(nextPark instanceof ExitTile) {
							nextIndex = ThreadLocalRandom.current().nextInt(0, neighbors.size());
							nextPark = neighbors.get(nextIndex);
						}
					} else if(neighbors.size() == 1){
						nextPark = neighbors.get(0);
					} else {
						nextPark = parkExit;
					}
					//System.out.println("got next space");
					if(!nextPark.isOccupied()) {
						//System.out.println("space is open");
						//if null, move and make current space null
						this.currentTile.setOccupied(false);
						nextPark.setOccupied(true);
						currentTile = nextPark;
						ticksToWait = currentTile.getWeight();
					} else {
						//System.out.println("space was occupied");
					}
					return;
				} else {
					System.out.println("no connected spaces");
				}
			} else {
				ticksToWait--;
				return;
			}
		} else if(state == State.EXITING) {
			//System.out.println("Exiting");
			if(ticksToWait == 0) {
				///System.out.println("ready to move");
				if(currentTile instanceof ParkingSpaceTile) {
					System.out.println("leaving parking space");
					ParkingSpaceTile temp = (ParkingSpaceTile)currentTile;
					Tile next = temp.getConnector();
					//System.out.println("x: " + currentTile.getCoordinates().x + " , y: " + currentTile.getCoordinates().y);
					if(next == null) {
						next = getConnectedParking();
					}
					if(!next.isOccupied()) {
						currentTile = next;
						currentTile.setOccupied(true);
						temp.setOccupied(false);
						ticksToWait = currentTile.getWeight();
					}
					return;
					
				} else if(currentTile instanceof ExitTile) {
					//go one tile more, then calculate new path below
					//System.out.println("At the exit");
					ExitTile temp = (ExitTile) currentTile;
					RoadTile next = temp.getExit();
					if(!next.isOccupied()) {
						currentTile = next;
						currentTile.setOccupied(true);
						temp.setOccupied(false);
						ticksToWait = currentTile.getWeight();
						state = State.MOVING;
					}
					return;
				} else if(currentTile instanceof ParkingTile) {
					ParkingTile temp = (ParkingTile)currentTile;
					//if there's an attached exit, take it
					if(temp.getExitTile() != null) {
						ExitTile next = temp.getExitTile();
						if(!next.isOccupied()) {
							currentTile = next;
							currentTile.setOccupied(true);
							temp.setOccupied(false);
							ticksToWait = currentTile.getWeight();
						}
					} else if(temp.getTowardExitTile() != null) {
						//theres a choice, so take the one leading out
						ParkingTile next = temp.getTowardExitTile();
						if(!next.isOccupied()) {
							currentTile = next;
							currentTile.setOccupied(true);
							temp.setOccupied(false);
							ticksToWait = currentTile.getWeight();
						}
					} else {
						//there's no attached exit, and theres no preference, so take the next parkingtile
						ParkingTile next = temp.getParkingNeighbors().get(0);
						if(!next.isOccupied()) {
							currentTile = next;
							currentTile.setOccupied(true);
							temp.setOccupied(false);
							ticksToWait = currentTile.getWeight();
						}
					}
					return;
				} else if(currentTile instanceof EntranceTile) {
					//find next, go to next
					EntranceTile temp = (EntranceTile) currentTile;
					ArrayList<ParkingTile> options = temp.getParkingNeighbors();
					//System.out.println(options.size());
					ParkingTile next = options.get(0);
					if(!next.isOccupied()) {
						currentTile = next;
						currentTile.setOccupied(true);
						temp.setOccupied(false);
						ticksToWait = currentTile.getWeight();
					}
					return;
				}
				
			} else {
				//System.out.println("counting down");
				ticksToWait--;
				return;
			}
		}
		
		if(destination == null) {
			//System.out.println("no destination");
			return;
		}
		
		//see if path needs to be calculated
		if(path == null || path.isEmpty()) {
			if(navSystem == null) {
				System.out.println("nav is null");
			}
			path = navSystem.findShortestPath(currentTile.getId(), destination.getEntrance().getId());
			//System.out.println("path calculated: " + path.size());
		}
		
		if(ticksToWait == 0) {
			//System.out.println(name + " moving");
			//get next path
			//System.out.println("x: " + currentTile.getCoordinates().x + " , y: " + currentTile.getCoordinates().y);
			nextRoad = path.get(0);
			
			if(!nextRoad.isOccupied()) {
				navSystem.updateDelay(currentTile.getId(), currentDelay, true);
				currentDelay = 0;
				path.remove(0);
				//if null, move and make current space null
				this.currentTile.setOccupied(false);
				nextRoad.setOccupied(true);
				this.currentTile = nextRoad;
				ticksToWait = this.currentTile.getWeight();
				if(currentTile.getId() == destination.getEntrance().getId()) {
					currentBuilding = destination;
					destination = null;
					if(currentTile instanceof EntranceTile) {
						state = State.PARKING;
					} else {
						state = State.READY;
					}
				}
			} else {
				currentDelay++;
				//if its been a long, wait, try another path
				maxDelay++;
				newRouteDelay++;
				if(maxDelay > 4096) {
					//the wait is too long.  pick a new destination
					state = State.READY;
					return;
				}
				if(newRouteDelay > 96) {
					navSystem.updateDelay(currentTile.getId(), currentDelay, false);
					path = navSystem.findShortestPath(currentTile.getId(), destination.getEntrance().getId());
					newRouteDelay = 0;
				}
				
			}
		} else {
			ticksToWait--;
		}
		
	}
	
	public boolean checkSpaces() {
		int x = currentTile.getCoordinates().x;
		int y = currentTile.getCoordinates().y;
		for(int c = 0; c < 4; c++) {
			Tile temp = landMap[x+xCheck[c]][y+yCheck[c]];
			if(temp instanceof ParkingSpaceTile) {
				if(!temp.isOccupied()) {
					//so the car knows how to get out of the spot
					((ParkingSpaceTile) temp).setConnector(currentTile);
					currentTile.setOccupied(false);
					temp.setOccupied(true);
					currentTile = temp;
					state = State.PARKED;
					ticksToWait = currentTile.getWeight();
					return true;
				}
			}
		}
		return false;
	}
	
	public Tile getConnectedParking() {
		int x = currentTile.getCoordinates().x;
		int y = currentTile.getCoordinates().y;
		for(int c = 0; c < 4; c++) {
			Tile temp = landMap[x+xCheck[c]][y+yCheck[c]];
			if(!(temp instanceof ParkingSpaceTile)) {
				if(temp instanceof ParkingTile || temp instanceof EntranceTile || temp instanceof ExitTile) {
					return temp;
					
				}
			}
			
		}
		return null;
	}
	
	public boolean isParked() {
		return state == State.PARKED;
	}
	
	public void paint(Graphics g) {
		g.setColor(Color.BLUE);
        g.fillRect((getX() * 10) + 2, (getY() * 10) + 2, 6, 6);
	}
	
}
