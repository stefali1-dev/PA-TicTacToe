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
        /*
        outerLoop:
        for(int r=0; r<board.length; r++) {
            for(int c=0; c<board[r].length; c++) {
                BoardButton square = (BoardButton)board[r][c];
                if(square.isAvailable()) {
                    Platform.runLater(square::fire);
                    break outerLoop;
                }
            }
        }
        */
        // Get the game board
        Node[][] board = game.getBoard();
        // Initialize best score to min or max depending on player's minimax role.
        // In this case the computer is the maximizing player and its it's turn, so default to a LOW initial max.
        int bestScore = Integer.MIN_VALUE;
        // This will have the row, column that represents our best move
        int[] move = new int[2];
        // Loop over all squares to find the minimizing/maximizing move.
        for(int r=0; r<board.length; r++) {
            for(int c=0; c<board[r].length; c++) {
                BoardButton square = (BoardButton)board[r][c];
                // If a square is available, test it out
                if(square.isAvailable()) {
                    // Simulate choosing this available square (i.e. my next move)
                    square.setText(this.getMarker());
                    // The next player can't be maximizing because I am maximizing, but I just moved so isMaximizing is false.
                    int score = minimax(game, square, 0, this.getTurn(), false);
                    // Remove my fake choice from board
                    square.setText("");
                    if( score > bestScore ) {
                        bestScore = score;
                        move[0] = r;
                        move[1] = c;
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
        //TODO: use Max^N algorithm to handle more than 2 players.
        String sp = spacing(depth);
        System.out.printf("%s(%d, %d)%n", sp, sq.getRow(), sq.getCol());
        if(game.checkWinner(sq.getRow(), sq.getCol(), game.getPlayers().get(playerIdx).getMarker())) {
            System.out.printf("%s  This would be a winner for %s%n", sp, game.getPlayers().get(playerIdx).getName());
            // TODO: handle case of Tie (i.e. score 0).
            // If its a winner, who is the winner?
            return (this.getTurn() == playerIdx) ? 1 : -1;
        }
        // Get the board.
        Node[][] board = game.getBoard();

        if(checkTie(board)) {
            System.out.printf("%sWould be a tie!%n", sp);
            return 0;
        }

        // Now that I've moved and checked if it was a winning move, progress to the next player
        playerIdx = (playerIdx+1) % game.getPlayers().size();

        int bestScore;
        if(isMaximizing) {
            //System.out.printf("%sMaxP: %d AI_T: %d%n", sp, playerIdx, this.getTurn());
            bestScore = Integer.MIN_VALUE;
            for(int r=0; r<board.length; r++) {
                for(int c=0; c<board[r].length; c++) {
                    BoardButton square = (BoardButton)board[r][c];
                    // If a square is available, test it out
                    if(square.isAvailable()) {
                        // Simulate choosing this available square
                        square.setText(game.getPlayers().get(playerIdx).getMarker());
                        int score = minimax(game, square, depth+1, playerIdx, false);
                        // Remove my fake choice from board
                        square.setText("");
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;
        } else {
            //System.out.printf("%sMinP: %d AI_T: %d%n", sp, playerIdx, this.getTurn());
            bestScore = Integer.MAX_VALUE;
            for(int r=0; r<board.length; r++) {
                for(int c=0; c<board[r].length; c++) {
                    BoardButton square = (BoardButton)board[r][c];
                    // If a square is available, test it out
                    if(square.isAvailable()) {
                        // Simulate choosing this available square
                        square.setText(game.getPlayers().get(playerIdx).getMarker());
                        int score = minimax(game, square, depth+1, playerIdx, true);
                        // Remove my fake choice from board
                        square.setText("");
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }
}