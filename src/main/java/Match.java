import java.util.UUID;

public class Match {

    private final UUID matchId;
    private final Character winner;
    private final Double winnerReturnRate;

    public Match(UUID matchId, Character winner, Double returnRateA, Double returnRateB) {
        this.matchId = matchId;
        this.winner = winner;
        if (winner == 'A') {
            winnerReturnRate = returnRateA;
        } else {
            winnerReturnRate = returnRateB;
        }
    }

    public UUID getMatchId() {
        return matchId;
    }

    public Character getWinner() {
        return winner;
    }

    public Double getWinnerReturnRate() {
        return winnerReturnRate;
    }

}
