package ling572;

import java.util.*;
import java.util.Map.Entry;

import ling572.util.Instance;

public class BeamSearch {
	private int topK;
	private int topN;
	private double beamSize;
	private MaxEntModel model;
	
	private BeamSearchNode rootNode = new BeamSearchNode();
	private BeamSearchNode bestNode;
	
	public BeamSearch(int topK, int topN, double beamSize, MaxEntModel model) {
		this.topK = topK;
		this.topN = topN;
		this.beamSize = beamSize;
		this.model = model;
	}
	
	public BeamSearchNode getBestNode() {
		return this.bestNode;
	}
	
	public void search(List<Instance> instances) {
		for (Instance instance : instances) {			
			for (BeamSearchNode node : this.rootNode.getLeaves()){
				String prevTagFeat = this.getPrevTagFeat(node.getTag());
				String prevTwoTagsFeat = this.getPrevTwoTagsFeat(node.getParentTag(), node.getTag());
				
				if (model.containsFeature(prevTagFeat))
					instance.addFeature(prevTagFeat, 1);

				if (model.containsFeature(prevTwoTagsFeat))
					instance.addFeature(prevTwoTagsFeat, 1);

				System.out.println(instance.getName() + " " + prevTagFeat + " " + prevTwoTagsFeat + " " + node.getPathProb());
							
				Map<String, Double> topTags = getTopTags(instance);
				this.setNodes(node, topTags);
				
				instance.removeFeature(prevTwoTagsFeat);
				instance.removeFeature(prevTagFeat);
			}
			
			this.pruneNodes();
		}
	}
	
	private void pruneNodes() {
		List<BeamSearchNode> leaves =  this.rootNode.getLeaves();
		
		Map<BeamSearchNode, Double> topNodes = new HashMap<BeamSearchNode, Double>();
		
		for (BeamSearchNode node : leaves) {
			topNodes.put(node, node.getPathProb());
		}
		
		topNodes = this.sortByValueDesc(topNodes, this.topK);
		
		double maxLgProb = Math.log10(Collections.max(topNodes.values()));
		
		Set<BeamSearchNode> toRemove = new HashSet<BeamSearchNode>();
		
		for (BeamSearchNode node : topNodes.keySet()) {
			double nodeLgProb = Math.log10(node.getPathProb()); 
			
			if (nodeLgProb + this.beamSize < maxLgProb) {
				toRemove.add(node);
			} else if (nodeLgProb == maxLgProb) {
				this.bestNode = node;
			}
		}
		
		for (BeamSearchNode node : toRemove) {
			topNodes.remove(node);
		}
		
		leaves.removeAll(topNodes.keySet());
		
		for (BeamSearchNode node : leaves) {
			BeamSearchNode parent = node.getParent();
			
			parent.removeChild(node.getTag());
			
			boolean isLeaf = parent.isLeaf();
			
			while (isLeaf && parent.getDepth() != 0) {
				String tag = parent.getTag();
				parent = parent.getParent();
				parent.removeChild(tag);
				isLeaf = parent.isLeaf();
			}
		}
	}
	
	private String getPrevTagFeat(String prevTag) {
		return "prevT=" + prevTag;
	}
	
	private String getPrevTwoTagsFeat(String prevTag2, String prevTag) {
		return "prevTwoTags=" + prevTag2 + "+" + prevTag;
	}
	
	private Map<String, Double> getTopTags(Instance instance) {
		return sortByValueDesc(model.scoreInstanceJava(instance), this.topN);
	}
	
	private void setNodes(BeamSearchNode node, Map<String, Double> newTags) {
		for (Map.Entry<String, Double> entry : newTags.entrySet()) {
			node.createChild(entry.getKey(), entry.getValue());
		}
	}

	public <T> Map<T,Double> sortByValueDesc(Map<T,Double> map, Integer n) {
		List<Entry<T,Double>> list = new LinkedList<Entry<T, Double>>(map.entrySet());
		
		Collections.sort(list, new Comparator<Entry<T,Double>>(){
			public int compare(Entry<T,Double> x, Entry<T,Double> y) {
				int c;
				//	return negative (to reverse)
				c = -(x.getValue()).compareTo(y.getValue());
				if (c==0)
					c=(x.getKey().toString()).compareTo(y.getKey().toString());
				
				return c;
			}
		});
			
		Map<T,Double> sortedTypes = new LinkedHashMap<T,Double>();
				
		int j=0;
		
		for(Iterator<Entry<T, Double>> i = list.iterator(); i.hasNext();) {		
			if (n != null && j >= n) 
				break;
			
			Entry<T, Double> entry = i.next();
			sortedTypes.put(entry.getKey(), entry.getValue());
			j++;
		}
		
		return sortedTypes;
	}

}