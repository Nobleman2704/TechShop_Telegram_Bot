package org.example.repository.basket;

import org.example.domain.model.Basket;
import org.example.repository.BaseRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BasketRepository extends BaseRepository<Basket> {
    String CLEAN_USER_BASKET = """
            call clean_user_basket(?::uuid, ?::uuid);
            """;
    String GET_BASKETS = "select * from baskets where user_id = ?::uuid";
    Optional<List<Basket>> getBasket(String userId);
    String UPDATE = """
            update baskets set amount = ?, total_price = ?,updated = now()
            where id = ?::uuid""";
    String GET_BASKET = "select * from baskets where id = ?::uuid";
    String ADD_BASKET = """
            insert into baskets (product_id, user_id, amount, total_price, product_price, product_name)
            VALUES (?::uuid, ?::uuid, ?, ?, ?, ?);""";
    String CHECK_BASKET = """
                    select check_basket(?::uuid,?::uuid);""";
    String ADD_AMOUNT = """
            update baskets set amount=amount+?, updated = now()
            where product_id = ?::uuid and user_id = ?::uuid;""";
    String ADD_USER_BASKETS = """
                                call add_user_basket(?::uuid, ?::uuid);
                                """;
    String GET_BASKET_ID_FROM_USER_BASKET = """
            select basket_id from user_baskets
            where user_id = ?::uuid""";
    String CLEAN_BASKET = """
                    call clean_all_user_basket(?::uuid);
                    """;
    void addBasket(Basket basket);

    Boolean checkBasket(String userId, String productId);

    void addAmount(UUID userId, int amount, String productId);

    void cleanUserBasket(String userId);

    void addUserBaskets(UUID userId, UUID basketId);

    UUID getBasketIdFromUserBasket(String userId);

    void cleanBasket(String toString, String toString1);
}
