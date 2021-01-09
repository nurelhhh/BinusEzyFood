package com.example.binusezyfood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private TextView restNameText;
    public static int REST_ID = 1;
    public static double CURRENT_LATITUDE, CURRENT_LONGITUDE;
    private LocationManager locationManager;
    private String provider;
    private Criteria criteria;

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean permissionDeniedBefore = false;

    private boolean isLocationOn = false;
    private boolean gpsEnabled = false;
    private boolean networkEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        restNameText = findViewById(R.id.restNameText);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        criteria = new Criteria();

        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        askLocationOn();
        getLocation(null);

    }

    private void askLocationOn() {
        if (!gpsEnabled && !networkEnabled) {
            isLocationOn = false;
            new AlertDialog.Builder(this)
                    .setMessage("Enable Location Service to get the nearest restaurant")
                    .setPositiveButton("Settings",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(intent);
                                }
                            })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            isLocationOn = true;
        }
    }

    public void getLocation(View view) {
        askLocationOn();

        provider = locationManager.getBestProvider(criteria, false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);

            permissionDeniedBefore = true;

            return;
        }

        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            onLocationChanged(location);
            getNearestRest();
            isLocationOn = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isLocationOn) {
            try {
                gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (gpsEnabled || networkEnabled) {
                getLocation(null);
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }

        if (permissionDeniedBefore) restartApp();
        locationManager.requestLocationUpdates(provider, 500, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    private void getNearestRest() {
        SQLiteOpenHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("RESTAURANTS", new String[]{"_id", "NAME", "ADDRESS", "LATITUDE", "LONGITUDE"}, null, null, null, null, null);

        double nearestDistance = Double.MAX_VALUE;
        float[] checkDistance = new float[1];
        double otherLatitude = 0.0;
        double otherLongitude = 0.0;
        if (cursor.moveToFirst()) {
            do {
                otherLatitude = Double.parseDouble(cursor.getString(3));
                otherLongitude = Double.parseDouble(cursor.getString(4));
                Location.distanceBetween(CURRENT_LATITUDE, CURRENT_LONGITUDE, otherLatitude, otherLongitude, checkDistance);
                if (checkDistance[0] < nearestDistance) {
                    nearestDistance = checkDistance[0];
                    REST_ID = cursor.getInt(0);
                    restNameText.setText("[NEAREST] " + cursor.getString(1));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }

    public void onChangeRest(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == 1) {
            REST_ID = data.getIntExtra("REST_ID", 1);
            restNameText.setText(data.getStringExtra("REST_NAME"));
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        CURRENT_LATITUDE = location.getLatitude();
        CURRENT_LONGITUDE = location.getLongitude();
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        //
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        //
    }

    private void restartApp() {
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(
                getBaseContext().getPackageName() );
        intent .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}