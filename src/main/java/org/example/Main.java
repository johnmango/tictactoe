package org.example;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Board board = new Board();
        byte humanChoice = getHumanChoice();

        while (!isTerminal(board)) {
            displayBoard(board);
            byte currentPlayer = nextPlayer(board);

            if (currentPlayer == humanChoice) {
                board = askForNextMove(board);
            } else {
                System.out.println("Computer is thinking...");
                board = getBoardAfterMove(board, minimax(board));
            }
        }

        displayBoard(board);
        byte winner = getWinner(board);

        if (winner == 0) {
            System.out.println("It's a tie!");
        } else {
            System.out.println("The winner is " + (winner == 1 ? "X" : "O"));
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
     * returns who make the next move
     */
    private static byte nextPlayer(Board board) {
        byte totalX = 0;
        byte totalO = 0;
        for (byte[] row : board.cell) {
            for (byte cell : row) {
                if (cell == -1) {
                    totalO++;
                } else if (cell == 1) {
                    totalX++;
                }
            }
        }

        if (totalO == totalX) {
            return 1;
        }
        return -1;
    }

    /**
     * returns if the board is terminal, i.e. someone has won or no turns possible (board is full)
     */
    private static boolean isTerminal(Board board) {
        if (getWinner(board) != 0) {
            return true;
        }

        for (byte[] row : board.cell) {
            for (byte cell : row) {
                if (cell == 0) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * returns the winner of the board if any:
     * -1 is for O;
     * +1 is for X;
     *  0 is for tie.
     */
    private static byte getWinner(Board board) {
        byte boardSize = (byte) board.cell.length;
        //first element is for X, second is for O
        byte[] mainDiagonal = {0, 0};
        byte[] secondaryDiagonal = {0, 0};
        byte[][] columnTotal = new byte[2][boardSize];

        for (byte row = 0; row < boardSize; row++) {
            byte[] rowTotal = {0, 0};

            for (byte col = 0; col < boardSize; col++) {
                byte cell = board.cell[row][col];
                byte playerIndex;

                if (cell == 0) {
                    continue;
                }

                if (cell == 1) {
                    playerIndex = 0;
                } else {
                    playerIndex = 1;
                }

                rowTotal[playerIndex]++;
                columnTotal[playerIndex][col]++;

                if (row == col) {
                    mainDiagonal[playerIndex]++;
                }

                if (row + col == boardSize - 1) {
                    secondaryDiagonal[playerIndex]++;
                }
            }

            if (rowTotal[0] == boardSize) {
                return 1;
            }
            if (rowTotal[1] == boardSize) {
                return -1;
            }
        }

        for (int i = 0; i <= 1; i++) {
            byte winnerIndex;
            if (i == 0) {
                winnerIndex = 1;
            } else {
                winnerIndex = -1;
            }

            if (mainDiagonal[i] == boardSize || secondaryDiagonal[i] == boardSize) {
                return winnerIndex;
            }

            for (byte column = 0; column < boardSize; column++) {
                if (columnTotal[i][column] == boardSize) {
                    return winnerIndex;
                }

            }

        }

        return 0;
    }

    /**
     * displays the board
     */
    private static void displayBoard(Board board) {
        for (byte[] row : board.cell) {
            for (byte cell : row) {
                String symbol;
                switch (cell) {
                    case -1 -> symbol = "O";
                    case 1 -> symbol = "X";
                    default -> symbol = " ";
                }

                System.out.print("[" + symbol + "]");
            }
            System.out.println();
        }
    }

    /**
     * return the board after the move was made. The move is a byte[row, col]
     */
    private static Board getBoardAfterMove(Board board, Byte[] move) {
        byte row = move[0];
        byte col = move[1];

        if (board.cell[row][col] != 0) {
            throw new RuntimeException("The cell " + row + ", " + col + "is already occupied.");
        }

        Board boardAfterMove = new Board(board.deepCopy().cell);
        boardAfterMove.cell[row][col] = nextPlayer(board);

        return boardAfterMove;
    }

    /**
     * ask for move and check if it's possible. Return new board with new move.
     */
    private static Board askForNextMove(Board board) {
        Scanner scanner = new Scanner(System.in);
        boolean isMovePossible = false;
        byte row;
        byte column;

        do {
            System.out.println("Enter next move.");
            do {
                System.out.print("Row (0 - " + (board.cell.length - 1) + "): ");
                row = scanner.nextByte();
            } while (row < 0 || row > 2);

            do {
                System.out.print("Column (0 - " + (board.cell.length - 1) + "): ");
                column = scanner.nextByte();
            } while (column < 0 || column > 2);

            if (board.cell[row][column] == 0) {
                isMovePossible = true;
            } else {
                System.out.println("This cell is used.");
                displayBoard(board);
            }
        } while (!isMovePossible);

        return getBoardAfterMove(board, new Byte[]{row, column});
    }

    /**
     * returns all possible moves for the board as an ArrayList of Bytes[] [row, col]
     */
    private static ArrayList<Byte[]> getPossibleMoves(Board board) {
        byte boardSize = (byte) board.cell.length;
        ArrayList<Byte[]> possibleMoves = new ArrayList<>();

        for (byte row = 0; row < boardSize; row++) {
            for (byte col = 0; col < boardSize; col++) {
                if (board.cell[row][col] == 0) {
                    possibleMoves.add(new Byte[]{row, col});
                }
            }
        }

        return possibleMoves;
    }

    /**
     * minimax
     */
    private static Byte[] minimax(Board board) {
        Byte[] result;
        if (nextPlayer(board) == 1) {
            result = maximize(board, (byte) 2);
        } else {
            result = minimize(board, (byte) -2);
        }

        return new Byte[]{result[1], result[2]};
    }

    /**
     * maximize
     */
    private static Byte[] maximize(Board board, byte minimum) {
        if (isTerminal(board)) {
            return new Byte[]{getWinner(board), null, null};
        }

        Byte[] v = {-2, null, null};
        ArrayList<Byte[]> possibleMoves = getPossibleMoves(board);
        for (Byte[] move : possibleMoves) {
            Board newBoard = getBoardAfterMove(board, move);
            Byte[] result = minimize(newBoard, v[0]);

            if (result[0] > minimum) {
                result[1] = move[0];
                result[2] = move[1];
                return result;
            }

            if (result[0] > v[0]) {
                v[0] = result[0];
                v[1] = move[0];
                v[2] = move[1];
            }
        }

        return v;
    }

    /**
     * minimize
     */
    private static Byte[] minimize(Board board, byte maximum) {
        if (isTerminal(board)) {
            return new Byte[]{getWinner(board), null, null};
        }

        Byte[] minimum = {2, null, null};
        ArrayList<Byte[]> possibleMoves = getPossibleMoves(board);
        for (Byte[] move : possibleMoves) {
            Board newBoard = getBoardAfterMove(board, move);
            Byte[] result = maximize(newBoard, minimum[0]);

            if (result[0] < maximum) {
                result[1] = move[0];
                result[2] = move[1];
                return result;
            }

            if (result[0] < minimum[0]) {
                minimum[0] = result[0];
                minimum[1] = move[0];
                minimum[2] = move[1];
            }
        }

        return minimum;
    }
}