package com.example.dz2;

public class MyNew {

    private String Date;
    private String UserMail;
    private String NewText;

    public MyNew(String Dates, String UserMails, String NewTexts) {
        this.Date= Dates;
        this.UserMail= UserMails;
        this.NewText= NewTexts;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String Dates) {
        this.Date = Dates;
    }

    public String getUserMail() {
        return UserMail;
    }

    public void setUserMail(String UserMails) {
        this.UserMail = UserMails;
    }

    public String getNewText() {
        return NewText;
    }

    public void setNewText(String NewTexts) {
        this.NewText = NewTexts;
    }

    @Override
    public String toString()  {
        return this.Date +" (User: "+ this.UserMail+")";
    }
}