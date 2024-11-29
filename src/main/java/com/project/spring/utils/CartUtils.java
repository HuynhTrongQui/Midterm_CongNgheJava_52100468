package com.project.spring.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.spring.dto.CartItemDTO;
import com.project.spring.model.CartItem;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.List;

public class CartUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String convertCartToJson(List<CartItemDTO> cart) {
        try {
            return objectMapper.writeValueAsString(cart);
        } catch (JsonProcessingException e) {
            // Handle the exception appropriately (e.g., log it)
            e.printStackTrace();
            return null;
        }
    }

    public static List<CartItemDTO> convertJsonToCart(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<CartItemDTO>>() {
            });
        } catch (IOException e) {
            // Handle the exception appropriately (e.g., log it)
            e.printStackTrace();
            return null;
        }
    }

}
