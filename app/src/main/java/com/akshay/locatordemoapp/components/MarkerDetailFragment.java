package com.akshay.locatordemoapp.components;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.akshay.locatordemoapp.R;
import com.akshay.locatordemoapp.utilities.ListLocationBin;
import com.akshay.locatordemoapp.utilities.MapConstants;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.StringTokenizer;

public class MarkerDetailFragment extends Fragment {

    private Activity mapLocatorActivity;
    private View inflatedMarkerView;
    private final String TAG = "MarkerDetailFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        inflatedMarkerView = inflater.inflate(R.layout.marker_detail_layout, container, false);

        mapLocatorActivity = this.getActivity();

        setMarkerDetails();

        return inflatedMarkerView;
    }

    private void setMarkerDetails(){

        Bundle bundle = getArguments();

        final ListLocationBin listLocationObj = bundle.getParcelable(MapConstants.MAP_DETAIL_BUNDLE);

        String locType = listLocationObj.getLocType();
        FrameLayout detailContainerLayout = (FrameLayout)inflatedMarkerView.findViewById(R.id.details_container);
        View childView;

        ((TextView)inflatedMarkerView.findViewById(R.id.branch_name_view)).setText(listLocationObj.getName());

        String addressString = listLocationObj.getAddress();
        String address = "";
        StringTokenizer st = new StringTokenizer(addressString," ");

        int j=0;
        while (st.hasMoreElements()) {

            String backChar = "";
            if(j%2==0){
                backChar = "\n";
            }
            address = address + st.nextToken()+ backChar;
            j++;
        }

        ((TextView) inflatedMarkerView.findViewById(R.id.address_text)).setText(address);

        String distance = listLocationObj.getDistance();
        if(distance.length() > 4){
            distance = distance.substring(0,4)+" miles";
        }else{
            distance = distance +" miles";
        }
        ((TextView)inflatedMarkerView.findViewById(R.id.distance_text)).setText(distance);

        try {

            if (locType.equalsIgnoreCase("atm")) {

                childView = mapLocatorActivity.getLayoutInflater().inflate(R.layout.atm_details, null);

                ((TextView) childView.findViewById(R.id.access_text)).setText(listLocationObj.getAccess());

                JSONArray languageArray = new JSONArray(listLocationObj.getLanguages());
                String language = "";
                for (int i = 0; i < languageArray.length(); i++) {
                    language = language+"\n"+languageArray.getString(i);
                }
                ((TextView) childView.findViewById(R.id.languages_text)).setText(language);

                JSONArray serviceArray = new JSONArray(listLocationObj.getServices());
                String service = "";
                for (int i = 0; i < serviceArray.length(); i++) {
                    service = service+"\n"+serviceArray.getString(i);
                }
                ((TextView) childView.findViewById(R.id.services_text)).setText(service);

            } else {
                childView = mapLocatorActivity.getLayoutInflater().inflate(R.layout.branch_details, null);

                ((TextView) childView.findViewById(R.id.atms_text)).setText(listLocationObj.getAtms());

                JSONArray lobbyHrsArray = new JSONArray(listLocationObj.getLobbyHrs());
                String lobbyHr = "";
                for (int i = 0; i < lobbyHrsArray.length(); i++) {
                    lobbyHr = lobbyHr+"\n"+lobbyHrsArray.getString(i);
                }
                ((TextView) childView.findViewById(R.id.lobby_text)).setText(lobbyHr);

                JSONArray driveUpArray = new JSONArray(listLocationObj.getDriveUpHrs());
                String driveUp = "";
                for (int i = 0; i < driveUpArray.length(); i++) {
                    driveUp = driveUp+"\n"+driveUpArray.getString(i);
                }
                ((TextView) childView.findViewById(R.id.drive_up_text)).setText(driveUp);

                ((TextView) childView.findViewById(R.id.type_text)).setText(listLocationObj.getType());
            }

            detailContainerLayout.addView(childView);

        }catch (JSONException e){
            e.printStackTrace();

        }catch (Exception e){
            e.printStackTrace();
        }

        inflatedMarkerView.findViewById(R.id.direction_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String lat = listLocationObj.getLat();
                String lng = listLocationObj.getLng();

                Uri gmmIntentUri = Uri.parse("geo:"+lat+","+lng);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(mapLocatorActivity.getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
    }
}
