package com.kishlaly.tests.uno.engine;

import com.kishlaly.tests.uno.utils.Constants;
import com.kishlaly.tests.uno.utils.DeckUtils;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Vladimir Kishlaly
 * @since 29.07.2017
 */
public class PlayersLogic {

    private final Player[] players;

    public PlayersLogic(Player[] players) {
        this.players = players;
    }

    public int[] drawCards(int playerID, String[][] deck, int cardsToPick, Status status) {
        Random random = new Random();
        Player player = players[playerID];
        ArrayList<Integer> cards = new ArrayList<>();
        int cardIndex;
        for (int j = 0; j < cardsToPick; j++) {
            do {
                DeckUtils.checkDesk(deck, players, status);
                cardIndex = random.nextInt(108);
            } while (deck[cardIndex][Constants.USAGE_INDEX].equals(Constants.TRUE));
            player.getHand().add(cardIndex);
            cards.add(cardIndex);
            deck[cardIndex][Constants.USAGE_INDEX] = Constants.TRUE;
        }

        int[] result = new int[cards.size()];
        for (int i = 0; i < cards.size(); i++) {
            result[i] = cards.get(i);
        }
        return result;
    }

    public void putACard(int playerIndex, int cardIndex, String[][] deck, Status status) {
        Player player = players[playerIndex];
        System.out.println(player.getId() + " put " + DeckUtils.showCardInfo(deck, cardIndex));
        ArrayList<Integer> hand = player.getHand();
        for (int i = 0; i < hand.size(); i++) {
            if (cardIndex == hand.get(i)) {
                hand.remove(i);
                break;
            }
        }
        switch (deck[cardIndex][Constants.TYPE_INDEX]) {
            case Constants.WILD_DRAW_FOUR:
                status.addDraw(4);
            case Constants.WILD_CARD:
                status.setTopCard(cardIndex);
                status.setCardType(deck[cardIndex][Constants.TYPE_INDEX]);
                status.setColor(findNextColor(playerIndex, deck));
                status.setCardToSelect(deck[cardIndex][Constants.NUM_INDEX]);
                break;
            case Constants.DRAW:
                status.addDraw(2);
            case Constants.NUM:
                status.setTopCard(cardIndex);
                status.setCardType(deck[cardIndex][Constants.TYPE_INDEX]);
                status.setColor(deck[cardIndex][Constants.COLOR_INDEX]);
                status.setCardToSelect(deck[cardIndex][Constants.NUM_INDEX]);
                break;
            case Constants.SKIP:
                status.addSkip(2);
                status.setTopCard(cardIndex);
                status.setCardType(deck[cardIndex][Constants.TYPE_INDEX]);
                status.setColor(deck[cardIndex][Constants.COLOR_INDEX]);
                status.setCardToSelect(deck[cardIndex][Constants.NUM_INDEX]);
                break;
            case Constants.REVERSE:
                status.reverseOrder();
                status.setTopCard(cardIndex);
                status.setCardType(deck[cardIndex][Constants.TYPE_INDEX]);
                status.setColor(deck[cardIndex][Constants.COLOR_INDEX]);
                status.setCardToSelect(deck[cardIndex][Constants.NUM_INDEX]);
                break;
        }
    }

    public int[] getAryOfPossibleCards(int playerIndex, String[][] deck, Status status) {
        ArrayList<Integer> list = new ArrayList<>();
        Player player = players[playerIndex];
        ArrayList<Integer> hand = player.getHand();
        String currentType = status.getType();
        String currentIndex = status.getNum();
        String currentColor = status.getColor();

        for (int i = 0; i < hand.size(); i++) {
            int fromHand = hand.get(i);
            if (status.getDraw() > 0) {
                if (DeckLogic.isSameType(deck, fromHand, Constants.WILD_DRAW_FOUR)) {
                    list.add(fromHand);
                } else if (DeckLogic.isSameType(deck, fromHand, Constants.DRAW) && DeckLogic.isSameColor(deck, fromHand, currentColor)) {
                    list.add(fromHand);
                }
            } else {
                if (DeckLogic.isSameType(deck, fromHand, Constants.WILD_DRAW_FOUR) || DeckLogic.isSameType(deck, fromHand, Constants.WILD_CARD)) {
                    list.add(fromHand);
                } else if (DeckLogic.isSameType(deck, fromHand, currentType) && DeckLogic.isSameSymbol(deck, fromHand, currentIndex)) {
                    list.add(fromHand);
                } else if (DeckLogic.isSameColor(deck, fromHand, currentColor)) {
                    list.add(fromHand);
                }
            }
        }
        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    public void selectRandom(int playerIndex, Status status, String[][] deck, int[] possibleCards) {
        putACard(playerIndex, possibleCards[new Random().nextInt(possibleCards.length)], deck, status);
    }

    public int[] tryToSelectAgain(int playerIndex, String[][] deck, Status status) {
        Player player = players[playerIndex];
        ArrayList<Integer> list = new ArrayList<>();
        ArrayList<Integer> hand = player.getHand();
        int topCard = status.getTopCard();
        String color = status.getColor();

        for (int i = 0; i < hand.size(); i++) {
            int formHand = hand.get(i);
            boolean isMatch = false;
            if (DeckLogic.isSameCard(deck, topCard, formHand) && DeckLogic.isSameSymbol(deck, topCard, formHand)) {
                isMatch = true;
            }
            if (DeckLogic.isSameType(deck, topCard, Constants.WILD_DRAW_FOUR)
                    && DeckLogic.isSameColor(deck, formHand, color)
                    && deck[formHand][Constants.TYPE_INDEX].equals(Constants.DRAW)) {
                isMatch = true;
            }
            if (isMatch) {
                list.add(formHand);
            }
        }
        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    private String findNextColor(int playerIndex, String[][] deck) {
        Player player = players[playerIndex];
        if (player.getId() == Constants.ME) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("You put a wild card. \nSelect the color: \"red\", \"blue\", \"yellow\", or \"green\": ");
            String newColor = scanner.nextLine();
            while ((!newColor.equals(Constants.RED))
                    && (!newColor.equals(Constants.BLUE))
                    && (!newColor.equals(Constants.YELLOW))
                    && (!newColor.equals(Constants.GREEN))) {
                System.out.print("Please, input a color from the list. ");
                newColor = scanner.nextLine();
            }
            return newColor;
        } else {
            int reds = 0, blues = 0, yellows = 0, greens = 0;
            for (int i = 0; i < player.getHandSize(); i++) {
                switch (deck[player.getHand().get(i)][Constants.COLOR_INDEX]) {
                    case Constants.RED:
                        reds++;
                        break;
                    case Constants.BLUE:
                        blues++;
                        break;
                    case Constants.YELLOW:
                        yellows++;
                        break;
                    case Constants.GREEN:
                        greens++;
                        break;
                }
            }
            int max = Math.max(reds, blues) > Math.max(yellows, greens) ? Math.max(reds, blues) : Math.max(yellows, greens);
            return max == reds ? Constants.RED : max == blues ? Constants.BLUE : max == yellows ? Constants.YELLOW : Constants.GREEN;
        }
    }

    public int ifWin(int id, int num, int max) {
        Player player = players[id];
        return (num + player.getHandSize()) > max ? id : -1;
    }

}
