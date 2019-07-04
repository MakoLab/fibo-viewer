package org.edmcouncil.spec.fibo.weasel.comparator;

import java.util.Comparator;
import java.util.List;

/**
 * Create by Michał Daniel (michal.daniel@makolab.com)
 */
public class WeaselComparators {

 public static  Comparator<String> getComparatorWithPriority(List<String> prioritySortList) {
    return (String obj1, String obj2) -> {

      if (obj1 == obj2) {
        return 0;
      }
      if (obj1 == null) {
        return -1;
      }
      if (obj2 == null) {
        return 1;
      }
      if (prioritySortList.contains(obj1) && !prioritySortList.contains(obj2)) {
        return -1;
      }
      if (!prioritySortList.contains(obj1) && prioritySortList.contains(obj2)) {
        return 1;
      }
      if (prioritySortList.contains(obj1) && prioritySortList.contains(obj2)) {
        int idxObj1 = prioritySortList.indexOf(obj1);
        int idxObj2 = prioritySortList.indexOf(obj2);

        return idxObj1 < idxObj2 ? -1 : 1;
      }

      return obj1.toLowerCase().compareTo(obj2.toLowerCase());

    };
  }
  
}
