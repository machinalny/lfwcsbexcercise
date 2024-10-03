/* Lukasz Lopusinski (machinalny) ©2024  */
package com.machinalny.lfwcsb.model;

public record Match(String homeTeam, String awayTeam, int homeScore, int awayScore) {

  @Override
  public String toString() {
    return String.format("%s %d - %s %d", homeTeam, homeScore, awayTeam, awayScore);
  }
}
