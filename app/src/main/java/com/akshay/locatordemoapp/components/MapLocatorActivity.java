package com.akshay.locatordemoapp.components;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.akshay.locatordemoapp.utilities.MapConstants;
import com.akshay.locatordemoapp.R;

public class MapLocatorActivity extends Activity {

    private final int MY_LOCATION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_locator);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        MapViewFragment mapFragment = new MapViewFragment();
        fragmentTransaction.add(R.id.map_fragment_container, mapFragment);
        fragmentTransaction.commit();

        //Check for permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                Toast.makeText(MapLocatorActivity.this, getResources().getString(R.string.need_map_permission), Toast.LENGTH_SHORT).show();
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(MapLocatorActivity.this, getResources().getString(R.string.cant_access_map), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void switchFragment(int fromFragment, Bundle bundle){

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(fromFragment == MapConstants.MAP_VIEW_FLAG){

            MarkerDetailFragment markerDetailFragment = new MarkerDetailFragment();
            markerDetailFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.map_fragment_container, markerDetailFragment);

        }else{

            MapViewFragment mapFragment = new MapViewFragment();
            fragmentTransaction.replace(R.id.map_fragment_container, mapFragment);
        }

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
