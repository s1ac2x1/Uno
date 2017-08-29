package com.kishlaly.tests.uno;

import com.kishlaly.tests.uno.engine.Player;
import com.kishlaly.tests.uno.engine.PlayersLogic;
import com.kishlaly.tests.uno.engine.Status;
import com.kishlaly.tests.uno.utils.Constants;
import com.kishlaly.tests.uno.utils.DeckUtils;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Vladimir Kishlaly
 * @since 29.07.2017
 */
public class Game {

    private String userName;
    private int numberOfPlayers;
    private Player[] players;
    private String[][] deck;
    private Random random = new Random();
    private boolean stopped = false;
    private boolean anotherWin = false;
    private boolean anotherLost = false;

    public void start(String userName, int numberOfPlayers) {
        this.userName = userName;
        this.numberOfPlayers = numberOfPlayers;
        Scanner scanner = new Scanner(System.in);

        deck = createDeck();
        players = createPlayers();
        PlayersLogic playersLogic = new PlayersLogic(players);
        initialDistribution();

        int initialCard;
        do {
            initialCard = drawOneCard();
        } while (!(deck[initialCard][Constants.TYPE_INDEX].equals(Constants.NUM)));

        println("\nTop card is: " + DeckUtils.showCardInfo(deck, initialCard) + "\n");
        Status status = new Status(initialCard, deck[initialCard][Constants.TYPE_INDEX], deck[initialCard][Constants.COLOR_INDEX], deck[initialCard][Constants.NUM_INDEX], players);

        while (true) {
            int playerNumber = startDesc(status);

            if (status.getDraw() > 0) {
                int[] possibleCards = playersLogic.getAryOfPossibleCards(playerNumber, deck, status);
                if (possibleCards.length == 0) {
                    if (hasMoreCards(scanner, playersLogic, status, playerNumber)) break;
                } else {
                    if (playerNumber == 0) {
                        nextMove(scanner, playersLogic, status, playerNumber, possibleCards);
                    } else {
                        playersLogic.selectRandom(playerNumber, status, deck, possibleCards);
                        while (true) {
                            int[] secondaryPossibleCards = playersLogic.tryToSelectAgain(playerNumber, deck, status);
                            if (secondaryPossibleCards.length == 0) {
                                break;
                            }
                            playersLogic.selectRandom(playerNumber, status, deck, secondaryPossibleCards);
                        }
                    }
                }
            } else {
                int[] possibleCards = playersLogic.getAryOfPossibleCards(playerNumber, deck, status);

                if (possibleCards.length == 0) {
                    if (playerNumber == 0) {
                        print("You have nothing to put! You have to draw one card. Press enter.");
                        scanner.nextLine();
                        if (playersLogic.ifWin(playerNumber, status.getDraw(), Constants.MAX_HAND_SIZE) >= 0) {
                            println(players[playerNumber].getId() + " lost!");
                            break;
                        }
                        print("Drawing a new card ... ");
                        int[] drawnCAry = playersLogic.drawCards(playerNumber, deck, 1, status);
                        println("The new card is:\t" + DeckUtils.showCardInfo(deck, drawnCAry[Constants.TYPE_INDEX]));
                    } else {
                        println(players[playerNumber].getId() + " had nothing to put and select one card");
                        if (playersLogic.ifWin(playerNumber, status.getDraw(), Constants.MAX_HAND_SIZE) >= 0) {
                            println(players[playerNumber].getId() + " lost!");
                            break;
                        }
                        playersLogic.drawCards(playerNumber, deck, 1, status);
                    }
                } else {
                    if (playerNumber == 0) {
                        println("You have " + possibleCards.length + " card" + ((possibleCards.length == 0) ? "" : "s") + " to put!");
                        println("Please select the first card to put from following:");
                        for (int i = 0; i < possibleCards.length; i++) {
                            println("\t\t[" + i + "]  " + DeckUtils.showCardInfo(deck, possibleCards[i]));
                        }
                        print("Which card do you prefer to put? ");
                        int cardNumToPut;
                        do {
                            cardNumToPut = Integer.parseInt(scanner.nextLine());
                            if (cardNumToPut < 0 || cardNumToPut >= possibleCards.length) {
                                print("Wrong number. please retry: ");
                            }
                        } while (cardNumToPut < 0 || cardNumToPut >= possibleCards.length);
                        cardNumToPut = possibleCards[cardNumToPut];
                        playersLogic.putACard(playerNumber, cardNumToPut, deck, status);
                        whatToChoose(scanner, playersLogic, status, playerNumber);
                    } else {
                        playersLogic.selectRandom(playerNumber, status, deck, possibleCards);
                        while (true) {
                            int[] secondaryPossibleCards = playersLogic.tryToSelectAgain(playerNumber, deck, status);
                            if (secondaryPossibleCards.length == 0) {
                                break;
                            }
                            playersLogic.selectRandom(playerNumber, status, deck, secondaryPossibleCards);
                        }
                    }
                }
            }
            for (int i = 0; i < players.length; i++) {
                if (players[i].getHandSize() == 0) {
                    println(players[i].getId() + " has used all cards\n");
                    anotherWin = true;
                }
            }
            if (anotherWin) {
                break;
            }
            for (int i = 0; i < players.length; i++) {
                int burst = playersLogic.ifWin(i, status.getDraw(), Constants.MAX_HAND_SIZE);
                if (burst >= 0) {
                    println(players[burst].getId() + " lost!");
                    anotherLost = true;
                }
            }
            if (anotherLost) {
                break;
            }
            if (status.getSkip() > 0) {
                for (int i = 0; i < status.getSkip(); i++) {
                    status.nextPlayer();
                }
            } else {
                status.nextPlayer();
            }
            status.clearSkip();
            DeckUtils.checkDesk(deck, players, status);
        }

        String winner = players[Constants.TYPE_INDEX].getId();
        if (!stopped) {
            for (int i = 0, j = 1; Math.max(i, j) < players.length; ) {
                if (players[i].getHandSize() > players[j].getHandSize()) {
                    winner = players[j].getId();
                    i++;
                } else {
                    winner = players[i].getId();
                    j++;
                }


            }
            println(winner + " is the winner!");
        }

        if ((!stopped) && winner.equals(players[Constants.TYPE_INDEX].getId())) {
            println("\nYou won this game!\n");
        } else if (stopped) {
            println("\nYou quit the game");
        } else {
            println("\nYou lost!");
        }
    }

