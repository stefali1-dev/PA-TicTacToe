package com.example.tictactoe_fx;

import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        Application.launch(TicTacToe.class, "--size=600", "--squares=3");
    }
}