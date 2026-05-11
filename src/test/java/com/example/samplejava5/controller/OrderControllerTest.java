package com.example.samplejava5.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createOrder() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"product\":\"Keyboard\",\"quantity\":2,\"price\":49.9,\"customer\":\"alice\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.product").value("Keyboard"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.price").value(49.9));
    }

    @Test
    void listOrders() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"product\":\"Mouse\",\"quantity\":1,\"price\":19.0,\"customer\":\"bob\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk());
    }

    @Test
    void getOrderNotFound() throws Exception {
        mockMvc.perform(get("/orders/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not found"));
    }

    @Test
    void searchOrders() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"product\":\"Monitor 27\",\"quantity\":1,\"price\":299.0,\"customer\":\"carol\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/orders/search").param("q", "monitor"))
                .andExpect(status().isOk());
    }

    @Test
    void createOrderValidation() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"product\":\"\",\"quantity\":-1,\"price\":-1}"))
                .andExpect(status().isBadRequest());
    }
}