    private void whatToChoose(Scanner scanner, PlayersLogic playersLogic, Status status, int playerNumber) {
        while (true) {
            int[] secondaryPossibleCards = playersLogic.tryToSelectAgain(playerNumber, deck, status);
            if (secondaryPossibleCards.length == 0) {
                break;
            }
            players[playerNumber].showHand(deck);
            println("You have " + secondaryPossibleCards.length + " more card" + ((secondaryPossibleCards.length == 0) ? "" : "s") + " to put!");
            println("Please select the next card:");
            for (int i = 0; i < secondaryPossibleCards.length; i++) {
                println("\t\t[" + i + "]   " + DeckUtils.showCardInfo(deck, secondaryPossibleCards[i]));
            }
            println("\t\t[" + secondaryPossibleCards.length + "]  Quit. Put nothing.");
            print("Which card do you prefer to put? input a number inside of [] ");
            int secondaryCardNumToPut = -1;
            secondaryCardNumToPut = Integer.parseInt(scanner.nextLine());
            while (secondaryCardNumToPut < 0 || secondaryCardNumToPut > secondaryPossibleCards.length) {
                print("Wrong number. please retry: ");
                secondaryCardNumToPut = Integer.parseInt(scanner.nextLine());
            }
            if (secondaryPossibleCards.length != secondaryCardNumToPut) {
                secondaryCardNumToPut = secondaryPossibleCards[secondaryCardNumToPut];
                playersLogic.putACard(playerNumber, secondaryCardNumToPut, deck, status);
            } else {
                println("You quit to put.");
                break;
            }
        }
    }

