import java.util.UUID;

public class Player {
    private final String playerId;
    private long balance = 0;

    public Player(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public long getBalance() {
        return balance;
    }

    public void addBalance(int amount) {
        this.balance += amount;
    }


}
