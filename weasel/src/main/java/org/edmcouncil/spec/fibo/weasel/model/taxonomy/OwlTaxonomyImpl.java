package org.edmcouncil.spec.fibo.weasel.model.taxonomy;

import java.util.LinkedList;
import java.util.List;
import org.edmcouncil.spec.fibo.weasel.model.OwlTaxonomy;
import org.edmcouncil.spec.fibo.weasel.model.Pair;

/**
 *
 * Create by Michał Daniel (michal.daniel@makolab.com)
 */
public class OwlTaxonomyImpl implements OwlTaxonomy<OwlTaxonomyElementImpl> {

  private List<List<OwlTaxonomyElementImpl>> value;

  public OwlTaxonomyImpl() {
    value = new LinkedList<>();
  }

  @Override
  public List<List<OwlTaxonomyElementImpl>> getValue() {
    return value;
  }

  public void setValue(List<List<OwlTaxonomyElementImpl>> value) {
    this.value = value;
  }

  public void addTaxonomy(List<OwlTaxonomyElementImpl> tax) {
    this.value.add(tax);
  }

  public void addTaxonomy(OwlTaxonomyImpl subCLassTax, OwlTaxonomyElementImpl taxEl) {

    for (List<OwlTaxonomyElementImpl> list : subCLassTax.getValue()) {
      list.add(taxEl);
      //list.add(0,taxEl);
      value.add(list);
    }

  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Taxonomy: \n");
    for (List<OwlTaxonomyElementImpl> list : value) {
      sb.append("\t");
      for (OwlTaxonomyElementImpl el : list) {
        sb.append(el.getValueA().getValue()).append(" > ");
      }
      sb.append("\n");
    }

    return sb.toString();
  }

}
