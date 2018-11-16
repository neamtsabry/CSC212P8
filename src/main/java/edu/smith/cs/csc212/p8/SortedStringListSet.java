package edu.smith.cs.csc212.p8;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This is an alternate implementation of a dictionary, based on a sorted list.
 * It often makes the most sense if the dictionary never changes (compared to a
 * TreeMap). You could write a delete, but it's tricky.
 * 
 * @author jfoley
 */
public class SortedStringListSet extends AbstractSet<String> {
	/**
	 * This is the sorted list of data.
	 */
	private List<String> data;

	/**
	 * This is the constructor: we take in data, copy and sort it (just to be sure).
	 * 
	 * @param data the input list.
	 */
	public SortedStringListSet(List<String> data) {
		this.data = new ArrayList<>(data);
		Collections.sort(this.data);
	}

	/**
	 * So we can use it in a for-loop.
	 */
	@Override
	public Iterator<String> iterator() {
		return data.iterator();
	}

	/**
	 * This method takes an object because it was invented before Java 5.
	 */
	@Override
	public boolean contains(Object key) {
		return binarySearch((String) key, 0, this.data.size()) >= 0;
	}

	/**
	 * @param query - the string to look for.
	 * @param start - the left-hand side of this search (inclusive)
	 * @param end   - the right-hand side of this search (exclusive)
	 * @return the index found, OR negative if not found.
	 */
	private int binarySearch(String query, int start, int end) {
		// return Collections.binarySearch(data, query);
		
		/*
		 * This part of the code is searching for the item iteratively.
		 * It goes to the middle, checks the items before it and after.
		 * It keeps doing this until it finds query
		 */
		
//		start=0;
//		end = this.data.size()-1;
//		while(start<=end) {
//			int mid = start+((end-start)/2);
//			if(this.data.get(mid).compareTo(query) == 0) {
//				return mid;
//			}
//			else if(this.data.get(mid).compareTo(query) < 0) {
//				start = mid+1;
//		    }
//			else {
//				end = mid - 1;
//			}
//		}
//		
//		return -1;
		
		/*
		 * I also tried recursively, which works the same way but 
		 * keeps calling the method until it finds query. It works 
		 * perfectly. 
		 */
		
		if(start>=end) {
			return -1;
		}
		
		else {
			int mid = start+((end-start)/2);
			if (data.get(mid).compareTo(query) < 0) {
				//start = mid + 1;
				return binarySearch(query, mid + 1, end);
			}

			else if (data.get(mid).compareTo(query) > 0){
				//end = mid - 1;
				return binarySearch(query, start, mid);
			}
			else {
				return mid;
			}
		}
	}

	/**
	 * So we know how big this set is.
	 */
	@Override
	public int size() {
		return data.size();
	}

}
