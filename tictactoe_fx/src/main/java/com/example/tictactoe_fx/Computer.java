package com.example.tictactoe_fx;

import javafx.application.Platform;
import javafx.scene.Node;

public class Computer extends Player {

    public Computer(String name, String marker, int turn) {
        super(name, marker, turn);
    }

    @Override
    //public void takeTurn(GridPane board) {
    public void takeTurn(TicTacToe game) {
        // Get the game board
        Node[][] board = game.getBoard();
        // Initialize best score to min or max depending on player's minimax role.
        // In this case the computer is the maximizing player and its it's turn, so default to a LOW initial max.
        int bestScore = Integer.MIN_VALUE;
        // This will have the row, column that represents our best move
        int[] move = new int[2];
        // Loop over all squares to find the minimizing/maximizing move.
        for(int row=0; row<board.length; row++) {
            for(int column=0; column<board[row].length; column++) {
                BoardButton square = (BoardButton)board[row][column];
                // If a square is available, test it out
                if(square.isAvailable()) {
                    // Simulate choosing this available square (i.e. my next move)
                    square.setText(this.getMarker());

                    // I just moved so isMaximizing is false.
                    int score = minimax(game, square, 0, this.getTurn(), false);
                    // Remove my fake choice from board
                    square.setText("");
                    if( score > bestScore ) {
                        bestScore = score;
                        move[0] = row;
                        move[1] = column;
                    }
                }
            }
        }
        Platform.runLater(((BoardButton)board[move[0]][move[1]])::fire);
    }

    private String spacing(int depth) {
        StringBuilder d = new StringBuilder();
        depth *= 2;
        d.append(" ".repeat(Math.max(0, depth)));
        return d.toString();
    }

    private boolean checkTie(Node[][] board) {
        for(Node[] nodes : board) {
            for (Node node : nodes) {
                if (((BoardButton) node).getText().trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private int minimax(TicTacToe game, BoardButton sq, int depth, int playerIdx, boolean isMaximizing) {
        // Indentation string based on depth for better visualization
        String sp = spacing(depth);

        // Print the current move for debugging
        System.out.printf("%s(%d, %d)%n", sp, sq.getRow(), sq.getCol());

        // Check if the current move is a winning move
        if (game.checkWinner(sq.getRow(), sq.getCol(), game.getPlayers().get(playerIdx).getMarker())) {
            System.out.printf("%sThis would be a winner for %s%n", sp, game.getPlayers().get(playerIdx).getName());

            // If it's a winner, determine the score based on the current player's turn
            return (this.getTurn() == playerIdx) ? 1 : -1;
        }

        // Get the board
        Node[][] board = game.getBoard();

        // Check if the game is a tie
        if (checkTie(board)) {
            System.out.printf("%sWould be a tie!%n", sp);
            return 0;
        }

        // Progress to the next player
        playerIdx = (playerIdx + 1) % game.getPlayers().size();

        int bestScore;

        if (isMaximizing) {
            // Maximizing player's turn
            bestScore = Integer.MIN_VALUE;

            // Iterate over each square on the board
            for (int r = 0; r < board.length; r++) {
                for (int c = 0; c < board[r].length; c++) {
                    BoardButton square = (BoardButton) board[r][c];

                    // If a square is available, test it out
                    if (square.isAvailable()) {
                        // Simulate choosing this available square
                        square.setText(game.getPlayers().get(playerIdx).getMarker());

                        // Recursively call minimax for the next depth, updating the player index and changing to minimizing turn
                        int score = minimax(game, square, depth + 1, playerIdx, false);

                        // Remove the simulated choice from the board
                        square.setText("");

                        // Update the best score by maximizing the obtained score
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
        } else {
            // Minimizing player's turn
            bestScore = Integer.MAX_VALUE;

            // Iterate over each square on the board
            for (int r = 0; r < board.length; r++) {
                for (int c = 0; c < board[r].length; c++) {
                    BoardButton square = (BoardButton) board[r][c];

                    // If a square is available, test it out
                    if (square.isAvailable()) {
                        // Simulate choosing this available square
                        square.setText(game.getPlayers().get(playerIdx).getMarker());

                        // Recursively call minimax for the next depth, updating the player index and changing to maximizing turn
                        int score = minimax(game, square, depth + 1, playerIdx, true);

                        // Remove the simulated choice from the board
                        square.setText("");

                        // Update the best score by minimizing the obtained score
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
        }
        System.out.println(bestScore);
        return bestScore;
    }

}