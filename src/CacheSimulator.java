import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class CacheSimulator {

	static int cacheSize;
	static int blockSize;
	int associativity;
	String fileName;
	static int TOTAL_BIT_SIZE_ADDR = 32;
	static int OFFSET_SIZE;
	int numOfIndices;
	static Cache cache;
	static List<String> instructions;
	static List<String> instAddresses;
	
	public CacheSimulator (String args[]) {
		
		cacheSize = Integer.parseInt(args[0].trim());
		blockSize = Integer.parseInt(args[1].trim());
		associativity = Integer.parseInt(args[2].trim());
		String fileName = args[3].trim(); 
		
		instructions = new ArrayList();
		instAddresses = new ArrayList();
		
		initCache();
	}
	
	private void initCache() { 

		int OFFSET_SIZE = (int) (Math.log(blockSize)/Math.log(2));
		int indexBits = (int) (Math.log(cacheSize*1024/(blockSize*associativity))/Math.log(2));
		int numOfIndicies = cacheSize*1024/blockSize;
		int tagBitSize = TOTAL_BIT_SIZE_ADDR - indexBits - OFFSET_SIZE;

		cache = new Cache(tagBitSize, OFFSET_SIZE, associativity, numOfIndicies);
		
		readTrace();
	}
	
	private static void readTrace() {
		
		Scanner reader = null;
		
		// Open file for reading
		try {
			reader = new Scanner(new File("trace.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Read file line by line
		while (reader.hasNext()) {
			String line = reader.nextLine();
			
			// Split instruction address and R/W address
			line = line.split(":")[1].trim();
			
			// Store R/W operations
			instructions.add(line.split(" ")[0]);
			
			// Convert address hex string to binary
			String hexString = line.split(" ")[1].replace("0x", "");
			String binaryString = "";
			
			for (int i = 0; i < hexString.length(); i++) {
				String c = hexString.substring(i, i+1);
				int hex = Integer.parseInt(c, 16);
				String bin = Integer.toBinaryString(hex);
				
				while (bin.length() < 4) {
					bin = "0"+bin;
				}

				binaryString = binaryString + bin;
			}
			
			// If binary string is 28 bits pad 0s to 32 bits
			if (binaryString.length() == 28) {
				binaryString = "0000" + binaryString;
			}
			
			// Store addresses in array for simulation
			instAddresses.add(binaryString.toString());
		}
		
	}
	
	public static void main(String args[]) {
		
		int totalMisses = 0;
		int readMisses = 0;
		int writeMisses = 0;
		double missRate = 0;
				
		CacheSimulator cs = new CacheSimulator(args);
		
		readTrace();
		int totalOperations = instructions.size();
		
		for (int i = 0; i < instructions.size(); i++) {
			
			String address = instAddresses.get(i);
			String operation = instructions.get(i);
			int cacheResult;
			
			if (operation.equals("R")) {
				cacheResult = cache.readFromCache(address);
			}
			else
			{
				cacheResult = cache.writeToCache(address);
			}
			
			if (operation.equals("R")) {
				if (cacheResult == cache.MISS) {
					readMisses++;
				}
			}
			else {
				if (cacheResult == cache.MISS) {
					writeMisses++;
				}
			}

		}
		
		totalMisses = readMisses + writeMisses;
		missRate = (double) totalMisses/totalOperations*100;
		int indexBits = TOTAL_BIT_SIZE_ADDR - cache.tagBits - cache.offsetBitSize;
		
		System.out.printf("Total instructions: %d\n" +
				"Address size bits: %d\n" +
				"Tag bits: %d\n" +
				"Index bits: %d\n" +
				"Offset bits: %d\n" +
				"Read misses: %d\n" +
				"Write misses: %d\n" +
				"Total misses: %d\n" +
				"Miss rate: %.2f%%",
				totalOperations,
				TOTAL_BIT_SIZE_ADDR, 
				cache.tagBits,
				indexBits,
				cache.offsetBitSize, 
				readMisses, 
				writeMisses, 
				totalMisses, 
				missRate);
	}
}
