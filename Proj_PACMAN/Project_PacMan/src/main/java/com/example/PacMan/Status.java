package com.example.PacMan;

import java.io.Serializable;

//Serialization - conversion of the state of an object into a byte stream

public class Status implements Serializable {
    private int ID;
    private int sliderStatus;
    private int coordinateY;

    public Status(int id, int sliderStatus, int coordinateY) {
        this.ID = id;
        this.sliderStatus = sliderStatus;
        this.coordinateY = coordinateY;
    }

    int getID() {
        return ID;
    }

    int getSliderStatus() {
        return sliderStatus;
    }

    public int getCoordinateY() {
        return this.coordinateY;
    }

    public String toString() {
        return "Status [ID=" + ID + ", sliderStatus=" + sliderStatus + "]";
    }

}
