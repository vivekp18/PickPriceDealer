package com.efunhub.starkio.pickpricedealer.Modal;

import java.util.HashMap;

/**
 * Created by Admin on 20-12-2018.
 */

public class ProductMainSpecification {


    public String main_specification_id;
    public String main_specification_name;

    public HashMap<String ,HashMap<String,String>> productSubSpecificationData;

    public ProductMainSpecification() {
    }

    public String getMain_specification_id() {
        return main_specification_id;
    }

    public void setMain_specification_id(String main_specification_id) {
        this.main_specification_id = main_specification_id;
    }

    public String getMain_specification_name() {
        return main_specification_name;
    }

    public void setMain_specification_name(String main_specification_name) {
        this.main_specification_name = main_specification_name;
    }

    public HashMap<String, HashMap<String, String>> getProductSubSpecificationData() {
        return productSubSpecificationData;
    }

    public void setProductSubSpecificationData(HashMap<String, HashMap<String, String>> productSubSpecificationData) {
        this.productSubSpecificationData = productSubSpecificationData;
    }
}
