import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        HashMap<UUID,Match> matches = readMatchesFromFile("match_data.txt");

        HashMap<UUID,Player> players = readPlayerActionsFromFile("player_data.txt",matches);

        String path = System.getProperty("user.dir")+"/src/main/java/"+"results.txt";
        writeResultsToFile(path, players);
    }

    public static HashMap<UUID,Match> readMatchesFromFile(String path){

        HashMap<UUID,Match> matches = new HashMap<>();
        InputStream matchStream = Main.class.getResourceAsStream(path);
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

        return matches;
    }

    public static HashMap<UUID,Player> readPlayerActionsFromFile(String path, HashMap<UUID,Match> matches){
        HashMap<UUID,Player> players = new HashMap<>();

        InputStream playerStream = Main.class.getResourceAsStream(path);
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
            }else{
                player = players.get(playerId);
            }

            if(!player.isLegit()){
                continue;
            }

            switch (operation) {
                case DEPOSIT -> player.addBalance(amount);
                case WITHDRAW -> {
                    if (player.getBalance() < amount) {
                        player.setIllegalOperation(line);
                        break;
                    }
                    player.addBalance(-amount);
                }
                case BET -> {
                    if (player.getBalance() < amount) {
                        player.setIllegalOperation(line);
                        break;
                    }

                    UUID matchId = UUID.fromString(parts[2]);
                    Match match = matches.get(matchId);
                    if (match == null) {
                        System.out.println("Match: [" + matchId + "] not found");
                        break;
                    }

                    Character side = parts[4].charAt(0);

                    player.bet(amount, match, side);
                }
            }

        }

        return players;
    }

    public static void writeResultsToFile(String path, HashMap<UUID,Player> players){

        List<Player> legitimatePlayers = new ArrayList<>();
        List<Player> illegitimatePlayers = new ArrayList<>();

        for (Player player : players.values()){
            if(player.isLegit()){
                legitimatePlayers.add(player);
            }else{
                illegitimatePlayers.add(player);
            }
        }

        legitimatePlayers.sort(Comparator.comparing(Player::getPlayerId));
        illegitimatePlayers.sort(Comparator.comparing(Player::getPlayerId));

        long casinoBalanceChange = 0;

        try {
            PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8);

            if(legitimatePlayers.isEmpty()){
                writer.println("");
            }else{
                for (Player player : legitimatePlayers) {
                    writer.println(player.getPlayerId() + " " + player.getBalance() + " " + player.getWinRate());
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}