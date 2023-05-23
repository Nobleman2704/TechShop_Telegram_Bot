package org.example.service.basket;

import org.example.domain.DTO.BaseResponse;
import org.example.domain.model.Basket;
import org.example.service.BaseService;

import java.util.List;
import java.util.UUID;

public interface BasketService extends BaseService {
    BaseResponse<List<Basket>> getBasket(String userId);

    void addBasket(String userId, String data);

    void cleanUserBasket(String userId);

    void cleanBasket(String userId);

    UUID getBasketIdFromUserBasket(String userId);

    Basket findById(UUID basketId);

    void cleanBasket(UUID id, UUID id1);

    BaseResponse changeProductAmount(String userId, String data);
}
