package com.abhi.microservices.orderservice;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "orders") // 'order' is a reserved keyword in SQL
@Data
public class Order {
    @Id
    private String orderId;
    private String productId;
    private int quantity;
    private double totalAmount;
}