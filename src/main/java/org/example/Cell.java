package org.example;

public class Cell {
    private static final byte BOARD_SIZE = Main.BOARD_SIZE;

    byte value;
    byte index;

    public Cell(byte index, byte value) {
        this.index = index;
        this.value = value;
    }

    public Cell(byte row, byte col, byte value) {
        this.index = (byte) (row * BOARD_SIZE + col);
        this.value = value;
    }


    public byte getRow() {
        return (byte) (this.index / BOARD_SIZE);
    }

    public byte getCol() {
        return (byte) (this.index % BOARD_SIZE);
    }

}
