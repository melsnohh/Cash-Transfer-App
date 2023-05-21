package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ClientTenmoService;
import com.techelevator.tenmo.services.ConsoleService;

import java.math.BigDecimal;
import java.security.Principal;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final ClientTenmoService clientTenmoService = new ClientTenmoService(API_BASE_URL);

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
		// TODO Auto-generated method stub
        System.out.println("Your current balance is: " +clientTenmoService.viewBalance(currentUser));

		
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
        clientTenmoService.printAllApprovedTransfer(currentUser);
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
        clientTenmoService.printAllPendingTransfer(currentUser);

        //Ask for transfer ID
        int transferID = consoleService.askForTransferID();
        //Allow exit to main menu when receiving "0"
        if(transferID==0){
            mainMenu();
        }

        Transfer transfer = new Transfer();
        transfer.setTransfer_id(transferID);

        //Confirm y/n for approval
        String userResponse = consoleService.askForApproval();

        if(userResponse.equals("0")){
            mainMenu();
        }if(userResponse.equalsIgnoreCase("Y")){
            transfer.setTransfer_status_id(2);
            clientTenmoService.updateTransferStatus(currentUser, transfer);
        }else if(userResponse.equalsIgnoreCase("N")){
            transfer.setTransfer_status_id(3);
            clientTenmoService.updateTransferStatus(currentUser, transfer);
        }

	}

	private void sendBucks() {
		// TODO Auto-generated method stub

        boolean keepLooping = true;
        int receiverID = -1;

        while(keepLooping) {
//        need to get all the userid and name and display it.
            clientTenmoService.printListOfUsername(currentUser);
            System.out.println();

            //Request info for receiverId
             receiverID = consoleService.getReceiverId();

            try {
                clientTenmoService.getAccountIdFromUserId(currentUser, receiverID);
                if(receiverID != currentUser.getUser().getId()){
                keepLooping = false;
                }else{
                    System.out.println("You cannot send funds to yourself.");
                }

            } catch (Exception e) {
                System.out.println("Invalid ID \n");
            }

        }
            //Request info for amount to send
            BigDecimal amountToTransfer = consoleService.getUserAmountBigDecimal();


            if(amountToTransfer.compareTo(new BigDecimal("0")) > 0) {
                //Check if requested amount is within balance
                if (amountToTransfer.compareTo(clientTenmoService.viewBalance(currentUser)) <= 0) {

                    //Consider to ask for confirmation Y/N "Are you sure you want to send.... to...."
                   boolean confirm = consoleService.printConfirmToSendMessage(receiverID, amountToTransfer);

                   if(confirm) {

                       //Get Account ID from User ID
                       int currentUserAccountID = clientTenmoService.getAccountIdFromUserId(currentUser, currentUser.getUser().getId());

                       int otherUserAccountID = clientTenmoService.getAccountIdFromUserId(currentUser, receiverID);


                       //Create new transfer with input
                       Transfer newTransfer = clientTenmoService.createNewTransfer(currentUserAccountID, otherUserAccountID, amountToTransfer);


                       //Post new transfer i.e. commit sendBucks
                       newTransfer = clientTenmoService.postNewTransfer(currentUser, newTransfer);

                       //update balance for 2 accounts. Print out transfer details. Confirm complete
                       clientTenmoService.printTransferDetailsByTransferId(currentUser, newTransfer.getTransfer_id());
                   }


                } else {
                    System.out.println("Transaction was cancelled due to insufficient balance");
                }

            }else{
                System.out.println("Transfer amount has to be above 0.");
            }

	}

	private void requestBucks() {
		// TODO Auto-generated method stub
        //Receive user's input
        boolean keepLooping = true;
        int requestingToID = -1;
        while(keepLooping) {
            clientTenmoService.printListOfUsername(currentUser);
            requestingToID = consoleService.getReceiverIdTransferRequest(currentUser);
            if(requestingToID != 0) {

                try {
                    clientTenmoService.getAccountIdFromUserId(currentUser, requestingToID);
                    keepLooping = false;
                } catch (Exception ex) {
                    System.out.println("Invalid ID. \n");
                    break;
                }


                BigDecimal requestAmount = consoleService.getUserAmountBigDecimal();

                if(requestAmount.compareTo(new BigDecimal("0")) > 0) {

                    //Confirming before proceeding to transaction
                    consoleService.printConfirmToSendRequestMessage(requestingToID, requestAmount);

                    //Posting transaction with Pending status
                    int currentUserAcountID = clientTenmoService.getAccountIdFromUserId(currentUser, currentUser.getUser().getId());
                    int otherUserAccountID = clientTenmoService.getAccountIdFromUserId(currentUser, requestingToID);

                    Transfer requestTransfer = clientTenmoService.createRequestTransfer(otherUserAccountID, currentUserAcountID, requestAmount);
                    requestTransfer = clientTenmoService.postNewTransferRequest(currentUser, requestTransfer);
                    clientTenmoService.printTransferDetailsByTransferId(currentUser, requestTransfer.getTransfer_id());
                }else{
                    System.out.println("Invalid input. Request amount is not greater than 0:");
                }

            }else{
                keepLooping = false;
            }

        }

	}



}
