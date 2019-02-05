package com.efunhub.starkio.pickpricedealer.Modal;

import java.util.HashMap;

/**
 * Created by Admin on 20-12-2018.
 */

public class ProductSubSpecification {

    public String mainSpecificationId;
    public String subSpecificationId;
    public String productSubSpecificationKey;
    public String productSubSpecificationValue;

    public HashMap<String,String> productSubSpecification;

    public HashMap<String ,HashMap<String,String>> productSubSpecificationDataData;



    public ProductSubSpecification() {
    }

    public String getMainSpecificationId() {
        return mainSpecificationId;
    }

    public void setMainSpecificationId(String mainSpecificationId) {
        this.mainSpecificationId = mainSpecificationId;
    }

    public String getProductSubSpecificationKey() {
        return productSubSpecificationKey;
    }

    public void setProductSubSpecificationKey(String productSubSpecificationKey) {
        this.productSubSpecificationKey = productSubSpecificationKey;
    }

    public String getProductSubSpecificationValue() {
        return productSubSpecificationValue;
    }

    public void setProductSubSpecificationValue(String productSubSpecificationValue) {
        this.productSubSpecificationValue = productSubSpecificationValue;
    }

    public HashMap<String, String> getProductSubSpecification() {
        return productSubSpecification;
    }

    public void setProductSubSpecification(HashMap<String, String> productSubSpecification) {
        this.productSubSpecification = productSubSpecification;
    }


    public HashMap<String, HashMap<String, String>> getProductSubSpecificationDataFinal() {
        return productSubSpecificationDataData;
    }

    public void setProductSubSpecificationDataFinal(HashMap<String, HashMap<String, String>> productSubSpecificationDataFinal) {
        this.productSubSpecificationDataData = productSubSpecificationDataFinal;
    }


    public String getSubSpecificationId() {
        return subSpecificationId;
    }

    public void setSubSpecificationId(String subSpecificationId) {
        this.subSpecificationId = subSpecificationId;
    }
}
