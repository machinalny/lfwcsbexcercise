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

  private Integer calculateHashOfHomeAndAwayTeamNames(Match match) {
    String homeAndAwayTeamNames = match.homeTeam() + match.awayTeam();
    return homeAndAwayTeamNames.hashCode();
  }

  @Override
  public void addMatch(Match match) {
    if (activeMatches == null) {
      initialize();
    }

    activeMatches.put(calculateHashOfHomeAndAwayTeamNames(match), match);
  }

  @Override
  public List<Match> getActiveMatches() {
    return activeMatches.values().stream().toList();
  }

  @Override
  public void removeMatch(Match match) {}
}
