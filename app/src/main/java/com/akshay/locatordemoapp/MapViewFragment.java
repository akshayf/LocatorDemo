package com.akshay.locatordemoapp;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapViewFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap map;
    private Activity mapLocatorActivity;
    private View inflatedMapView;
    private RequestQueue mRequestQueue;
    private final String REQUEST_TAG = "request_tag";
    private final int GPS_ENABLE_REQUEST = 110;
    private final String TAG = "MapViewFragment";
    private double currentLat;
    private double currentLng;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflatedMapView = inflater.inflate(R.layout.map_view_layout, container, false);

        mapLocatorActivity = this.getActivity();

        mapView = (MapView) inflatedMapView.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        map = mapView.getMap();

        map.setMyLocationEnabled(true);

        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                double longitude = location.getLongitude();
                double latitude = location.getLatitude();

                callLocationService(latitude, longitude);
            }
        });

        checkForGPSEnabled();

        return inflatedMapView;
    }

    private void checkForGPSEnabled(){
        try {
            int gpsEnable = Settings.Secure.getInt(mapLocatorActivity.getContentResolver(), Settings.Secure.LOCATION_MODE);
            if (gpsEnable == 0) {
                Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(onGPS, GPS_ENABLE_REQUEST);
            }else{
                getLatLong();
            }
        }catch (android.provider.Settings.SettingNotFoundException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GPS_ENABLE_REQUEST){
            if(requestCode == 1){
                getLatLong();
            }
        }
    }

    public void getLatLong(){

        if (ContextCompat.checkSelfPermission(mapLocatorActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationManager lm = (LocationManager) mapLocatorActivity.getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(location != null) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();

                callLocationService(latitude, longitude);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Toast.makeText(mapLocatorActivity, "onMapReady ",Toast.LENGTH_SHORT).show();
    }

    public void setMapMarkers(List<ListLocationBin> locationList){

        for(int i=0; i<locationList.size(); i++){

            ListLocationBin listLocationBin = locationList.get(i);

            LatLng position = new LatLng(Double.parseDouble(listLocationBin.getLat()), Double.parseDouble(listLocationBin.getLng()));

            String LocType;
            if(listLocationBin.getLocType().equalsIgnoreCase("atm")){
                LocType = "ATM";
            }else{
                LocType = "Financial Center";
            }

            Marker marker = map.addMarker(new MarkerOptions()
                    .position(position)
                    .title(LocType)
                    .snippet(listLocationBin.getAddress()));
        }

        LatLng currentPos = new LatLng(currentLat,currentLng);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 15));
        map.animateCamera(CameraUpdateFactory.zoomIn());
        //map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                //marker.getId();
                ListLocationBin listLocationBin = null;

                MarkerDetailFragment markerDetailFragment = new MarkerDetailFragment();
                Bundle bundle = new Bundle();
                //bundle.putSerializable(MapConstants.MAP_DETAIL_BUNDLE, listLocationBin);
                markerDetailFragment.setArguments(bundle);

                ((MapLocatorActivity) getActivity()).switchFragment(MapConstants.MapViewFragmentFlag);

                return false;
            }
        });
    }

    public void callLocationService(double latitude, double longitude){

        currentLat = latitude;
        currentLng = longitude;

        Log.d(TAG, "callLocationService... latitude "+latitude+"  longitude "+longitude);

        mRequestQueue = Volley.newRequestQueue(mapLocatorActivity);
        String url = "https://m.chase.com/PSRWeb/location/list.action?lat="+latitude+"&lng="+longitude;

        JsonObjectRequest jsObjRequest;

        jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        parseMapJson(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        jsObjRequest.setTag(REQUEST_TAG);
        mRequestQueue.add(jsObjRequest);
    }

    public void parseMapJson(JSONObject responseObj){

        try {

            JSONArray locationsArray = responseObj.getJSONArray("locations");

            if(locationsArray != null) {

                List<ListLocationBin> locationList = new ArrayList<>();

                for (int i = 0; i < locationsArray.length(); i++) {

                    JSONObject jObj = (JSONObject) locationsArray.get(i);

                    ListLocationBin listLocationBin = new ListLocationBin();
                    listLocationBin.setState(jObj.getString("state"));
                    listLocationBin.setLocType(jObj.getString("locType"));
                    listLocationBin.setLabel(jObj.getString("label"));
                    listLocationBin.setAddress(jObj.getString("address"));
                    listLocationBin.setCity(jObj.getString("city"));
                    listLocationBin.setZip(jObj.getString("zip"));
                    listLocationBin.setName(jObj.getString("name"));
                    listLocationBin.setLat(jObj.getString("lat"));
                    listLocationBin.setLng(jObj.getString("lng"));
                    listLocationBin.setBank(jObj.getString("bank"));
                    listLocationBin.setServices(jObj.getJSONArray("services"));
                    listLocationBin.setDistance(jObj.getString("distance"));

                    if(listLocationBin.getLocType().equalsIgnoreCase("atm")) {
                        listLocationBin.setAccess(jObj.getString("access"));
                        listLocationBin.setLanguages(jObj.getJSONArray("languages"));
                    }else{
                        listLocationBin.setType(jObj.getString("type"));
                        listLocationBin.setLobbyHrs(jObj.getJSONArray("lobbyHrs"));
                        listLocationBin.setDriveUpHrs(jObj.getJSONArray("driveUpHrs"));
                        listLocationBin.setAtms(jObj.getString("atms"));
                        listLocationBin.setPhone(jObj.getString("phone"));
                    }

                    locationList.add(listLocationBin);
                }

                setMapMarkers(locationList);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if(id == R.id.all_locations_button){

            inflatedMapView.findViewById(R.id.all_locations_button).setSelected(true);
            inflatedMapView.findViewById(R.id.atm_locations_button).setSelected(false);
            inflatedMapView.findViewById(R.id.financial_location_button).setSelected(false);

        }else if(id == R.id.atm_locations_button){

            inflatedMapView.findViewById(R.id.all_locations_button).setSelected(false);
            inflatedMapView.findViewById(R.id.atm_locations_button).setSelected(true);
            inflatedMapView.findViewById(R.id.financial_location_button).setSelected(false);

        }else if(id == R.id.financial_location_button){

            inflatedMapView.findViewById(R.id.all_locations_button).setSelected(false);
            inflatedMapView.findViewById(R.id.atm_locations_button).setSelected(false);
            inflatedMapView.findViewById(R.id.financial_location_button).setSelected(true);
        }
    }

    @Override
    public void onStop () {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(REQUEST_TAG);
        }
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}
