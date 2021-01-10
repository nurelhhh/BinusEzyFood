package com.example.binusezyfood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.binusezyfood.DataClasses.ItemType;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private TextView restNameText;
    public static int REST_ID = 1;
    public static double CURRENT_LATITUDE, CURRENT_LONGITUDE;
    public static double RECEIVER_LATITUDE, RECEIVER_LONGITUDE;
    public static String RECEIVER_ADDRESS;
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

    private List<Address> addressSuggestions;
    private final String[] currentAddress = new String[] {""};
    private AutoCompleteTextView addressSelectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        restNameText = findViewById(R.id.restNameText);
        addressSelectText = (AutoCompleteTextView) findViewById(R.id.addressSelectText);
        addressSelectText.setOnItemClickListener((parent, view, position, id) -> {
            RECEIVER_LATITUDE = addressSuggestions.get(position).getLatitude();
            RECEIVER_LONGITUDE = addressSuggestions.get(position).getLongitude();
            RECEIVER_ADDRESS = addressSuggestions.get(position).getAddressLine(0);
        });

        addressSelectText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentAddress[0] = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


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


        RecyclerView recyclerView = findViewById(R.id.type_recycler);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        Vector<ItemType> itemTypes = getItemTypes();

        RecyclerView.Adapter mAdapter = new ItemTypeAdapter(itemTypes);
        recyclerView.setAdapter(mAdapter);
    }

    public void onChangeAddress(View view) {
        addressSuggestions = getLocationFromAddress(getApplicationContext(), currentAddress[0]);

        if (addressSuggestions == null) return;

        String[] addresses = new String[addressSuggestions.size()];
        for (int i=0; i<addressSuggestions.size(); i++) {
            addresses[i] = addressSuggestions.get(i).getAddressLine(0);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, addresses);

        addressSelectText.setAdapter(adapter);
        addressSelectText.showDropDown();
        Utils.hideKeyboard(this);
    }

    public void setAddressFromCurrentLocation(View view) {

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
            setAddressCurrentNow();
            isLocationOn = true;
        }
    }

    private void setAddressCurrentNow() {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(CURRENT_LATITUDE, CURRENT_LONGITUDE, 1);
            currentAddress[0] = addresses.get(0).getAddressLine(0);
            addressSelectText.setText(currentAddress[0]);

            RECEIVER_LATITUDE = addresses.get(0).getLatitude();
            RECEIVER_LONGITUDE = addresses.get(0).getLongitude();
            RECEIVER_ADDRESS = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Address> getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);

            return address;

        } catch (IOException ex) {

            ex.printStackTrace();
        }
        return null;
    }

    private Vector<ItemType> getItemTypes() {
        Vector<ItemType> itemTypes = new Vector<>();

        Cursor cursor = Utils.getDb(this).query("ITEM_TYPES", new String[]{"_id", "NAME", "IMAGE"}, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                itemTypes.add(new ItemType(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2)));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return itemTypes;
    }

    private void askLocationOn() {
        if (!gpsEnabled && !networkEnabled) {
            isLocationOn = false;
            new AlertDialog.Builder(this)
                    .setMessage("Enable Location Service to get the nearest restaurant")
                    .setPositiveButton("Settings",
                            (dialog, which) -> {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
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
        Cursor cursor = Utils.getDb(this).query("RESTAURANTS", new String[]{"_id", "NAME", "ADDRESS", "LATITUDE", "LONGITUDE"}, null, null, null, null, null);

        double nearestDistance = Double.MAX_VALUE;
        float[] checkDistance = new float[1];
        double otherLatitude = 0.0;
        double otherLongitude = 0.0;
        if (cursor.moveToFirst()) {
            do {
                otherLatitude = Double.parseDouble(cursor.getString(3));
                otherLongitude = Double.parseDouble(cursor.getString(4));
                Location.distanceBetween(RECEIVER_LATITUDE, RECEIVER_LONGITUDE, otherLatitude, otherLongitude, checkDistance);
                if (checkDistance[0] < nearestDistance) {
                    nearestDistance = checkDistance[0];
                    REST_ID = cursor.getInt(0);
                    restNameText.setText("[NEAREST] " + cursor.getString(1));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
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
                getBaseContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void onHistory(View view) {
        Intent intent = new Intent(this, TransactionActivity.class);
        startActivity(intent);
    }

    public void onCart(View view) {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

    public void onTopUp(View view) {
        Intent intent = new Intent(this, TopUpActivity.class);
        startActivity(intent);
    }
}