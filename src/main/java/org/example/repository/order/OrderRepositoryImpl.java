package org.example.repository.order;

import lombok.SneakyThrows;
import org.example.domain.model.Order;
import org.example.repository.user.UserRepositoryImpl;
import org.example.util.BeanUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderRepositoryImpl implements OrderRepository{
    private final Connection connection = BeanUtil.getConnection();
    private OrderRepositoryImpl(){}

    private final UserRepositoryImpl userRepository = UserRepositoryImpl.getInstance();
    @SneakyThrows
    @Override
    public Optional<List<Order>> getOrders(String userChatId) {
        String userId = userRepository.findUserByChatId(userChatId).get().getId().toString();
        PreparedStatement orders = connection.prepareStatement(ORDERS);
        orders.setString(1, userId);
        orders.execute();
        ResultSet resultSet = orders.getResultSet();
        if (resultSet.next()){
            List<Order> orders1 = new LinkedList<>();
            orders1.add(Order.map(resultSet));
            while (resultSet.next()){
                orders1.add(Order.map(resultSet));
            }
            return Optional.of(orders1);
        }else {
            return Optional.empty();
        }
    }

    private static OrderRepositoryImpl orderRepository;
    public static OrderRepositoryImpl getInstance(){
        return (orderRepository==null)?orderRepository = new OrderRepositoryImpl()
                :orderRepository;
    }
    @SneakyThrows
    @Override
    public int save(Order order) {
        PreparedStatement save = connection.prepareStatement(SAVE);
        save.setString(1, order.getProductId().toString());
        save.setString(2, order.getUserId().toString());
        save.setInt(3, order.getAmount());
        save.setDouble(4, order.getTotalPrice());
        save.setDouble(5, order.getProductPrice());
        save.setString(6, order.getProductName());
        save.execute();
        return 0;
    }

    @Override
    public Order getById(UUID id) {
        return null;
    }

    @Override
    public void update(Order order) {

    }
}
