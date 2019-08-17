package com.smallgames.abhyuday.impossibletictactoe;

import java.util.ArrayList;

public class GameAgainstHuman {

    private ArrayList<String> winningSetsArrayList = new ArrayList<>();

    public enum sign {x, o, _blank_}

    private sign[] board = {null, sign._blank_, sign._blank_, sign._blank_, sign._blank_, sign._blank_, sign._blank_, sign._blank_, sign._blank_, sign._blank_};
    private sign player1Sign, player2Sign;
    private String currentPlayer1Cells = "", currentPlayer2Cells = "", winner = "none", winningSet = "";
    private int emptySpaces;

    public GameAgainstHuman() {

        buildWinningSetsArrayList();
        emptySpaces = board.length - 1;
    }

    public void setSigns (sign player1Sign, sign player2Sign) {
        this.player1Sign = player1Sign;
        this.player2Sign = player2Sign;
    }

    private void buildWinningSetsArrayList() {
        winningSetsArrayList.add("123");
        winningSetsArrayList.add("456");
        winningSetsArrayList.add("789");
        winningSetsArrayList.add("147");
        winningSetsArrayList.add("258");
        winningSetsArrayList.add("369");
        winningSetsArrayList.add("159");
        winningSetsArrayList.add("357");
    }

    public void markPlayer1Choice(int player1Choice) {
        board[player1ChosenCell(player1Choice)] = player1Sign;
        emptySpaces--;
    }

    private int player1ChosenCell(int player1Choice) throws NumberFormatException {
        currentPlayer1Cells = currentPlayer1Cells.concat(player1Choice + "");
        return player1Choice;
    }

    public void markPlayer2Choice(int player2Choice) {
        board[player2ChosenCell(player2Choice)] = player2Sign;
        emptySpaces--;
    }

    private int player2ChosenCell(int player2Choice) throws NumberFormatException {
        currentPlayer2Cells = currentPlayer2Cells.concat(player2Choice + "");
        return player2Choice;
    }

    public boolean isWinningSetFormed() {
        for (int i = 0; i < winningSetsArrayList.size(); i++) {

            if (currentPlayer1Cells.contains(winningSetsArrayList.get(i).substring(0, 1)) && currentPlayer1Cells.contains(winningSetsArrayList.get(i).substring(1, 2)) && currentPlayer1Cells.contains(winningSetsArrayList.get(i).substring(2, 3))) {
                winner = "player1";
                winningSet = winningSetsArrayList.get(i);
                return true;
            } else if (currentPlayer2Cells.contains(winningSetsArrayList.get(i).substring(0, 1)) && currentPlayer2Cells.contains(winningSetsArrayList.get(i).substring(1, 2)) && currentPlayer2Cells.contains(winningSetsArrayList.get(i).substring(2, 3))) {
                winner = "player2";
                winningSet = winningSetsArrayList.get(i);
                return true;
            }
        }
        return false;
    }

    public String getWinningSet() { return winningSet;}

    public String getWinner() { return winner;  }

    public int getEmptySpaces() {return emptySpaces;}

}
