package com.codervai.campusdeal.model;

import org.parceler.Parcel;

import java.util.Date;
import java.util.List;

@Parcel
public class Product {
    private String id;
    private String title;
    private String description;
    private String category;
    private double price;
    private boolean negotiable;

    private boolean urgent;

    private String sellerId;

    private String sellerName;
    private Date uploadDate;
    private List<String> imageUriList;
    private MyLocation productLocation;

    public Product() {
    }

    public Product(String id, String title, String description, String category, double price, boolean negotiable, boolean urgent, String sellerId, String sellerName, Date uploadDate, List<String> imageUriList, MyLocation productLocation) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        this.negotiable = negotiable;
        this.urgent = urgent;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.uploadDate = uploadDate;
        this.imageUriList = imageUriList;
        this.productLocation = productLocation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isNegotiable() {
        return negotiable;
    }

    public void setNegotiable(boolean negotiable) {
        this.negotiable = negotiable;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public List<String> getImageUriList() {
        return imageUriList;
    }

    public void setImageUriList(List<String> imageUriList) {
        this.imageUriList = imageUriList;
    }

    public MyLocation getProductLocation() {
        return productLocation;
    }

    public void setProductLocation(MyLocation productLocation) {
        this.productLocation = productLocation;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(!(obj instanceof Product)) return false;
        Product product = (Product) obj;
        return this.id.equals(product.getId());
    }
}
