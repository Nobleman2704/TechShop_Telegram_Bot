package org.example.repository.order;

import org.example.domain.model.Order;
import org.example.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends BaseRepository<Order> {
    String ORDERS = "select * from orders where user_id = ?::uuid";
    String SAVE = """
            insert into orders (product_id, user_id, amount, total_price, product_price, product_name)
            VALUES (?::uuid, ?::uuid, ?, ?, ?, ?);""";
    Optional<List<Order>> getOrders(String userId);
}
