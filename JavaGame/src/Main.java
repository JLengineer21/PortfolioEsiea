import java.util.Scanner;
import java.util.Random;
import services.LoggingService;
import services.ConcreteLoggingService;

class ChoosePlayersModeRequest {
}

class ChoosePlayersModeResponse {
    private final int choice;
    private final int computerDifficulty;

    public ChoosePlayersModeResponse(int choice, int computerDifficulty) {
        this.choice = choice;
        this.computerDifficulty = computerDifficulty;
    }

    public int getChoice() {
        return choice;
    }

    public int getComputerDifficulty() {
        return computerDifficulty;
    }
}

interface RequestHandler<Request, Response> {
    Response handle(Request request);
}

// ChoosePlayersModeInteractor
class ChoosePlayersModeInteractor implements RequestHandler<ChoosePlayersModeRequest, ChoosePlayersModeResponse> {
    public ChoosePlayersModeResponse handle(ChoosePlayersModeRequest request) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Player 1 Settings:");
        System.out.println("1. Play against Player 2");
        System.out.println("2. Play against the computer");
        System.out.print("Enter your choice (1 or 2): ");
        int choice = scanner.nextInt();

        if (choice == 2) {
            System.out.println("Computer Difficulty Selection:");
            System.out.println("1. Easy (Random)");
            System.out.println("2. Medium");
            System.out.println("3. Hard (Advanced)");
            System.out.print("Choose the computer difficulty (1, 2, or 3): ");
            int computerDifficulty = scanner.nextInt();
            return new ChoosePlayersModeResponse(choice, computerDifficulty);
        }

        return new ChoosePlayersModeResponse(choice, -1); // -1 indicates no computer difficulty.
    }
}

// ChooseComputerDifficultyRequest
class ChooseComputerDifficultyRequest {
}

// ChooseComputerDifficultyResponse
class ChooseComputerDifficultyResponse {
    private final int difficulty;
    private final String difficultyName;

    public ChooseComputerDifficultyResponse(int difficulty, String difficultyName) {
        this.difficulty = difficulty;
        this.difficultyName = difficultyName;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getDifficultyName() {
        return difficultyName;
    }
}


class ChooseComputerDifficultyInteractor
        implements RequestHandler<ChooseComputerDifficultyRequest, ChooseComputerDifficultyResponse> {
    public ChooseComputerDifficultyResponse handle(ChooseComputerDifficultyRequest request) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Computer Difficulty Selection:");
        System.out.println("1. Easy (Random)");
        System.out.println("2. Medium");
        System.out.println("3. Hard (Advanced)");
        System.out.print("Choose the computer difficulty (1, 2, or 3): ");
        int computerDifficulty = scanner.nextInt();
        String difficultyName = getDifficultyName(computerDifficulty);
        return new ChooseComputerDifficultyResponse(computerDifficulty, difficultyName);
    }

    private String getDifficultyName(int computerDifficulty) {
        if (computerDifficulty == 1) {
            return "Easy (Random)";
        } else if (computerDifficulty == 3) {
            return "Hard (Advanced)";
        } else {
            return "Medium";
        }
    }
}

class DisplayRulesInteractor {




    public void displayRules() {
        String rules = "Match Game Rules:\n" +
                "Players take turns removing 1, 2, or 3 matches from the board.\n" +
                "The player who removes the last match wins.";
        System.out.println(rules);
        // Log the displayed rules using the LoggingService

    }
}


// PlayerMoveInteractor
class PlayerMoveInteractor {
    public int getPlayerMove(String currentPlayerName) {
        Scanner scanner = new Scanner(System.in);
        int move;
        do {
            System.out.print(currentPlayerName + ", enter your move (1, 2, or 3 matches): ");
            move = scanner.nextInt();
        } while (move < 1 || move > 3);
        return move;
    }
}

// ComputerMoveRequest
class ComputerMoveRequest {
    private final int computerDifficulty;
    private final int matches;

    public ComputerMoveRequest(int computerDifficulty, int matches) {
        this.computerDifficulty = computerDifficulty;
        this.matches = matches;
    }

    public int getComputerDifficulty() {
        return computerDifficulty;
    }

    public int getMatches() {
        return matches;
    }
}

// ComputerMoveResponse
class ComputerMoveResponse {
    private final int computerMove;

    public ComputerMoveResponse(int computerMove) {
        this.computerMove = computerMove;
    }

    public int getComputerMove() {
        return computerMove;
    }
}

