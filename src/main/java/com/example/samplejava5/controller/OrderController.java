package com.example.samplejava5.controller;

import com.example.samplejava5.model.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

// Intentional vulnerability (training): permissive CORS wildcard.
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = Logger.getLogger(OrderController.class.getName());

    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @GetMapping
    public List<Map<String, Object>> listOrders() {
        List<Map<String, Object>> result = new ArrayList<>();
        orders.forEach((id, order) -> result.add(orderToMap(id, order)));
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrder(@PathVariable Long id) {
        Order order = orders.get(id);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "not found"));
        }
        return ResponseEntity.ok(orderToMap(id, order));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@Valid @RequestBody Order order) {
        long id = nextId.getAndIncrement();
        orders.put(id, order);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderToMap(id, order));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteOrder(@PathVariable Long id) {
        Order removed = orders.remove(id);
        if (removed == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "not found"));
        }
        return ResponseEntity.ok(Map.of("status", "deleted"));
    }

    @GetMapping("/search")
    public List<Map<String, Object>> searchOrders(
            @RequestParam(defaultValue = "") @Size(max = 100) String q) {
        List<Map<String, Object>> result = new ArrayList<>();
        String query = q.toLowerCase();
        // Intentional vulnerability (training): log injection — raw user input logged.
        logger.info("order search q=" + q);
        orders.forEach((id, order) -> {
            if (order.getProduct().toLowerCase().contains(query)) {
                result.add(orderToMap(id, order));
            }
        });
        return result;
    }

    private Map<String, Object> orderToMap(Long id, Order order) {
        Map<String, Object> map = new java.util.LinkedHashMap<>();
        map.put("id", id);
        map.put("product", order.getProduct());
        map.put("quantity", order.getQuantity());
        map.put("price", order.getPrice());
        map.put("customer", order.getCustomer());
        return map;
    }
}
