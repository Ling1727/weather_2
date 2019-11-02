package com.example.weather.model;


public class MainActivityModel {
    boolean isLocation;
    int size;
    int current;
    boolean isLoading=false;
    String ctiy;


    public MainActivityModel(boolean isLocation,int size,int current,String ctiy){
        this.isLocation=isLocation;
        this.size=size;
        this.current=current;
        this.ctiy=ctiy;
    }

    public boolean isLocation() {
        return isLocation;
    }

    public void setLocation(boolean location) {
        isLocation = location;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public String getCtiy() {
        return ctiy;
    }

    public void setCtiy(String ctiy) {
        this.ctiy = ctiy;
    }
}