// ComputerMoveInteractor
class ComputerMoveInteractor
        implements RequestHandler<ComputerMoveRequest, ComputerMoveResponse> {
    public ComputerMoveResponse handle(ComputerMoveRequest request) {
        int computerMove = getComputerMove(request.getComputerDifficulty(), request.getMatches());
        return new ComputerMoveResponse(computerMove);
    }

    private int getComputerMove(int computerDifficulty, int matches) {
        if (computerDifficulty == 3) {
            int computerMove = (matches - 1) % 4;
            if (computerMove == 0) {
                computerMove = 1;
            }
            System.out.println("Computer (Hard) removes " + computerMove + " matches.");
            return computerMove;
        } else {
            int computerMove = (matches - 1) % 4;
            if (computerMove == 0) {
                computerMove = (new Random().nextInt(3)) + 1;
            }
            System.out.println("Computer (Medium) removes " + computerMove + " matches.");
            return computerMove;
        }
    }
}

// DisplayWinnerInteractor
class DisplayWinnerInteractor {
    public void displayWinner(String winnerName) {
        System.out.println(winnerName + " wins!");
    }
}

// PlayAgainInteractor
class PlayAgainInteractor {
    public boolean playAgain() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("End of Game");
        System.out.println("1. Play again");
        System.out.println("2. Exit");
        System.out.print("Enter your choice (1 or 2): ");
        int choice = scanner.nextInt();
        return choice == 1;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        LoggingService loggingService = new ConcreteLoggingService();

        // DisplayRulesInteractor
        DisplayRulesInteractor rulesInteractor = new DisplayRulesInteractor();
        // Prompt the user to display rules
        System.out.print("Do you want to display game rules? (yes/no): ");
        String displayRulesChoice = scanner.next().toLowerCase();

        if (displayRulesChoice.equals("yes")) {
            rulesInteractor.displayRules();
        }

        // ChoosePlayersModeInteractor
        RequestHandler<ChoosePlayersModeRequest, ChoosePlayersModeResponse> playersModeInteractor =
                new ChoosePlayersModeInteractor();
        ChoosePlayersModeRequest choosePlayersModeRequest = new ChoosePlayersModeRequest();
        ChoosePlayersModeResponse choosePlayersModeResponse = playersModeInteractor.handle(choosePlayersModeRequest);
        int players = choosePlayersModeResponse.getChoice();
        int computerDifficulty = choosePlayersModeResponse.getComputerDifficulty();

        // Get player names
        String player1Name = getPlayerName(scanner, 1);
        String player2Name = (players == 2) ? getPlayerName(scanner, 2) : "Computer";

        boolean isPlayer1Turn = true;

        // Game Round
        while (true) {
            int matches = chooseGameMode(scanner);

            while (matches > 0) {
                System.out.println("Matches on the board: " + matches);
                String currentPlayerName = isPlayer1Turn ? player1Name : player2Name;

                // PlayerMoveInteractor
                PlayerMoveInteractor playerMoveInteractor = new PlayerMoveInteractor();
                int playerMove = playerMoveInteractor.getPlayerMove(currentPlayerName);

                matches -= playerMove;
                isPlayer1Turn = !isPlayer1Turn;
            }

            // Winner Display
            String winnerName = isPlayer1Turn ? player2Name : player1Name;
            DisplayWinnerInteractor displayWinnerInteractor = new DisplayWinnerInteractor();
            displayWinnerInteractor.displayWinner(winnerName);

            // End of the game
            if (!playAgain(scanner)) {
                System.out.println("Goodbye!");
                break;
            }
        }
    }

    public static int chooseGameMode(Scanner scanner) {
        System.out.println("Welcome to the Match Game!");
        System.out.println("Game Mode Choice:");
        System.out.println("1. Classic Mode (fast-paced)");
        System.out.println("2. Custom Mode (potentially longer game)");
        System.out.print("Enter your choice (1 or 2): ");

        int choice = scanner.nextInt();
        if (choice == 2) {
            System.out.print("Enter the number of matches for Custom Mode: ");
            return scanner.nextInt();
        }
        return 20; // Default to 20 matches for Classic Mode.
    }

    public static String getPlayerName(Scanner scanner, int playerNumber) {
        System.out.print("Enter the name for Player " + playerNumber + ": ");
        return scanner.next();
    }

    public static boolean playAgain(Scanner scanner) {
        PlayAgainInteractor playAgainInteractor = new PlayAgainInteractor();
        return playAgainInteractor.playAgain();
    }
}
