package com.akshay.locatordemoapp.utilities;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
/*
* Test ListLocationBin parcel object
* and validate all values stored in that
* */
public class ListLocationBinTest {

    private ListLocationBin locationBin;

    @Test
    public void listLocationBin_ParcelableWriteRead() {
        // Set up the Parcelable object to send and receive.
        locationBin = new ListLocationBin("state", "locType", "label", "address", "city", "zip",
                "name", "lat", "lng", "bank", "type", "lobbyHrs",
                "driveUpHrs", "atms", "services", "phone", "distance",
                "access", "languages");

        // Write the data.
        Parcel parcel = Parcel.obtain();
        locationBin.writeToParcel(parcel, locationBin.describeContents());

        parcel.setDataPosition(0);

        // Read the data.
        ListLocationBin createdFromParcel = (ListLocationBin)ListLocationBin.CREATOR.createFromParcel(parcel);

        // Verify that the received data is correct.
        assertThat(createdFromParcel.getState(), is("state"));
        assertThat(createdFromParcel.getLocType(), is("locType"));
        assertThat(createdFromParcel.getLabel(), is("label"));
        assertThat(createdFromParcel.getAddress(), is("address"));
        assertThat(createdFromParcel.getCity(), is("city"));
        assertThat(createdFromParcel.getZip(), is("zip"));
        assertThat(createdFromParcel.getName(), is("name"));
        assertThat(createdFromParcel.getLat(), is("lat"));
        assertThat(createdFromParcel.getLng(), is("lng"));
        assertThat(createdFromParcel.getBank(), is("bank"));
        assertThat(createdFromParcel.getType(), is("type"));
        assertThat(createdFromParcel.getLobbyHrs(), is("lobbyHrs"));
        assertThat(createdFromParcel.getDriveUpHrs(), is("driveUpHrs"));
        assertThat(createdFromParcel.getAtms(), is("atms"));
        assertThat(createdFromParcel.getServices(), is("services"));
        assertThat(createdFromParcel.getPhone(), is("phone"));
        assertThat(createdFromParcel.getDistance(), is("distance"));
        assertThat(createdFromParcel.getAccess(), is("access"));
        assertThat(createdFromParcel.getLanguages(), is("languages"));
    }

}
