package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Board board = new Board();
        byte humanChoice = getHumanChoice();

        while (!board.isTerminal) {
            board.displayBoard();
            byte currentPlayer = board.getNextPlayer();

            if (currentPlayer == humanChoice) {
                board = askForNextMove(board);
            } else {
                System.out.println("Computer is thinking...");
                board = board.getBoardAfterMove(Game.minimax(board));
            }
        }

        board.displayBoard();
        if (board.winner == 0) {
            System.out.println("It's a tie!");
        } else {
            System.out.println("The winner is " + (board.winner == 1 ? "X" : "O"));
        }

    }

    /**
     * asks human which side they want to play
     */
    private static byte getHumanChoice() {
        System.out.println("Choose your side: X or O");
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("X")) {
            return 1;
        } else if (choice.equalsIgnoreCase("O")) {
            return -1;
        } else {
            System.out.println("Invalid choice. Please choose X or O.");
            return getHumanChoice();
        }
    }


    /**
     * ask for move and check if it's possible. Return new board with new move.
     */
    private static Board askForNextMove(Board board) {
        Scanner scanner = new Scanner(System.in);
        byte row;
        byte column;
        Move playerMove;

        do {
            System.out.println("Enter next move.");
            do {
                System.out.print("Row (0 - " + (Board.BOARD_SIZE - 1) + "): ");
                row = scanner.nextByte();
            } while (row < 0 || row > 2);

            do {
                System.out.print("Column (0 - " + (Board.BOARD_SIZE- 1) + "): ");
                column = scanner.nextByte();
            } while (column < 0 || column > 2);

            playerMove = new Move(row, column, board.getNextPlayer());
            if (board.moveIsNotPossible(playerMove)) {
                System.out.println("This cell is used.");
                playerMove.value = 55;
            }

        } while (playerMove.value == 55);

        return board.getBoardAfterMove(playerMove);
    }
}

