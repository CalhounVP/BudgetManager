package com.github.calhounvp.entities;

import com.github.calhounvp.services.EmailSender;
import com.github.calhounvp.tools.TerminalTool;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;

/***
 * The User class is made under the assumption that
 * everyone in the company has to get permission from the
 * same financial manager and that everyone has the same
 * spending limit.
 */
public class User {
    //__________________________________Properties__________________________________
    private final String userName;
    private final String userEmail;
    private final String userSuperiorEmail;
    private final BigDecimal userMonthlyLimit;
    private BigDecimal amountSpent;
    private ArrayList<SpendRequest> currentRequests;

    {
        userSuperiorEmail = "dummy@mail.com";
        userMonthlyLimit = new BigDecimal(500);
    }

    //_________________________________Constructors_________________________________
    public User (String userName, ArrayList<SpendRequest> currentRequests) {
        this.userName = userName;
        this.userEmail = this.userName.concat("@mail.com");
        setCurrentRequests(currentRequests);
    }

    //____________________________________Methods___________________________________
    //************************************setters***********************************
    private void setAmountSpentNow() {
        //it will make the sum of all the approved requests so you know the amount spent this period
        this.amountSpent = currentRequests.stream()
                                            .filter(spendRequest -> spendRequest.getStatus()
                                                        .equals(SpendRequest.Status.APPROVED))
                                            .map(SpendRequest::getRequestCost)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void setCurrentRequests(ArrayList<SpendRequest> currentRequests) {
        //if the currentRequests arrayList is null it will create an empty one
        this.currentRequests = Objects.requireNonNullElseGet(currentRequests, ArrayList::new);
        this.setAmountSpentNow();
    }

    //************************************getters***********************************
    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserSuperiorEmail() {
        return userSuperiorEmail;
    }

    public BigDecimal getUserMonthlyLimit() {
        return userMonthlyLimit;
    }

    public BigDecimal getAmountSpent() {
        return amountSpent;
    }

    public ArrayList<SpendRequest> getCurrentRequests() {
        return currentRequests;
    }

    //*********************************Utility Method*******************************
    public void addToSpending (SpendRequest request) {
        this.amountSpent = this.amountSpent.add(request.getRequestCost());
    }

    public void addSpendRequest(SpendRequest spendRequest) {
        /*if the sum of amountSpent and the new cost is smaller or equal to the limit
         the transaction will automatically be approved and added to the amount spent*/
        if (amountSpent.add(spendRequest.getRequestCost()).compareTo(userMonthlyLimit) <= 0) {
            spendRequest.setStatus(SpendRequest.Status.APPROVED);
            addToSpending(spendRequest);
            TerminalTool.approvedRequest(this);
        }
        //else it will send an email to the superior through the EmailSender class
        else {
            String spendingList = currentRequests.stream().map(SpendRequest::getRequestInfo).reduce("", String::concat);
            new EmailSender(this).sendEmail(spendRequest, spendingList);
            TerminalTool.confirmationNeededRequest();
        }
        // any transaction approved or not will be added to the list
        currentRequests.add(spendRequest);
    }

    //********************************Inherited Method******************************
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getUserName().equals(user.getUserName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserName());
    }
}
