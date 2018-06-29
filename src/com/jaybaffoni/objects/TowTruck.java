package com.jaybaffoni.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import com.jaybaffoni.objects.Car.State;
import com.jaybaffoni.tiles.EntranceTile;
import com.jaybaffoni.tiles.ExitTile;
import com.jaybaffoni.tiles.ParkingSpaceTile;
import com.jaybaffoni.tiles.ParkingTile;
import com.jaybaffoni.tiles.RoadTile;
import com.jaybaffoni.tiles.Tile;
import com.jaybaffoni.tiles.TruckParkingTile;

public class TowTruck extends Car{
	
	Tile trailer;
	int trailerDirection = 1;
	Car target = null;
	Car inTow = null;
	AutoShop homeLocation = null;
	int[] xCheck = {1,-1,0,0};
	int[] yCheck = {0,0,1,-1};
	
	enum State {
		READY, LOCATING, TOWING, UNLOADING, PARKING, EXITING;
	}
	State state;

	public TowTruck(String name, Tile[][] landMap, int x, int y, AutoShop home) {
		super(name, landMap, x, y);
		trailer = landMap[x][y-1];
		trailer.setOccupied(true);
		this.homeLocation = home;
		state = State.PARKING;
	}
	
	public void move() {
		//System.out.println("moving");
		Tile nextRoad;
		switch (state) {
			case READY:
				return;
			case LOCATING:
				//System.out.println("trying to locate");
				if(path == null || path.isEmpty()) {
					if(navSystem == null) {
						//System.out.println("nav is null");
					}
					path = navSystem.findShortestPath(currentTile.getId(), target.getCurrentTile().getId());
				}
				
				//follow path
				if(ticksToWait == 0) {
					if(currentTile.getId() == target.getCurrentTile().getId()) {
						//tow truck has reached disabled car
						inTow = target;
						target = null;
						inTow.getCurrentTile().setOccupied(false);
						inTow.setCurrentTile(trailer);
						path = null;
						state = State.TOWING;
						return;
					}
					nextRoad = path.get(0);
					
					path.remove(0);
					//this.currentTile.setOccupied(false);
					//nextRoad.setOccupied(true);
					setNext(nextRoad);
					ticksToWait = this.currentTile.getWeight();
					
				} else {
					ticksToWait--;
				}
				break;
			case TOWING:
				if(path == null || path.isEmpty()) {
					if(navSystem == null) {
						//System.out.println("nav is null");
					}
					path = navSystem.findShortestPath(currentTile.getId(), homeLocation.getEntrance().getId());
				}
				
				//follow path
				if(ticksToWait == 0) {
					nextRoad = path.get(0);
					
					path.remove(0);
					setNext(nextRoad);
					inTow.setCurrentTile(trailer);
					ticksToWait = this.currentTile.getWeight();
					if(currentTile.getId() == homeLocation.getEntrance().getId()) {
						//tow truck has reached home
						//inTow = null;
						path = null;
						state = State.UNLOADING;
					}
				} else {
					ticksToWait--;
				}
				break;
			case UNLOADING:
				if(ticksToWait == 0) {
					if(currentTile instanceof EntranceTile) {
						EntranceTile temp = (EntranceTile) currentTile;
						ParkingTile next = temp.getParkingNeighbors().get(0);
						setNext(next);
						inTow.setCurrentTile(trailer);
						ticksToWait = this.currentTile.getWeight();
					} else if(currentTile instanceof ParkingTile) {
						ParkingTile temp = (ParkingTile) currentTile;
						ParkingTile next = temp.getParkingNeighbors().get(0);
						setNext(next);
						inTow.setCurrentTile(trailer);
						ticksToWait = this.currentTile.getWeight();
						if(inTow != null) {
							//System.out.println("checking...");
							//trailer.print();
							checkSpaces();
						}
					}
				} else {
					ticksToWait--;
				}
				
				break;
			case PARKING:
				if(ticksToWait == 0) {
					if(currentTile instanceof TruckParkingTile) {
						TruckParkingTile temp = (TruckParkingTile) currentTile;
						if(temp.getNeighbor() != null) {
							TruckParkingTile next = temp.getNeighbor();
							if(!next.isOccupied()) {
								currentTile.setOccupied(false);
								trailer.setOccupied(false);
								setNext(next);
								currentTile.setOccupied(true);
								trailer.setOccupied(true);
							} else {
								//System.out.println("occupied");
							}
						} else {
							//System.out.println("ready");
							state = State.READY;
							homeLocation.setNextInLine(this);
						}
						
					} else if(currentTile instanceof ParkingTile) {
						ParkingTile temp = (ParkingTile) currentTile;
						if(temp.getTruckTile() != null) {
							TruckParkingTile next = temp.getTruckTile();
							if(!next.isOccupied()) {
								setNext(next);
								currentTile.setOccupied(true);
								trailer.setOccupied(true);
							}
						} else {
							ParkingTile next = temp.getParkingNeighbors().get(0);
							setNext(next);
						}
					}
					ticksToWait = this.currentTile.getWeight();
				} else {
					ticksToWait--;
				}
				break;
			case EXITING:
				if(ticksToWait == 0) {
					if(currentTile instanceof TruckParkingTile) {
						TruckParkingTile temp = (TruckParkingTile) currentTile;
						ParkingTile next = temp.getParking();
						currentTile.setOccupied(false);
						trailer.setOccupied(false);
						setNext(next);
					} else if (currentTile instanceof ExitTile) {
						ExitTile temp = (ExitTile) currentTile;
						RoadTile next = temp.getExit();
						setNext(next);
						state = State.LOCATING;
					} else if (currentTile instanceof ParkingTile) {
						ParkingTile temp = (ParkingTile) currentTile;
						if(temp.getParkingNeighbors().isEmpty()) {
							ExitTile next = temp.getExitTile();
							setNext(next);
						} else {
							ParkingTile next = temp.getParkingNeighbors().get(0);
							setNext(next);
						}
						
						
					}
					ticksToWait = this.currentTile.getWeight();
				} else {
					ticksToWait--;
				}
				
				//if out, set to locating
				break;
		}
	
	}
	
