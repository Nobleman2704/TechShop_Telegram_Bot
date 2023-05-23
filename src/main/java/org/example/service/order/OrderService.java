package org.example.service.order;

import org.example.domain.DTO.BaseResponse;
import org.example.domain.model.Basket;
import org.example.domain.model.Order;
import org.example.service.BaseService;

public interface OrderService extends BaseService<Basket, String> {
    BaseResponse<String> getOrders(String userId);

    BaseResponse orderProduct(String userId);

    BaseResponse orderAll(String userId);
}
