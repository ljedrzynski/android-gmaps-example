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

    public ParkingSpace(Location location, User user) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.occupied = false;
        this.lastOccupierId = user.getId();
        this.reporterId = user.getId();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParkingSpace that = (ParkingSpace) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (latitude != null ? !latitude.equals(that.latitude) : that.latitude != null)
            return false;
        if (longitude != null ? !longitude.equals(that.longitude) : that.longitude != null)
            return false;
        if (occupied != null ? !occupied.equals(that.occupied) : that.occupied != null)
            return false;
        if (currOccupierId != null ? !currOccupierId.equals(that.currOccupierId) : that.currOccupierId != null)
            return false;
        if (lastOccupierId != null ? !lastOccupierId.equals(that.lastOccupierId) : that.lastOccupierId != null)
            return false;
        if (reporterId != null ? !reporterId.equals(that.reporterId) : that.reporterId != null)
            return false;
        if (createdAt != null ? !createdAt.equals(that.createdAt) : that.createdAt != null)
            return false;
        return updatedAt != null ? updatedAt.equals(that.updatedAt) : that.updatedAt == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + (occupied != null ? occupied.hashCode() : 0);
        result = 31 * result + (currOccupierId != null ? currOccupierId.hashCode() : 0);
        result = 31 * result + (lastOccupierId != null ? lastOccupierId.hashCode() : 0);
        result = 31 * result + (reporterId != null ? reporterId.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ParkingSpace{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", occupied=" + occupied +
                ", currOccupierId=" + currOccupierId +
                ", lastOccupierId=" + lastOccupierId +
                ", reporterId=" + reporterId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
