/* Lukasz Lopusinski (machinalny) ©2024  */
package com.machinalny.lfwcsb.storage;

import com.machinalny.lfwcsb.model.Match;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchScoreBoardInMemoryStorage implements MatchScoreBoardStorage {

  private Map<Integer, Match> activeMatches;

  public void initialize() {
    this.activeMatches = new HashMap<>();
  }

  private Integer calculateHashOfHomeAndAwayTeamNames(String homeTeamName, String awayTeamName) {
    String homeAndAwayTeamNames = homeTeamName + awayTeamName;
    return homeAndAwayTeamNames.hashCode();
  }

  private Integer calculateHashOfMatch(Match match) {
    return calculateHashOfHomeAndAwayTeamNames(match.homeTeam(), match.awayTeam());
  }

  @Override
  public void upsertMatch(Match match) {
    activeMatches.put(calculateHashOfMatch(match), match);
  }

  @Override
  public Match getMatch(String homeTeam, String awayTeam) {
    Integer hashOfMatch = calculateHashOfHomeAndAwayTeamNames(homeTeam, awayTeam);
    return activeMatches.getOrDefault(hashOfMatch, null);
  }

  @Override
  public Match getMatchByTeam(String teamName) {
    return this.activeMatches.values().stream()
        .filter(match -> match.homeTeam().equals(teamName) || match.awayTeam().equals(teamName))
        .findFirst()
        .orElse(null);
  }

  @Override
  public List<Match> getActiveMatches() {
    return activeMatches.values().stream().toList();
  }

  @Override
  public void removeMatch(Match match) {}
}
