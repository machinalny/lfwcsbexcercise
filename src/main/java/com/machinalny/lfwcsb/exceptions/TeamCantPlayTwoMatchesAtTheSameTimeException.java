/* Lukasz Lopusinski (machinalny) ©2024  */
package com.machinalny.lfwcsb.exceptions;

public class TeamCantPlayTwoMatchesAtTheSameTimeException extends RuntimeException {
  public TeamCantPlayTwoMatchesAtTheSameTimeException(String message) {
    super(message);
  }
}
