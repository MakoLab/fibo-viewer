package org.edmcouncil.spec.fibo.view.model;

import java.util.LinkedHashMap;
import java.util.List;
import org.apache.solr.common.SolrDocument;

public class MapBean {

  private LinkedHashMap<String, List<SolrDocument>> map;

  public LinkedHashMap<String, List<SolrDocument>> getMap() {
    return map;
  }

  public void setMap(LinkedHashMap<String, List<SolrDocument>> map) {
    this.map = map;
  }

}
