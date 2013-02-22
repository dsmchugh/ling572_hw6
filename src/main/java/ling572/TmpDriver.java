package ling572;

import java.io.*;
import java.util.*;

import ling572.util.Instance;
import ling572.util.VectorFileReader;

public class TmpDriver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File testData = new File(args[0]);
		File boundaryFile = new File(args[1]);
		File modelFile = new File(args[2]);
		File sysOutput = new File(args[3]);
		double beamSize = Double.parseDouble(args[4]);
		int topN = Integer.parseInt(args[5]);
		int topK = Integer.parseInt(args[6]);
		
		MaxEntModel model = new MaxEntModel();
		model.loadFromFile(modelFile);
		
		List<Instance> allInstances = VectorFileReader.indexInstances(testData);
		
		BeamSearch beamSearch = new BeamSearch(topK, topN, beamSize, model);
		beamSearch.search(allInstances);
		
		BeamSearchNode node = beamSearch.getBestNode();

		List<String> tags = new ArrayList<String>();
		
		while (node.getParent() != null) {
			tags.add(node.getName() + " " + node.getGoldTag() + " " + node.getTag() + " " + node.getNodeProb());
			node = node.getParent();
		}		
		
		Collections.reverse(tags);
		
		for (String tag : tags) {
			System.out.println(tag);
		}
	}
}
