package com.codervai.campusdeal.model;

import androidx.annotation.Nullable;

import org.parceler.Parcel;

import java.util.Date;

@Parcel
public class DealRequest {
    private String productId;
    private String title;
    private String buyerId;
    private String buyerName;
    private String sellerId;
    private String sellerName;
    private Date date;

    public DealRequest() {
    }

    public DealRequest(String productId, String title, String buyerId, String buyerName, String sellerId, String sellerName, Date date) {
        this.productId = productId;
        this.title = title;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.date = date;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getProductId() {
        return productId;
    }

    public String getTitle() {
        return title;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null) return false;
        if(!(obj instanceof DealRequest)) return false;

        DealRequest dealRequest = (DealRequest) obj;
        return this.productId.equals(dealRequest.productId);
    }
}
