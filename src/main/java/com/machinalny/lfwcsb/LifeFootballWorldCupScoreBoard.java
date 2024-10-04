/* Lukasz Lopusinski (machinalny) ©2024  */
package com.machinalny.lfwcsb;

import com.machinalny.lfwcsb.exceptions.NotStartedMatchException;
import com.machinalny.lfwcsb.exceptions.TeamNameException;
import com.machinalny.lfwcsb.model.Match;
import com.machinalny.lfwcsb.storage.MatchScoreBoardStorage;
import java.util.List;
import java.util.Objects;

public class LifeFootballWorldCupScoreBoard {

  private MatchScoreBoardStorage matchScoreBoardStorage;

  public LifeFootballWorldCupScoreBoard(MatchScoreBoardStorage matchScoreBoardStorage) {
    this.matchScoreBoardStorage = matchScoreBoardStorage;
    this.matchScoreBoardStorage.initialize();
  }

  public void startMatch(String homeTeam, String awayTeam) {
    if (Objects.equals(homeTeam, awayTeam)) {
      throw new TeamNameException("One team cannot play with itself on World Cup");
    }
    Match newMatch = new Match(homeTeam, awayTeam, 0, 0);
    this.matchScoreBoardStorage.upsertMatch(newMatch);
  }

  public void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {
    Match match = this.matchScoreBoardStorage.getMatch(homeTeam, awayTeam);
    if (match == null) {
      throw new NotStartedMatchException("Can't update score of not updated match");
    }
    Match updatedMatch = new Match(homeTeam, awayTeam, homeScore, awayScore);
    this.matchScoreBoardStorage.upsertMatch(updatedMatch);
  }

  public String getSummaryOfMatchesInProgress() {
    List<Match> activeMatches = this.matchScoreBoardStorage.getActiveMatches();
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < activeMatches.size(); i++) {
      Match match = activeMatches.get(i);
      stringBuilder.append(String.format("%d.%s", i + 1, match.toString()));
    }
    return stringBuilder.toString();
  }
}
