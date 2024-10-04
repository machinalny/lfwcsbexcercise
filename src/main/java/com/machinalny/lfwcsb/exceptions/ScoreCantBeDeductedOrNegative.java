/* Lukasz Lopusinski (machinalny) ©2024  */
package com.machinalny.lfwcsb.exceptions;

public class ScoreCantBeDeductedOrNegative extends RuntimeException {
  public ScoreCantBeDeductedOrNegative(String message) {
    super(message);
  }
}
