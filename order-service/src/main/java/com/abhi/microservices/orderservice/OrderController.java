package com.abhi.microservices.orderservice;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final RestClient restClient;

    public OrderController(OrderRepository orderRepository, RestClient restClient) {
        this.orderRepository = orderRepository;
        this.restClient = restClient;
    }

    @PostMapping
    @CircuitBreaker(name = "productServiceCB", fallbackMethod = "placeOrderFallback")
    public String placeOrder(@RequestParam String productId,@RequestParam int quantity) {
//        ProductResponse product = restClient.get()
//                .uri("http://localhost:8081/products/" + productId)
//                .retrieve()
//                .body(ProductResponse.class);
        // BEFORE: ProductResponse product = restClient.get().uri("http://localhost:8081/products/" + productId)...

        // AFTER: No ports, no localhost!
        ProductResponse product = restClient.get()
                .uri("http://product-service/products/" + productId) // <-- Dynamic Lookup!
                .retrieve()
                .body(ProductResponse.class);

        if (product == null) {
            return "Order Failed: Product doesn't exist.";
        }

        // 2. Create and Save the Order
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalAmount(product.getPrice() * quantity);

        orderRepository.save(order);

        return "Order Placed Successfully! Order ID: " + order.getOrderId() + " | Total: INR " + order.getTotalAmount();
    }

    // 2. The Fallback Method: Executed automatically when the circuit is OPEN or when a call crashes
    public String placeOrderFallback(String productId, int quantity, Exception exception) {
        return "⚠️ Order System is temporarily degraded. " +
                "We couldn't verify the product status right now. " +
                "Please try again in 10 seconds. (Reason: " + exception.getMessage() + ")";
    }
}
