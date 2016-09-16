package com.akshay.locatordemoapp.components;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.akshay.locatordemoapp.R;
import com.akshay.locatordemoapp.utilities.MapConstants;

/**
 * <h1>MapLocatorActivity</h1>
 * The Activity use to check permission availability
 * and do the switching of fragments
 *
 * @author  Akshay Faye
 */
public class MapLocatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_locator);

        //Check for permission above API level 23
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    Toast.makeText(MapLocatorActivity.this, getResources().getString(R.string.need_map_permission), Toast.LENGTH_SHORT).show();
                } else {
                    GetThePermission();
                }
            }else{
                addMapFragment();
            }
        }else{
            addMapFragment();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MapConstants.MY_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                addMapFragment();
            } else {
                Toast.makeText(MapLocatorActivity.this, getResources().getString(R.string.need_map_permission), Toast.LENGTH_LONG).show();

                GetThePermission();
            }
        }
    }

    /**
     * Method to show permission dialog
     * */
    private void GetThePermission(){

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MapConstants.MY_LOCATION_REQUEST_CODE);
    }

    /**
     * Method to add mapFragment in fragment container
     * */
    public void addMapFragment(){

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        MapViewFragment mapFragment = new MapViewFragment();
        fragmentTransaction.add(R.id.map_fragment_container, mapFragment);
        fragmentTransaction.commit();
    }

    /**
     * This is the method to replace current fragment with new fragment
     * and shares the dat between them.
     * @param fromFragment Current Fragment.
     * @param bundle Bundle contain Parcelable object.
     */
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
