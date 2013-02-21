package ling572;

import java.util.*;

public class BeamSearchNode {
	private static final String NULL_TAG = "BOS";
	
	private BeamSearchNode parent = null;
	private Map<String, BeamSearchNode> children = new HashMap<String, BeamSearchNode>();
	private double nodeProb = 1;
	private double pathProb = 1;
	private int depth = 0;
	private String tag = NULL_TAG;
	
	public int getDepth() {
		return this.depth;
	}
	
	public void createChild(String tag, double prob) {
		BeamSearchNode newNode = new BeamSearchNode();
		newNode.depth = this.getDepth() + 1;
		newNode.nodeProb = prob;
		newNode.pathProb = this.pathProb * prob;
		newNode.parent = this;
		newNode.setTag(tag);
		children.put(tag, newNode);
	}

	public List<BeamSearchNode> getLeaves() {
		List<BeamSearchNode> leaves = new ArrayList<BeamSearchNode>();
		
		if (this.isLeaf()) {
			leaves = new ArrayList<BeamSearchNode>();
			leaves.add(this);
		} else {
			for (BeamSearchNode child : this.children.values()) {
				leaves.addAll(child.getLeaves());
			}
		}
			
		return leaves;
	}

	public boolean isLeaf() {
		return this.children.isEmpty();
	}
	
	public String getTag(){
		return this.tag;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public BeamSearchNode getParent() {
		return this.parent;
	}
	
	public void removeChild(String tag) {
		this.children.remove(tag);
	}
	
	public String getParentTag() {
		return this.getParent() == null ? NULL_TAG : this.getParent().getTag();		
	}

	public double getPathProb() {
		return this.pathProb;
	}
	
	public double getNodeProb() {
		return this.nodeProb;
	}
	
	@Override
	public String toString() {
		return this.getTag();
	}
}
