package com.akshay.locatordemoapp.components;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.akshay.locatordemoapp.utilities.MapConstants;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SmallTest
/*
* Test location service
* and validate output is not null
* */
public class LocationServiceTest extends ActivityInstrumentationTestCase2<MapLocatorActivity> {

    private Activity mActivity;
    private double latitude;
    private double longitude;

    public LocationServiceTest(){
        super(MapLocatorActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();

        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = getActivity();
        latitude = 32.938791;
        longitude = -96.855391;
    }

    @Test
    public void callLocationServiceTest() {

        RequestQueue mRequestQueue = Volley.newRequestQueue(mActivity);
        String url = "https://m.chase.com/PSRWeb/location/list.action?lat="+latitude+"&lng="+longitude;

        JsonObjectRequest jsObjRequest;

        jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        assertNotNull(response);
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

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