	public boolean checkSpaces() {
		int x = trailer.getX();
		int y = trailer.getY();
		//System.out.println(x + "," + y);
		for(int c = 0; c < 4; c++) {
			Tile temp = landMap[x+xCheck[c]][y+yCheck[c]];
			//System.out.println("Checking " + (x + xCheck[c]) + "," + (y + yCheck[c]));
			if(temp instanceof ParkingSpaceTile) {
				//System.out.println("found a space");
				if(!temp.isOccupied()) {
					//so the car knows how to get out of the spot
					((ParkingSpaceTile) temp).setConnector(currentTile);
					//currentTile.setOccupied(false);
					temp.setOccupied(true);
					//homeLocation.capacity--;
					inTow.setNext(temp);
					homeLocation.addToRepairs(inTow);
					inTow = null;
					state = State.PARKING;
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean setTarget(Car target) {
		//System.out.println("setting target...");
		if(state == State.READY) {
			this.target = target;
			state = State.EXITING;
			//System.out.println("set");
			return true;
		}
		return false;
	}
	
	public boolean isReady() {
		return state == State.READY;
	}
	
	public void setNext(Tile next) {
		setTrailer();
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
	
	public void setTrailer() {
		if(currentTile.getX() == trailer.getX()) {
			//same x, car is moving up or down
			if(currentTile.getY() > trailer.getY()) {
				//next y is bigger, car is moving down
				trailerDirection = 1;
			} else {
				//next y is smaller, car is moving up
				trailerDirection = 3;
			}
		} else {
			//same y, car is going horizontal
			if(currentTile.getX() > trailer.getX()) {
				//next x is bigger, car is moving L to R
				trailerDirection = 0;
			} else {
				//next x is smaller, car is moving R to L
				trailerDirection = 2;
			}
		}
		
		trailer = currentTile;
	}
	
	public void paint(Graphics g) {
		g.setColor(Color.BLUE);
        g.fillRect((getX() * 10) + 1, (getY() * 10) + 1, 8, 8);
        g.setColor(Color.decode("800000"));
        g.drawRect((trailer.getX() * 10) + 1, (trailer.getY() * 10) + 1, 8, 8);
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
