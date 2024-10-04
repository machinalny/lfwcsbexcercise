/* Lukasz Lopusinski (machinalny) ©2024  */
package com.machinalny.lfwcsb;

import static org.junit.jupiter.api.Assertions.*;

import com.machinalny.lfwcsb.exceptions.NotStartedMatchException;
import com.machinalny.lfwcsb.exceptions.ScoreCantBeDeductedOrNegative;
import com.machinalny.lfwcsb.exceptions.TeamCantPlayTwoMatchesAtTheSameTimeException;
import com.machinalny.lfwcsb.exceptions.TeamNameException;
import com.machinalny.lfwcsb.storage.MatchScoreBoardInMemoryStorage;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class LifeFootballWorldCupScoreBoardTest {

  LifeFootballWorldCupScoreBoard scoreBoard;

  private static Stream<Arguments> providePairsWithNegativeScores() {
    return Stream.of(
        Arguments.of(1, -1),
        Arguments.of(-1, 0),
        Arguments.of(Integer.MAX_VALUE, Integer.MIN_VALUE));
  }

  private static Stream<Arguments> provideTeamNamesIdentifiedAsTheSameTeam() {
    return Stream.of(
        Arguments.of("Uruguay", "uruguay"),
        Arguments.of("United States Of America", "United-States of America"),
        Arguments.of("United States Of America", "United-States-of-America"),
        Arguments.of("United States Of America", " united States-of-america"),
        Arguments.of("United States Of America", "United States-of-america "));
  }

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

  @Test
  void scoreCannotBeDeductedAsVarIsNotYetSupported() {
    scoreBoard.startMatch("Uruguay", "Panama");
    scoreBoard.updateScore("Uruguay", "Panama", 2, 0);
    assertThrows(
        ScoreCantBeDeductedOrNegative.class,
        () -> scoreBoard.updateScore("Uruguay", "Panama", 1, 0));
  }

  @ParameterizedTest
  @MethodSource("providePairsWithNegativeScores")
  void scoreCannotBeNegative(int homeScore, int awayScore) {
    scoreBoard.startMatch("Uruguay", "Panama");
    scoreBoard.updateScore("Uruguay", "Panama", 2, 0);
    assertThrows(
        ScoreCantBeDeductedOrNegative.class,
        () -> scoreBoard.updateScore("Uruguay", "Panama", homeScore, awayScore));
  }

  @Test
  void teamShouldOnlyBeAllowedToPlayOneMatchAtATime() {
    scoreBoard.startMatch("Uruguay", "Panama");
    assertThrows(
        TeamCantPlayTwoMatchesAtTheSameTimeException.class,
        () -> scoreBoard.startMatch("Uruguay", "Panama"));
    assertThrows(
        TeamCantPlayTwoMatchesAtTheSameTimeException.class,
        () -> scoreBoard.startMatch("Brazil", "Panama"));
    assertThrows(
        TeamCantPlayTwoMatchesAtTheSameTimeException.class,
        () -> scoreBoard.startMatch("Uruguay", "Brazil"));
  }

  @ParameterizedTest
  @MethodSource("provideTeamNamesIdentifiedAsTheSameTeam")
  void validTeamNamesShouldIdentifyAsExistingTeam(String originalName, String alternativeName) {
    scoreBoard.startMatch(originalName, "Panama");
    assertThrows(
        TeamCantPlayTwoMatchesAtTheSameTimeException.class,
        () -> scoreBoard.startMatch(alternativeName, "Brazil"));
  }

  @ParameterizedTest
  @MethodSource("provideTeamNamesIdentifiedAsTheSameTeam")
  void validTeamNamesShouldIdentifyAsExistingTeamForUpdate(
      String originalName, String alternativeName) {
    scoreBoard.startMatch(originalName, "Panama");
    scoreBoard.updateScore(alternativeName, "Panama", 2, 0);
    var expectedSummary =
        String.format("""
                1.%s 2 - Panama 0
                """, originalName);
    assertEquals(expectedSummary.strip(), scoreBoard.getSummaryOfMatchesInProgress().strip());
  }

  @Test
  void finishingMatchShouldRemoveItFromScoreBoard() {
    scoreBoard.startMatch("Uruguay", "Panama");
    var scoreBoardBeforeFinishing = scoreBoard.getSummaryOfMatchesInProgress().strip();
    scoreBoard.finishMatch("Uruguay", "Panama");
    assertNotEquals(scoreBoardBeforeFinishing, scoreBoard.getSummaryOfMatchesInProgress().strip());
  }
}
