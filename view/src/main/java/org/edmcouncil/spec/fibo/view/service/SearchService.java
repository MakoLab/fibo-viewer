package org.edmcouncil.spec.fibo.view.service;

import java.util.LinkedList;
import org.edmcouncil.spec.fibo.weasel.model.OwlDetails;
import org.edmcouncil.spec.fibo.view.util.ModelBuilder;
import org.edmcouncil.spec.fibo.weasel.ontology.WeaselOntologyManager;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Create by Michał Daniel (michal.daniel@makolab.com)
 */
@Service
public class SearchService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);

  @Autowired
  private WeaselOntologyManager ets;

  public void search(String query, ModelBuilder mb) {
    List<OwlDetails> weaselTerms;
    OwlDetails wd = ets.getDetailsByIri(query);
    weaselTerms = new LinkedList<>();
    weaselTerms.add(wd);
    mb.setQuery(query)
        .ontoDetails(weaselTerms);
  }

}
