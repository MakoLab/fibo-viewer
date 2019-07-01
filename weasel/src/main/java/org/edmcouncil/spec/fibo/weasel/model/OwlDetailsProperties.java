package org.edmcouncil.spec.fibo.weasel.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.edmcouncil.spec.fibo.weasel.comparator.WeaselComparators;

/**
 * Create by Michał Daniel (michal.daniel@makolab.com)
 */
public class OwlDetailsProperties {

  private Map<String, List<String>> properties;

  public OwlDetailsProperties() {
    properties = new HashMap<>();
  }
  

  public void addProperty(String key, String property) {
    if (this.properties == null) {
      this.properties = new HashMap<>();
    }

    List<String> propertiesList = properties.get(key);
    if (propertiesList == null) {
      propertiesList = new LinkedList<>();
    }

    propertiesList.add(property);
    properties.put(key, propertiesList);
  }

  public Map<String, List<String>> getProperties() {
    return properties;
  }

  public void sort(List<String> priotityList){
    Comparator<String> comparator = WeaselComparators.getComparatorWithPriority(priotityList);
    SortedSet<String> keys = new TreeSet<>(comparator);
    keys.addAll(properties.keySet());

    Map<String, List<String>> result = new LinkedHashMap<>();
    keys.forEach((key) -> {
      result.put(key, properties.get(key));
    });
    properties = result;
  }
  
}
