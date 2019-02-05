package com.efunhub.starkio.pickpricedealer.Modal;

/**
 * Created by Admin on 10-12-2018.
 */

public class Country {

    public String coutryId;
    public String countryName;


    public Country() {
    }

    public Country(String coutryId, String countryName) {
        this.coutryId = coutryId;
        this.countryName = countryName;
    }

    public String getCoutryId() {
        return coutryId;
    }

    public void setCoutryId(String coutryId) {
        this.coutryId = coutryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
