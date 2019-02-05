package com.efunhub.starkio.pickpricedealer.Modal;

/**
 * Created by Admin on 19-12-2018.
 */

public class Brand {

    public String brand_id;
    public String brand_name;
    public String brandImage;


    public Brand() {
    }

    public Brand(String brand_id, String brand_name, String brandImage) {
        this.brand_id = brand_id;
        this.brand_name = brand_name;
        this.brandImage = brandImage;
    }

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getBimage() {
        return brandImage;
    }

    public void setBimage(String brandImage) {
        this.brandImage = brandImage;
    }
}
