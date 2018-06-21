package com.jaybaffoni;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jaybaffoni.objects.Building;
import com.jaybaffoni.objects.BuildingTile;
import com.jaybaffoni.objects.Car;
import com.jaybaffoni.objects.EntranceTile;
import com.jaybaffoni.objects.ExitTile;
import com.jaybaffoni.objects.House;
import com.jaybaffoni.objects.Navigator;
import com.jaybaffoni.objects.Office;
import com.jaybaffoni.objects.ParkingSpaceTile;
import com.jaybaffoni.objects.ParkingTile;
import com.jaybaffoni.objects.RoadTile;
import com.jaybaffoni.objects.Tile;
import com.jaybaffoni.objects.Vehicle;

public class MainMap {
	
	//static MyCanvas canvas;
	
	static final int chunkSize = 16;
	static int bluePrintWidth = 8;
	static int bluePrintHeight = 5;
	static int gridWidth;
	static int gridHeight;
	static Tile[][] landMap;
	static RoadTile[] roads;
	static ArrayList<Vehicle> vehicles;
	static int roadCount = 0;
	static int parkingCount = 0;
	
	static int speed = 1;
	static Navigator GPS;
	
	static int houseCount = 0;
	static int officeCount = 0;
	static Map<String, House> houses = new HashMap<String,House>();
	static Map<String, Office> offices = new HashMap<String,Office>();
	
	static String[] pieces = {"littleHomes", "mediumHomes", "largeHomes", "shortBuilding", "tallBuilding", "highway"};
	static int[][] bluePrint/* = {{2,2,5,2,2},
								{1,1,5,1,1},
								{1,1,5,1,1},
								{0,0,5,0,0},
								{0,0,5,0,0},
								{3,3,5,3,3},
								{4,4,5,4,4}}*/;
	
	//static int[][] bluePrint = {{0},{4}};
	//static Car testCar;
	//static Car parkedCar;

	public static void main(String[] args) {
		
		bluePrint = new int[bluePrintWidth][bluePrintHeight];
		
		for(int x = 0; x < bluePrintWidth; x++) {
			for(int y = 0; y < bluePrintHeight; y++) {
				bluePrint[x][y] = ThreadLocalRandom.current().nextInt(0, 5);
			}
		}
		for(int i = 0; i < bluePrintWidth; i++) {
			bluePrint[i][bluePrintHeight-3] = 5;
		}
		
		gridWidth = bluePrintWidth * chunkSize;
	    gridHeight = bluePrintHeight * chunkSize;
		setup();
		
		System.out.println("Program Started");
		
	    createAndShowGUI();
	    
	}

	private static void createAndShowGUI() {
        JFrame f = new JFrame("Simulator");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MyPanel pan = new MyPanel(vehicles, landMap);
        f.add(pan);
        f.pack();
        f.setVisible(true);
        
        Thread thread = new Thread(){
            public void run(){
            	while(true) {
                	move(pan);
                	try {
        				Thread.sleep(speed);
        			} catch (InterruptedException e) {
        				e.printStackTrace();
        			}
                }
            }
          };

          thread.start();
        
        
    }
	
