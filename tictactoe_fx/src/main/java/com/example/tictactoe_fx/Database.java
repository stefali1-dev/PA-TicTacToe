package com.example.tictactoe_fx;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database implements Serializable {
    private Map<ArrayList<String>, String> boardInfo;


    public Database(Map<ArrayList<String>, String> boardInfo) {
        this.boardInfo = boardInfo;
    }
    public Database() {
        this.boardInfo = new HashMap<>();
    }

    public Map<ArrayList<String>, String> getBoardInfo() {
        return boardInfo;
    }

    public void setBoardInfo(Map<ArrayList<String>, String> boardInfo) {
        this.boardInfo = boardInfo;
    }

    public void saveGame(String name, String marker, int row, int col){
        ArrayList info = new ArrayList<String>();
        info.add(marker);
        info.add(Integer.toString(row));
        info.add(Integer.toString(col));
        boardInfo.put(info, name);

    }

    public void saveGameToFile(Database database) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(
                new File("C:\\Users\\alexm\\OneDrive\\Desktop\\tictactoe\\PA-TicTacToe\\tictactoe_fx\\src\\main\\java\\com\\example\\tictactoe_fx\\db_files\\" + "tictactoe.json"), database);
    }
}
