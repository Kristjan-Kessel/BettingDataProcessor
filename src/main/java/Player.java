import java.math.BigDecimal;
import java.util.UUID;

public class Player {
    private final UUID playerId;
    private long balance = 0;
    private int betCount = 0;
    private int wins = 0;
    private long totalWinnings = 0;

    private String illegalOperation = "";

    public Player(UUID playerId) {
        this.playerId = playerId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public long getBalance() {
        return balance;
    }

    public void addBalance(int amount) {
        this.balance += amount;
    }

    public BigDecimal getWinrate(){
        if(betCount == 0){
            return BigDecimal.valueOf(0,2);
        }
        return BigDecimal.valueOf(wins)
                .divide(BigDecimal.valueOf(betCount), 2, BigDecimal.ROUND_HALF_EVEN);
    }

    public void setIllegalOperation(String illegalOperation) {
        this.illegalOperation = illegalOperation;
    }

    public String getIllegalOperation() {
        String[] parts = (illegalOperation+" ").split(",");
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if(part.equals(" ") || part.equals("")){
                part = "null";
            }
            builder.append(part+" ");
        }

        return builder.toString();
    }

    public void bet(int amount, Match match, Character side){
        betCount++;
        if(side == match.getWinner()){
            int winnings = (int) (amount * match.getWinnerReturnRate());
            balance += winnings;
            totalWinnings += winnings;
            wins++;
        }else if(!match.getWinner().equals('D')){
            balance -= amount;
            totalWinnings -= amount;
        }
        //else: it was a draw, do nothing
    }

    public long getTotalWinnings() {
        return totalWinnings;
    }

}
