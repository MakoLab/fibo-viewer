package org.edmcouncil.spec.fibo.weasel.model.impl;

import org.edmcouncil.spec.fibo.weasel.model.Pair;

/**
 *
 * Create by Michał Daniel (michal.daniel@makolab.com)
 *
 * @param <A>
 * @param <B>
 */
public class PairImpl<A, B> implements Pair<A, B> {

  private A valA;
  private B valB;

  @Override
  public A getValueA() {
    return this.valA;
  }

  @Override
  public B getValueB() {
    return this.valB;
  }

  public void setValueA(A valA) {
    this.valA = valA;
  }

  public void setValueB(B valB) {
    this.valB = valB;
  }

  @Override
  public String toString() {
    return "Pair<" + valA + ", " + valB + '>';
  }

}
