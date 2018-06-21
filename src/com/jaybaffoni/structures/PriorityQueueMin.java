package com.jaybaffoni.structures;

import java.util.ArrayList;

public class PriorityQueueMin <T extends Comparable<T>> {
	
	private int size;
	//private int maxSize;
	private ArrayList<T> heap = null;

	public PriorityQueueMin() {
		this.size = 0;
		//this.maxSize = 2;
		heap = new ArrayList<T>();
	}
	
	public void add(T value) {
		//System.out.println("new thing added to pq");
		//System.out.println(value.toString());
		heap.add(value);
		bubble(size);
		size++;
	}
	
	private void bubble(int index) {
		
		int parentIndex = (index - 1) / 2;
		while(index > 0 && (heap.get(index).compareTo(heap.get(parentIndex)) <= 0)) {
			swap(parentIndex, index);
			index = parentIndex;
			parentIndex = (index - 1) / 2;
		}
		
	}
	
	private void swap(int index1, int index2) {
		
		T temp = heap.get(index1);
		heap.set(index1, heap.get(index2));
		heap.set(index2, temp);
		
	}
	
	public T peek() {
		
		if(isEmpty()) {
			// TODO throw error
		}
		return heap.get(0);
		
	}
	
	public T poll() {
		if(isEmpty()) {
			System.out.println("nothing to poll");
		}
		T toReturn = removeAt(0);
		//System.out.println(toReturn.toString());
		return toReturn;
	}
	
	public T removeAt(int index) {
		
		size--;
		T toReturn = heap.get(index);
		//System.out.println(toReturn.toString());
		swap(index, size);
		//heap.set(size,  null);
		heap.remove(size);
		
		if(index == size) {
			return toReturn;
		}
		
		T swapped = heap.get(index);
		sink(index);
		
		if(heap.get(index).equals(swapped)) {
			bubble(index);
		}
		
		return toReturn;
		
	}
	
	private void sink(int index) {
		
		while(true) {
			int leftChildIndex = (2 * index) + 1;
			int rightChildIndex = (2 * index) + 2;
			int smaller = leftChildIndex;
			if(rightChildIndex < size && compare(rightChildIndex, leftChildIndex) <= 0){
				smaller = rightChildIndex;
			}
			if(leftChildIndex >= size || compare(index, smaller) <= 0) {
				break;
			}
			swap(smaller, index);
			index = smaller;
		}
		
	}
	
	public int compare(int index1, int index2) {
		return heap.get(index1).compareTo(heap.get(index2));
	}
	
	
	public boolean isEmpty() {
		
		return size == 0;
		
	}
	
	public int realSize() {
		return heap.size();
	}
	
	public String toString() {
		String toReturn = "";
		for(int count = 0; count < size; count++) {
			toReturn = toReturn + heap.get(count).toString() + ",";
		}
		return toReturn;
	}
}
