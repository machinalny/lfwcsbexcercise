/* Lukasz Lopusinski (machinalny) ©2024  */
package com.machinalny.lfwcsb;

import com.machinalny.lfwcsb.exceptions.TeamNameException;
import java.util.Objects;

public class LifeFootballWorldCupScoreBoard {

  public static void startMatch(String homeTeam, String awayTeam) {
    if (Objects.equals(homeTeam, awayTeam)) {
      throw new TeamNameException("One team cannot play with itself on World Cup");
    }
  }
}
