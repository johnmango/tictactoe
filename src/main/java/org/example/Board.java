package org.example;

public class Board {
    byte[][] cell;

    public Board(byte[][] cells) {
        for (byte[] row : cells) {
            for (int cell : row) {
                if (cell < -1 || cell > 1) {
                    throw new RuntimeException("Cell value is " + cell + ". Allowed values only -1, 0, 1");
                }
            }
        }

        this.cell = cells;
    }

    public Board() {
        this.cell = new byte[][]{{0, 0, 0},
                {0, 0, 0},
                {0, 0, 0}};
    }

    public Board deepCopy() {
        byte boardSize = (byte) this.cell.length;
        byte[][] newCells = new byte[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            newCells[i] = this.cell[i].clone();
        }

        return new Board(newCells);
    }
}
