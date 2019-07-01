package org.edmcouncil.spec.fibo.view.util;

import org.edmcouncil.spec.fibo.view.model.Query;
import org.edmcouncil.spec.fibo.weasel.model.OwlDetails;
import java.util.List;

import org.springframework.ui.Model;

public class ModelBuilder {

  private final Model model;

  public ModelBuilder(Model builderModel) {
    this.model = builderModel;
  }

  public Model getModel() {
    return model;
  }

  public ModelBuilder setQuery(String query) {
    if (query == null) {
      model.addAttribute("query", new Query());
    } else {
      Query q = new Query();
      q.setValue(query);
      model.addAttribute("query", q);
    }
    return this;
  }

  public ModelBuilder emptyQuery() {
    model.addAttribute("query", new Query());
    return this;
  }

  public ModelBuilder ontoDetails(List<OwlDetails> details) {

    model.addAttribute("tree_si_list", details);
    model.addAttribute("tree_si_display", true);
    model.addAttribute("cl_display", true);

    return this;
  }

}
