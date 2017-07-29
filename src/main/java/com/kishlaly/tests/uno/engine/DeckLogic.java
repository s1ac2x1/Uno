package com.kishlaly.tests.uno.engine;

import com.kishlaly.tests.uno.utils.Constants;

/**
 * @author Vladimir Kishlaly
 * @since 29.07.2017
 */
public class DeckLogic {

    public static boolean isSameCard(String[][] deck, int firstCardIndex, int secondCardIndex) {
        return deck[firstCardIndex][Constants.TYPE_INDEX].equals(deck[secondCardIndex][Constants.TYPE_INDEX]);
    }

    public static boolean isSameType(String[][] deck, int firstCardIndex, String secondCardType) {
        return deck[firstCardIndex][Constants.TYPE_INDEX].equals(secondCardType);
    }

    public static boolean isSameColor(String[][] deck, int firstCardIndex, String secondCardColor) {
        return deck[firstCardIndex][Constants.COLOR_INDEX].equals(secondCardColor);
    }

    public static boolean isSameSymbol(String[][] deck, int firstCardIndex, int secondCardIndex) {
        return deck[firstCardIndex][Constants.NUM_INDEX].equals(deck[secondCardIndex][Constants.NUM_INDEX]);
    }

    public static boolean isSameSymbol(String[][] deck, int firstCardIndex, String secondCardSymbol) {
        return deck[firstCardIndex][Constants.NUM_INDEX].equals(secondCardSymbol);
    }
}