    private void nextMove(Scanner scanner, PlayersLogic playersLogic, Status status, int playerNumber, int[] possibleCards) {
        print("Your have " + possibleCards.length + " card" + ((possibleCards.length == 0) ? "" : "s") + " to select");
        for (int i = 0; i < possibleCards.length; i++) {
            println("\t\t[" + i + "]  " + DeckUtils.showCardInfo(deck, possibleCards[i]));
        }
        print("Select the number ");
        int cardNumToPut = Integer.parseInt(scanner.nextLine());
        while (cardNumToPut < 0 || cardNumToPut >= possibleCards.length) {
            print("Wrong index ");
            Integer.parseInt(scanner.nextLine());
        }
        cardNumToPut = possibleCards[cardNumToPut];
        playersLogic.putACard(playerNumber, cardNumToPut, deck, status);
        makeATurn(scanner, playersLogic, status, playerNumber);
    }

    private boolean hasMoreCards(Scanner scanner, PlayersLogic playersLogic, Status status, int playerNumber) {
        if (playerNumber == 0) {
            if (emptyCards(scanner, playersLogic, status, playerNumber)) return true;
        } else {
            println(players[playerNumber].getId() + " had nothing to put and picked up " + status.getDraw() + " cards.");
            if (playersLogic.ifWin(playerNumber, status.getDraw(), Constants.MAX_HAND_SIZE) >= 0) {
                println(players[playerNumber].getId() + " lost!");
                return true;
            }
            playersLogic.drawCards(playerNumber, deck, status.getDraw(), status);
            status.clearDraw();
        }
        return false;
    }

    private void makeATurn(Scanner scanner, PlayersLogic playersLogic, Status status, int playerNumber) {
        while (true) {
            int[] secondaryPossibleCards = playersLogic.tryToSelectAgain(playerNumber, deck, status);
            if (secondaryPossibleCards.length == 0) {
                break;
            }
            players[playerNumber].showHand(deck);
            println("You have " + secondaryPossibleCards.length + " more card" + ((secondaryPossibleCards.length == 0) ? "" : "s"));
            for (int i = 0; i < secondaryPossibleCards.length; i++) {
                println("\t\t[" + i + "]   " + DeckUtils.showCardInfo(deck, secondaryPossibleCards[i]));
            }
            println("\t\t[" + secondaryPossibleCards.length + "]. Put nothing.");
            print("Select the number ");
            int secondaryCardNumToPut = Integer.parseInt(scanner.nextLine());
            while (secondaryCardNumToPut < 0 || secondaryCardNumToPut > secondaryPossibleCards.length) {
                print("Wrong number. please retry: ");
                secondaryCardNumToPut = Integer.parseInt(scanner.nextLine());
            }
            if (secondaryPossibleCards.length != secondaryCardNumToPut) {
                secondaryCardNumToPut = secondaryPossibleCards[secondaryCardNumToPut];
                playersLogic.putACard(playerNumber, secondaryCardNumToPut, deck, status);
            } else {
                println("You quit to put.");
                break;
            }

        }
    }

    private boolean emptyCards(Scanner scanner, PlayersLogic playersLogic, Status status, int playerNumber) {
        print("You have no draw card to put! You must pick up " + status.getDraw() + " cards. Press enter.");
        scanner.nextLine();
        if (playersLogic.ifWin(playerNumber, status.getDraw(), Constants.MAX_HAND_SIZE) >= 0) {
            println(players[playerNumber].getId() + " lost!");
            return true;
        }
        println("Getting new cards..");
        int[] drawnCAry = playersLogic.drawCards(playerNumber, deck, status.getDraw(), status);
        status.clearDraw();
        print("The new cards are:\t");
        for (int i = 0; i < drawnCAry.length; i++) {
            if (i > 0) {
                print("\t\t\t");
            }
            println(DeckUtils.showCardInfo(deck, drawnCAry[i]));
        }
        return false;
    }

    private int startDesc(Status status) {
        status.showCurrentGameStatus(deck);
        int playerNumber = status.getCurrentPlayerIndex();
        if (playerNumber == 0) {
            println("Your cards (" + players[Constants.TYPE_INDEX].getHandSize() + " left) :");
            for (int i = 0; i < players[Constants.TYPE_INDEX].getHandSize(); i++) {
                println("\t\t" + DeckUtils.showCardInfo(deck, players[Constants.TYPE_INDEX].getHandCardNumber(i)));
            }
            println(Constants.DELIMITER);
        }
        return playerNumber;
    }

