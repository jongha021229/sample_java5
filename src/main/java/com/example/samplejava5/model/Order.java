package com.example.samplejava5.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class Order implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank
    private String product;

    @NotNull
    @Positive
    private Integer quantity;

    @NotNull
    @Positive
    private Double price;

    private String customer;

    public Order() {
    }

    public Order(String product, Integer quantity, Double price, String customer) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.customer = customer;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }
}
