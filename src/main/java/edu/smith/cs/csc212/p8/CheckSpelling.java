package edu.smith.cs.csc212.p8;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class CheckSpelling {
	/**
	 * This is the book you want to analyze.
	 */
	public static final String bookPath = "src/main/resources/sirdo.txt";

	/**
	 * https://stackoverflow.com/questions/36519354/cant-read-the-whole-file-into-a-string-in-java
	 * 
	 * @param bookPath -> file path in project
	 * @return ->
	 */
	public static String readFileAsString(String bookPath) {
		String text = "";
		try {

			text = new String(Files.readAllBytes(Paths.get(bookPath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return text;
	}

	/**
	 * Read all lines from the UNIX dictionary.
	 * 
	 * @return a list of words!
	 */
	public static List<String> loadDictionary() {
		long start = System.nanoTime();
		List<String> words;
		try {
			words = Files.readAllLines(new File("src/main/resources/words").toPath());
		} catch (IOException e) {
			throw new RuntimeException("Couldn't find dictionary.", e);
		}
		long end = System.nanoTime();
		double time = (end - start) / 1e9;
		System.out.println("Loaded " + words.size() + " entries in " + time + " seconds.");
		return words;
	}

	/**
	 * This method looks for all the words in a dictionary.
	 * 
	 * @param words      - the "queries"
	 * @param dictionary - the data structure.
	 */
	public static void timeLookup(List<String> words, Collection<String> dictionary) {
		long startLookup = System.nanoTime();

		int found = 0;
		for (String w : words) {
			if (dictionary.contains(w)) {
				found++;
			}
		}

		long endLookup = System.nanoTime();
		double fractionFound = found / (double) words.size();
		// increase fractoinFound size
		double timeSpentPerItem = (endLookup - startLookup) / ((double) words.size());
		int nsPerItem = (int) timeSpentPerItem;
		System.out.println(dictionary.getClass().getSimpleName() + ": Lookup of items found=" + fractionFound + " time="
				+ nsPerItem + " ns/item");
	}

	public static void main(String[] args) {
		// --- Load the dictionary.
		List<String> listOfWords = loadDictionary();

		// start of the time
		long treeStartTime = System.nanoTime();

		// --- Create a bunch of data structures for testing:
		//TreeSet<String> treeOfWords = new TreeSet<>(listOfWords);
		
		// B: Give it one piece of data at a time.
		TreeSet<String> treeOfWords = new TreeSet<>();
		for (String w : listOfWords) {
			treeOfWords.add(w);
		}

		// end of the time
		long treeEndTime = System.nanoTime();

		// calculate the change in time taken and print it
		double treeCountTime = (treeEndTime - treeStartTime) / (1e9);
		System.out.println("Time it took to fill Tree: " + treeCountTime);

		long hashStartTime = System.nanoTime();

		//HashSet<String> hashOfWords = new HashSet<>(listOfWords);

		// B: Give it one piece of data at a time.
		HashSet<String> hashOfWords = new HashSet<>();
		for (String w : listOfWords) {
			hashOfWords.add(w);
		}

		long hashEndTime = System.nanoTime();

		// calculate the change in time taken and print it
		double hashCountTime = (hashEndTime - hashStartTime) / (1e9);
		System.out.println("Time it took to fill Hash Set: " + hashCountTime);

		long sortedStartTime = System.nanoTime();

		SortedStringListSet bsl = new SortedStringListSet(listOfWords);

		long sortedEndTime = System.nanoTime();

		// calculate the change in time taken and print it
		double sortedCountTime = (sortedEndTime - sortedStartTime) / (1e9);
		System.out.println("Time it took to fill Sorted String List: " + sortedCountTime);

		long charTrieStartTime = System.nanoTime();

		CharTrie trie = new CharTrie();
		for (String w : listOfWords) {
			trie.insert(w);
		}

		long charTrieEndTime = System.nanoTime();

		// calculate the change in time taken and print it
		double charTrieTime = (charTrieEndTime - charTrieStartTime) / (1e9);
		System.out.println("Time it took to fill CharTrie " + charTrieTime);

		long llHashStartTime = System.nanoTime();

		LLHash hm100k = new LLHash(200000);
		for (String w : listOfWords) {
			hm100k.add(w);
		}

		long llHashEndTime = System.nanoTime();

		// calculate the change in time taken and print it
		double llHashTime = (llHashEndTime - llHashStartTime) / (1e9);
		System.out.println("Time it took to fill LLHash: " + llHashTime);

		// --- Make sure that every word in the dictionary is in the dictionary:
		timeLookup(listOfWords, treeOfWords);
		timeLookup(listOfWords, hashOfWords);
		timeLookup(listOfWords, bsl);
		timeLookup(listOfWords, trie);
		timeLookup(listOfWords, hm100k);

		// --- Create a dataset of mixed hits and misses:
		// List<String> hitsAndMisses = new ArrayList<>();
		
		for (int j=0; j<2; j++) {
			System.out.println("Warm-up, j="+j);
			for (int i=0; i<=10; i++) {
				double fraction = i / 10.0;
				// --- Create a dataset of mixed hits and misses:
				List<String> hitsAndMisses = createMixedDataset(listOfWords, 10000, fraction);
					
				timeLookup(hitsAndMisses, treeOfWords);
				timeLookup(hitsAndMisses, hashOfWords);
				timeLookup(hitsAndMisses, bsl);
				timeLookup(hitsAndMisses, trie);
				timeLookup(hitsAndMisses, hm100k);
			}
		}

		// --- linear list timing:
		// Looking up in a list is so slow, we need to sample:
		System.out.println("Start of list: ");
		timeLookup(listOfWords.subList(0, 1000), listOfWords);
		System.out.println("End of list: ");
		timeLookup(listOfWords.subList(listOfWords.size() - 100, listOfWords.size()), listOfWords);

		// --- print statistics about the data structures:
		System.out.println("Count-Nodes: " + trie.countNodes());
		System.out.println("Count-Items: " + hm100k.size());

		System.out.println("Count-Collisions[100k]: " + hm100k.countCollisions());
		System.out.println("Count-Used-Buckets[100k]: " + hm100k.countUsedBuckets());
		System.out.println("Load-Factor[100k]: " + hm100k.countUsedBuckets() / 100000.0);

		System.out.println("log_2 of listOfWords.size(): " + listOfWords.size());

		String book = readFileAsString(bookPath);

		List<String> sirDoyle = WordSplitter.splitTextToWords(book);
		
		// used this method to find the fraction of words misspelled 
		// compares the list of words in the book to the list of words 
//		timeLookup(sirDoyle, listOfWords);
		
		timeLookup(sirDoyle, treeOfWords);
		timeLookup(sirDoyle, hashOfWords);
		timeLookup(sirDoyle, bsl);
		timeLookup(sirDoyle, trie);
		timeLookup(sirDoyle, hm100k);
		
		// we'll make a new hash set as to remove all duplicates
		HashSet<String> things = new HashSet<>(sirDoyle);
		sirDoyle.clear();
		sirDoyle.addAll(things);
		
		// make a new list for all the misspelled words
		List<String> missedList = new ArrayList<>();
		
		// making a list of words that were misspelled
		// to check if words are misspelled
		for(String stuff : sirDoyle) {
			if(! listOfWords.contains(stuff)) {
				missedList.add(stuff);
			}
		}
		
		// System.out.println(missedList);
		
		// System.out.println("List of words misspelled"+missedList);
		System.out.println("Done!");
	}

	private static List<String> createMixedDataset(List<String> listOfWords, int i, double fraction) {
		// shuffle the words in the dictionary
		Collections.shuffle(listOfWords);
		
		// make a new list
		List<String> newList = new ArrayList<>();
		
		// sublist the original list to just have the first 10000 items
		newList = listOfWords.subList(0, i);
		
		// we will then have a new size, which is the fraction of the original size
		double sizeHit = (newList.size())*fraction;
		
		// go up till the new size of the list
		for(int j = 0; j<sizeHit; j++) {
			// add to our new list 
			newList.add((newList.get(j)).replace(newList.get(j), (newList.get(j)+"bedengan")));
			newList.remove(newList.get(j));
		}
		
		return newList;
	}

}
