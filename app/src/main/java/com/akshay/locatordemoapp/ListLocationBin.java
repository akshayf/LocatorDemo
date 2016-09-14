package com.akshay.locatordemoapp;

import org.json.JSONArray;

import java.util.List;

public class ListLocationBin {

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
    private JSONArray lobbyHrs;
    private JSONArray driveUpHrs;
    private String atms;
    private JSONArray services;
    private String phone;
    private String distance;

    private String access;
    private JSONArray languages;

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

    public JSONArray getLobbyHrs() {
        return lobbyHrs;
    }

    public void setLobbyHrs(JSONArray lobbyHrs) {
        this.lobbyHrs = lobbyHrs;
    }

    public JSONArray getDriveUpHrs() {
        return driveUpHrs;
    }

    public void setDriveUpHrs(JSONArray driveUpHrs) {
        this.driveUpHrs = driveUpHrs;
    }

    public String getAtms() {
        return atms;
    }

    public void setAtms(String atms) {
        this.atms = atms;
    }

    public JSONArray getServices() {
        return services;
    }

    public void setServices(JSONArray services) {
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

    public JSONArray getLanguages() {
        return languages;
    }

    public void setLanguages(JSONArray languages) {
        this.languages = languages;
    }
}
