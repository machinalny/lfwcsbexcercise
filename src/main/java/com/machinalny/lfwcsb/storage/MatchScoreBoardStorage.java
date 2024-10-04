/* Lukasz Lopusinski (machinalny) ©2024  */
package com.machinalny.lfwcsb.storage;

import com.machinalny.lfwcsb.model.Match;
import java.util.List;

public interface MatchScoreBoardStorage {

  void initialize();

  void upsertMatch(Match match);

  Match getMatch(String homeTeam, String awayTeam);

  Match getMatchByTeam(String teamName);

  List<Match> getActiveMatches();

  void removeMatch(Match match);
}
