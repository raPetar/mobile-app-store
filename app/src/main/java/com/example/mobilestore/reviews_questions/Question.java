package com.example.mobilestore.reviews_questions;

public class Question {
    public int QuestionID;
    public int MainThread;
    public String UserName;
    public String questionText;

    public Question(int questionID, int mainThread, String userName, String text) {
        QuestionID = questionID;
        MainThread = mainThread;
        UserName = userName;
        questionText = text;
    }

    public int getQuestionID() {
        return QuestionID;
    }

    public int getMainThread() {
        return MainThread;
    }

    public String getUserName() {
        return UserName;
    }

    public String getQuestionText() {
        return questionText;
    }
}
