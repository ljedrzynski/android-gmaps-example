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

    @SerializedName("address_info")
    private String addressInfo;

    private Boolean occupied;

    @SerializedName("curr_occupier_id")
    private Long currOccupierId;

    @SerializedName("last_occupier_id")
    private Long lastOccupierId;

    @SerializedName("reporter_id")
    private Long reporterId;

    private Date createdAt;

    private Date updatedAt;

    private ParkingSpace(ParkingSpaceBuilder parkingSpaceBuilder) {
        this.latitude = parkingSpaceBuilder.latitude;
        this.longitude = parkingSpaceBuilder.longitude;
        this.occupied = parkingSpaceBuilder.occupied;
        this.lastOccupierId = parkingSpaceBuilder.lastOccupierId;
        this.currOccupierId = parkingSpaceBuilder.currOccupierId;
        this.reporterId = parkingSpaceBuilder.reporterId;
        this.addressInfo = parkingSpaceBuilder.addressInfo;
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

    public ParkingSpace setLatitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public Double getLongitude() {
        return longitude;
    }

    public ParkingSpace setLongitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public String getAddressInfo() {
        return addressInfo;
    }

    public ParkingSpace setAddressInfo(String addressInfo) {
        this.addressInfo = addressInfo;
        return this;
    }

    public Boolean getOccupied() {
        return occupied;
    }

    public ParkingSpace setOccupied(Boolean occupied) {
        this.occupied = occupied;
        return this;
    }

    public Long getCurrOccupierId() {
        return currOccupierId;
    }

    public ParkingSpace setCurrOccupierId(Long currOccupierId) {
        this.currOccupierId = currOccupierId;
        return this;
    }

    public Long getLastOccupierId() {
        return lastOccupierId;
    }

    public ParkingSpace setLastOccupierId(Long lastOccupierId) {
        this.lastOccupierId = lastOccupierId;
        return this;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public ParkingSpace setReporterId(Long reporterId) {
        this.reporterId = reporterId;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public ParkingSpace setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public ParkingSpace setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
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
        if (addressInfo != null ? !addressInfo.equals(that.addressInfo) : that.addressInfo != null)
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
        result = 31 * result + (addressInfo != null ? addressInfo.hashCode() : 0);
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
                ", addressInfo='" + addressInfo + '\'' +
                ", occupied=" + occupied +
                ", currOccupierId=" + currOccupierId +
                ", lastOccupierId=" + lastOccupierId +
                ", reporterId=" + reporterId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public static class ParkingSpaceBuilder {

        /*Required attributes*/
        private final Double latitude;
        private final Double longitude;

        /*Optional attributes*/
        private Boolean occupied;
        private String addressInfo;
        private Long currOccupierId;
        private Long lastOccupierId;
        private Long reporterId;

        public ParkingSpaceBuilder(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public ParkingSpaceBuilder setOccupied(Boolean occupied) {
            this.occupied = occupied;
            return this;
        }

        public ParkingSpaceBuilder setAddressInfo(String addressInfo) {
            this.addressInfo = addressInfo;
            return this;
        }

        public ParkingSpaceBuilder setCurrOccupierId(Long currOccupierId) {
            this.currOccupierId = currOccupierId;
            return this;
        }

        public ParkingSpaceBuilder setLastOccupierId(Long lastOccupierId) {
            this.lastOccupierId = lastOccupierId;
            return this;
        }

        public ParkingSpaceBuilder setReporterId(Long reporterId) {
            this.reporterId = reporterId;
            return this;
        }

        public ParkingSpace build() {
            return new ParkingSpace(this);
        }


    }
}
