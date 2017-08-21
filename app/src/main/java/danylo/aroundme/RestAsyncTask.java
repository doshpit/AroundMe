package danylo.aroundme;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class RestAsyncTask extends AsyncTask<Void, Void, LatLng[]> {

    String search;
    String lat ;
    String lng;
    public ApiAccessResponse delegate;
    //PLace your google places api key here
    String key = "PLACES_KEY";



    public RestAsyncTask(ApiAccessResponse del, String place, String latitude, String longitude){
        delegate = del;
        search = place;
        lat = latitude;
        lng = longitude;
    }

    @Override
    protected LatLng[] doInBackground(Void...params) {
        // Create URL
        URL places = null;
        LatLng[] tm = null;
        String result = null;
        try {
            places = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                    lat+","+lng+"&radius=5000&keyword=" + search + "&key="+key);

            HttpURLConnection myConnection = (HttpURLConnection) places.openConnection();
            myConnection.setRequestProperty("User-Agent", "Chrome/60.0.0.0");
            myConnection.setRequestProperty("Accept",
                    "application/json");

            int code = myConnection.getResponseCode();
            if (code == 200) {
                InputStream responseBody = null;
                responseBody = myConnection.getInputStream();
                BufferedReader reader = null;
                reader = new BufferedReader(new InputStreamReader(responseBody, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                result = sb.toString();
                if(responseBody != null)responseBody.close();
                myConnection.disconnect();

                JSONObject jObject = new JSONObject(result);
                JSONArray results  = jObject.getJSONArray("results");
                tm = new LatLng[results.length()];
                for (int i = 0; i < results.length(); i++) {
                    JSONObject r = results.getJSONObject(i);
                    JSONObject geometry  = r.getJSONObject("geometry");
                    JSONObject location  = geometry.getJSONObject("location");
                    Double lat = location.getDouble("lat");
                    Double lng = location.getDouble("lng");
                    tm[i]= new LatLng(lat, lng);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return tm;
    }

    @Override
    protected void onPostExecute(LatLng[] result) {
        delegate.postResult(result, search);

    }
}