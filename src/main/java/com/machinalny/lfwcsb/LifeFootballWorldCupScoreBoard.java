/* Lukasz Lopusinski (machinalny) ©2024  */
package com.machinalny.lfwcsb;

import com.machinalny.lfwcsb.exceptions.NotStartedMatchException;
import com.machinalny.lfwcsb.exceptions.ScoreCantBeDeductedOrNegative;
import com.machinalny.lfwcsb.exceptions.TeamCantPlayTwoMatchesAtTheSameTimeException;
import com.machinalny.lfwcsb.exceptions.TeamNameException;
import com.machinalny.lfwcsb.model.Match;
import com.machinalny.lfwcsb.storage.MatchScoreBoardStorage;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class LifeFootballWorldCupScoreBoard {

  private MatchScoreBoardStorage matchScoreBoardStorage;

  public LifeFootballWorldCupScoreBoard(MatchScoreBoardStorage matchScoreBoardStorage) {
    this.matchScoreBoardStorage = matchScoreBoardStorage;
    this.matchScoreBoardStorage.initialize();
  }

  private String sanitizeTeamName(String teamName) {
    return Arrays.stream(teamName.strip().replace("-", " ").split("\\s+"))
        .map(StringUtils::capitalize)
        .collect(Collectors.joining(" "));
  }

  public void startMatch(String homeTeam, String awayTeam) {
    String sanitizedHomeTeam = sanitizeTeamName(homeTeam);
    String sanitizedAwayTeam = sanitizeTeamName(awayTeam);
    if (Objects.equals(sanitizedHomeTeam, sanitizedAwayTeam)) {
      throw new TeamNameException("One team cannot play with itself on World Cup");
    }
    Match homeTeamMatch = this.matchScoreBoardStorage.getMatchByTeam(sanitizedHomeTeam);
    Match awayTeamMath = this.matchScoreBoardStorage.getMatchByTeam(sanitizedAwayTeam);
    if (homeTeamMatch != null || awayTeamMath != null) {
      throw new TeamCantPlayTwoMatchesAtTheSameTimeException(
          String.format(
              "Team %s can't play two matches at the same time",
              homeTeamMatch != null ? homeTeamMatch : awayTeamMath));
    }
    Match newMatch = new Match(sanitizedHomeTeam, sanitizedAwayTeam, 0, 0);
    this.matchScoreBoardStorage.upsertMatch(newMatch);
  }

  public void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {
    Match match = this.matchScoreBoardStorage.getMatch(homeTeam, awayTeam);
    if (match == null) {
      throw new NotStartedMatchException("Can't update score of not updated match");
    }
    if (homeScore < match.homeScore()
        || awayScore < match.awayScore()
        || homeScore < 0
        || awayScore < 0) {
      throw new ScoreCantBeDeductedOrNegative("Score can't be deducted or negative");
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
