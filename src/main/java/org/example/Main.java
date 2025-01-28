package org.example;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private final static byte X = 1;
    private final static byte O = -1;
    public final static byte BOARD_SIZE = 3;

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
            System.out.println("The winner is " + (winner == X ? "X" : "O"));
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
            return X;
        } else if (choice.equalsIgnoreCase("O")) {
            return O;
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

        for (Cell cell : board.cellsNew) {
            if (cell.value == X) {
                totalX++;
            } else if (cell.value == O) {
                totalO++;
            }
        }

        if (totalO == totalX) {
            return X;
        }
        return O;
    }

    /**
     * returns if the board is terminal, i.e. someone has won or no turns possible (board is full)
     */
    private static boolean isTerminal(Board board) {
        if (getWinner(board) != 0) {
            return true;
        }

        for (Cell cell : board.cellsNew) {
            if (cell.value == 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * returns the winner of the board if any:
     * -1 is for O;
     * +1 is for X;
     * 0 is for tie.
     */
    private static byte getWinner(Board board) {
        // FIXME: review this method
        byte boardSize = (byte) board.cellsNew.length;
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
        for (Cell cell : board.cellsNew) {
            String cellSymbol;
            switch (cell.value) {
                case X -> cellSymbol = "[X]";
                case O -> cellSymbol = "[O]";
                default -> cellSymbol = "[ ]";
            }

            System.out.print(cellSymbol);
            if (cell.getCol() == BOARD_SIZE - 1) {
                System.out.println();
            }

        }
    }

    /**
     * return the board after the move was made. The move is a byte[row, col]
     */
    private static Board getBoardAfterMove(Board board, Cell move) {
        if (board.cellsNew[move.index].value != 0) {
            throw new RuntimeException("The cell " + move.getRow() + ", " + move.getCol() + "is already occupied.");
        }

        // TODO: check deepCopy operation here
        Board boardAfterMove = board.deepCopy();
        boardAfterMove.cellsNew[move.index].value = nextPlayer(board);

        System.out.println("IF two boards below are identical, then board.deepCopy method is not working " +
                "\nand should be rewritten==============!");
        displayBoard(board);
        displayBoard(boardAfterMove);

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
                System.out.print("Row (0 - " + (BOARD_SIZE - 1) + "): ");
                row = scanner.nextByte();
            } while (row < 0 || row > BOARD_SIZE - 2);

            do {
                System.out.print("Column (0 - " + (BOARD_SIZE - 1) + "): ");
                column = scanner.nextByte();
            } while (column < 0 || column > BOARD_SIZE - 2);

            if (board.cellsNew[row * BOARD_SIZE + column].value == 0) {
                isMovePossible = true;
            } else {
                System.out.println("This cell is used.");
                displayBoard(board);
            }
        } while (!isMovePossible);

        return getBoardAfterMove(board, new Cell(row, column, (byte) 0));
    }

    /**
     * returns all possible moves for the board as an ArrayList of Bytes[] [row, col]
     */
    private static ArrayList<Cell> getPossibleMoves(Board board) {
        ArrayList<Cell> possibleMovesCell = new ArrayList<>();
        for (Cell cell : board.cellsNew) {
            if (cell.value == 0) {
                possibleMovesCell.add(cell);
            }
        }

        return possibleMovesCell;
    }

    /**
     * minimax
     */
    private static Cell minimax(Board board) {
        Cell result;
        if (nextPlayer(board) == 1) {
            result = maximize(board, (byte) 2).cell();
        } else {
            result = minimize(board, (byte) -2).cell();
        }

        return result;
    }

    /**
     * maximize
     */
    private static ResultWithCell maximize(Board board, byte minimum) {
        if (isTerminal(board)) {
            return new ResultWithCell(getWinner(board), null);
        }

//        Byte[] v = {-2, null, null};
        ResultWithCell v = new ResultWithCell((byte) -2, null);
        ArrayList<Cell> possibleMoves = getPossibleMoves(board);
        for (Cell move : possibleMoves) {
            Board newBoard = getBoardAfterMove(board, move);
            ResultWithCell result = minimize(newBoard, v.result());
            byte resultValue = result.result();

            if (resultValue > minimum) {
                result = new ResultWithCell(resultValue, move);
                return result;
            }

            if (resultValue > v.result()) {
                v = result;
            }
        }

        return v;
    }

    /**
     * minimize
     */
    private static ResultWithCell minimize(Board board, byte maximum) {
        if (isTerminal(board)) {
            return new ResultWithCell(getWinner(board), null);
        }

//        Byte[] minimum = {2, null, null};
        ResultWithCell v = new ResultWithCell((byte) 2, null);
        ArrayList<Cell> possibleMoves = getPossibleMoves(board);
        for (Cell move : possibleMoves) {
            Board newBoard = getBoardAfterMove(board, move);
            ResultWithCell result = maximize(newBoard, v.result());
            byte resultValue = result.result();

            if (resultValue < maximum) {
                result = new ResultWithCell(resultValue, move);
                return result;
            }

            if (resultValue < v.result()) {
                v = result;
            }
        }

        return v;
    }
}