package com.codervai.campusdeal.model;

import org.parceler.Parcel;

import java.util.Date;
import java.util.Objects;

@Parcel
public class Deal {
    private DealRequest dealInfo;
    private Product product;
    private Date date;

    public Deal() {
    }

    public Deal(DealRequest dealInfo, Product product, Date date) {
        this.dealInfo = dealInfo;
        this.product = product;
        this.date = date;
    }

    public DealRequest getDealInfo() {
        return dealInfo;
    }

    public void setDealInfo(DealRequest dealInfo) {
        this.dealInfo = dealInfo;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date dealDate) {
        this.date = dealDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deal deal = (Deal) o;
        return this.product.getId().equals(deal.product.getId());
    }
}