	static void setup() {
		ArrayList<RoadTile> roadsToProcess = new ArrayList<RoadTile>();
		vehicles = new ArrayList<Vehicle>();
		//GPS = null;
		landMap = new Tile[gridWidth][gridHeight];
		for(int x = 0; x < bluePrintWidth; x++) {
			for(int y = 0; y < bluePrintHeight; y++) {
				//System.out.println(x + "," + y);
				getData(x,y, roadsToProcess);
			}
		}
		System.out.println(roadCount);
		roads = new RoadTile[roadCount];
		for(RoadTile r: roadsToProcess) {
			roads[r.getId()] = r;
		}
		//printMap(landMap);
		for(int x = 0; x < bluePrintWidth; x++) {
			for(int y = 0; y < bluePrintHeight; y++) {
				//System.out.println(x + "," + y);
				getPaths(x,y);
			}
		}
		
		GPS = new Navigator(roads);
		
		for(Vehicle v: vehicles) {
			if(v instanceof Car) {
				((Car) v).setNavSystem(GPS);
			}
		}
		/*for(int x = 0; x < 100; x++) {
			Car temp = new Car("(A)", GPS, landMap, x, 0);
			//temp.setDestination(buildings.get("b5"));
			temp.setDestination(getRandomBuilding());
			vehicles.add(temp);
		}
		for(int x = 0; x < 100; x++) {
			Car temp = new Car("(A)", GPS, landMap, x, 79);
			temp.setDestination(getRandomBuilding());
			vehicles.add(temp);
		}*/
		
		//parkedCar = new Car("c", roads, landMap, 0,0);
		//vehicles.add(parkedCar);
		//testCar = new Car("b", roads, landMap, 6,0);
		//testCar.setDestination(buildings.get("building0"));
		
		/*vehicles.add(new DecoyCar("(A)", landMap, 0,0));
		vehicles.add(new DecoyCar("(B)", landMap, 1,0));
		vehicles.add(new DecoyCar("(C)", landMap, 2,0));
		vehicles.add(new DecoyCar("(D)", landMap, 3,0));
		vehicles.add(new DecoyCar("(E)", landMap, 4,0));
		vehicles.add(new DecoyCar("(F)", landMap, 5,0));
		vehicles.add(new DecoyCar("(G)", landMap, 6,0));
		vehicles.add(new DecoyCar("(H)", landMap, 7,0));
		vehicles.add(new DecoyCar("(I)", landMap, 8,0));
		vehicles.add(new DecoyCar("(J)", landMap, 9,0));
		vehicles.add(new DecoyCar("(K)", landMap, 10,0));
		vehicles.add(new DecoyCar("(L)", landMap, 11,0));
		vehicles.add(new DecoyCar("(M)", landMap, 12,0));
		vehicles.add(new DecoyCar("(N)", landMap, 13,0));
		vehicles.add(new DecoyCar("(O)", landMap, 14,0));
		vehicles.add(new DecoyCar("(P)", landMap, 15,0));*/
		
		printMaps();
	}
	
