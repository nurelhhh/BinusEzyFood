package com.example.binusezyfood;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Vector;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        SQLiteOpenHelper dbHelper = new DBHelper(this);
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            createMarkers(mMap, db);
            mMap.setOnInfoWindowClickListener(this);
        } catch (SQLException e) {
            Toast.makeText(this, "Cannot open the database", Toast.LENGTH_SHORT).show();
        }

    }

    private void createMarkers(GoogleMap mMap, SQLiteDatabase db) {

        userPositionMarker(mMap);

        Vector<LatLng> latLngVector = new Vector<>();
        Cursor cursor = db.query("RESTAURANTS", new String[] {"_id", "NAME", "ADDRESS", "LATITUDE", "LONGITUDE"}, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                latLngVector.add(new LatLng(
                        Double.parseDouble(cursor.getString(3)),
                        Double.parseDouble(cursor.getString(4))
                ));

                mMap.addMarker(new MarkerOptions()
                        .position(latLngVector.lastElement())
                        .title(cursor.getString(1))
                        .snippet(cursor.getString(2))
                ).setTag(cursor.getInt(0) + "," + cursor.getString(1));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();


        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (LatLng latLng: latLngVector) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 11));
    }

    private void userPositionMarker(GoogleMap mMap) {
        mMap.addMarker(
                new MarkerOptions()
                .position(new LatLng(MainActivity.CURRENT_LATITUDE, MainActivity.CURRENT_LONGITUDE))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.person_pin_small))
        );

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent();

        String[] data = marker.getTag().toString().split(",");

        intent.putExtra("REST_ID", data[0]);
        intent.putExtra("REST_NAME", data[1]);
        setResult(1, intent);
        finish();
    }

}