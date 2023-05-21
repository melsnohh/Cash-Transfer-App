package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.UserCredentials;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

//Handling PENDING requests
    public int askForTransferID(){
        int transferID = 0;
       while(true) {
           System.out.println("Please enter transfer ID to approve/reject (0 to cancel): ");
           String transferIdInput = scanner.nextLine();
           try {
               transferID = Integer.parseInt(transferIdInput);
               break;
           } catch (Exception ex) {
               System.out.println("Invalid transfer ID");
               break;
           }
       }

       return transferID;
    }

    public String askForApproval(){
        boolean keepingLooping = true;
        String selectionForPendingRequest = "";

        while(keepingLooping) {
            System.out.println("Would you like to approve the below transaction? Y/ N  (0 to cancel)");
            selectionForPendingRequest = scanner.nextLine();

            if( !(selectionForPendingRequest.equalsIgnoreCase("y")  || selectionForPendingRequest.equalsIgnoreCase("n") || selectionForPendingRequest.equalsIgnoreCase("0"))){
                System.out.println("Invalid Input ");
            }else {
                keepingLooping = false;
            }
        }
            return selectionForPendingRequest;

    }

// SEND request
public int getReceiverId() {
    boolean correctInfo = true;
    int receiverId = 0;
    while (correctInfo) {
        System.out.println("Please enter user ID to send to (O to cancel)");
        String receiverIDInput = scanner.nextLine();
        try {
            receiverId = Integer.parseInt(receiverIDInput);
            correctInfo = false;
        } catch (Exception ex) {
            System.out.println("Invalid input. Please try again");
            break;
        }
    }
    return receiverId;
}

    public int getReceiverIdTransferRequest(AuthenticatedUser currentUser) {
        boolean correctInfo = true;
        int receiverId = 0;
        while (correctInfo ) {
            System.out.println("Enter ID of user you are requesting from (0 to cancel):");
            String receiverIDInput = scanner.nextLine();

            try {
                receiverId = Integer.parseInt(receiverIDInput);
                if(receiverId != currentUser.getUser().getId() || receiverId == 0){
                    correctInfo = false;
                }else{
                    System.out.println("You cannot send a request to yourself...");
                }

            } catch (Exception ex) {
                System.out.println("Invalid input. Please try again");
//                break;
            }
        }
        return receiverId;
    }


public BigDecimal getUserAmountBigDecimal(){
        boolean correctInfo2 = true;
        BigDecimal userAmountBigDecimal = null;
        while (correctInfo2) {
            System.out.println("Enter amount:");
            String userAmount = scanner.nextLine();
            try {
                 userAmountBigDecimal = new BigDecimal(userAmount);
                correctInfo2 = false;
            }catch (Exception e){
                System.out.println("Invalid input. Please try again");
            }
        }
        return userAmountBigDecimal;
    }

    public boolean printConfirmToSendMessage(int receiverId, BigDecimal userAmountBigDecimal){
        System.out.println("Please confirm if you would like to send $"+userAmountBigDecimal+" to this user ID: "+receiverId+" Y/N");
        String userInput = scanner.nextLine();
        if(userInput.equalsIgnoreCase("N")){
            System.out.println("Transaction was cancelled");
            return false;
        }

        return true;
    }

    public void printConfirmToSendRequestMessage(int requestID, BigDecimal requestAmount){
        System.out.println("Please confirm if you would like to request $"+requestAmount+" from this user ID: "+requestID+" Y/N");
        String userInput = scanner.nextLine();
        if(userInput.equalsIgnoreCase("N")){
            System.out.println("Transaction was cancelled");
        }
    }


}
