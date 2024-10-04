/* Lukasz Lopusinski (machinalny) ©2024  */
package com.machinalny.lfwcsb.model;

import java.time.LocalDateTime;

public record Match(
    String homeTeam, String awayTeam, int homeScore, int awayScore, LocalDateTime startOfMatch) {

  public Match(String homeTeam, String awayTeam, int homeScore, int awayScore) {
    this(homeTeam, awayTeam, homeScore, awayScore, LocalDateTime.now());
  }

  @Override
  public String toString() {
    return String.format("%s %d - %s %d", homeTeam, homeScore, awayTeam, awayScore);
  }

  public int getTotalScore() {
    return homeScore + awayScore;
  }
}
