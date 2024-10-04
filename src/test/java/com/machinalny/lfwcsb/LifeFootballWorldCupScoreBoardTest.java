/* Lukasz Lopusinski (machinalny) ©2024  */
package com.machinalny.lfwcsb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.machinalny.lfwcsb.exceptions.NotStartedMatchException;
import com.machinalny.lfwcsb.exceptions.TeamNameException;
import com.machinalny.lfwcsb.storage.MatchScoreBoardInMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LifeFootballWorldCupScoreBoardTest {

  LifeFootballWorldCupScoreBoard scoreBoard;

  @BeforeEach
  void setUp() {
    scoreBoard = new LifeFootballWorldCupScoreBoard(new MatchScoreBoardInMemoryStorage());
  }

  @Test
  void teamCannotPlayWithItselfOnWorldCup() {
    assertThrows(
        TeamNameException.class,
        () -> scoreBoard.startMatch("Uruguay", "Uruguay"),
        "One team cannot play with itself on World Cup");
  }

  @Test
  void startedMatchIsPresentInSummary() {
    scoreBoard.startMatch("Uruguay", "Panama");
    var expectedSummary = """
                1.Uruguay 0 - Panama 0
                """;
    assertEquals(expectedSummary.strip(), scoreBoard.getSummaryOfMatchesInProgress().strip());
  }

  @Test
  void scoreUpdateIsReflectedInSummary() {
    scoreBoard.startMatch("Uruguay", "Panama");
    scoreBoard.updateScore("Uruguay", "Panama", 1, 0);
    var expectedSummary = """
                1.Uruguay 1 - Panama 0
                """;
    assertEquals(expectedSummary.strip(), scoreBoard.getSummaryOfMatchesInProgress().strip());
  }

  @Test
  void scoreCannotBeUpdatedForNotStartedMatch() {
    assertThrows(
        NotStartedMatchException.class, () -> scoreBoard.updateScore("Uruguay", "Panama", 2, 0));
  }
}