	//takes the coordinates of the blueprint as parameters
	static void getData(int bigX, int bigY, ArrayList<RoadTile> roadsToProcess) {
		String pieceName = pieces[bluePrint[bigX][bigY]];
		//System.out.println(pieceName);
		String fileName = "src/com/jaybaffoni/" + pieceName + ".html";
		File pieceFile = new File(fileName);
		
		int xOffset = bigX * chunkSize;
		int yOffset = bigY * chunkSize;
		
		//System.out.println(xOffset + "," + yOffset);
		
		//int roadCount = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(pieceFile));
			String line = "";
			int x = 0;
			int y = 0;
			while((line = br.readLine()) != null){
				int index = line.indexOf("#");
				if(index != -1) {
					//System.out.println("placing at " + (x + xOffset) + "," + (y + yOffset));
					String color = line.substring(index + 1, index + 7);
					//System.out.println(color);
					Tile temp;
					switch(color){
					case "000000":
						//black, building
						temp = new BuildingTile(0, x + xOffset,y + yOffset);
						landMap[x + xOffset][y + yOffset] = temp;
						//roadsToProcess.add((RoadTile) temp);
						//roadCount++;
						break;
					case "808080":
						//dark grey, road
						temp = new RoadTile(x + xOffset,y + yOffset,roadCount,6);
						landMap[x + xOffset][y + yOffset] = temp;
						roadsToProcess.add((RoadTile) temp);
						roadCount++;
						break;
					case "aaaaaa":
						//light grey, main road
						temp = new RoadTile(x + xOffset,y + yOffset,roadCount,4);
						landMap[x + xOffset][y + yOffset] = temp;
						roadsToProcess.add((RoadTile) temp);
						roadCount++;
						break;
					case "ffffff":
						//white, highway
						temp = new RoadTile(x + xOffset,y + yOffset,roadCount,3);
						landMap[x + xOffset][y + yOffset] = temp;
						roadsToProcess.add((RoadTile) temp);
						roadCount++;
						break;
					case "ff00ff":
						//magenta, parking
						temp = new ParkingTile(x + xOffset,y + yOffset,parkingCount,12);
						landMap[x + xOffset][y + yOffset] = temp;
						//roadsToProcess.add((RoadTile) temp);
						parkingCount++;
						break;
					case "ffff00":
						//yellow, space
						temp = new ParkingSpaceTile(x + xOffset,y + yOffset,roadCount,256);
						landMap[x + xOffset][y + yOffset] = temp;
						//roadsToProcess.add((RoadTile) temp);
						//roadCount++;
						if(pieceName.equals("littleHomes") || pieceName.equals("mediumHomes") || pieceName.equals("largeHomes")) {
							Car car = new Car("(A)", landMap, x + xOffset, y + yOffset);
							//car.setDestination(getRandomBuilding());
							vehicles.add(car);
							temp.setOccupied(true);
						}
						
						break;
					case "00ff00":
						//green, entrance
						temp = new EntranceTile(x + xOffset,y + yOffset,roadCount,12);
						landMap[x + xOffset][y + yOffset] = temp;
						roadsToProcess.add((RoadTile) temp);
						roadCount++;
						break;
					case "ff0000":
						//red, exit
						temp = new ExitTile(x + xOffset,y + yOffset,roadCount,12);
						landMap[x + xOffset][y + yOffset] = temp;
						//roadsToProcess.add((RoadTile) temp);
						//roadCount++;
						break;
					case "008000":
						//dark green, lawn
						break;
					}
					x++;
					if(x >= chunkSize) {
						x = 0;
						y++;
					}
				}
			}
			br.close();
			
			createBuildings(pieceName, xOffset, yOffset);
			
		} catch (IOException e) {
			System.out.println("IOException");
			System.out.println(e.toString());
		}
		
	}
	
	
	static public void getPaths(int bigX, int bigY) {
		String pieceName = pieces[bluePrint[bigX][bigY]];
		//System.out.println(pieceName);
		String fileName = "src/com/jaybaffoni/" + pieceName + "Paths.txt";
		File pieceFile = new File(fileName);
		
		int xOffset = bigX * chunkSize;
		int yOffset = bigY * chunkSize;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(pieceFile));
			String line = "";
			int x = 0;
			int y = 0;
			while((line = br.readLine()) != null){
				if(!line.startsWith("//")) {
					String[] pairs = line.split(";");
				for(int i = 0; i < pairs.length - 1; i++) {
					//System.out.println("(" + pairs[i] + ") --> (" + pairs[i+1] + ")");
					join(pairs[i], pairs[i+1], xOffset, yOffset);
				}
				}
				
			}
			br.close();
		} catch (IOException e) {
			System.out.println("IOException");
			System.out.println(e.toString());
		}
	}
	
	static void move(MyPanel pan) {
		for(Vehicle v: vehicles) {
			v.move();
			if(v instanceof Car) {
				Car c = (Car) v;
				if(c.isParked() || c.isReady()) {
					c.setDestination(getRandomOffice());
					//c.setDestination(buildings.get("b5"));
				}
			}
		}
		//testCar.move();
		pan.repaint();
		
		//check all roads, and update the delay to 0 if they are unoccupied
		for(RoadTile r: roads) {
			if(!r.isOccupied()) {
				GPS.updateDelay(r.getId(), 0, true);
			}
		}
	}
	

	
	static String printMap(Object[][] mapToPrint) {
		String toReturn = "<html>";
		for(int x = 0; x < gridHeight; x++) {
			String toPrint = "";
			for(int y = 0; y < gridWidth; y++) {
				if(mapToPrint[y][x] != null) {
					//Reverse the order of x,y so it matches the input when printed
					toPrint += mapToPrint[y][x].toString();
				} else {
					toPrint += "---";
				}
				
			}
			toReturn += "<br>" + toPrint;
			//System.out.println(toPrint);
		}
		
		toReturn += "</html>";
		return toReturn;
	}
	
	static String printMaps() {
		int a = landMap.length;
		int b = landMap[0].length;
		String[][] toPrint = new String[a][b];
		for(int x = 0; x < gridWidth; x++) {
			for(int y = 0; y < gridHeight; y++) {
				if(landMap[x][y] != null) {
					toPrint[x][y] = landMap[x][y].toString();
				} else {
					toPrint[x][y] = "---";
				}
			}
		}
		for(Vehicle v: vehicles) {
			toPrint[v.getX()][v.getY()] = v.toString();
		}
		return(printMap(toPrint));
	}
	
	static void createRoads() {
		
		try {
			File roadFile = new File("src/com/jaybaffoni/roads2.txt");
			BufferedReader br = new BufferedReader(new FileReader(roadFile));
			String line = "";
			while((line = br.readLine()) != null){
				//System.out.println(line);
				if(!line.startsWith("//")) {
					String[] values = line.split(",");
					//join(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
				}
			}
			br.close();
		} catch (IOException e) {
			System.out.println("IOException");
			System.out.println(e.toString());
		}
		
	}
	
	static void join(String a, String b, int xOffset, int yOffset) {
		String[] aCoords = a.split(",");
		String[] bCoords = b.split(",");
		int ax = Integer.parseInt(aCoords[0]) + xOffset;
		int ay = Integer.parseInt(aCoords[1]) + yOffset;
		int bx = Integer.parseInt(bCoords[0]) + xOffset;
		int by = Integer.parseInt(bCoords[1]) + yOffset;
		
		if(ax < 0 || ax > gridWidth - 1 || bx < 0 || bx > gridWidth - 1) {
			//System.out.println("out of bounds");
			return;
		}
		if(ay < 0 || ay > gridHeight - 1 || by < 0 || by > gridHeight - 1) {
			//System.out.println("out of bounds");
			return;
		}
		//System.out.println("Good");
		Tile check = landMap[ax][ay];
		Tile neighbor = landMap[bx][by];
		if(check instanceof EntranceTile) {
			EntranceTile temp = (EntranceTile) check;
			if(neighbor instanceof ExitTile) {
				ExitTile toAdd = (ExitTile) neighbor;
				temp.addParkingNeighbor(toAdd);
			} else if(neighbor instanceof ParkingTile) {
				ParkingTile toAdd = (ParkingTile) neighbor;
				temp.addParkingNeighbor(toAdd);
			} else if(neighbor instanceof ParkingSpaceTile) {
				ParkingSpaceTile toAdd = (ParkingSpaceTile) neighbor;
				temp.addParkingSpaceNeighbor(toAdd);
			}
		} else if(check instanceof RoadTile) {
			RoadTile temp = (RoadTile) check;
			RoadTile toAdd = (RoadTile) neighbor;
			temp.addNeighbor(toAdd);
		} else if(check instanceof ExitTile) {
			ExitTile temp = (ExitTile) check;
			RoadTile toAdd = (RoadTile) neighbor;
			temp.setExit(toAdd);
		} else if(check instanceof ParkingTile) {
			ParkingTile temp = (ParkingTile) check;
			if(neighbor instanceof ExitTile) {
				ExitTile toAdd = (ExitTile) neighbor;
				temp.addExit(toAdd);
			}else if(neighbor instanceof ParkingTile) {
				ParkingTile toAdd = (ParkingTile) neighbor;
				temp.addNeighbor(toAdd);
			}
		} else if(check instanceof ParkingSpaceTile) {
			ParkingSpaceTile temp = (ParkingSpaceTile) check;
			temp.setConnector(neighbor);
		}
		
		
		//roads[a].addNeighbor(roads[b]);
	}
	
	public static Office getRandomOffice() {
		Object[] keys = offices.keySet().toArray();
		//System.out.println(keys.length);
		String bString = keys[ThreadLocalRandom.current().nextInt(0, keys.length)].toString();
		return offices.get(bString);
	}
	
	public static House getRandomHouse() {
		Object[] keys = houses.keySet().toArray();
		//System.out.println(keys.length);
		String bString = keys[ThreadLocalRandom.current().nextInt(0, keys.length)].toString();
		return houses.get(bString);
	}
	
	static void createBuildings(String pieceName, int xOffset, int yOffset) {
		
		EntranceTile ent;
		ExitTile exit;
		Building toAdd;
		int parkOffset;
		
		switch(pieceName) {
		
			case "shortBuilding":
				
				ent = (EntranceTile)landMap[8 + xOffset][14 + yOffset];
				exit = (ExitTile)landMap[7+xOffset][14 + yOffset];
				toAdd = new Office(("o" + officeCount), ent, exit, 28);
				//add parking for toadd;
				parkOffset = landMap[2 + xOffset][10 + yOffset].getId();
				//System.out.println(parkOffset);
				for(int x = 2; x <= 13; x++) {
					toAdd.addParking((ParkingTile)landMap[x + xOffset][10 + yOffset], parkOffset);
					toAdd.addParking((ParkingTile)landMap[x + xOffset][13 + yOffset], parkOffset);
				}
				toAdd.addParking((ParkingTile)landMap[2 + xOffset][11 + yOffset], parkOffset);
				toAdd.addParking((ParkingTile)landMap[2 + xOffset][12 + yOffset], parkOffset);
				toAdd.addParking((ParkingTile)landMap[13 + xOffset][11 + yOffset], parkOffset);
				toAdd.addParking((ParkingTile)landMap[13 + xOffset][12 + yOffset], parkOffset);
				toAdd.setFinalParkingIndex(landMap[7 + xOffset][13 + yOffset].getId() - parkOffset);
				offices.put(toAdd.getId(), (Office)toAdd);
				officeCount++;
				//System.out.println(Arrays.toString(toAdd.getLot()));
				break;
			
			case "tallBuilding":
				
				ent = (EntranceTile)landMap[8 + xOffset][14 + yOffset];
				exit = (ExitTile)landMap[7+xOffset][14 + yOffset];
				toAdd = new Office(("b" + officeCount), ent, exit, 44);
				parkOffset = landMap[2 + xOffset][7 + yOffset].getId();
				
				for(int x = 2; x <= 13; x++) {
					toAdd.addParking((ParkingTile)landMap[x + xOffset][7 + yOffset], parkOffset);
					toAdd.addParking((ParkingTile)landMap[x + xOffset][10 + yOffset], parkOffset);
					toAdd.addParking((ParkingTile)landMap[x + xOffset][13 + yOffset], parkOffset);
				}
				toAdd.addParking((ParkingTile)landMap[2 + xOffset][11 + yOffset], parkOffset);
				toAdd.addParking((ParkingTile)landMap[2 + xOffset][12 + yOffset], parkOffset);
				toAdd.addParking((ParkingTile)landMap[13 + xOffset][11 + yOffset], parkOffset);
				toAdd.addParking((ParkingTile)landMap[13 + xOffset][12 + yOffset], parkOffset);
				toAdd.addParking((ParkingTile)landMap[2 + xOffset][8 + yOffset], parkOffset);
				toAdd.addParking((ParkingTile)landMap[2 + xOffset][9 + yOffset], parkOffset);
				toAdd.addParking((ParkingTile)landMap[13 + xOffset][8 + yOffset], parkOffset);
				toAdd.addParking((ParkingTile)landMap[13 + xOffset][9 + yOffset], parkOffset);
				toAdd.setFinalParkingIndex(landMap[7 + xOffset][13 + yOffset].getId() - parkOffset);
				offices.put(toAdd.getId(), (Office)toAdd);
				officeCount++;
				
				break;
				
			case "largeHomes":
				
				ent = (EntranceTile)landMap[4 + xOffset][6 + yOffset];
				exit = (ExitTile)landMap[3 + xOffset][6 + yOffset];
				toAdd = new House(("b" + houseCount), ent, exit, 2);
				parkOffset = landMap[3 + xOffset][5 + yOffset].getId();
				toAdd.addParking((ParkingTile)landMap[3 + xOffset][5 + yOffset], parkOffset);
				toAdd.addParking((ParkingTile)landMap[4 + xOffset][5 + yOffset], parkOffset);
				toAdd.setFinalParkingIndex(landMap[3 + xOffset][5 + yOffset].getId() - parkOffset);
				houses.put(toAdd.getId(), (House)toAdd);
				houseCount++;
				
				ent = (EntranceTile)landMap[12 + xOffset][6 + yOffset];
				exit = (ExitTile)landMap[11 + xOffset][6 + yOffset];
				toAdd = new House(("b" + houseCount), ent, exit, 2);
				parkOffset = landMap[11 + xOffset][5 + yOffset].getId();
				toAdd.addParking((ParkingTile)landMap[11 + xOffset][5 + yOffset], parkOffset);
				toAdd.addParking((ParkingTile)landMap[12 + xOffset][5 + yOffset], parkOffset);
				toAdd.setFinalParkingIndex(landMap[11 + xOffset][5 + yOffset].getId() - parkOffset);
				houses.put(toAdd.getId(), (House)toAdd);
				houseCount++;
				
				ent = (EntranceTile)landMap[3 + xOffset][9 + yOffset];
				exit = (ExitTile)landMap[4 + xOffset][9 + yOffset];
				toAdd = new House(("b" + houseCount), ent, exit, 2);
				parkOffset = landMap[3 + xOffset][10 + yOffset].getId();
				toAdd.addParking((ParkingTile)landMap[3 + xOffset][10 + yOffset], parkOffset);
				toAdd.addParking((ParkingTile)landMap[4 + xOffset][10 + yOffset], parkOffset);
				toAdd.setFinalParkingIndex(landMap[4 + xOffset][10 + yOffset].getId() - parkOffset);
				houses.put(toAdd.getId(), (House)toAdd);
				houseCount++;
				
				ent = (EntranceTile)landMap[11 + xOffset][9 + yOffset];
				exit = (ExitTile)landMap[12 + xOffset][9 + yOffset];
				toAdd = new House(("b" + houseCount), ent, exit, 2);
				parkOffset = landMap[11 + xOffset][10 + yOffset].getId();
				toAdd.addParking((ParkingTile)landMap[11 + xOffset][10 + yOffset], parkOffset);
				toAdd.addParking((ParkingTile)landMap[12 + xOffset][10 + yOffset], parkOffset);
				toAdd.setFinalParkingIndex(landMap[12 + xOffset][10 + yOffset].getId() - parkOffset);
				houses.put(toAdd.getId(), (House)toAdd);
				houseCount++;
				
				break;
				
			case "mediumHomes":
				
				ent = (EntranceTile)landMap[4 + xOffset][1 + yOffset];
				exit = (ExitTile)landMap[6 + xOffset][1 + yOffset];
				toAdd = new House(("b" + houseCount), ent, exit, 1);
				parkOffset = landMap[5 + xOffset][1 + yOffset].getId();
				toAdd.addParking((ParkingTile)landMap[5 + xOffset][1 + yOffset], parkOffset);
				toAdd.setFinalParkingIndex(landMap[5 + xOffset][1 + yOffset].getId() - parkOffset);
				houses.put(toAdd.getId(), (House)toAdd);
				houseCount++;
				
				ent = (EntranceTile)landMap[12 + xOffset][1 + yOffset];
				exit = (ExitTile)landMap[14 + xOffset][1 + yOffset];
				toAdd = new House(("b" + houseCount), ent, exit, 1);
				parkOffset = landMap[13 + xOffset][1 + yOffset].getId();
				toAdd.addParking((ParkingTile)landMap[13 + xOffset][1 + yOffset], parkOffset);
				toAdd.setFinalParkingIndex(landMap[13 + xOffset][1 + yOffset].getId() - parkOffset);
				houses.put(toAdd.getId(), (House)toAdd);
				houseCount++;
				
				ent = (EntranceTile)landMap[3 + xOffset][6 + yOffset];
				exit = (ExitTile)landMap[1 + xOffset][6 + yOffset];
				toAdd = new House(("b" + houseCount), ent, exit, 1);
				parkOffset = landMap[2 + xOffset][6 + yOffset].getId();
				toAdd.addParking((ParkingTile)landMap[2 + xOffset][6 + yOffset], parkOffset);
				toAdd.setFinalParkingIndex(landMap[2 + xOffset][6 + yOffset].getId() - parkOffset);
				houses.put(toAdd.getId(), (House)toAdd);
				houseCount++;
				
				ent = (EntranceTile)landMap[11 + xOffset][6 + yOffset];
				exit = (ExitTile)landMap[9 + xOffset][6 + yOffset];
				toAdd = new House(("b" + houseCount), ent, exit, 1);
				parkOffset = landMap[10 + xOffset][6 + yOffset].getId();
				toAdd.addParking((ParkingTile)landMap[10 + xOffset][6 + yOffset], parkOffset);
				toAdd.setFinalParkingIndex(landMap[10 + xOffset][6 + yOffset].getId() - parkOffset);
				houses.put(toAdd.getId(), (House)toAdd);
				houseCount++;
				
				ent = (EntranceTile)landMap[4 + xOffset][9 + yOffset];
				exit = (ExitTile)landMap[6 + xOffset][9 + yOffset];
				toAdd = new House(("b" + houseCount), ent, exit, 1);
				parkOffset = landMap[5 + xOffset][9 + yOffset].getId();
				toAdd.addParking((ParkingTile)landMap[5 + xOffset][9 + yOffset], parkOffset);
				toAdd.setFinalParkingIndex(landMap[5 + xOffset][9 + yOffset].getId() - parkOffset);
				houses.put(toAdd.getId(), (House)toAdd);
				houseCount++;
				
				ent = (EntranceTile)landMap[12 + xOffset][9 + yOffset];
				exit = (ExitTile)landMap[14 + xOffset][9 + yOffset];
				toAdd = new House(("b" + houseCount), ent, exit, 1);
				parkOffset = landMap[13 + xOffset][9 + yOffset].getId();
				toAdd.addParking((ParkingTile)landMap[13 + xOffset][9 + yOffset], parkOffset);
				toAdd.setFinalParkingIndex(landMap[13 + xOffset][9 + yOffset].getId() - parkOffset);
				houses.put(toAdd.getId(), (House)toAdd);
				houseCount++;
				
				ent = (EntranceTile)landMap[3 + xOffset][14 + yOffset];
				exit = (ExitTile)landMap[1 + xOffset][14 + yOffset];
				toAdd = new House(("b" + houseCount), ent, exit, 1);
				parkOffset = landMap[2 + xOffset][14 + yOffset].getId();
				toAdd.addParking((ParkingTile)landMap[2 + xOffset][14 + yOffset], parkOffset);
				toAdd.setFinalParkingIndex(landMap[2 + xOffset][14 + yOffset].getId() - parkOffset);
				houses.put(toAdd.getId(), (House)toAdd);
				houseCount++;
				
				ent = (EntranceTile)landMap[11 + xOffset][14 + yOffset];
				exit = (ExitTile)landMap[9 + xOffset][14 + yOffset];
				toAdd = new House(("b" + houseCount), ent, exit, 1);
				parkOffset = landMap[10 + xOffset][14 + yOffset].getId();
				toAdd.addParking((ParkingTile)landMap[10 + xOffset][14 + yOffset], parkOffset);
				toAdd.setFinalParkingIndex(landMap[10 + xOffset][14 + yOffset].getId() - parkOffset);
				houses.put(toAdd.getId(), (House)toAdd);
				houseCount++;
				
				break;
			
			case "littleHomes":
				
				for(int x = 1; x < 5; x += 3) {
					System.out.println(x);
					ent = (EntranceTile)landMap[x + xOffset][1 + yOffset];
					exit = (ExitTile)landMap[x + 1 + xOffset][1 + yOffset];
					toAdd = new House(("b" + houseCount), ent, exit, 0);
					houses.put(toAdd.getId(), (House)toAdd);
					houseCount++;
					
					ent = (EntranceTile)landMap[x + xOffset][9 + yOffset];
					exit = (ExitTile)landMap[x + 1 + xOffset][9 + yOffset];
					toAdd = new House(("b" + houseCount), ent, exit, 0);
					houses.put(toAdd.getId(), (House)toAdd);
					houseCount++;
					
					ent = (EntranceTile)landMap[x + 8 + xOffset][1 + yOffset];
					exit = (ExitTile)landMap[x + 9 + xOffset][1 + yOffset];
					toAdd = new House(("b" + houseCount), ent, exit, 0);
					houses.put(toAdd.getId(), (House)toAdd);
					houseCount++;
					
					ent = (EntranceTile)landMap[x + 8 + xOffset][9 + yOffset];
					exit = (ExitTile)landMap[x + 9 + xOffset][9 + yOffset];
					toAdd = new House(("b" + houseCount), ent, exit, 0);
					houses.put(toAdd.getId(), (House)toAdd);
					houseCount++;
				}
				
				for(int x = 14; x > 10; x -= 3) {
					ent = (EntranceTile)landMap[x + xOffset][14 + yOffset];
					exit = (ExitTile)landMap[x - 1 + xOffset][6 + yOffset];
					toAdd = new House(("b" + houseCount), ent, exit, 0);
					houses.put(toAdd.getId(), (House)toAdd);
					houseCount++;
					
					ent = (EntranceTile)landMap[x + xOffset][14 + yOffset];
					exit = (ExitTile)landMap[x - 1 + xOffset][6 + yOffset];
					toAdd = new House(("b" + houseCount), ent, exit, 0);
					houses.put(toAdd.getId(), (House)toAdd);
					houseCount++;
					
					ent = (EntranceTile)landMap[x - 8 + xOffset][6 + yOffset];
					exit = (ExitTile)landMap[x - 9 + xOffset][6 + yOffset];
					toAdd = new House(("b" + houseCount), ent, exit, 0);
					houses.put(toAdd.getId(), (House)toAdd);
					houseCount++;
					
					ent = (EntranceTile)landMap[x - 8 + xOffset][14 + yOffset];
					exit = (ExitTile)landMap[x - 9 + xOffset][14 + yOffset];
					toAdd = new House(("b" + houseCount), ent, exit, 0);
					houses.put(toAdd.getId(), (House)toAdd);
					houseCount++;
				}
				
				break;
		}
		
	}
	

}

class MyPanel extends JPanel {
    
    ArrayList<Vehicle> vehicles;
    Tile[][] landMap;
    
    int width;
    int height;
    
    Color dkGreen = Color.decode("#008000");

    public MyPanel(ArrayList<Vehicle> vehicles, Tile[][] landMap) {
        setBorder(BorderFactory.createLineBorder(Color.black));
        this.vehicles = vehicles;
        this.landMap = landMap;
        
        width = landMap.length;
        height = landMap[0].length;
        System.out.println(width + "," + height);
    }

    public Dimension getPreferredSize() {
        return new Dimension(width*10, height*10);
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);       

        g.setColor(dkGreen);
        g.fillRect(0, 0, width*10, height*10);
        
        for(int x = 0; x < width; x++) {
        	for(int y = 0; y < height; y++) {
        		if(landMap[x][y] != null) {
        			Tile temp = landMap[x][y];
        			temp.paint(g);
        		}
        	}
        	
        }
        
        
        for(Vehicle v: vehicles) {
        	v.paint(g);
        }
        
        //System.out.println("painted");
    }  
}