    public String[][] createDeck() {
        String[][] deck = new String[108][4];
        for (int i = 0; i < deck.length; i++) {
            if (i < 4) {
                deck[i][Constants.TYPE_INDEX] = Constants.WILD_DRAW_FOUR;
            } else if (i < 8) {
                deck[i][Constants.TYPE_INDEX] = Constants.WILD_CARD;
            } else if (i < 16) {
                deck[i][Constants.TYPE_INDEX] = Constants.SKIP;
            } else if (i < 24) {
                deck[i][Constants.TYPE_INDEX] = Constants.REVERSE;
            } else if (i < 32) {
                deck[i][Constants.TYPE_INDEX] = Constants.DRAW;
            } else {
                deck[i][Constants.TYPE_INDEX] = Constants.NUM;
            }
        }
        for (int i = 0; i < deck.length; i++) {
            if (i < 8) {
                deck[i][Constants.COLOR_INDEX] = Constants.NONE;
            } else if (i < 32) {
                if (i % 8 < 2) {
                    deck[i][Constants.COLOR_INDEX] = Constants.RED;
                } else if (i % 8 < 4) {
                    deck[i][Constants.COLOR_INDEX] = Constants.BLUE;
                } else if (i % 8 < 6) {
                    deck[i][Constants.COLOR_INDEX] = Constants.YELLOW;
                } else {
                    deck[i][Constants.COLOR_INDEX] = Constants.GREEN;
                }
            } else {
                if (i < 51) {
                    deck[i][Constants.COLOR_INDEX] = Constants.RED;
                } else if (i < 70) {
                    deck[i][Constants.COLOR_INDEX] = Constants.BLUE;
                } else if (i < 89) {
                    deck[i][Constants.COLOR_INDEX] = Constants.YELLOW;
                } else {
                    deck[i][Constants.COLOR_INDEX] = Constants.GREEN;
                }
            }
        }
        for (int i = 0, j = 0; i < deck.length; i++) {
            if (i < 32) {
                deck[i][Constants.NUM_INDEX] = Constants.NONE;
            } else {
                if (j < 1) {
                    deck[i][Constants.NUM_INDEX] = "0";
                    j++;
                } else {
                    deck[i][Constants.NUM_INDEX] = "" + ((j + 1) / 2);
                    j++;
                    if (j > 18) {
                        j = 0;
                    }
                }
            }
        }
        for (int i = 0; i < deck.length; i++) {
            deck[i][Constants.USAGE_INDEX] = Constants.FALSE;
        }
        return deck;
    }

    public Player[] createPlayers() {
        Player[] players = new Player[numberOfPlayers];
        players[Constants.TYPE_INDEX] = new Player(Constants.ME, userName);
        for (int i = 1; i < numberOfPlayers; i++) {
            String name = "Player-" + i;
            players[i] = new Player(name, name);
        }
        return players;
    }

    public void initialDistribution() {
        for (int i = 0; i < players.length; i++) {
            ArrayList<Integer> cards = new ArrayList<Integer>(0);
            int newCardNum;
            for (int j = 0; j < Constants.CARDS_TO_PLAY; j++) {
                do {
                    newCardNum = random.nextInt(108);
                } while (deck[newCardNum][Constants.USAGE_INDEX].equals(Constants.TRUE));
                cards.add(newCardNum);
                deck[newCardNum][Constants.USAGE_INDEX] = Constants.TRUE;
            }
            players[i].init(cards);
            cards.clear();
        }
    }

    public int drawOneCard() {
        int newCardNum;
        do {
            newCardNum = random.nextInt(108);
        } while (deck[newCardNum][Constants.USAGE_INDEX].equals(Constants.TRUE));
        deck[newCardNum][Constants.USAGE_INDEX] = Constants.TRUE;
        return newCardNum;
    }

    private void println(String str) {
        System.out.println(str);
    }

    private void print(String str) {
        System.out.print(str);
    }

}
