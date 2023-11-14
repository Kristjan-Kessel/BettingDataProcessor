import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        HashMap<UUID,Player> players = new HashMap<>();
        HashMap<UUID,Match> matches = new HashMap<>();

        ArrayList<Player> legitimatePlayers = new ArrayList<>();
        ArrayList<Player> illegitimatePlayers = new ArrayList<>();

        //matches
        InputStream matchStream = Main.class.getResourceAsStream("match_data.txt");
        Scanner matchScanner = new Scanner(matchStream, StandardCharsets.UTF_8);

        while (matchScanner.hasNextLine()){
            String line = matchScanner.nextLine();
            String[] parts = line.split(",");
            UUID matchId = UUID.fromString(parts[0]);
            Double returnRateA = Double.parseDouble(parts[1]);
            Double returnRateB = Double.parseDouble(parts[2]);
            Character winner = parts[3].charAt(0);
            matches.put(matchId, new Match(matchId, winner, returnRateA, returnRateB));
        }

        //players
        InputStream playerStream = Main.class.getResourceAsStream("player_data.txt");
        Scanner playerScanner = new Scanner(playerStream, StandardCharsets.UTF_8);

        while (playerScanner.hasNextLine()) {
            String line = playerScanner.nextLine();
            String[] parts = line.split(",");

            UUID playerId = UUID.fromString(parts[0]);
            Operation operation = Operation.valueOf(parts[1]);
            int amount = Integer.parseInt(parts[3]);

            Player player;

            if(!players.containsKey(playerId)) {
                player = new Player(playerId);
                players.put(playerId, player);
                legitimatePlayers.add(player);
            }else{
                player = players.get(playerId);
            }

            if(illegitimatePlayers.contains(player)){
                continue;
            }

            switch (operation) {
                case DEPOSIT:
                    player.addBalance(amount);
                    break;
                case WITHDRAW:

                    if(player.getBalance()<amount){
                        illegitimatePlayers.add(player);
                        legitimatePlayers.remove(player);
                        player.setIllegalOperation(line);
                        break;
                    }

                    player.addBalance(-amount);
                    break;
                case BET:
                    UUID matchId = UUID.fromString(parts[2]);

                    if(player.getBalance()<amount){
                        illegitimatePlayers.add(player);
                        legitimatePlayers.remove(player);
                        player.setIllegalOperation(line);
                        break;
                    }

                    Character side = parts[4].charAt(0);
                    Match match = matches.get(matchId);
                    if(match == null){
                        System.out.println("Match: ["+matchId+"] not found");
                        break;
                    }

                    player.bet(amount, match, side);
                    break;
            }

        }

        //results
        legitimatePlayers.sort(Comparator.comparing(Player::getPlayerId));
        illegitimatePlayers.sort(Comparator.comparing(Player::getPlayerId));

        long casinoBalanceChange = 0;

        try {
            String path = System.getProperty("user.dir") + "/src/main/java/";
            PrintWriter writer = new PrintWriter(path+"results.txt", "UTF-8");

            if(legitimatePlayers.isEmpty()){
                writer.println("");
            }else{
                for (Player player : legitimatePlayers) {
                    writer.println(player.getPlayerId() + " " + player.getBalance() + " " + player.getWinrate());
                    casinoBalanceChange -= player.getTotalWinnings();
                }
            }
            writer.println("");

            if(illegitimatePlayers.isEmpty()) {
                writer.println("");
            }else{
                for (Player player : illegitimatePlayers){
                    writer.println(player.getIllegalOperation());
                }
            }
            writer.println("");

            writer.println(casinoBalanceChange);

            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
