package com.example.weather.event;

public class EventMessage {
    private int what;
    private String message;
    public EventMessage(int what,String message){
        this.what=what;
        this.message=message;
    }


    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
