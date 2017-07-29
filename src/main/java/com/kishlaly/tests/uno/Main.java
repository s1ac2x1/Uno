package com.kishlaly.tests.uno;

import java.util.Scanner;

/**
 * @author Vladimir Kishlaly
 * @since 29.07.2017
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduce yourself: ");
        String userName = scanner.nextLine();
        System.out.println("Hello, " + userName + ". Lets begin!\n");

        System.out.println("Other players number [1 to 5]? ");
        int totalNumberOfPlayers = Integer.parseInt(scanner.nextLine()) + 1;
        while (totalNumberOfPlayers < 2 || totalNumberOfPlayers > 6) {
            System.out.println("Please input a number from 1 to 5: ");
            totalNumberOfPlayers = Integer.parseInt(scanner.nextLine()) + 1;
        }

        new Game().start(userName, totalNumberOfPlayers);
    }

}
