package ling572.util;

import java.io.*;
import java.util.*;

public class VectorFileReader {
    public static List<Instance> indexInstances(File dataFile) {
        List<Instance> instances = new ArrayList<Instance>();

        // line formatted as: name label feature1 value1 feature2 value2 ..."
        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split("\\s+");

                Instance instance = new Instance();
                instance.setName(splitLine[0]);
                instance.setLabel(splitLine[1]);
                
                for (int i = 2; i < splitLine.length; i=i+2) {                    
                    String feature = splitLine[i];
                    int value = Integer.parseInt(splitLine[i+1]);
                    instance.addFeature(feature, value);
                }

                instances.add(instance);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return instances;
    }
}