package com.abhi.microservices.orderservice;

import lombok.Data;

@Data
public class ProductResponse {
    private String id;
    private String name;
    private double price;
}