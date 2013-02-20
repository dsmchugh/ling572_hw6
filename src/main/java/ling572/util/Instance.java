package ling572.util;

import java.util.*;

public class Instance {

	private String name;
    private String label;
    private Map<String,Integer> features;

    public Instance() {
        this.features = new HashMap<String,Integer>();
    }

    public void addFeature(String feature, int value) {
        this.features.put(feature, value);
    }

    public String getLabel() {
        return this.label;
    }
    
    public void setLabel(String label) {
    	this.label = label;
    }
    
    public String getName() {
    	return this.name;
    }
    
    public int getSize() {
    	return this.features.size();
    }
    
    public void setName(String name) {
    	this.name = name;
    }

    public boolean containsFeature(String feature) {
        return this.features.containsKey(feature);
    }

    public Integer getFeatureValue(String feature) {
        return this.features.get(feature);
    }

    public Integer getFeatureValueOrDefault(String feature, int val) {
        if (this.containsFeature(feature))
            return this.features.get(feature);
        else
            return val;
    }

    public Map<String,Integer> getFeatures() {
        return this.features;
    }

    public void removeFeature(String feature) {
        this.features.remove(feature);
    }
}