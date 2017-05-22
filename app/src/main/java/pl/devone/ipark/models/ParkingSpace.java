package pl.devone.ipark.models;

import android.location.Location;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ljedrzynski on 22.05.2017.
 */

public class ParkingSpace implements Serializable {

    private Long id;
    private Double latitude;
    private Double longitude;
    private Boolean occupied;

    @SerializedName("curr_occupier_id")
    private Long currOccupierId;

    @SerializedName("last_occupier_id")
    private Long lastOccupierId;

    @SerializedName("reporter_id")
    private Long reporterId;

    private Date createdAt;
    private Date updatedAt;

    public ParkingSpace(Location location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getOccupied() {
        return occupied;
    }

    public void setOccupied(Boolean occupied) {
        this.occupied = occupied;
    }

    public Long getCurrOccupierId() {
        return currOccupierId;
    }

    public void setCurrOccupierId(Long currOccupierId) {
        this.currOccupierId = currOccupierId;
    }

    public Long getLastOccupierId() {
        return lastOccupierId;
    }

    public void setLastOccupierId(Long lastOccupierId) {
        this.lastOccupierId = lastOccupierId;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
