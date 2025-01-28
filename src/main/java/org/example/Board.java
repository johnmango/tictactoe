package org.example;

public class Board {
    Cell[] cellsNew = new Cell[9];

    public Board(byte[] cells) {
        if (cells.length != 9) {
            throw new RuntimeException("Invalid cell count. Expected 9, got " + cells.length);
        }

        for (byte i = 0; i < 9; i++) {
            if (cells[i] < -1 || cells[i] > 1) {
                throw new RuntimeException("Cell value is " + cells[i] + ". Allowed values only -1, 0, 1");
            }

            this.cellsNew[i] = new Cell(i, cells[i]);
        }

    }

    public Board() {
        this(new byte[9]);
    }

    public Board(Cell[] cells) {
        this.cellsNew = cells;
    }

    public Board deepCopy() {
        Cell[] newCells = this.cellsNew.clone();

        return new Board(newCells);
    }
}
