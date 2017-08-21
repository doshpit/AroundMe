package danylo.aroundme;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import static danylo.aroundme.R.id.map;

class PlacesList {
    HashMap<String, BitmapDescriptor> places = new HashMap<String, BitmapDescriptor>();

    public PlacesList() {
        places.put("Tim_Hortons", BitmapDescriptorFactory.fromResource(R.drawable.tim));
        places.put("Starbucks", BitmapDescriptorFactory.fromResource(R.drawable.starbucks));
        places.put("Walmart", BitmapDescriptorFactory.fromResource(R.drawable.walmart));
        places.put("Subway", BitmapDescriptorFactory.fromResource(R.drawable.subway));
        places.put("Food_Basics", BitmapDescriptorFactory.fromResource(R.drawable.basics));
    }
}

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ApiAccessResponse {

    private GoogleMap mMap;
    LocationManager lm ;
    Location location ;
    double longitude;
    double latitude ;
    PlacesList placeList;


    public void postResult (LatLng[] location, String place){
        BitmapDescriptor icon= null;
        icon = placeList.places.get(place);
        for (int i = 0; i< location.length; ++i){
            mMap.addMarker(new MarkerOptions().position(location[i]).title(place)
                    .title(place.replace("_", " "))
                    .icon(icon));
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
        @Override
        public void onProviderDisabled(String prov) {}
        @Override
        public void onProviderEnabled(String prov) {}
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

    };

    public void updateLocations() {
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        LatLng myLocation = new LatLng(latitude, longitude);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(myLocation).title("My Location").
                icon(BitmapDescriptorFactory.fromResource(R.drawable.me)));

        for (String key  : placeList.places.keySet()) {
            RestAsyncTask task = new RestAsyncTask(this, key, String.valueOf(latitude), String.valueOf(longitude));
            task.execute();
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15.0f));
    }

    public void onClickUpdate(View v) {
        Toast.makeText(this, "Updating locations", Toast.LENGTH_LONG).show();
        updateLocations();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
        placeList = new PlacesList();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateLocations();
    }
}
