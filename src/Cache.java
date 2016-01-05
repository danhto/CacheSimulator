import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class Cache {

	public int tagBits;
	public int blockBits;
	public int offsetBitSize;
	public int numOfSets;
	private int n_way_associative;
	private ArrayList<String> cache;
	public int MISS = 0;
	public int HIT = 1;
	private int hmArray[];
	
	/*
	 * Class simulates a cache with parameterized block address size, tag size, offset size, associativity of cache and number of indices in cache
	 */
	public Cache(int tagBitSize, int offset, int associativity, int numOfIndices) {
		
		tagBits = tagBitSize;
		offsetBitSize = offset;
		n_way_associative = associativity;
		cache = new ArrayList<String>();
		
		for (int i = 0; i < numOfIndices; i++) {
			String tmp = "0";		
			cache.add(tmp);
		}
	}
	
	/*
	 * Method writes to cache and checks if it is a hit or miss.
	 * If data is not found write the data to the cache.
	 */
	public int writeToCache(String address) {
		
		String tagBinary = address.substring(0, tagBits);
		String indexBinary = address.substring(tagBits, address.length() - offsetBitSize);
		int index = Integer.parseInt(indexBinary, 2);

		System.out.println("Addr: "+address);
		System.out.println("Index: "+index);
		System.out.println("Tag: "+Integer.parseInt(tagBinary, 2));
		
		if (index == 0) {
			index = ((index + 1)*n_way_associative) - n_way_associative;
		}
		else {
			index = (index*n_way_associative) - n_way_associative;
		}
		
		String cacheLine;
		hmArray = new int[n_way_associative];
		int writeStatus = MISS;
		int n = 0;
		Random rand = new Random();
		
		// Goto cache line identified by index and search for data
		for (int i = index; i < (index + n_way_associative); i++) {
			cacheLine = cache.get(index).trim();
			
			// If location searched is invalid store it's index for potential write
			if (cacheLine.equals("0")) {
				hmArray[n] = i;
			}
			// If location is valid check if it contains the tag entry, if yes status is HIT,
			// if do not store index for write, because it has data
			else {
				if (cacheLine.equals(tagBinary)) {
					writeStatus = HIT;
				}
				else {
					hmArray[n] = -1;	
				}	
			}
			
			n++;
			
			// If data is found then cache search is a HIT, no need to check other cache lines
			if (writeStatus == HIT) {
				break;
			}
			
		}
		
		// If data is not found, write it into the cache
		if (writeStatus == MISS) {
			boolean written = false;
			int writeTries = 0;
			
			// Randomly select an index within cache line to write to
			while (!written) {

				int writeLoc = rand.nextInt(n_way_associative);
				writeTries++;
				
				// Prioritize writing to an empty cache line first
				if (hmArray[writeLoc] != -1) {
					cache.add(hmArray[writeLoc], tagBinary);
					written = true;							
				}	
				
				// If a non-empty cache line cannot be found then randomly select a cache line to write to
				if (!written && writeTries == n_way_associative) {						
					writeLoc = rand.nextInt(n_way_associative);
					cache.add(index + writeLoc, tagBinary);
					written = true;
				}
			}
		}

		return writeStatus;
	}
	
	public int readFromCache(String address) {
		
		String tagBinary = address.substring(0, tagBits - 1);
		String indexBinary = address.substring(tagBits, address.length() - 1 - offsetBitSize);
		int index = Integer.getInteger(indexBinary, 2); 
		
		if (index == 0) {
			index = ((index + 1)*n_way_associative) - n_way_associative;
		}
		else {
			index = (index*n_way_associative) - n_way_associative;
		}
		
		String cacheLine;
		hmArray = new int[n_way_associative];
		int readStatus = MISS;
		int n = 0;
		Random rand = new Random();
		
		// Goto cache line identified by index and search for data
		for (int i = index; i < (index + n_way_associative); i++) {
			cacheLine = cache.get(i).trim();
			
			// If location searched is invalid store it's index for potential write
			if (cacheLine.equals("0")) {
				hmArray[n] = i;
			}
			// If location is valid check if it contains the tag entry, if yes status is HIT,
			// if do not store index for write, because it has data
			else {
				if (cacheLine.equals(tagBinary)) {
					readStatus = HIT;
				}
				else {
					hmArray[n] = -1;	
				}	
			}
			
			n++;
			
			// If data is found then cache search is a HIT, no need to check other cache lines
			if (readStatus == HIT) {
				break;
			}
			
		}
		
		// If data is not found, write it into the cache
		if (readStatus == MISS) {
			boolean written = false;
			int writeTries = 0;
			
			// Randomly select an index within cache line to write to
			while (!written) {

				int writeLoc = rand.nextInt(n_way_associative);

				writeTries++;
				
				// Prioritize writing to an empty cache line first
				if (hmArray[writeLoc] != -1) {
					cache.add(hmArray[writeLoc], tagBinary);
					written = true;							
				}	
				
				// If a non-empty cache line cannot be found then randomly select a cache line to write to
				if (!written && writeTries == n_way_associative) {						
					writeLoc = rand.nextInt(n_way_associative);
					cache.add(index + writeLoc, tagBinary);
					written = true;
				}
			}
			
		}

		return readStatus;
	}
}
