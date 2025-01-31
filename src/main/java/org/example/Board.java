package org.example;

public class Board {
    public static final byte BOARD_SIZE = 3;
    private static final byte TOTAL_CELLS = (byte) Math.pow(BOARD_SIZE, 2);

    private static final byte X = 1;
    private static final byte O = -1;


    private final byte[][] cell;
    private byte availableCells = TOTAL_CELLS;
    private byte mainDiagonal = 0;
    private byte auxDiagonal = 0;

    byte winner = 0;
    boolean isTerminal = false;

    public Board(byte[][] cells) {
        if (cells.length != BOARD_SIZE) {
            throw new RuntimeException("Invalid board size. Expected: " + BOARD_SIZE + "; Got: " + cells.length);
        }

        this.cell = cells;
    }

    public Board() {
        this.cell = new byte[][]{{0, 0, 0},
                                {0, 0, 0},
                                {0, 0, 0}};
    }

    /**
     * Returns new board with new move on it and checks for winner and if isTerminal
     */
    public Board getBoardAfterMove(Move move) {
        if (moveIsNotPossible(move)) {
            throw new RuntimeException("Cell is occupied! [" + move.row + ";" + move.col);
        }

        Board boardAfterMove = this.getDeepCopyWithNewMove(move);
        boardAfterMove.availableCells--;

        if (move.row == move.col) {
            boardAfterMove.mainDiagonal += move.value;
        }

        if (move.row + move.col == BOARD_SIZE - 1) {
            boardAfterMove.auxDiagonal += move.value;
        }

        boardAfterMove.checkWinner(move);

        return boardAfterMove;
    }

    /**
     * Method returns deep copy of board with new move
     */
    private Board getDeepCopyWithNewMove(Move move) {
        byte[][] newCells = new byte[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            newCells[i] = this.cell[i].clone();
        }

        newCells[move.row][move.col] = this.getNextPlayer();
        Board newBoard = new Board(newCells);

        newBoard.availableCells = this.availableCells;
        newBoard.mainDiagonal = this.mainDiagonal;
        newBoard.auxDiagonal = this.auxDiagonal;

        return newBoard;
    }

    /**
     * method sets this.winner if there is a winner on the board and sets this.isTerminal if the board is terminal
     * i.e. no moves left or someone has won. Parameter 'move' is the last move made on this board.
     */
    private void checkWinner(Move move) {
        if (Math.abs(mainDiagonal) == BOARD_SIZE || Math.abs(auxDiagonal) == BOARD_SIZE) {
            this.winner = move.value;
            this.isTerminal = true;
            return;
        }

        if (this.availableCells == 0) {
            this.isTerminal = true;
        }

        boolean isRowWins = true;
        boolean isColWins = true;

        for (int i = 0; i < BOARD_SIZE; i++) {
            if (this.cell[i][move.col] != move.value) {
                isColWins = false;
            }
            
            if (this.cell[move.row][i] != move.value) {
                isRowWins = false;
            }

            if (!isRowWins && !isColWins) {
                return;
            }
        }

        this.winner = move.value;
        this.isTerminal = true;
    }

    /**
     * Method returns who is the next to make a move on the board
     */
    public byte getNextPlayer() {
        if ((TOTAL_CELLS - availableCells) % 2 == 0) {
            return X;
        }

        return O;
    }

    /**
     * Displays the board
     */
    public void displayBoard() {
        for (byte[] row : cell) {
            for (byte col : row) {
                String symbol;
                switch (col) {
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
     * Checks if the cell on the board is empty
     */
    public boolean moveIsNotPossible(Move move) {
        if (isTerminal) {
            throw new RuntimeException("Board is terminal. No new moves possible");
        }
        return cell[move.row][move.col] != 0;
    }

    /**
     * Returns move[] array of all possible moves on the board
     */
    public Move[] getPossibleMoves() {
        if (isTerminal) {
            return new Move[0];
        }

        Move[] possibleMoves = new Move[availableCells];
        byte possibleMovesIndex = 0;
        byte nextPlayer = this.getNextPlayer();
        for (byte row = 0; row < BOARD_SIZE; row++) {
            for (byte col = 0; col < BOARD_SIZE; col++) {
                if (cell[row][col] == 0) {
                    possibleMoves[possibleMovesIndex++] = new Move(row, col, nextPlayer);
                }
            }
        }

        return possibleMoves;
    }
}
