package com.ecommerce.e_commerce_api.service;

import com.ecommerce.e_commerce_api.repository.OrderRepository;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderCodeGenerator {

    private final OrderRepository orderRepository;

    public OrderCodeGenerator(OrderRepository orderRepository){
        this.orderRepository=orderRepository;
    }

    public String generateOrderCode(){
        LocalDate today=LocalDate.now();
        String datePart=today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        LocalDateTime startOfDay=today.atStartOfDay();
        LocalDateTime endOfDay=today.atTime(LocalTime.MAX);

        long todayOrderCount=orderRepository.countByOrderDateBetween(startOfDay,endOfDay);
        long nextSequence =todayOrderCount;

        String sequencePart=String.format("%04d",nextSequence);

        return "ORD-" + datePart + "-" + sequencePart;
    }
}
