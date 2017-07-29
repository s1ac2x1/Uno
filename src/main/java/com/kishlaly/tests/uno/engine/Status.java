package com.kishlaly.tests.uno.engine;

import com.kishlaly.tests.uno.utils.Constants;

import java.util.Random;

public class Status {

    private int topCard;
    private String cardType;
    private String color;
    private String number;
    private int draw = 0;
    private int skip = 0;
    private int[] orders;

    public Status(int topCard,
                  String cardType,
                  String color,
                  String number,
                  Player[] players) {
        this.topCard = topCard;
        this.cardType = cardType;
        this.color = color;
        this.number = number;
        this.orders = new int[players.length];
        int index = new Random().nextInt(players.length);
        for (int i = 0; i < players.length; i++, index++) {
            if (index < players.length) {
                this.orders[index] = i;
            } else {
                index = 0;
                this.orders[index] = i;
            }
        }
    }

    public int getTopCard() {
        return topCard;
    }

    public String getType() {
        return cardType;
    }

    public String getColor() {
        return color;
    }

    public String getNum() {
        return number;
    }

    public int getDraw() {
        return draw;
    }

    public int getSkip() {
        return skip;
    }

    public int getCurrentPlayerIndex() {
        return orders[Constants.TYPE_INDEX];
    }

    public int getPlayersNumber() {
        return orders.length;
    }

    public void setTopCard(int num) {
        this.topCard = num;
    }

    public void setCardType(String type) {
        this.cardType = type;
    }

    public void setColor(String c) {
        this.color = c;
    }

    public void setCardToSelect(String num) {
        this.number = num;
    }

    public void addDraw(int num) {
        draw += num;
    }

    public void addSkip(int num) {
        skip += num;
    }

    public void clearSkip() {
        skip = 0;
    }

    public void clearDraw() {
        draw = 0;
    }

    public void showCurrentGameStatus(String[][] deck) {
        System.out.println("------- Game status -------");
        System.out.println("Draws: \t" + draw);
        System.out.println("Color: \t" + color);
        System.out.print("Pile top:  ");
        switch (deck[topCard][Constants.TYPE_INDEX]) {
            case Constants.WILD_DRAW_FOUR:
                System.out.print("Wild Draw Four");
                break;
            case Constants.WILD_CARD:
                System.out.print("Wild");
                break;
            case Constants.NUM:
                System.out.print(deck[topCard][Constants.NUM_INDEX] + " of " + deck[topCard][Constants.COLOR_INDEX]);
                break;
            case Constants.DRAW:
                System.out.print("Draw Two of " + deck[topCard][Constants.COLOR_INDEX]);
                break;
            case Constants.SKIP:
                System.out.print("Skip of " + deck[topCard][Constants.COLOR_INDEX]);
                break;
            case Constants.REVERSE:
                System.out.print("Reverse of " + deck[topCard][Constants.COLOR_INDEX]);
                break;
        }
        System.out.println("\n" + Constants.DELIMITER);

    }

    public void nextPlayer() {
        int currentPlayer = orders[0];
        for (int i = 0; i < (getPlayersNumber() - 1); i++) {
            orders[i] = orders[i + 1];
        }
        orders[orders.length - 1] = currentPlayer;
    }

    public void reverseOrder() {
        int[] newAry = new int[orders.length];
        newAry[0] = orders[0];
        for (int i = 1; i < orders.length; i++) {
            newAry[newAry.length - i] = orders[i];
        }
        orders = newAry;
    }

}
