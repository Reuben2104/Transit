package transit;

import java.util.ArrayList;

/**
 * This class contains methods which perform various operations on a layered linked
 * list to simulate transit
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class Transit {
	private TNode trainZero; // a reference to the zero node in the train layer

	/* 
	 * Default constructor used by the driver and Autolab. 
	 * DO NOT use in your code.
	 * DO NOT remove from this file
	 */ 
	public Transit() { trainZero = null; }

	/* 
	 * Default constructor used by the driver and Autolab. 
	 * DO NOT use in your code.
	 * DO NOT remove from this file
	 */
	public Transit(TNode tz) { trainZero = tz; }
	
	/*
	 * Getter method for trainZero
	 *
	 * DO NOT remove from this file.
	 */
	public TNode getTrainZero () {
		return trainZero;
	}

	/**
	 * Makes a layered linked list representing the given arrays of train stations, bus
	 * stops, and walking locations. Each layer begins with a location of 0, even though
	 * the arrays don't contain the value 0. Store the zero node in the train layer in
	 * the instance variable trainZero.
	 * 
	 * @param trainStations Int array listing all the train stations
	 * @param busStops Int array listing all the bus stops
	 * @param locations Int array listing all the walking locations (always increments by 1)
	 */
	public void makeList(int[] trainStations, int[] busStops, int[] locations) {
		TNode trainPtr = new TNode(0);
		TNode busPtr = new TNode(0);
		TNode wPtr = new TNode(0);

		trainZero = trainPtr;

		trainPtr.setDown(busPtr);
		busPtr.setDown(wPtr);

		TNode[] stops = new TNode[locations.length];

		for(int i = 1; i <= locations.length; i++) {
			TNode location = new TNode(i);
			stops[i - 1] = location;

			wPtr.setNext(location);

			wPtr = location;
		}

		for(int i = 0; i < busStops.length; i++) {
			int busStopLoc = busStops[i];

			TNode busStop = new TNode(busStopLoc);
			busStop.setDown(stops[busStopLoc - 1]);

			stops[busStopLoc - 1] = busStop;
			busPtr.setNext(busStop);

			busPtr = busStop;
		}

		for(int i = 0; i < trainStations.length; i++) {
			int tLocation = trainStations[i];

			TNode station = new TNode(trainStations[i]);
			station.setDown(stops[tLocation - 1]);
			trainPtr.setNext(station);

			trainPtr = station;
		}
	}
	
	/**
	 * Modifies the layered list to remove the given train station but NOT its associated
	 * bus stop or walking location. Do nothing if the train station doesn't exist
	 * 
	 * @param station The location of the train station to remove
	 */
	public void removeTrainStation(int station) {
	    TNode prev = getTrainZero();

		if(prev.getNext() == null ) {
			return;
		}

		TNode currentStation = prev.getNext();

		while(currentStation != null) {
			if (station == currentStation.getLocation()) {
				prev.setNext(currentStation.getNext());
				return;
			}

			prev = currentStation;
			currentStation = currentStation.getNext();
		}
	}

	/**
	 * Modifies the layered list to add a new bus stop at the specified location. Do nothing
	 * if there is no corresponding walking location.
	 * 
	 * @param busStop The location of the bus stop to add
	 */
	public void addBusStop(int busStop) {
		TNode prev = getTrainZero().getDown();

		if(prev.getNext() == null ) {
			return;
		}

		TNode currentStation = prev.getNext();

		while(currentStation != null) {
			if(currentStation.getLocation() == busStop) {
				return;
			}

			if (prev.getLocation() < busStop && currentStation.getLocation() > busStop) {
				TNode newBusStop = new TNode(busStop);
				newBusStop.setNext(currentStation);
				prev.setNext(newBusStop);

				TNode genTemp = getTrainZero().getDown().getDown();
				while(genTemp.getLocation() != newBusStop.getLocation()) {
					genTemp = genTemp.getNext();
				}
	
				newBusStop.setDown(genTemp);
	
				return;
			}

			prev = currentStation;
			currentStation = currentStation.getNext();
		}

		if(prev.getLocation() < busStop) {
			TNode newBusStop = new TNode(busStop);
			prev.setNext(newBusStop);

			TNode genTemp = getTrainZero().getDown().getDown();
			while(genTemp.getLocation() != newBusStop.getLocation()) {
				genTemp = genTemp.getNext();
			}

			newBusStop.setDown(genTemp);
		}
	}
	
	/**
	 * Determines the optimal path to get to a given destination in the walking layer, and 
	 * collects all the nodes which are visited in this path into an arraylist. 
	 * 
	 * @param destination An int representing the destination
	 * @return
	 */
	public ArrayList<TNode> bestPath(int destination) {
		TNode prevStat = getTrainZero();
		TNode currentStation = prevStat.getNext();

		ArrayList<TNode> path = new ArrayList<TNode>();

		while (currentStation != null) {
			path.add(prevStat);

			if(currentStation.getLocation() > destination) {
				break;
			}

			prevStat = currentStation;
			currentStation = currentStation.getNext();
		}

		if(currentStation == null) {
			path.add(prevStat);
		}

		prevStat = prevStat.getDown();
		currentStation = prevStat.getNext();

		while(currentStation != null) {
			path.add(prevStat);

			if(currentStation.getLocation() > destination) {
				break;
			}

			prevStat = currentStation;
			currentStation = currentStation.getNext();
		}

		if(currentStation == null) {
			path.add(prevStat);
		}

		prevStat = prevStat.getDown();
		currentStation = prevStat.getNext();

		while(currentStation != null) {
			path.add(prevStat);

			if(prevStat.getLocation() == destination) {
				break;
			}

			prevStat = currentStation;
			currentStation = currentStation.getNext();
		}

		if(currentStation == null) {
			path.add(prevStat);
		}
	    
	    return path;
	}

	/**
	 * Returns a deep copy of the given layered list, which contains exactly the same
	 * locations and connections, but every node is a NEW node.
	 * 
	 * @return A reference to the train zero node of a deep copy
	 */
	public TNode duplicate() {
		TNode root = new TNode(0);

		TNode trainPtr = root;
		TNode busPtr = new TNode(0);
		TNode wPtr = new TNode(0);

		root.setDown(busPtr);
		busPtr.setDown(wPtr);

		TNode prev = getTrainZero().getDown().getDown();
		TNode oldTemp = prev.getNext();

		while(oldTemp != null) {
			TNode walkingNode = new TNode(oldTemp.getLocation());
			wPtr.setNext(walkingNode);

			prev = oldTemp;
			oldTemp = oldTemp.getNext();
			wPtr = walkingNode;
		}
		
		prev = getTrainZero().getDown();
		oldTemp = prev.getNext();
		TNode genTemp = null;

		while(oldTemp != null) {
			TNode busNode = new TNode(oldTemp.getLocation());
			busPtr.setNext(busNode);

			genTemp = root.getDown().getDown();
			while(genTemp.getLocation() != busNode.getLocation()) {
				genTemp = genTemp.getNext();
			}

			busNode.setDown(genTemp);

			prev = oldTemp;
			oldTemp = oldTemp.getNext();
			busPtr = busNode;
		}

		prev = getTrainZero();
		oldTemp = prev.getNext();


		while(oldTemp != null) {
			TNode trainNode = new TNode(oldTemp.getLocation());
			trainPtr.setNext(trainNode);

			genTemp = root.getDown();
			while(genTemp.getLocation() != trainNode.getLocation()) {
				genTemp = genTemp.getNext();
			}

			trainNode.setDown(genTemp);

			prev = oldTemp;
			oldTemp = oldTemp.getNext();
			trainPtr = trainNode;
		}

	    return root;
	}

	/**
	 * Modifies the given layered list to add a scooter layer in between the bus and
	 * walking layer.
	 * 
	 * @param scooterStops An int array representing where the scooter stops are located
	 */
	public void addScooter(int[] scooterStops) {
		TNode busStart = getTrainZero().getDown();

		TNode scooterTemp = new TNode(0);
		scooterTemp.setDown(busStart.getDown());
		busStart.setDown(scooterTemp);

		for(int i = 0; i < scooterStops.length; i++) {
			TNode newScooterStop = new TNode(scooterStops[i]);
			scooterTemp.setNext(newScooterStop);

			TNode genTemp = busStart.getDown().getDown();
			while(genTemp.getLocation() != newScooterStop.getLocation()) {
				genTemp = genTemp.getNext();
			}

			newScooterStop.setDown(genTemp);

			scooterTemp = newScooterStop;
		}

		TNode pbs = busStart;
		TNode currentBusStop = busStart.getNext();

		while(currentBusStop != null) {
			TNode genTemp = busStart.getDown();
			while(genTemp.getLocation() != currentBusStop.getLocation()) {
				genTemp = genTemp.getNext();
			}

			currentBusStop.setDown(genTemp);

			pbs = currentBusStop;
			currentBusStop = currentBusStop.getNext();
		}
	}

	/**
	 * Used by the driver to display the layered linked list. 
	 * DO NOT edit.
	 */
	public void printList() {
		// Traverse the starts of the layers, then the layers within
		for (TNode vertPtr = trainZero; vertPtr != null; vertPtr = vertPtr.getDown()) {
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// Output the location, then prepare for the arrow to the next
				StdOut.print(horizPtr.getLocation());
				if (horizPtr.getNext() == null) break;
				
				// Spacing is determined by the numbers in the walking layer
				for (int i = horizPtr.getLocation()+1; i < horizPtr.getNext().getLocation(); i++) {
					StdOut.print("--");
					int numLen = String.valueOf(i).length();
					for (int j = 0; j < numLen; j++) StdOut.print("-");
				}
				StdOut.print("->");
			}

			// Prepare for vertical lines
			if (vertPtr.getDown() == null) break;
			StdOut.println();
			
			TNode downPtr = vertPtr.getDown();
			// Reset horizPtr, and output a | under each number
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				while (downPtr.getLocation() < horizPtr.getLocation()) downPtr = downPtr.getNext();
				if (downPtr.getLocation() == horizPtr.getLocation() && horizPtr.getDown() == downPtr) StdOut.print("|");
				else StdOut.print(" ");
				int numLen = String.valueOf(horizPtr.getLocation()).length();
				for (int j = 0; j < numLen-1; j++) StdOut.print(" ");
				
				if (horizPtr.getNext() == null) break;
				
				for (int i = horizPtr.getLocation()+1; i <= horizPtr.getNext().getLocation(); i++) {
					StdOut.print("  ");

					if (i != horizPtr.getNext().getLocation()) {
						numLen = String.valueOf(i).length();
						for (int j = 0; j < numLen; j++) StdOut.print(" ");
					}
				}
			}
			StdOut.println();
		}
		StdOut.println();
	}
	
	/**
	 * Used by the driver to display best path. 
	 * DO NOT edit.
	 */
	public void printBestPath(int destination) {
		ArrayList<TNode> path = bestPath(destination);
		for (TNode vertPtr = trainZero; vertPtr != null; vertPtr = vertPtr.getDown()) {
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// ONLY print the number if this node is in the path, otherwise spaces
				if (path.contains(horizPtr)) StdOut.print(horizPtr.getLocation());
				else {
					int numLen = String.valueOf(horizPtr.getLocation()).length();
					for (int i = 0; i < numLen; i++) StdOut.print(" ");
				}
				if (horizPtr.getNext() == null) break;
				
				// ONLY print the edge if both ends are in the path, otherwise spaces
				String separator = (path.contains(horizPtr) && path.contains(horizPtr.getNext())) ? ">" : " ";
				for (int i = horizPtr.getLocation()+1; i < horizPtr.getNext().getLocation(); i++) {
					StdOut.print(separator + separator);
					
					int numLen = String.valueOf(i).length();
					for (int j = 0; j < numLen; j++) StdOut.print(separator);
				}

				StdOut.print(separator + separator);
			}
			
			if (vertPtr.getDown() == null) break;
			StdOut.println();

			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// ONLY print the vertical edge if both ends are in the path, otherwise space
				StdOut.print((path.contains(horizPtr) && path.contains(horizPtr.getDown())) ? "V" : " ");
				int numLen = String.valueOf(horizPtr.getLocation()).length();
				for (int j = 0; j < numLen-1; j++) StdOut.print(" ");
				
				if (horizPtr.getNext() == null) break;
				
				for (int i = horizPtr.getLocation()+1; i <= horizPtr.getNext().getLocation(); i++) {
					StdOut.print("  ");

					if (i != horizPtr.getNext().getLocation()) {
						numLen = String.valueOf(i).length();
						for (int j = 0; j < numLen; j++) StdOut.print(" ");
					}
				}
			}
			StdOut.println();
		}
		StdOut.println();
	}
}