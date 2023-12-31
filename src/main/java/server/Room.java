package server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class Room {
    private final String roomName;
    private ClientHandler host;
    private ClientHandler player2;
    private ClientHandler whoToPlay;
    private int[][] hostBoard, player2Board;
    private CountDownLatch latchPlacingPhase, latchRoomPhase;
    private boolean gameOver;
    private ArrayList<ArrayList<String>> hostArrayList, player2ArrayList;

    public Room(String roomName, ClientHandler host) {
        this.roomName = roomName;
        this.host = host;
        this.player2 = null;
        this.whoToPlay = host;
        this.hostBoard = new int[10][10];
        this.player2Board = new int[10][10];
        this.gameOver = false;

        this.latchPlacingPhase = new CountDownLatch(2);
        this.latchRoomPhase = new CountDownLatch(1);

        for (int i = 0; i < hostBoard.length; i++) {
            Arrays.fill(hostBoard[i], 0);
            Arrays.fill(player2Board[i], 0);
        }
    }

    public void setHostArrayList(ArrayList<ArrayList<String>> hostArrayList) {
        this.hostArrayList = hostArrayList;
    }

    public void setPlayer2ArrayList(ArrayList<ArrayList<String>> player2ArrayList) {
        this.player2ArrayList = player2ArrayList;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public ClientHandler getPlayerWhoDoesntPlay(){
        if(whoToPlay != host){
            return host;
        }
        else{
            return player2;
        }
    }


    public ArrayList<ArrayList<String>> getArrayBasedOnPlayerWhoDoesntPlay() {
        if(whoToPlay != host){
            return hostArrayList;
        }
        else{
            return player2ArrayList;
        }
    }

    public String getRoomName() {
        return roomName;
    }

    public ClientHandler getHost() {
        return host;
    }

    public ClientHandler getPlayer2() {
        return player2;
    }

    public void addPlayer2(ClientHandler player) {
        this.player2 = player;
    }

    public ClientHandler getWhoToPlay() {
        return whoToPlay;
    }

    public void setWhoToPlay(ClientHandler whoToPlay) {
        this.whoToPlay = whoToPlay;
    }

    public CountDownLatch getLatchPlacingPhase() {
        return latchPlacingPhase;
    }

    public CountDownLatch getLatchRoomPhase() {
        return latchRoomPhase;
    }

    @Override
    public String toString() {
        return "Room: " + roomName;
    }
}
