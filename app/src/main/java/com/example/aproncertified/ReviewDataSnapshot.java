package com.example.aproncertified;

import java.io.Serializable;

public class ReviewDataSnapshot implements Serializable {

    String key;
    ReviewClass value;

    public ReviewDataSnapshot(String key, ReviewClass value) {
        this.key = key;
        this.value = value;
    }

    public ReviewDataSnapshot() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ReviewClass getValue() {
        return value;
    }

    public void setValue(ReviewClass value) {
        this.value = value;
    }
}
