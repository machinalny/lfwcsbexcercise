/* Lukasz Lopusinski (machinalny) ©2024  */
package com.machinalny.lfwcsb.storage;

import com.machinalny.lfwcsb.model.Match;
import java.util.List;

public interface MatchScoreBoardStorage {

  void addMatch(Match match);

  List<Match> getActiveMatches();

  void removeMatch(Match match);
}
