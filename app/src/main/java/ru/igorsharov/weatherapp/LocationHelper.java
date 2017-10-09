package ru.igorsharov.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.EditText;

import java.io.IOException;
import java.util.List;


class LocationHelper {

    private Activity activity;
    /**
     * Private field for store a link to LocationManager object
     */
    private LocationManager locManager = null;

    /**
     * Private field for store a link to the Location Listener object
     */
    private LocListener locListener = null;

    private Location loc;

    LocationHelper(Activity activity) {
        this.activity = activity;
        /* Get a link to the LocationHelper Manager */
        locManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
    }


    Location getLastLoc() {
        /* Get information from Network location provider */
        loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc == null) {
            loc = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc == null) {
                loc = locManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
        }
        return loc;
    }

    void setLocListener(long minTime, float minDist) {
        /* Create Location Listener object (if needed) */
        if (locListener == null)
            locListener = new LocListener();

		/* Setting up Location Listener */
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDist, locListener);
    }

    void removeLocUpd() {
        /* Remove Location Listener */
        locManager.removeUpdates(locListener);
    }

    /**
     * Get address string by location value
     */
    String getAddressStr(android.location.Location loc) {

		/* Define variable for store result */
        String str = "";

		/* Create Geocoder object */
        Geocoder geo = new Geocoder(activity);

		/* Get addresses list by location and prepare result */
        try {

			/* Get addresses list by location and prepare result */
            List<Address> aList = geo.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);

			/* Get address */
            if (aList.size() > 0) {

				/* Get first element from List */
                Address a = aList.get(0);
                str = a.getAdminArea();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }

		/* Return a value */
        return str;
    }


    /**
     * Class that implements Location Listener interface
     */
    private class LocListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }
}
