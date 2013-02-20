package ling572.util;

import java.util.*;

public class Instance {

    private String label;
    private Map<String,Integer> features;

    public Instance(String label) {
        this.label = label;
        this.features = new HashMap<String,Integer>();
    }

    public void addFeature(String feature, int value) {
        this.features.put(feature, value);
    }

    public String getLabel() {
        return this.label;
    }

    public boolean hasFeature(String feature) {
        return this.features.containsKey(feature);
    }

    public Integer getFeatureValue(String feature) {
        return this.features.get(feature);
    }

    public Integer getFeatureValueOrDefault(String feature, int val) {
        if (this.hasFeature(feature))
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