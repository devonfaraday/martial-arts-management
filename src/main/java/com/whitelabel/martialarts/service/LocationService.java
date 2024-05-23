package com.whitelabel.martialarts.service;

import com.whitelabel.martialarts.model.Location;
import java.util.List;

public interface LocationService {
    List<Location> getAllLocations();
    Location getLocationById(Long id);
    Location createLocation(Location location);
    Location updateLocation(Long id, Location location);
    void deleteLocation(Long id);
}
