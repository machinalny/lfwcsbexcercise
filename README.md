# Live Football World Cup Score Board


## Task requirements:
You are working in a sports data company, and we would like you to develop a new Live Football
World Cup Scoreboard library that shows all the ongoing matches and their scores.

### The scoreboard supports the following operations:
1. Start a new match, assuming initial score 0 – 0 and adding it the scoreboard.
This should capture following parameters:
a. Home team
b. Away team
2. Update score. This should receive a pair of absolute scores: home team score and away
team score.
3. Finish match currently in progress. This removes a match from the scoreboard.
4. Get a summary of matches in progress ordered by their total score. The matches with the
same total score will be returned ordered by the most recently started match in the
scoreboard.

### For example, if following matches are started in the specified order and their scores respectively updated:
1. Mexico 0 - Canada 5
2. Spain 10 - Brazil 2
3. Germany 2 - France 2
4. Uruguay 6 - Italy 6
5. Argentina 3 - Australia 1

### The summary should be as follows:

1. Uruguay 6 - Italy 6
2. Spain 10 - Brazil 2
3. Mexico 0 - Canada 5
4. Argentina 3 - Australia 1
5. Germany 2 - France 2

## NOTES
- Libary should have an interface exposed with:
- startMatch(homeTeam, awayTeam)
- updateScore(homeTeam, awayTeam, homeTeamScore, awayTeamScore)
- finishMatch(homeTeam, awayTeam)
- getSummaryOfMatchesInProgress()

### Assumptions
1. Team can have only one match at a time. Team-A won't start next match until match in progress is running, we can validate startMatch method for that.
2. Team name validation is needed, Urugway and urugway -> Are the same team. Team A and team-a are the same team. We will present teams as Urugway and Team A in summary. Any other combination will be treated as separated team.
2. Match will always be started and then updated. Validation needed for updateScore/finishMatch -> If no active match for Team-A then no update or finish is possible.
3. Match can finish without an update -> No score change, no need for update.
4. No VAR, no score deduction -> If match is updated with score then the score cannot be lower in next update -> Team A 1-0 Team B, next update cannot be Team A 0-0 Team B
5. Score cannot be negative.
6. Home and away team has to be different.
