/* Lukasz Lopusinski (machinalny) ©2024  */
package com.machinalny.lfwcsb;

import static com.machinalny.lfwcsb.LifeFootballWorldCupScoreBoard.startMatch;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.machinalny.lfwcsb.exceptions.TeamNameException;
import org.junit.jupiter.api.Test;

class LifeFootballWorldCupScoreBoardTest {

  @Test
  void teamCannotPlayWithItselfOnWorldCup() {
    assertThrows(
        TeamNameException.class,
        () -> startMatch("Urugway", "Urugway"),
        "One team cannot play with itself on World Cup");
  }
}
