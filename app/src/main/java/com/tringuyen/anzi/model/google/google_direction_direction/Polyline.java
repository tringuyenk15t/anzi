
package com.tringuyen.anzi.model.google.google_direction_direction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Polyline {

    @SerializedName("points")
    @Expose
    private String points;

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

}
