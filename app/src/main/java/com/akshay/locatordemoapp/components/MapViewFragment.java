package com.akshay.locatordemoapp.components;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.akshay.locatordemoapp.R;
import com.akshay.locatordemoapp.utilities.ListLocationBin;
import com.akshay.locatordemoapp.utilities.MapConstants;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapViewFragment extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private MapView mapView;
    private GoogleMap map;
    private Activity mapLocatorActivity;
    private View inflatedMapView;
    private RequestQueue mRequestQueue;
    private final String TAG = "MapViewFragment";
    private double currentLat = -1;
    private double currentLng = -1;
    private static GoogleApiClient client;
    private List<ListLocationBin> locationList;

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
        map.getUiSettings().setMyLocationButtonEnabled(true);

        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

                checkForGPSEnabled();
                return false;
            }
        });

        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();

                    callLocationService(latitude, longitude);
            }
        });

        checkForGPSEnabled();

        inflatedMapView.findViewById(R.id.all_locations_button).setOnClickListener(this);
        inflatedMapView.findViewById(R.id.atm_locations_button).setOnClickListener(this);
        inflatedMapView.findViewById(R.id.branches_location_button).setOnClickListener(this);

        return inflatedMapView;
    }

    //Function to check for GPS enable otherwise ask for ennoblement
    private void checkForGPSEnabled(){

        if(checkForInternetConnection()) {

            if (client == null) {
                client = new GoogleApiClient.Builder(mapLocatorActivity)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();
                client.connect();
            }

            final LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);
            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(client, builder.build());

            if (result != null) {
                result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                    @Override
                    public void onResult(LocationSettingsResult locationSettingsResult) {
                        final Status status = locationSettingsResult.getStatus();

                        switch (status.getStatusCode()) {
                            case LocationSettingsStatusCodes.SUCCESS:

                                getLatLong();

                                break;
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                                try {

                                    if (status.hasResolution()) {
                                        status.startResolutionForResult(getActivity(), 1000);
                                    }
                                } catch (IntentSender.SendIntentException e) {

                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                                break;
                        }
                    }
                });
            }
        }else{
            Toast.makeText(mapLocatorActivity, getResources().getString(R.string.net_not_available), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == MapConstants.GPS_ENABLE_REQUEST){
            if(requestCode == 1){
                getLatLong();
            }
        }
    }

    //Function to get current lat, long
    public void getLatLong(){

        if (ContextCompat.checkSelfPermission(mapLocatorActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationManager locationManager = (LocationManager) mapLocatorActivity.getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(location != null) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();

                callLocationService(latitude, longitude);
            }
        }
    }

    //Set the map markers by markerType
    public void setMapMarkers(String markerType){

        //clear markers on the map
        map.clear();

        if(locationList != null) {

            final SparseArray<Marker> markerArray = new SparseArray<>();

            for (int i = 0; i < locationList.size(); i++) {

                ListLocationBin listLocationObj = locationList.get(i);

                if (markerType.equalsIgnoreCase(MapConstants.ALL_MARKERS)) {

                    addMarker(markerArray, listLocationObj, i);
                } else if (markerType.equalsIgnoreCase(MapConstants.ATM_MARKERS) && listLocationObj.getLocType().equalsIgnoreCase("atm")) {

                    addMarker(markerArray, listLocationObj, i);
                } else if (markerType.equalsIgnoreCase(MapConstants.BRANCH_MARKERS) && listLocationObj.getLocType().equalsIgnoreCase("branch")) {

                    addMarker(markerArray, listLocationObj, i);
                }
            }

            LatLng currentPos = new LatLng(currentLat, currentLng);

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 11));
            map.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);

            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                    for (int i = 0; i < markerArray.size(); i++) {

                        int key = markerArray.keyAt(i);
                        Marker myMarker = markerArray.get(key);

                        if (marker.equals(myMarker)) {

                            ListLocationBin listLocationObj = locationList.get(key);

                            Bundle bundle = new Bundle();
                            bundle.putParcelable(MapConstants.MAP_DETAIL_BUNDLE, listLocationObj);
                            ((MapLocatorActivity) getActivity()).switchFragment(MapConstants.MAP_VIEW_FLAG, bundle);

                            break;
                        }
                    }
                }
            });
        }
    }

    //Add multiple markers on the map
    public void addMarker(SparseArray<Marker> markerArray, ListLocationBin listLocationObj, int markerPosition){

        String LocType;
        if(listLocationObj.getLocType().equalsIgnoreCase("atm")){
            LocType = "ATM";
        }else{
            LocType = "BRANCH";
        }

        LatLng position = new LatLng(Double.parseDouble(listLocationObj.getLat()), Double.parseDouble(listLocationObj.getLng()));

        Marker myMarker = map.addMarker(new MarkerOptions()
                .position(position)
                .title(LocType)
                .snippet(listLocationObj.getAddress()));

        markerArray.put(markerPosition, myMarker);
    }

    //Function to call service and get the details
    public void callLocationService(double latitude, double longitude){

        map.setOnMyLocationChangeListener(null);

        currentLat = latitude;
        currentLng = longitude;

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

        jsObjRequest.setTag(MapConstants.REQUEST_TAG);
        mRequestQueue.add(jsObjRequest);
    }

    //Function to parse the json object
    public void parseMapJson(JSONObject responseObj){

        try {

            JSONArray locationsArray = responseObj.getJSONArray("locations");

            if(locationsArray != null) {

                locationList = new ArrayList<>();

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
                    listLocationBin.setServices(jObj.getString("services"));
                    listLocationBin.setDistance(jObj.getString("distance"));

                    if(listLocationBin.getLocType().equalsIgnoreCase("atm")) {
                        listLocationBin.setAccess(jObj.getString("access"));
                        listLocationBin.setLanguages(jObj.getString("languages"));
                    }else{
                        listLocationBin.setType(jObj.getString("type"));
                        listLocationBin.setLobbyHrs(jObj.getString("lobbyHrs"));
                        listLocationBin.setDriveUpHrs(jObj.getString("driveUpHrs"));
                        listLocationBin.setAtms(jObj.getString("atms"));
                        listLocationBin.setPhone(jObj.getString("phone"));
                    }

                    locationList.add(listLocationBin);
                }

                inflatedMapView.findViewById(R.id.all_locations_button).performClick();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if(id == R.id.all_locations_button){

            inflatedMapView.findViewById(R.id.all_locations_button).setSelected(true);
            inflatedMapView.findViewById(R.id.atm_locations_button).setSelected(false);
            inflatedMapView.findViewById(R.id.branches_location_button).setSelected(false);

            setMapMarkers(MapConstants.ALL_MARKERS);

        }else if(id == R.id.atm_locations_button){

            inflatedMapView.findViewById(R.id.all_locations_button).setSelected(false);
            inflatedMapView.findViewById(R.id.atm_locations_button).setSelected(true);
            inflatedMapView.findViewById(R.id.branches_location_button).setSelected(false);

            setMapMarkers(MapConstants.ATM_MARKERS);

        }else if(id == R.id.branches_location_button){

            inflatedMapView.findViewById(R.id.all_locations_button).setSelected(false);
            inflatedMapView.findViewById(R.id.atm_locations_button).setSelected(false);
            inflatedMapView.findViewById(R.id.branches_location_button).setSelected(true);

            setMapMarkers(MapConstants.BRANCH_MARKERS);
        }
    }

    //Function to check Internet connectivity
    public boolean checkForInternetConnection(){

        boolean internetFlag = false;

        final ConnectivityManager conMgr = (ConnectivityManager) mapLocatorActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            internetFlag = true;
        }
        return  internetFlag;
    }

    @Override
    public void onStop () {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(MapConstants.REQUEST_TAG);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mapView != null)
            mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mapView != null)
            mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mapView != null)
            mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if(mapView != null)
            mapView.onLowMemory();
    }

}
