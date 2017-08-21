package danylo.aroundme;

import com.google.android.gms.maps.model.LatLng;

public interface ApiAccessResponse {
    void postResult(LatLng[] result, String search);
}