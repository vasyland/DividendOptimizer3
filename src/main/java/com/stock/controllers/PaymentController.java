package com.stock.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    
    @Value("${stripe.api.key}")
    private String stripeApiKey;
    
//    @PostMapping("/create-payment-intent")
//    public Map<String, Object> createPaymentIntent(@RequestBody Map<String, Object> payload) {
//        Stripe.apiKey = stripeApiKey;
//        
//        try {
//            Long amount = Long.parseLong(payload.get("amount").toString());
//            String currency = (String) payload.get("currency");
//            
//            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
//                .setAmount(amount)
//                .setCurrency(currency)
//                .setAutomaticPaymentMethods(
//                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
//                        .setEnabled(true)
//                        .build()
//                )
//                .build();
//            
//            PaymentIntent paymentIntent = PaymentIntent.create(params);
//            
//            Map<String, Object> response = new HashMap<>();
//            response.put("clientSecret", paymentIntent.getClientSecret());
//            return response;
//        } catch (StripeException e) {
//            throw new RuntimeException("Error creating payment intent", e);
//        }
//    }
}
