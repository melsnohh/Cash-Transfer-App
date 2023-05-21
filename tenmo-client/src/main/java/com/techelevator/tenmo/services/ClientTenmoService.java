package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class ClientTenmoService {
    private final String baseURL;
    private final RestTemplate restTemplate = new RestTemplate();

    public ClientTenmoService(String baseURL) {
        this.baseURL = baseURL;
    }

    public BigDecimal viewBalance (AuthenticatedUser authenticatedUser){
        User user = authenticatedUser.getUser();

        ResponseEntity<BigDecimal> response = restTemplate.exchange(baseURL + "user/"+user.getId(), HttpMethod.GET, createCredentialsEntity(authenticatedUser), BigDecimal.class);
        return response.getBody();
    }

    public Integer getAccountIdFromUserId(AuthenticatedUser authenticatedUser,int otherUserId){
        User user = authenticatedUser.getUser();
        ResponseEntity<Integer> response = restTemplate.exchange(baseURL + "user/"+user.getId()+"/Users/"+otherUserId, HttpMethod.GET, createCredentialsEntity(authenticatedUser), Integer.class);
        return response.getBody();
    }

    public String getUsernameByAccountID(AuthenticatedUser authenticatedUser, int accountID){
        User user = authenticatedUser.getUser();
        ResponseEntity<String> response = restTemplate.exchange(baseURL + "user/"+user.getId()+"/Transfer/"+accountID, HttpMethod.GET, createCredentialsEntity(authenticatedUser), String.class);
        return response.getBody();
    }

    public User[] getLisOfUsernameAndId(AuthenticatedUser authenticatedUser){
        User user = authenticatedUser.getUser();
        ResponseEntity<User[]> response = restTemplate.exchange(baseURL + "user/"+user.getId()+"/Users", HttpMethod.GET, createCredentialsEntity(authenticatedUser), User[].class);
        return response.getBody();
    }

    public void printListOfUsername(AuthenticatedUser authenticatedUser){

        User[] listOfUsernameAndId = getLisOfUsernameAndId(authenticatedUser);
        System.out.printf("%-20s %-20s\n", "User IDs", "Usernames");
        for(User user : listOfUsernameAndId){
            if(user != null){
                System.out.printf("%-20s %-20s\n", user.getId(), user.getUsername());
            }
        }

    }

    public Transfer[] viewAllApprovedTransfer(AuthenticatedUser authenticatedUser){
        User user = authenticatedUser.getUser();

        ResponseEntity<Transfer[]> response = restTemplate.exchange(baseURL + "user/"+user.getId()+"/Transfer", HttpMethod.GET, createCredentialsEntity(authenticatedUser), Transfer[].class);
        return response.getBody();
    }

    public void printAllApprovedTransfer(AuthenticatedUser authenticatedUser){
        Transfer[] transferHistory = viewAllApprovedTransfer(authenticatedUser);
        String currentUsername = authenticatedUser.getUser().getUsername();
        System.out.printf("%-20s %-20s %-10s\n", "Transfer ID", "Details", "Amount");
        for(Transfer transfer : transferHistory){

            if(transfer != null){
                String usernameForAccountFrom = getUsernameByAccountID(authenticatedUser,transfer.getAccount_from());
                String usernameForAccountTo = getUsernameByAccountID(authenticatedUser,transfer.getAccount_to());
                if(currentUsername.equals(usernameForAccountFrom)){
                    System.out.printf("%-20s %-20s %-10s\n", transfer.getTransfer_id(), "To: "+usernameForAccountTo, transfer.getAmount());
                }
                if(currentUsername.equals(usernameForAccountTo)){
                    System.out.printf("%-20s %-20s %-10s\n", transfer.getTransfer_id(), "From: "+usernameForAccountFrom, transfer.getAmount());
                }
            }
        }
    }

    public Transfer[] viewAllPendingTransfer(AuthenticatedUser authenticatedUser){
        User user = authenticatedUser.getUser();

        ResponseEntity<Transfer[]> response = restTemplate.exchange(baseURL + "user/"+user.getId()+"/Transfer/Pending", HttpMethod.GET, createCredentialsEntity(authenticatedUser), Transfer[].class);
        return response.getBody();
    }
    public void printAllPendingTransfer(AuthenticatedUser authenticatedUser){
        Transfer[] transferHistory = viewAllPendingTransfer(authenticatedUser);
        String currentUsername = authenticatedUser.getUser().getUsername();
        System.out.printf("%-20s %-20s %-10s\n", "Transfer ID", "To", "Amount");
        for(Transfer transfer : transferHistory){
            String usernameForAccountTo = getUsernameByAccountID(authenticatedUser,transfer.getAccount_to());
            //Set conditions to printout only pending that user is allowed to approve (i.e. sending money to others)
            if(transfer != null && (!(currentUsername.equals(usernameForAccountTo)))){
                System.out.printf("%-20s %-20s %-10s\n", transfer.getTransfer_id(), usernameForAccountTo, transfer.getAmount());
            }
        }
        System.out.println("");
    }
    public Transfer[] viewTransferDetailsByTransferId(AuthenticatedUser authenticatedUser, int transferId){
        User user = authenticatedUser.getUser();

        ResponseEntity<Transfer[]> response = restTemplate.exchange(baseURL + "user/"+user.getId()+"/Transfer/Transaction/"+ transferId, HttpMethod.GET, createCredentialsEntity(authenticatedUser), Transfer[].class);
        return response.getBody();
    }
    public void printTransferDetailsByTransferId(AuthenticatedUser authenticatedUser, int transferId){
        Transfer[] transferHistory = viewTransferDetailsByTransferId(authenticatedUser, transferId);
        String currentUsername = authenticatedUser.getUser().getUsername();
        System.out.printf("%-20s %-20s %-10s\n", "Transfer ID", "Details", "Amount");
        for(Transfer transfer : transferHistory){

            if(transfer != null){
                String usernameForAccountFrom = getUsernameByAccountID(authenticatedUser,transfer.getAccount_from());
                String usernameForAccountTo = getUsernameByAccountID(authenticatedUser,transfer.getAccount_to());
                if(currentUsername.equals(usernameForAccountFrom)){
                    System.out.printf("%-20s %-20s %-10s\n", transfer.getTransfer_id(), "To: "+usernameForAccountTo, transfer.getAmount());
                }
                if(currentUsername.equals(usernameForAccountTo)){
                    System.out.printf("%-20s %-20s %-10s\n", transfer.getTransfer_id(), "From: "+usernameForAccountFrom, transfer.getAmount());
                }
            }
        }
        System.out.println("");
    }

    public Transfer postNewTransfer(AuthenticatedUser authenticatedUser, Transfer transfer) {
        User user = authenticatedUser.getUser();
        Transfer newTransfer = null;
        try {
            newTransfer = restTemplate.postForObject(baseURL + "user/" + user.getId() + "/Transfer/", makeTransferEntity(transfer, authenticatedUser), Transfer.class);
            System.out.println("Transaction completed");
        } catch (ResourceAccessException | RestClientResponseException ex) {
            //Update this when working through exception handling
            System.out.println("Error. Please try again");
        }
        return newTransfer;

    }

    public Transfer postNewTransferRequest(AuthenticatedUser authenticatedUser, Transfer transfer) {
        User user = authenticatedUser.getUser();
        Transfer newTransfer = null;
        try {
            newTransfer = restTemplate.postForObject(baseURL + "user/" + user.getId() + "/TransferRequest/", makeTransferEntity(transfer, authenticatedUser), Transfer.class);
            System.out.println("Request was sent");
        } catch (ResourceAccessException | RestClientResponseException ex) {
            //Update this when working through exception handling
            System.out.println("Error. Please try again");
        }
        return newTransfer;

    }

    public void updateTransferStatus(AuthenticatedUser authenticatedUser, Transfer transfer) {
        User user = authenticatedUser.getUser();
        try {
            restTemplate.put(baseURL + "user/" + user.getId() + "/Transfer/Transaction", makeTransferEntity(transfer, authenticatedUser));
            System.out.println("Transaction completed");
        } catch (ResourceAccessException | RestClientResponseException ex) {
            //Update this when working through exception handling
            System.out.println("Error. Please try again");
        }

    }

    public Transfer createNewTransfer(int userAccountId, int receiverAccountId,  BigDecimal amount){
        Transfer transfer = new Transfer();
        //Consider using enum (transfer type 2 = Send; status 2 = approved)
        transfer.setTransfer_type_id(2);
        transfer.setTransfer_status_id(2);
        transfer.setAccount_from(userAccountId);
        transfer.setAccount_to(receiverAccountId);
        transfer.setAmount(amount);

        return transfer;
    }

    public Transfer createRequestTransfer(int userAccountId, int receiverAccountId,  BigDecimal amount){
        Transfer transfer = new Transfer();
        //Consider using enum (transfer type 2 = Send; status 2 = approved)
        transfer.setTransfer_type_id(1);
        transfer.setTransfer_status_id(1);
        transfer.setAccount_from(userAccountId);
        transfer.setAccount_to(receiverAccountId);
        transfer.setAmount(amount);

        return transfer;
    }



    private HttpEntity<Void> createCredentialsEntity(AuthenticatedUser authenticatedUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticatedUser.getToken());
        return new HttpEntity<>(headers);
    }

    public HttpEntity<Transfer> makeTransferEntity(Transfer transfer, AuthenticatedUser user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(user.getToken());

        return new HttpEntity<>(transfer, headers);
    }

}
