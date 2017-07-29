package com.kishlaly.tests.uno.engine;

import com.kishlaly.tests.uno.utils.Constants;
import com.kishlaly.tests.uno.utils.DeckUtils;

import java.util.ArrayList;

/**
 * Class of Player to hold player's information
 */
public class Player {

    private String id;
    private String displayName;
    private ArrayList<Integer> hand = new ArrayList<>();

    public Player(String id, String displayName) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ArrayList<Integer> getHand() {
        return hand;
    }

    public String getId() {
        return id;
    }

    public int getHandSize() {
        return hand.size();
    }

    public int getHandValue(int id) {
        return this.hand.get(id);
    }

    public int getHandCardNumber(int num) {
        return this.hand.get(num);
    }

    public void init(ArrayList<Integer> cards) {
        for (int i = 0; i < cards.size(); i++) {
            this.hand.add(cards.get(i));
        }
    }

    public void showHand(String[][] deck) {
        if (id.equals(Constants.ME)) {
            System.out.println("You have " + getHandSize() + " cards :");
            for (int i = 0; i < getHandSize(); i++) {
                System.out.println("\t" + DeckUtils.showCardInfo(deck, this.getHandCardNumber(i)));
            }
            System.out.println(Constants.DELIMITER);
        }
    }

}