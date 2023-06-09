package com.example.tictactoe_fx;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class TicTacToe extends Application {

    private Node[][] gridPaneArray = null; // A way to keep track of my board and access it by x, y
    private Label lblState;
    private GridPane board;
    private BorderPane gameLayout;
    private double size;
    private int squares;
    private int squaresSquared;
    private int squaresPlayed = 0;
    private int playerIdx = -1; // Initialize to -1 so that the first play starts at player 0
    private ArrayList<Player> players;
    // Player types
    PlayerFactory playerFactory = new PlayerFactory();

    Database data = new Database();

    @Override
    public void init() throws Exception {
        super.init();
        Parameters params = getParameters();
        this.size = Double.parseDouble(params.getNamed().get("size"));
        this.squares = Integer.parseInt(params.getNamed().get("squares"));
        this.squaresSquared = this.squares * this.squares;
        this.players = new ArrayList<>();


    }

    @Override
    public void start(Stage window) throws IOException {
        /**
         *
         */
        //System.out.println(data.getPlayersNameAndMarks() + "1");
        window.setWidth(size);
        window.setHeight(size);

        // Create main layout
        gameLayout = new BorderPane();

        // Create menu for main layout
        HBox menu = new HBox();
        menu.setPadding(new Insets(20, 20, 20, 20));
        menu.setSpacing(10);
        menu.setAlignment(Pos.CENTER);

        Button btnPlayGame = new Button("Ready to play!");
        btnPlayGame.setOnAction((event) -> this.startOver());

        Button btnLoadGame = new Button("Load Game");
        btnLoadGame.setOnAction((event) -> {
            try {
                //Upload players from backup
                this.data = data.uploadGameFromFile(data);
                this.loadGame();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Button btnSaveGame = new Button("Save Game");
        btnSaveGame.setOnAction((event) -> {
            try {
                //Save game state
                this.saveGame();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Instance variable so we can provide feedback between views
        this.lblState = new Label("Getting game ready...");
        // Add components to menu
        menu.getChildren().addAll(btnPlayGame, lblState);
        menu.getChildren().addAll(btnLoadGame);
        menu.getChildren().addAll(btnSaveGame);

        gameLayout.setBottom(menu);

        VBox vbConfigMenu = createConfigMenu();
        gameLayout.setCenter(vbConfigMenu);

        // Create main scene with layout
        Scene scene = new Scene(gameLayout);

        // Show the main scene
        window.setScene(scene);
        window.show();
    }

    public VBox createConfigMenu() {
        /**
         * Creates player configuration menu and extract this info, hidden when game starts
         */
        VBox vbLayout = new VBox();
        // Create a combo box with supported player types to select the player type
        ComboBox<PlayerFactory.PlayerTypes> comboBox =
                new ComboBox<>(FXCollections.observableArrayList(PlayerFactory.PlayerTypes.values()));
        comboBox.setMaxWidth(Double.MAX_VALUE);

        // Create the player name menu
        HBox hbNameLayout = new HBox();
        hbNameLayout.setPadding(new Insets(10, 10, 10, 10));
        hbNameLayout.setSpacing(10);
        hbNameLayout.setAlignment(Pos.CENTER);
        Label lblPlayerName = new Label("Player Name:");
        TextField tfPlayerName = new TextField();
        hbNameLayout.getChildren().addAll(lblPlayerName, tfPlayerName);
        hbNameLayout.setMaxWidth(Double.MAX_VALUE);

        // Create the player marker menu
        HBox hbMarkerLayout = new HBox();
        hbMarkerLayout.setPadding(new Insets(10, 10, 10, 10));
        hbMarkerLayout.setSpacing(10);
        hbMarkerLayout.setAlignment(Pos.CENTER);
        hbMarkerLayout.setMaxWidth(Double.MAX_VALUE);
        Label lblMarker = new Label("Player Mark:");
        TextField tfMarker = new TextField();
        hbMarkerLayout.getChildren().addAll(lblMarker, tfMarker);

        // Create Add player buttons for menu
        Button btnAddPlayer = new Button("Add Player");
        btnAddPlayer.setMaxWidth(Double.MAX_VALUE);

        btnAddPlayer.setOnAction((event) -> {
            //Get players info one by one
            String name = tfPlayerName.getText();
            String marker = tfMarker.getText();
            int turn = this.players.size();

            //Add new crated players
            this.players.add(
                    this.playerFactory.getPlayer(comboBox.getValue(), name, marker, turn));
            this.data.savePlayerType(name, comboBox.getValue());

            // Clear the menu components.
            comboBox.setValue(null);
            tfPlayerName.setText(null);
            tfMarker.setText(null);

            // Update feedback to let us know the player was added.
            this.lblState.setText(String.format("Player %s : %s added...", name, marker));
        });
        vbLayout.getChildren().addAll(comboBox, hbNameLayout, hbMarkerLayout, btnAddPlayer);
        vbLayout.setFillWidth(true);
        return vbLayout;
    }

    private void startOver() {
        /**
         * Function that restarts game
         */
        this.playerIdx = -1;
        this.squaresPlayed = 0;
        // Create Tic Tac Toe subview, and set it into the parent layout's center position, replacing the config menu
        this.board = createBoard();
        this.gameLayout.setCenter(this.board);
        // Start the game. After this, game continues with button board button clicks.
        play();
    }


    private void loadGame() throws IOException {
        /**
         * Function that load a game from a backup file using database class
         */
        this.squaresPlayed = 0;
        ArrayList<String> namesAndMarks = data.getPlayersNameAndMarks();
        int player1turn = data.getPlayerTurn(namesAndMarks.get(0));
        int player2turn = data.getPlayerTurn(namesAndMarks.get(2));
        if(player2turn > player1turn)
            this.playerIdx = 1;
        else
            this.playerIdx = 0;
        System.out.println(player1turn +"   "+ namesAndMarks.get(0) + "  " + player2turn + "    " + namesAndMarks.get(2));
        this.players.add(
                this.playerFactory.getPlayer(data.getPlayerType(namesAndMarks.get(0)),
                        namesAndMarks.get(0), namesAndMarks.get(1), player1turn));
        this.players.add(
                this.playerFactory.getPlayer(data.getPlayerType(namesAndMarks.get(2)),
                        namesAndMarks.get(2), namesAndMarks.get(3), player2turn));


        // Create Tic Tac Toe subview, and set it into the parent layout's center position, replacing the config
        // menu.
        this.board = loadBoard();
        this.gameLayout.setCenter(this.board);
        // Start the game. After this, game progress progresses with button board button clicks.
        play();
    }



    private void saveGame() throws IOException {
        /**
         * Function that save state of a game using database class, saves board moves and players info
         */
        for(int i = 0; i<3; i++)
            for(int j =0; j<3; j++) {
                if(!Objects.equals(((Button)this.gridPaneArray[i][j]).getText(), "   ")) {
                    //System.out.println(((Button)this.gridPaneArray[i][j]).getText());
                    //System.out.println(this.getPlayerNameByMarker(((Button)this.gridPaneArray[i][j]).getText()));
                    data.saveGame(this.getPlayerNameByMarker(((Button)this.gridPaneArray[i][j]).getText()),
                            ((Button)this.gridPaneArray[i][j]).getText(), i, j);
                }
            }
        data.saveGameToFile();
    }

    public String getPlayerNameByMarker(String marker){
        /**
         * Finds a player in players list by marker
         */
        for (Player player:players) {
            if (player.getMarker().equals(marker))
                return player.getName();
        }
        return null;
    }

    public Node[][] getBoard() {
        return this.gridPaneArray;
    }

    private void play() {
        /**
         * Function to start the game by choosing a player to take a turn
         */
        Player p = this.nextPlayer();
        this.lblState.setText(String.format("Waiting for player %s to move...", p.getName()));
        p.takeTurn(this);
        // Keeping track of plays, so we can tell when the board is filled
        this.squaresPlayed++;
    }

    private Player nextPlayer() {
        /**
         * Function to track players turn
         */
        this.playerIdx++;
        return this.players.get(this.playerIdx %= this.players.size());
    }

    public ArrayList<Player> getPlayers() {
        /**
         * Return game players
         */
        return this.players;
    }

    public boolean checkTie() {
        /**
         * Check for a tie by comparing sqaures played with total number of squares
         */
        return this.squaresPlayed == this.squaresSquared;
    }

    public boolean checkWinner(int r, int c, String marker) {
        /**
         * Check if player by mark won the game
         */
        int col, row, diag, rdiag = diag = row = col = 0;
        for(int i=0; i<this.squares; i++) {
            if(((Button)this.gridPaneArray[r][i]).getText().equals(marker)) row++;
            if(((Button)this.gridPaneArray[i][c]).getText().equals(marker)) col++;
            if(((Button)this.gridPaneArray[i][i]).getText().equals(marker)) diag++;
            if(((Button)this.gridPaneArray[i][this.squares - (i + 1)]).getText().equals(marker)) rdiag++;
        }
        return row == this.squares || col == this.squares || diag == this.squares || rdiag == this.squares;
    }

    private void announceWinner(Player current) {
        /**
         *Function that pop out a window to announce the winner and starts the game over on close
         */
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        if( current == null) { // implies a tie
            a.setTitle("We have a tie!!!");
            a.setContentText("Sorry no winner this time.");
        } else {
            a.setTitle("We have a winner!!!");
            a.setContentText(String.format("and the winner is %s", current.getName()));
        }
        a.setOnCloseRequest((dialogEvent -> {this.startOver();}));
        a.show();
    }

    private GridPane createBoard() {
        /**
         * Function to create the game board that will pot out when starting the game
         */
        GridPane board = new GridPane();
        for(int r=0; r<this.squares; r++) {
            // Set this row size constraint
            RowConstraints rc = new RowConstraints();
            rc.setFillHeight(true);
            rc.setVgrow(Priority.ALWAYS);
            board.getRowConstraints().add(rc);

            for(int c=0; c<this.squares; c++) {
                // Set this column size constraints, but just once
                if( r == 0) {
                    ColumnConstraints cc = new ColumnConstraints();
                    cc.setFillWidth(true);
                    cc.setHgrow(Priority.ALWAYS);
                    board.getColumnConstraints().add(cc);
                }
                // Add Button to row column location.
                BoardButton square = new BoardButton(c, r, "   ");
                square.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                square.setOnAction((event) -> {
                    // If this button is already marked give an illegal move notice.
                    if(square.isAvailable()) {
                        // Mark this board button with the current players marker
                        Player current = this.players.get(this.playerIdx);
                        square.setText(current.getMarker());
                        // TODO: Check for winner here, and announce the winner before going to next player if there is one.
                        if(checkTie()) {
                            this.announceWinner(null);
                        } else if (checkWinner(square.getRow(), square.getCol(), current.getMarker())) {
                            this.announceWinner(current);
                        } else {
                            // continue playing if no winner is found.
                            play();
                        }
                    } else {
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a. setTitle("Illegal Move!");
                        a.setContentText("That square is already occupied.");
                        a.show();
                    }
                });
                board.add(square, c, r);
            }
        }
        board.setAlignment(Pos.CENTER);
        initializeGridPaneArray(board);
        return board;
    }

    private void initializeGridPaneArray(GridPane board) {
        /**
         * Function to add squares to a Node for easier access
         */
        this.gridPaneArray = new Node[this.squares][this.squares];
        for(Node node : board.getChildren())
        {
            this.gridPaneArray[GridPane.getRowIndex(node)][GridPane.getColumnIndex(node)] = node;
        }
    }

    private GridPane loadBoard() throws IOException {
        /**
         * Function that loads game board from backup
         */
        GridPane board = new GridPane();
        for (int r = 0; r < this.squares; r++) {
            // Set this row size constraint
            RowConstraints rc = new RowConstraints();
            rc.setFillHeight(true);
            rc.setVgrow(Priority.ALWAYS);
            board.getRowConstraints().add(rc);
            for (int c = 0; c < this.squares; c++) {
                // Set this column size constraints, but just once
                if (r == 0) {
                    ColumnConstraints cc = new ColumnConstraints();
                    cc.setFillWidth(true);
                    cc.setHgrow(Priority.ALWAYS);
                    board.getColumnConstraints().add(cc);
                }
                // Add Button to row column location.
                BoardButton square = new BoardButton(c, r, "   ");
                square.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                if(data.containsMove(r,c)){
                    //System.out.println(r + "  " + c);
                    square.setText(data.getPlayerMark(r,c));
                    this.squaresPlayed++;
                }
                square.setOnAction((event) -> {
                    // If this button is already marked give an illegal move notice.
                    if (square.isAvailable()) {
                        // Mark this board button with the current players marker
                        Player current = this.players.get(this.playerIdx);
                        square.setText(current.getMarker());
                        // TODO: Check for winner here, and announce the winner before going to next player if there is one.
                        if (checkTie()) {
                            this.announceWinner(null);
                        } else if (checkWinner(square.getRow(), square.getCol(), current.getMarker())) {
                            this.announceWinner(current);
                        } else {
                            // continue playing if no winner is found.
                            play();
                        }
                    } else {
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setTitle("Illegal Move!");
                        a.setContentText("That square is already occupied.");
                        a.show();
                    }
                });
                board.add(square, c, r);
            }
        }
        board.setAlignment(Pos.CENTER);
        initializeGridPaneArray(board);

        return board;
    }
}