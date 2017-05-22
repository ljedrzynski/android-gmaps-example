package pl.devone.ipark.services.parkingspace.callbacks;

import java.util.List;

import pl.devone.ipark.models.ParkingSpace;

/**
 * Created by ljedrzynski on 22.05.2017.
 */

public interface ParkingSpaceFetchCallback {

    void onSuccess(List<ParkingSpace> parkingSpaces);

    void onFailure();
}
