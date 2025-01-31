package org.example;

public class Game {
    private static final byte X = 1;
    private static final byte O = -1;
    private static final byte BOARD_SIZE = Board.BOARD_SIZE;

    /**
     * Returns best of all possible moves.
     */
    public static Move minimax(Board board) {
        if (board.isTerminal) {
            throw new RuntimeException("Board is terminal!");
        }

        byte nextPlayer = board.getNextPlayer();
        byte extremeValue = (byte) (nextPlayer * -1);

        Move[] possibleMovesMoves = board.getPossibleMoves();
        Move resultMove = null;

        for (Move move : possibleMovesMoves) {
            Board newBoard = board.getBoardAfterMove(move);

            if (nextPlayer == X) {
                move.value = maximize(newBoard, (byte) 0);
                if (move.value > extremeValue) {
                    extremeValue = move.value;
                    resultMove = move;
                }
            } else {
                move.value = minimize(newBoard, (byte) 0);
                if (move.value < extremeValue) {
                    extremeValue = move.value;
                    resultMove = move;
                }
            }
        }

        return resultMove;
    }

    /**
     * Returns best of all available moves which will give maximum result (for X player)
     */
    private static byte maximize(Board board, byte minimum) {
        if (board.isTerminal) {
            return board.winner;
        }

        byte v = -2;
        Move[] possibleMoves = board.getPossibleMoves();
        for (Move move : possibleMoves) {
            Board newBoard = board.getBoardAfterMove(move);
            byte result = minimize(newBoard, v);

            if (result > minimum) {
                return result;
            }

            if (result > v) {
                v = result;
            }
        }

        return v;
    }

    /**
     * Returns best of all available moves which will give minimum result (for O player)
     */
    private static byte minimize(Board board, byte maximum) {
        if (board.isTerminal) {
            return board.winner;
        }

        byte v = 2;
        Move[] possibleMoves = board.getPossibleMoves();
        for (Move move : possibleMoves) {
            Board newBoard = board.getBoardAfterMove(move);

            byte result = maximize(newBoard, v);

            if (result < maximum) {
                return result;
            }

            if (result < v) {
                v = result;
            }
        }

        return v;
    }
}
