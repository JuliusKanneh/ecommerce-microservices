package com.wisdomtech.orderservice.service;

import com.wisdomtech.orderservice.config.WebClientConfig;
import com.wisdomtech.orderservice.dto.OrderLineItemDto;
import com.wisdomtech.orderservice.dto.OrderRequest;
import com.wisdomtech.orderservice.model.Order;
import com.wisdomtech.orderservice.model.OrderLineItem;
import com.wisdomtech.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClientConfig webClient;

    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItemDto()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItems(orderLineItems);

        //call inventory service to check if product exist. If exists, make order otherwise, handle exception.
        //use webclient to make a synchronous request

        Boolean result = webClient.webClient().get()
                .uri("http://localhost:9091/api/inventory")
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (result){
            orderRepository.save(order);
        }else {
            throw new IllegalArgumentException("Product not in stock, please try again later");
        }

    }

    private OrderLineItem mapToDto(OrderLineItemDto orderLineItemDto) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setPrice(orderLineItemDto.getPrice());
        orderLineItem.setQuantity(orderLineItemDto.getQuantity());
        orderLineItem.setSkuCode(orderLineItemDto.getSkuCode());
        return orderLineItem;
    }
}
