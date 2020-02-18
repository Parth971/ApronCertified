package com.example.aproncertified;

import java.io.Serializable;
import java.util.List;

public class CancelApprovalClass implements Serializable {

    FormDetails formDetails;
    String reason;
    List<ReviewDataSnapshot> reviewClass;

    public CancelApprovalClass(FormDetails formDetails, String reason, List<ReviewDataSnapshot> reviewClass) {
        this.formDetails = formDetails;
        this.reason = reason;
        this.reviewClass = reviewClass;
    }

    public CancelApprovalClass() {
    }

    public List<ReviewDataSnapshot> getReviewClass() {
        return reviewClass;
    }

    public void setReviewClass(List<ReviewDataSnapshot> reviewClass) {
        this.reviewClass = reviewClass;
    }

    public FormDetails getFormDetails() {
        return formDetails;
    }

    public void setFormDetails(FormDetails formDetails) {
        this.formDetails = formDetails;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
