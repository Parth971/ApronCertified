package com.example.aproncertified;

import java.io.Serializable;
import java.util.List;

public class ReviewClass implements Serializable {

    float rating;
    String feedback;
    String name;
    String date;
    List<String> reviewImagesUri;

    public ReviewClass() {
    }

    public ReviewClass(float rating, String feedback, String name, String date, List<String> reviewImagesUri) {
        this.rating = rating;
        this.feedback = feedback;
        this.name = name;
        this.date = date;
        this.reviewImagesUri = reviewImagesUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getReviewImagesUri() {
        return reviewImagesUri;
    }

    public void setReviewImagesUri(List<String> reviewImagesUri) {
        this.reviewImagesUri = reviewImagesUri;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
