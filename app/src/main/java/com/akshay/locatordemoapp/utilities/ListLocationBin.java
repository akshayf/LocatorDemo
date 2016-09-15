package com.akshay.locatordemoapp.utilities;

import android.os.Parcel;
import android.os.Parcelable;

public class ListLocationBin implements Parcelable {

    private String state;
    private String locType;
    private String label;
    private String address;
    private String city;
    private String zip;
    private String name;
    private String lat;
    private String lng;
    private String bank;
    private String type;
    private String lobbyHrs;
    private String driveUpHrs;
    private String atms;
    private String services;
    private String phone;
    private String distance;

    private String access;
    private String languages;

    public ListLocationBin() {
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLocType() {
        return locType;
    }

    public void setLocType(String locType) {
        this.locType = locType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLobbyHrs() {
        return lobbyHrs;
    }

    public void setLobbyHrs(String lobbyHrs) {
        this.lobbyHrs = lobbyHrs;
    }

    public String getDriveUpHrs() {
        return driveUpHrs;
    }

    public void setDriveUpHrs(String driveUpHrs) {
        this.driveUpHrs = driveUpHrs;
    }

    public String getAtms() {
        return atms;
    }

    public void setAtms(String atms) {
        this.atms = atms;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.state, this.locType, this.label, this.address, this.city, this.zip,
                this.name, this.lat, this.lng, this.bank, this.type, this.lobbyHrs, this.driveUpHrs, this.atms, this.services,
                this.phone, this.distance, this.access, this.languages});
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ListLocationBin createFromParcel(Parcel in) {
            return new ListLocationBin(in);
        }

        public ListLocationBin[] newArray(int size) {
            return new ListLocationBin[size];
        }
    };

    public ListLocationBin(Parcel in){
        String[] data = new String[19];

        in.readStringArray(data);
        this.state = data[0];
        this.locType = data[1];
        this.label = data[2];
        this.address = data[3];
        this.city = data[4];
        this.zip = data[5];
        this.name = data[6];
        this.lat = data[7];
        this.lng = data[8];
        this.bank = data[9];
        this.type = data[10];
        this.lobbyHrs = data[11];
        this.driveUpHrs = data[12];
        this.atms = data[13];
        this.services = data[14];
        this.phone = data[15];
        this.distance = data[16];
        this.access = data[17];
        this.languages = data[18];
    }
}
