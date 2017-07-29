package com.kishlaly.tests.uno.utils;

import com.kishlaly.tests.uno.engine.Player;
import com.kishlaly.tests.uno.engine.Status;

/**
 * @author Vladimir Kishlaly
 * @since 29.07.2017
 */
public class DeckUtils {

    /**
     * Check whether deck has enough cards. Refresh otherwise.
     *
     * @param deck    the current game deck
     * @param players all participants
     * @param status  the game status
     */
    public static void checkDesk(String[][] deck, Player[] players, Status status) {
        int count = 0;
        for (int i = 0; i < deck.length; i++) {
            count += deck[i][Constants.USAGE_INDEX].equals(Constants.TRUE) ? 1 : 0;
            if (count > 10) {
                break;
            }
        }
        if (count <= 10) {
            for (int i = 0; i < deck.length; i++) {
                deck[i][Constants.USAGE_INDEX] = Constants.FALSE;
            }
            deck[status.getTopCard()][Constants.USAGE_INDEX] = Constants.FALSE;
            for (int i = 0; i < players.length; i++) {
                for (int j = 0; j < players[i].getHandSize(); j++) {
                    deck[players[i].getHandValue(j)][Constants.USAGE_INDEX] = Constants.TRUE;
                }
            }
        }
    }

    public static String showCardInfo(String[][] deck, int cardNum) {
        switch (deck[cardNum][Constants.TYPE_INDEX]) {
            case Constants.WILD_DRAW_FOUR:
                return "Wild Draw Four";
            case Constants.WILD_CARD:
                return "Wild";
            case Constants.NUM:
                return deck[cardNum][Constants.NUM_INDEX] + " of " + deck[cardNum][Constants.COLOR_INDEX];
            case Constants.DRAW:
                return "Draw Two of " + deck[cardNum][Constants.COLOR_INDEX];
            case Constants.SKIP:
                return "Skip of " + deck[cardNum][Constants.COLOR_INDEX];
            case Constants.REVERSE:
                return "Reverse of " + deck[cardNum][Constants.COLOR_INDEX];
            default:
                return "";
        }
    }
}
