package com.github.calhounvp.entities;

import java.math.BigDecimal;

/***
 * The SpendRequest assumes that requests are inputted
 * once and that's final. However the status could be updated
 * from pending to approved or rejected.
 */
public class SpendRequest {
    //__________________________________Properties__________________________________
    private final String requestInfo;
    private final BigDecimal requestCost;
    private Status status;

    //________________________________Nested Classes________________________________
    public enum Status {
        PENDING, APPROVED, REJECTED
    }

    //_________________________________Constructors_________________________________
    public SpendRequest(String requestInfo, BigDecimal requestCost) {
        this(requestInfo, requestCost, Status.PENDING);
    }

    public SpendRequest(String requestInfo, BigDecimal requestCost, Status status) {
        this.requestInfo = requestInfo;
        this.requestCost = requestCost;
        this.status = status;
    }

    //____________________________________Methods___________________________________
    //************************************setters***********************************
    public void setStatus(Status status) {
        if (!this.status.equals(status)) {
            this.status = status;
        }
    }

    //************************************getters***********************************
    public String getRequestInfo() {
        return requestInfo;
    }

    public BigDecimal getRequestCost() {
        return requestCost;
    }

    public Status getStatus() {
        return status;
    }

    //********************************Inherited Method******************************
    @Override
    public String toString() {
        return String.format("%.2f€: %s %n", requestCost.doubleValue(), requestInfo);
    }
}
