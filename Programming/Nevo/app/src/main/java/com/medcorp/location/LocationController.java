package com.medcorp.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.medcorp.event.LocationChangedEvent;

import org.greenrobot.eventbus.EventBus;

import net.medcorp.library.permission.PermissionRequestDialogBuilder;

/**
 * Created by med on 16/12/19.
 */

public class LocationController {
    private Context context;

    public LocationController(Context context) {
        this.context = context;
    }

    //TODO when and where invoke this function???
    public void startUpdateLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w("LocationController","to get location,must ask for location perission");
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location==null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if(location==null){
            Log.w("LocationController","can't get location");
            //TODO continue monitor location???
            return;
        }
        EventBus.getDefault().post(new LocationChangedEvent(location));
    }
    public void stopLocation()
    {
        //TODO stop location listener ???
    }
}
