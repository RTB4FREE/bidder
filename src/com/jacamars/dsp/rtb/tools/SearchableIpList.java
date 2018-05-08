package com.jacamars.dsp.rtb.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchableIpList {

	public static List<Long> in = new ArrayList<Long>();
	public static List<Long> out = new ArrayList<Long>();

	public static Map<String, SearchableIpList> symbols = new HashMap<String, SearchableIpList>();
	List<Range> list = new ArrayList<Range>();
	int high;

	public static boolean searchTable(String key, String ip) {
		SearchableIpList x = SearchableIpList.symbols.get(key);
		if (x == null)
			return false;
		return x.search(ip);
	}

	public static boolean searchTable(String key, long ip) {
		SearchableIpList x = SearchableIpList.symbols.get(key);
		if (x == null)
			return false;
		return x.search(ip);
	}

	public static void main(String args[]) throws Exception {
		SearchableIpList sr = new SearchableIpList("ISP", "/home/ben/Downloads/ISP.txt");

		for (int i = 0; i < in.size(); i++) {
			String ip = longToIp(in.get(i));
			long now = System.nanoTime();
			boolean x = sr.binarySearch(in.get(i));
			now = System.nanoTime() - now;
			now /= 1000;
			System.out.println(ip + " " + x + " " + now + " micro seconds");
		}
		// 203.180.58.167-203.180.58.187
		// 203.212.37.105-203.212.37.125
	}

	public SearchableIpList(String name, String file) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(file));
		long x = 0, k = 0;
		long old = 0;
		String[] parts = null;
		String oldpart = null;
		for (String line; (line = br.readLine()) != null;) {
			long start = 0;
			long end = 0;
			parts = line.split("-");
			if (parts[0].length() > 0) {

				start = ipToLong(parts[0]);
				String test = longToIp(start);
				long xx = ipToLong(test);

				end = ipToLong(parts[1]);

				if (k % 1000 == 0 && in.size() < 10) {
					in.add((long)1);
				}

				Range r = new Range(start, end);
				list.add(r);
			}
			
			k++;
		}
		high = list.size() - 1;
		symbols.put(name, this);

	}

	public boolean search(String ip) {
		long address = ipToLong(ip);
		return search(address);
	}

	public boolean search(long key) {
		int low = 0;
		while (high >= low) {
			int middle = (low + high) / 2;
			Range data = list.get(middle);
			if (key >= data.start && key <= data.end) {
				return true;
			}
			if (data.start < key) {
				low = middle + 1;
			}
			if (data.start > key) {
				high = middle - 1;
			}
		}
		return false;
	}

	public boolean binarySearch(long key) {
		int position;
		int comparisonCount = 1; // counting the number of comparisons
									// (optional)
		int lowerbound = 0;
		int upperbound = list.size() - 1;

		// To start, find the subscript of the middle position.
		position = (lowerbound + upperbound) / 2;

		while (true) {
			Range r = list.get(position);
			if (lowerbound <= upperbound) {
				comparisonCount++;
				if (r.start > key) // If the number is > key, ..
				{ // decrease position by one.
					upperbound = position - 1;
				} else {
					lowerbound = position + 1; // Else, increase position by
												// one.
				}
				position = (lowerbound + upperbound) / 2;
			}
			if (lowerbound <= upperbound) {
				return true;
			} else
				return false;
		}
	}

	public static long ipToLong(String ipAddress) {

		String[] ipAddressInArray = ipAddress.split("\\.");

		long result = 0;
		for (int i = 0; i < ipAddressInArray.length; i++) {

			int power = 3 - i;
			int ip = Integer.parseInt(ipAddressInArray[i]);
			result += ip * Math.pow(256, power);

		}

		return result;
	}

	public static String longToIp(long ip) {
		if (ip > 4294967295l || ip < 0) {
			throw new IllegalArgumentException("invalid ip");
		}
		StringBuilder ipAddress = new StringBuilder();
		for (int i = 3; i >= 0; i--) {
			int shift = i * 8;
			ipAddress.append((ip & (0xff << shift)) >> shift);
			if (i > 0) {
				ipAddress.append(".");
			}
		}
		return ipAddress.toString();
	}
}

class Range {
	public long start;
	public long end;

	public Range(long start, long end) {
		this.start = start;
		this.end = end;
	}
}