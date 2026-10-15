package com.aiinterview.platform.model.dto.response;

import com.aiinterview.platform.model.entity.Order;
import com.aiinterview.platform.model.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderResponse {

    private Long id;
    private String orderCode;
    private Long packageId;
    private String packageName;
    private Long amount;
    private OrderStatus status;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .packageId(order.getServicePackage().getId())
                .packageName(order.getPackageName())
                .amount(order.getAmount())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .createdAt(order.getCreatedAt())
                .paidAt(order.getPaidAt())
                .build();
    }
}
