package com.techelevator.tenmo.services;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfers;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Scanner;

public class TransferService {

    private String BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser currentUser;

    public  TransferService(String url, AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
        BASE_URL = url;
    }

    public Transfers[] transfersList() {
        Transfers [] output = null;
        try {
            output = restTemplate.exchange(BASE_URL + "account/transfers/" + currentUser.getUser().getId(), HttpMethod.GET, makeAuthEntity(), Transfers[].class).getBody();
            System.out.println("-------------------------------------------\r\n" +
                    "Transfers\r\n" +
                    "ID          From/To                 Amount\r\n" +
                    "-------------------------------------------\r\n");
            String fromOrTo = "";
            String name = "";
            for (Transfers i : output) {
                if (currentUser.getUser().getId() == i.getAccountFrom()) {
                    fromOrTo = "From: ";
                    name = i.getUserTo();
                } else {
                    fromOrTo = "To: ";
                    name = i.getUserFrom();
                }
                System.out.println(i.getTransferId() +"\t\t" + fromOrTo + name + "\t\t$" + i.getAmount());
            }
            System.out.print("-------------------------------------------\r\n" +
                    "Please enter transfer ID to view details (0 to cancel): ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            if (Integer.parseInt(input) != 0) {
                boolean foundTransferId = false;
                for (Transfers i : output) {
                    if (Integer.parseInt(input) == i.getTransferId()) {
                        Transfers temp = restTemplate.exchange(BASE_URL + "transfers/" + i.getTransferId(), HttpMethod.GET, makeAuthEntity(), Transfers.class).getBody();
                        foundTransferId = true;
                        System.out.println("--------------------------------------------\r\n" +
                                "Transfer Details\r\n" +
                                "--------------------------------------------\r\n" +
                                " Id: "+ temp.getTransferId() + "\r\n" +
                                " From: " + temp.getUserFrom() + "\r\n" +
                                " To: " + temp.getUserTo() + "\r\n" +
                                " Type: " + temp.getTransferType() + "\r\n" +
                                " Status: " + temp.getTransferStatus() + "\r\n" +
                                " Amount: $" + temp.getAmount());
                    }
                }
                if (!foundTransferId) {
                    System.out.println("Not a valid transfer ID");
                }
            }
        } catch (Exception e) {
            System.out.println("Something went wrong... Opps! We have all your money now!");
        }
        return output;
    }


    private HttpEntity<Transfers> makeTransferEntity(Transfers transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Transfers> entity = new HttpEntity<>(transfer, headers);
        return entity;
    }

    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }
}
