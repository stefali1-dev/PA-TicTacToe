package com.example.tictactoe_fx;

public class Human extends Player {
    public Human(String name, String marker, int turn) {
        super(name, marker, turn);
    }

    @Override
    //public void takeTurn(GridPane board) {
    public void takeTurn(TicTacToe game) {
        // Sentient players do nothing because we're waiting on them for external input.
    }

}