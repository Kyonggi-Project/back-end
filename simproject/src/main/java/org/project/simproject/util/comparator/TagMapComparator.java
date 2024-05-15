package org.project.simproject.util.comparator;

import java.util.*;

public class TagMapComparator {

    public static List<String> comparator(HashMap<String, Integer> tagMap){
        List<Map.Entry<String, Integer>> list = new LinkedList<>(tagMap.entrySet());

        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        List<String> tag = new ArrayList<>();

        for(Map.Entry<String, Integer> entry : list){
            tag.add(entry.getKey());
        }

        return tag;
    }
}
