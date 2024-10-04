/* Lukasz Lopusinski (machinalny) ©2024  */
package com.machinalny.lfwcsb;

import static org.junit.jupiter.api.Assertions.*;

import com.machinalny.lfwcsb.exceptions.NotStartedMatchException;
import com.machinalny.lfwcsb.exceptions.ScoreCantBeDeductedOrNegative;
import com.machinalny.lfwcsb.exceptions.TeamCantPlayTwoMatchesAtTheSameTimeException;
import com.machinalny.lfwcsb.exceptions.TeamNameException;
import com.machinalny.lfwcsb.storage.MatchScoreBoardInMemoryStorage;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

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
        String.format(
            """
                        1.%s 2 - Panama 0
                        """,
            originalName);
    assertEquals(expectedSummary.strip(), scoreBoard.getSummaryOfMatchesInProgress().strip());
  }

  @Test
  void finishingMatchShouldRemoveItFromScoreBoard() {
    scoreBoard.startMatch("Uruguay", "Panama");
    var scoreBoardBeforeFinishing = scoreBoard.getSummaryOfMatchesInProgress().strip();
    scoreBoard.finishMatch("Uruguay", "Panama");
    assertNotEquals(scoreBoardBeforeFinishing, scoreBoard.getSummaryOfMatchesInProgress().strip());
  }

  @Test
  void cantFinishNotStartedMatch() {
    assertThrows(NotStartedMatchException.class, () -> scoreBoard.finishMatch("Uruguay", "Panama"));
  }

  @Test
  void summaryShouldBeOrderedByTotalScoreOfMatches() {
    scoreBoard.startMatch("Uruguay", "Panama");
    scoreBoard.startMatch("Brazil", "Germany");
    scoreBoard.startMatch("Poland", "Netherlands");

    scoreBoard.updateScore("Uruguay", "Panama", 2, 0);
    scoreBoard.updateScore("Brazil", "Germany", 0, 8);
    scoreBoard.updateScore("Poland", "Netherlands", 1, 3);

    var expectedSummary =
        """
                        1.Brazil 0 - Germany 8
                        2.Poland 1 - Netherlands 3
                        3.Uruguay 2 - Panama 0
                        """;
    assertEquals(expectedSummary.strip(), scoreBoard.getSummaryOfMatchesInProgress().strip());
  }

  @Test
  void summaryShouldBeOrderedByTotalScoreOfMatchesAndByMatchTimeStart() {
    var stubbedLocalDateTime1stMatch = LocalDateTime.of(2020, 01, 01, 22, 22, 22);
    var stubbedLocalDateTime2ndMatch = LocalDateTime.of(2020, 01, 01, 22, 22, 23);
    var stubbedLocalDateTime3rdMatch = LocalDateTime.of(2020, 01, 01, 22, 22, 24);
    var stubbedLocalDateTime4thMatch = LocalDateTime.of(2020, 01, 01, 22, 22, 25);
    var stubbedLocalDateTime5thMatch = LocalDateTime.of(2020, 01, 01, 22, 22, 26);

    try (MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class)) {
      mockedStatic
          .when(LocalDateTime::now)
          .thenReturn(
              stubbedLocalDateTime1stMatch,
              stubbedLocalDateTime2ndMatch,
              stubbedLocalDateTime3rdMatch,
              stubbedLocalDateTime4thMatch,
              stubbedLocalDateTime5thMatch);
      scoreBoard.startMatch("Mexico", "Canada");
      scoreBoard.startMatch("Spain", "Brazil");
      scoreBoard.startMatch("Germany", "France");
      scoreBoard.startMatch("Uruguay", "Italy");
      scoreBoard.startMatch("Argentina", "Australia");

      scoreBoard.updateScore("Mexico", "Canada", 0, 5);
      scoreBoard.updateScore("Spain", "Brazil", 10, 2);
      scoreBoard.updateScore("Germany", "France", 2, 2);
      scoreBoard.updateScore("Uruguay", "Italy", 6, 6);
      scoreBoard.updateScore("Argentina", "Australia", 3, 1);

      var expectedSummary =
          """
                            1.Uruguay 6 - Italy 6
                            2.Spain 10 - Brazil 2
                            3.Mexico 0 - Canada 5
                            4.Argentina 3 - Australia 1
                            5.Germany 2 - France 2
                            """;
      assertEquals(expectedSummary.strip(), scoreBoard.getSummaryOfMatchesInProgress().strip());
    }
  }
}
