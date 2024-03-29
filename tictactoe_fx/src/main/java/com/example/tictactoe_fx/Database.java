package com.example.tictactoe_fx;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Database implements Serializable {
    private static final String filepath = "C:\\Users\\alexm\\OneDrive\\Desktop\\tictactoe\\PA-TicTacToe\\tictactoe_fx\\src\\main\\java\\com\\example\\tictactoe_fx\\db_files\\tictactoe.json";
    private Map<String, ArrayList<String>> boardInfo;

//    private List<Player> players = new ArrayList<>();
//
//    public void addPlayers(ArrayList<Player> players){
//        this.players = players;
//    }
    public Database(Map<String, ArrayList<String>> boardInfo) {
        this.boardInfo = boardInfo;
    }
    public Database() {
        this.boardInfo = new HashMap<>();
    }

    public Map<String, ArrayList<String>> getBoardInfo() {
        return boardInfo;
    }

    public void setBoardInfo(Map<String, ArrayList<String>> boardInfo) {
        this.boardInfo = boardInfo;
    }

    public void saveGame(String name, String marker, int row, int col){
        ArrayList<String> playerInfo = new ArrayList<String>();
        playerInfo.add(marker);
        playerInfo.add(name);
        boardInfo.put(Integer.toString(row) + Integer.toString(col), playerInfo);

    }

    public void savePlayerType(String name, PlayerFactory.PlayerTypes type){
        ArrayList<String> playerInfo = new ArrayList<String>();
        playerInfo.add(String.valueOf(type));
        boardInfo.put(name, playerInfo);
    }

    public PlayerFactory.PlayerTypes getPlayerType(String name){
        if(this.boardInfo.get(name).get(0) == "Computer")
            return PlayerFactory.PlayerTypes.COMPUTER;
        else return PlayerFactory.PlayerTypes.SENTIENT;
    }

    public void saveGameToFile() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(
                new File(filepath), this);
    }

    public Database uploadGameFromFile(Database database) throws IOException {
        //return database;

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(
                new File(filepath),
                Database.class);

    }

    public boolean containsMove(int row, int col){
        return this.boardInfo.containsKey(Integer.toString(row) + Integer.toString(col));
    }

    public String getPlayerName(int row, int col){
//        ArrayList<String> info = new ArrayList<>();
//        info.add(Integer.toString(row));
//        info.add(Integer.toString(col));
        return this.boardInfo.get(Integer.toString(row) + Integer.toString(col)).get(1);
    }
    
    public ArrayList<String> getPlayersNameAndMarks(){
        ArrayList<String> names = new ArrayList<>();
        String name1 = null;
        String name2 = null;
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++) {
                if(this.boardInfo.containsKey(Integer.toString(row) + Integer.toString(col))){
                    if(name1 == null){
                        name1 = this.boardInfo.get(Integer.toString(row) + Integer.toString(col)).get(1);
                        names.add(name1);
                        names.add(this.boardInfo.get(Integer.toString(row) + Integer.toString(col)).get(0));
                    }
                    else {
                        if(!name1.equals(this.boardInfo.get(Integer.toString(row) + Integer.toString(col)).get(1)) && name2 == null){
                            name2 = this.boardInfo.get(Integer.toString(row) + Integer.toString(col)).get(1);
                            names.add(name2);
                            names.add(this.boardInfo.get(Integer.toString(row) + Integer.toString(col)).get(0));
                            break;
                        }
                    }
                }
            }
        System.out.println(boardInfo);
        return names;
    }


    public Integer getPlayerTurn(String name){
        int turnPlayer2 = 0, turnPlayer1 = 0;

        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++) {
                if(this.boardInfo.containsKey(Integer.toString(row) + Integer.toString(col))){
                    //System.out.println(this.boardInfo.get(Integer.toString(row) + Integer.toString(col)).get(1));
                    if (name.equals(this.boardInfo.get(Integer.toString(row) + Integer.toString(col)).get(1)))
                        turnPlayer1 += 1;
                    else
                        turnPlayer2 +=1;
                    //System.out.println(turnPlayer1 + " " + turnPlayer2);
                }
            }
        if(turnPlayer1 > turnPlayer2)
            return 1;
        else
            return 0;
    }

    public String getPlayerMark(int row, int col){
//        ArrayList<String> info = new ArrayList<>();
//        info.add(Integer.toString(row));
//        info.add(Integer.toString(col));
        return this.boardInfo.get(Integer.toString(row) + Integer.toString(col)).get(0);
    }

}
