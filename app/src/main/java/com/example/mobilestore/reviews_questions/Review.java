package com.example.mobilestore.reviews_questions;

public class Review {
    public int ReviewID;
    public int MainThread;
    public String UserName;
    public String reviewText;

    public Review(int reviewID, int mainThread, String userName, String text) {
        ReviewID = reviewID;
        MainThread = mainThread;
        UserName = userName;
        reviewText = text;
    }

    public int getReviewID() {
        return ReviewID;
    }

    public int getMainThread() {
        return MainThread;
    }

    public String getUserName() {
        return UserName;
    }

    public String getReviewText() {
        return reviewText;
    }
}


