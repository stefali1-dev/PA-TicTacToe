package com.example.tictactoe_fx;

public class PlayerFactory {
    public enum PlayerTypes {
        COMPUTER("Computer"),
        SENTIENT("Sentient");
        private final String label;
        PlayerTypes(String label) {
            this.label = label;
        }
        public String toString() {
            return label;
        }
    }
    public Player getPlayer(PlayerTypes playerType, String name, String marker, int turn){
        return switch (playerType) {
            case COMPUTER -> new Computer(name, marker, turn);
            case SENTIENT -> new Sentient(name, marker, turn);
        };
    }
}