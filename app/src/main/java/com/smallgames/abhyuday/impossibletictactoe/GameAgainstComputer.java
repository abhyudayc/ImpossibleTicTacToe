package com.smallgames.abhyuday.impossibletictactoe;

import java.util.ArrayList;

public class GameAgainstComputer {

    private ArrayList<String> winningSetsArrayList = new ArrayList<>();

    public enum sign {x, o, _blank_}

    private sign[] board = {null, sign._blank_, sign._blank_, sign._blank_, sign._blank_, sign._blank_, sign._blank_, sign._blank_, sign._blank_, sign._blank_};
    private sign userSign, computerSign;
    private String currentUserCells = "", currentComputerCells = "", winner = "none", winningSet = "";
    private int emptySpaces;

    public GameAgainstComputer() {

        buildWinningSetsArrayList();
        emptySpaces = board.length - 1;
    }

    public void setSigns (sign userSign, sign computerSign) {
        this.userSign = userSign;
        this.computerSign = computerSign;
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


    public void markUserChoice(int userChoice) {
        board[userChosenCell(userChoice)] = userSign;
        emptySpaces--;
    }

    public int markComputerChoice() {
        int a = computerChosenCell();
        board[a] = computerSign;
        emptySpaces--;
        System.out.println(a);
        return a;
    }

    private int computerChosenCell() {

        int computerChoice = 0;

        computerChoice = tryComputerWinNow();

        if (computerChoice != -1) {
            currentComputerCells = currentComputerCells.concat(computerChoice + "");
			System.out.println("computer winning!!");
            return computerChoice;
        }

        computerChoice = stopUserWinNow();

        if (computerChoice != -1) {
            currentComputerCells = currentComputerCells.concat(computerChoice + "");
			System.out.println("user winning stopped!!");
            return computerChoice;
        }

        computerChoice = putAt5or1orElse();

        if (computerChoice != -1) {
            currentComputerCells = currentComputerCells.concat(computerChoice + "");
			System.out.println("generically putting");
            return computerChoice;
        }

        System.out.println("This should not get printed" +computerChoice);

        computerChoice = 0;
        return computerChoice;
    }

    private int tryComputerWinNow() {

        if (currentComputerCells.length() <= 1)
            return -1;

        for (int i = 0; i < winningSetsArrayList.size(); i++) {
            for (int x = 0; x < currentComputerCells.length(); x++) {
                for (int y = x + 1; y < currentComputerCells.length(); y++) {
                    if (winningSetsArrayList.get(i).contains(currentComputerCells.substring(x, x + 1)) && winningSetsArrayList.get(i).contains(currentComputerCells.substring(y, y + 1))) {
                        int j = 0;
                        try {
                            while(board[Integer.parseInt(winningSetsArrayList.get(i).substring(j, ++j))] != sign._blank_);
                            return Integer.parseInt(winningSetsArrayList.get(i).substring(--j, ++j));
                        } catch (Exception e) {
                            break;
                        }
                    }
                }
            }
        }

        return -1;
    }

    private int stopUserWinNow() {

        if (currentUserCells.length() <= 1)
            return -1;

        for (int i = 0; i < winningSetsArrayList.size(); i++) {

            for (int x = 0; x < currentUserCells.length(); x++) {
                for (int y = x + 1; y < currentUserCells.length(); y++) {
                    if (winningSetsArrayList.get(i).contains(currentUserCells.substring(x, x + 1)) && winningSetsArrayList.get(i).contains(currentUserCells.substring(y, y + 1))) {
                        int j = 0;
                        try {
                            while(board[Integer.parseInt(winningSetsArrayList.get(i).substring(j, ++j))] != sign._blank_);
                            int returnCell = Integer.parseInt(winningSetsArrayList.get(i).substring(--j, ++j));
                            winningSetsArrayList.remove(i);
                            return returnCell;
                        } catch (Exception e) {
                            break;
                        }
                    }
                }
            }
        }

        return -1;
    }

    private int putAt5or1orElse() {

        if (currentUserCells.length() <= 1) {
            if (currentUserCells.equals("1"))
                return 5;
            if (currentUserCells.equals("9"))
                return 5;
            if (currentUserCells.equals("3"))
                return 5;
            if (currentUserCells.equals("7"))
                return 5;
            if (currentUserCells.equals("5"))
                return 1;
            if (board[5] == sign._blank_)
                return 5;
        }
        else {
            if (board[5] == sign._blank_)
                return 5;
            if (currentUserCells.substring(currentUserCells.length() - 1).equals("1") || currentUserCells.substring(currentUserCells.length() - 1).equals("3") || currentUserCells.substring(currentUserCells.length() - 1).equals("7") || currentUserCells.substring(currentUserCells.length() - 1).equals("9")) {

                if (currentUserCells.substring(currentUserCells.length() - 2, currentUserCells.length() - 1).equals("2") || currentUserCells.substring(currentUserCells.length() - 2, currentUserCells.length() - 1).equals("8") ) {
                    if (currentUserCells.substring(currentUserCells.length() - 1).equals("1") && board[7] == sign._blank_)
                        return 7;
                    if (currentUserCells.substring(currentUserCells.length() - 1).equals("7") && board[1] == sign._blank_)
                        return 1;
                    if (currentUserCells.substring(currentUserCells.length() - 1).equals("9") && board[3] == sign._blank_)
                        return 3;
                    if (currentUserCells.substring(currentUserCells.length() - 1).equals("3") && board[9] == sign._blank_)
                        return 9;
                } else if (currentUserCells.substring(currentUserCells.length() - 2, currentUserCells.length() - 1).equals("4") || currentUserCells.substring(currentUserCells.length() - 2, currentUserCells.length() - 1).equals("6") ) {
                    if (currentUserCells.substring(currentUserCells.length() - 1).equals("1") && board[3] == sign._blank_)
                        return 3;
                    if (currentUserCells.substring(currentUserCells.length() - 1).equals("3") && board[1] == sign._blank_)
                        return 1;
                    if (currentUserCells.substring(currentUserCells.length() - 1).equals("9") && board[7] == sign._blank_)
                        return 7;
                    if (currentUserCells.substring(currentUserCells.length() - 1).equals("7") && board[9] == sign._blank_)
                        return 9;
                } else if (currentUserCells.substring(currentUserCells.length() - 2, currentUserCells.length() - 1).equals("1") || currentUserCells.substring(currentUserCells.length() - 2, currentUserCells.length() - 1).equals("3") || currentUserCells.substring(currentUserCells.length() - 2, currentUserCells.length() - 1).equals("7") || currentUserCells.substring(currentUserCells.length() - 2, currentUserCells.length() - 1).equals("9")) {
                    if (currentUserCells.substring(currentUserCells.length() - 1).equals("1") && board[2] == sign._blank_)
                        return 2;
                    if (currentUserCells.substring(currentUserCells.length() - 1).equals("3") && board[2] == sign._blank_)
                        return 2;
                    if (currentUserCells.substring(currentUserCells.length() - 1).equals("9") && board[8] == sign._blank_)
                        return 8;
                    if (currentUserCells.substring(currentUserCells.length() - 1).equals("7") && board[8] == sign._blank_)
                        return 8;
                }
            } else if (currentUserCells.substring(currentUserCells.length() - 1).equals("8") || currentUserCells.substring(currentUserCells.length() - 1).equals("6")) {
                if (currentUserCells.substring(currentUserCells.length() - 1).equals("8")) {
                    if (currentUserCells.substring(currentUserCells.length() - 2, currentUserCells.length() - 1).equals("1") && board[9] == sign._blank_)
                        return 9;
                    if (currentUserCells.substring(currentUserCells.length() - 2, currentUserCells.length() - 1).equals("3") && board[7] == sign._blank_)
                        return 7;
                    if (currentUserCells.substring(currentUserCells.length() - 2, currentUserCells.length() - 1).equals("6") && board[3] == sign._blank_)
                        return 3;
                } else if (currentUserCells.substring(currentUserCells.length() - 1).equals("6")) {
                    if (currentUserCells.substring(currentUserCells.length() - 2, currentUserCells.length() - 1).equals("7") && board[3] == sign._blank_)
                        return 3;
                    if (currentUserCells.substring(currentUserCells.length() - 2, currentUserCells.length() - 1).equals("8") && board[3] == sign._blank_)
                        return 3;
                }
            }
            if (board[1] == sign._blank_)
                return 1;
            if (board[3] == sign._blank_)
                return 3;
            if (board[7] == sign._blank_)
                return 7;
            if (board[9] == sign._blank_)
                return 9;
            for (int i = 1; i < board.length; i++)
                if (board[i] == sign._blank_)
                    return i;
        }
        return -1;
    }

    private int userChosenCell(int userChoice) throws NumberFormatException {
        currentUserCells = currentUserCells.concat(userChoice + "");
        return userChoice;
    }

    public boolean isWinningSetFormed() {
        for (int i = 0; i < winningSetsArrayList.size(); i++) {

            if (currentUserCells.contains(winningSetsArrayList.get(i).substring(0, 1)) && currentUserCells.contains(winningSetsArrayList.get(i).substring(1, 2)) && currentUserCells.contains(winningSetsArrayList.get(i).substring(2, 3))) {
                winner = "user";
                winningSet = winningSetsArrayList.get(i);
                return true;
            } else if (currentComputerCells.contains(winningSetsArrayList.get(i).substring(0, 1)) && currentComputerCells.contains(winningSetsArrayList.get(i).substring(1, 2)) && currentComputerCells.contains(winningSetsArrayList.get(i).substring(2, 3))) {
                winner = "computer";
                winningSet = winningSetsArrayList.get(i);
                return true;
            }
        }
        return false;
    }

    public String getWinningSet() { return winningSet;}

    public String getWinner() { return winner;  }

    public int getEmptySpaces() {return emptySpaces;}

    public String getFilledCells() { return currentUserCells + currentComputerCells; }

}
