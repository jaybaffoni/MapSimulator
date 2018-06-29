package com.jaybaffoni.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import com.jaybaffoni.tiles.EntranceTile;
import com.jaybaffoni.tiles.ExitTile;
import com.jaybaffoni.tiles.ParkingSpaceTile;
import com.jaybaffoni.tiles.ParkingTile;
import com.jaybaffoni.tiles.RoadTile;
import com.jaybaffoni.tiles.Tile;

public class Car extends Vehicle{

	protected Tile currentTile;
	private RoadTile nextRoad = null;
	private Building currentBuilding = null;
	private Building destination = null;
	protected int ticksToWait = 0;
	private String name;
	protected ArrayList<RoadTile> path;
	Navigator navSystem;
	Internet net;
	Tile[][] landMap;
	enum State {
		READY, MOVING, PARKING, PARKED, EXITING, DISABLED, REPAIRING;
	}
	State state;
	int direction = 0;
	// 0 is left to right, 1 is top to bottom, 2 is right to left, 3 is bottom to top;
	int[] xCheck = {1,-1,0,0};
	int[] yCheck = {0,0,1,-1};
	//ArrayList<ParkingTile> pathToExit;
	int currentDelay = 0;
	int newRouteDelay = 0;
	int maxDelay = 0;
	int durability;
	boolean towOnItsWay = false;
	
	public Car(String name, Tile[][] landMap, int x, int y) {
		this.name = name;
		this.currentTile = landMap[x][y];
		this.currentTile.setOccupied(true);
		ticksToWait = currentTile.getWeight();
		state = State.PARKED;
		this.landMap = landMap;
		
		durability = ThreadLocalRandom.current().nextInt(1000, 8000);
	}
	
	public void setInternet(Internet net) {
		this.net = net;
		this.navSystem = net.getGPS();
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
	
	public Tile getCurrentTile() {
		return currentTile;
	}
	
	public void setCurrentTile(Tile current) {
		this.currentTile = current;
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
		//System.out.println(direction);
		//check if the car is going anywhere
		if(state == State.DISABLED) {
			if(!towOnItsWay) {
				System.out.println("calling");
				AutoShop shop = net.getAutoShop();
				if(shop != null) {
					shop.addTarget(this);
					towOnItsWay = true;
				} 
			}
			
			return;
		}
		//System.out.println(durability);
		if((state == State.READY || state == State.MOVING) && durability <= 0) {
			//car is worn out, needs to call for a tow
			state = State.DISABLED;
			towOnItsWay = false;
			return;
		}
		if(state == State.REPAIRING) {
			if(ticksToWait == 0) {
				state = State.EXITING;
			} else {
				ticksToWait--;
			}
		}
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
							setNext(next);
							ticksToWait = currentTile.getWeight();
							state = State.MOVING;
							destination = currentBuilding;
						}
						return;
					} else if(currentTile instanceof ParkingTile) {
						ParkingTile loc = (ParkingTile)currentTile;
						neighbors = loc.getParkingNeighbors();
						//System.out.println("from parking");
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
						setNext(nextPark);
						ticksToWait = currentTile.getWeight();
					} else {
						//System.out.println("space was occupied");
					}
					return;
				} else {
					//System.out.println("no connected spaces");
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
					//System.out.println("leaving parking space");
					ParkingSpaceTile temp = (ParkingSpaceTile)currentTile;
					Tile next = temp.getConnector();
					//System.out.println("x: " + currentTile.getCoordinates().x + " , y: " + currentTile.getCoordinates().y);
					if(next == null) {
						next = getConnectedParking();
					}
					if(!next.isOccupied()) {
						setNext(next);
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
						setNext(next);
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
							setNext(next);
							currentTile.setOccupied(true);
							temp.setOccupied(false);
							ticksToWait = currentTile.getWeight();
						}
					} else if(temp.getTowardExitTile() != null) {
						//theres a choice, so take the one leading out
						ParkingTile next = temp.getTowardExitTile();
						if(!next.isOccupied()) {
							setNext(next);
							currentTile.setOccupied(true);
							temp.setOccupied(false);
							ticksToWait = currentTile.getWeight();
						}
					} else {
						//there's no attached exit, and theres no preference, so take the next parkingtile
						ParkingTile next = temp.getParkingNeighbors().get(0);
						if(!next.isOccupied()) {
							setNext(next);
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
						setNext(next);
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
		if(state == State.MOVING) {
			if(path == null || path.isEmpty()) {
				//System.out.println("Finding path");
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
					if(!(nextRoad instanceof EntranceTile)) {
						durability -= 3;
					}
					navSystem.updateDelay(currentTile.getId(), currentDelay, true);
					currentDelay = 0;
					path.remove(0);
					//if null, move and make current space null
					this.currentTile.setOccupied(false);
					nextRoad.setOccupied(true);
					setNext(nextRoad);
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
					/*if(maxDelay > 4096) {
						//the wait is too long.  pick a new destination
						System.out.println("taking too long");
						state = State.READY;
						return;
					}*/
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
		
		
	}
	
	public void beginRepairs() {
		state = State.REPAIRING;
		path = null;
		ticksToWait = 4096;
		durability = ThreadLocalRandom.current().nextInt(3000, 5000);
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
					setNext(temp);
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
	
	public void setNext(Tile next) {
		if(next.getX() == currentTile.getX()) {
			//same x, car is moving up or down
			if(next.getY() > currentTile.getY()) {
				//next y is bigger, car is moving down
				direction = 1;
			} else {
				//next y is smaller, car is moving up
				direction = 3;
			}
		} else {
			//same y, car is going horizontal
			if(next.getX() > currentTile.getX()) {
				//next x is bigger, car is moving L to R
				direction = 0;
			} else {
				//next x is smaller, car is moving R to L
				direction = 2;
			}
		}
		
		currentTile = next;
	}
	
	public boolean isParked() {
		return state == State.PARKED;
	}
	
	public boolean isRepairing() {
		return state == State.REPAIRING;
	}
	
	public void paint(Graphics g) {
		g.setColor(Color.BLUE);
		if(state == State.DISABLED) {
			g.setColor(Color.RED);
		} else if (state == State.REPAIRING) {
			g.setColor(Color.ORANGE);
		}
        g.fillRect((getX() * 10) + 2, (getY() * 10) + 2, 6, 6);
        //paint driver
        g.setColor(Color.YELLOW);
        switch (direction) {
        	case 0:
        		g.fillOval((getX() * 10) + 5, (getY() * 10) + 1, 3, 3);
        		break;
        	case 1:
        		g.fillOval((getX() * 10) + 5, (getY() * 10) + 5, 3, 3);
        		break;
        	case 2:
        		g.fillOval((getX() * 10) + 1, (getY() * 10) + 5, 3, 3);
        		break;
        	case 3:
        		g.fillOval((getX() * 10) + 1, (getY() * 10) + 1, 3, 3);
        		break;
        }
        
        
	}
	
}
