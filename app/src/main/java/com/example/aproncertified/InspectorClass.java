package com.example.aproncertified;

import java.io.Serializable;

public class InspectorClass implements Serializable {

    String strLocationMarks;
    String strFoodMarks;
    String strChefMarks;
    String strInspectionComment;
    String date;
    String inspectorId;
    String inspectorName;

    public InspectorClass(String strLocationMarks, String strFoodMarks, String strChefMarks, String strInspectionComment,
                          String date, String inspectorId, String inspectorName) {
        this.strLocationMarks = strLocationMarks;
        this.strFoodMarks = strFoodMarks;
        this.strChefMarks = strChefMarks;
        this.strInspectionComment = strInspectionComment;
        this.date = date;
        this.inspectorId = inspectorId;
        this.inspectorName = inspectorName;
    }

    public InspectorClass() {
    }

    public String getStrLocationMarks() {
        return strLocationMarks;
    }

    public void setStrLocationMarks(String strLocationMarks) {
        this.strLocationMarks = strLocationMarks;
    }

    public String getStrFoodMarks() {
        return strFoodMarks;
    }

    public void setStrFoodMarks(String strFoodMarks) {
        this.strFoodMarks = strFoodMarks;
    }

    public String getStrChefMarks() {
        return strChefMarks;
    }

    public void setStrChefMarks(String strChefMarks) {
        this.strChefMarks = strChefMarks;
    }

    public String getStrInspectionComment() {
        return strInspectionComment;
    }

    public void setStrInspectionComment(String strInspectionComment) {
        this.strInspectionComment = strInspectionComment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInspectorId() {
        return inspectorId;
    }

    public void setInspectorId(String inspectorId) {
        this.inspectorId = inspectorId;
    }

    public String getInspectorName() {
        return inspectorName;
    }

    public void setInspectorName(String inspectorName) {
        this.inspectorName = inspectorName;
    }
}
