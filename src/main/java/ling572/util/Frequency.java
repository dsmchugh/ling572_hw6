package ling572.util;
import java.io.*;
import java.util.*;
import java.util.Map.*;

public class Frequency<T extends Comparable<T>> implements Serializable {
	private static final long serialVersionUID = -6086147079437535958L;
	private Map<T, Integer> counts;
	private int size;
	
	public Frequency() {
		this.counts = new HashMap<T, Integer>();
		this.size = 0;
	}
	
	public void save(File outputFile) {		
		try (ObjectOutputStream objectOutput = new ObjectOutputStream(new FileOutputStream(outputFile))) {
				objectOutput.writeObject(this);
				objectOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error: Cannot save " + outputFile.getName());
			System.exit(1);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Frequency load(File importFile) {
		Frequency frequency = null;
		
		try(FileInputStream fileInput = new FileInputStream(importFile);
			ObjectInputStream objectInput = new ObjectInputStream(fileInput);) {
			frequency = (Frequency)objectInput.readObject();
				objectInput.close();
		  } catch (IOException e) {
				e.getStackTrace();
				System.out.println("Error: Cannot read from " + importFile.getName());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
		}
		
		return frequency;
	}
		
	public void count(T type) {
		this.count(type, 1);
	}
	
	public void count(T type, int quantity) {
		Integer typeCount = this.counts.get(type);
		
		if (typeCount == null)
			typeCount = 0;
			
		typeCount = typeCount + quantity;
			
		counts.put(type, typeCount);
		this.size = this.size + quantity;
	}
	
	public int get(T type) {
		Integer count = this.counts.get(type);
		return count == null ? 0 : count;
	}
	
	public double getProbability(T type) {
		Integer count = this.get(type);
		
		if (count == null)
			count = 0;
		
		return (double)count/(double)this.size;
	}
	
	public void removeSingletons() {
		Set<T> removeList = new HashSet<T>();
		
		for (Map.Entry<T, Integer> entry : this.counts.entrySet()) {
			if (entry.getValue() == 1) {
				removeList.add(entry.getKey());
			}
		}
		
		for (T removeItem : removeList) {
			this.counts.remove(removeItem);
		}
	}
	
	public void printDescending(int n) {
		for (Entry<T, Integer> typeCount : getCountsSortedByValueDesc(n).entrySet()) {
			System.out.println(typeCount.getKey() + "\t" + typeCount.getValue());
		}
	}
	
	public int getSize() {
		return this.size;
	}
	
	public int getLength() {
		return this.counts.size();
	}

	public double getEntropy() {
		double entropy = 0;
		
		for (T key : counts.keySet()) {
			double probability = this.getProbability(key);
			entropy -= probability * getLog2x(probability);
		}
		
		return entropy;
	}
		
	private static double getLog2x(double x) {
		return Math.log10(x)/Math.log10(2);
	}
	
	public Map<T, Integer> getCounts() {
		return this.counts;
	}
	
	public void setCount(T type, int count) {
		this.counts.put(type, count);
	}
	
	public Map<T,Integer> getCountsSortedByValueDesc() {
		return getCountsSortedByValueDesc(null);
	}
	
	public Map<T,Integer> getCountsSortedByValueDesc(Integer n) {
		List<Entry<T,Integer>> list = new LinkedList<Entry<T, Integer>>(getCounts().entrySet());
		
		Collections.sort(list, new Comparator<Entry<T,Integer>>(){
			public int compare(Entry<T,Integer> x, Entry<T,Integer> y) {
				int c;
				//	return negative (to reverse)
				c = -(x.getValue()).compareTo(y.getValue());
				
				if (c==0)
					c=(x.getKey()).compareTo(y.getKey());
				
				return c;
			}
		});
			
		Map<T,Integer> sortedTypes = new LinkedHashMap<T,Integer>();
				
		int j=0;
		
		for(Iterator<Entry<T, Integer>> i = list.iterator(); i.hasNext();) {		
			if (n != null && j >= n) 
				break;
			
			Entry<T, Integer> entry = i.next();
			sortedTypes.put(entry.getKey(), entry.getValue());
			j++;
		}
		
		return sortedTypes;
	}

	public Map<T, Integer> getCountsSortedByKey() {
		List<Entry<T,Integer>> list = new LinkedList<Entry<T, Integer>>(getCounts().entrySet());
		
		Collections.sort(list, new Comparator<Entry<T,Integer>>(){
			public int compare(Entry<T,Integer> x, Entry<T,Integer> y) {
				return x.getKey().compareTo(y.getKey());
			}
		});
			
		Map<T,Integer> sortedTypes = new LinkedHashMap<T,Integer>();
					
		for(Iterator<Entry<T, Integer>> i = list.iterator(); i.hasNext();) {
			
			Entry<T, Integer> entry = i.next();
			sortedTypes.put(entry.getKey(), entry.getValue());
		}
		
		return sortedTypes;
	}
}