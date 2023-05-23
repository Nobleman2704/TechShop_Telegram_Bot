package org.example.repository.basket;

import lombok.SneakyThrows;
import org.example.domain.model.Basket;
import org.example.repository.user.UserRepository;
import org.example.repository.user.UserRepositoryImpl;
import org.example.util.BeanUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasketRepositoryImpl implements BasketRepository{
    private final Connection connection = BeanUtil.getConnection();
    private final UserRepository userRepository = UserRepositoryImpl.getInstance();
    private BasketRepositoryImpl(){}

    @SneakyThrows
    @Override
    public void addAmount(UUID userId, int amount, String productId) {
        PreparedStatement addAmount = connection.prepareStatement(ADD_AMOUNT);
        addAmount.setInt(1, amount);
        addAmount.setString(2, productId);
        addAmount.setString(3, userId.toString());
        addAmount.execute();
    }

    @SneakyThrows
    @Override
    public Boolean checkBasket(String userId, String productId) {
        PreparedStatement check = connection.prepareStatement(CHECK_BASKET);
        check.setString(1, userId);
        check.setString(2, productId);
        check.execute();
        ResultSet resultSet = check.getResultSet();
        if (resultSet.next()){
            return resultSet.getBoolean(1);
        }
        return null;
    }

    @SneakyThrows
    @Override
    public void addBasket(Basket basket) {
        PreparedStatement add = connection.prepareStatement(ADD_BASKET);
        add.setString(1, basket.getProductId().toString());
        add.setString(2, basket.getUserId().toString());
        add.setInt(3, basket.getAmount());
        add.setDouble(4, basket.getTotalPrice());
        add.setDouble(5, basket.getProductPrice());
        add.setString(6, basket.getProductName());
        add.execute();
    }

    private static BasketRepositoryImpl basketRepository;
    public static BasketRepositoryImpl getInstance(){
        return (basketRepository == null)? basketRepository = new BasketRepositoryImpl()
                :basketRepository;
    }

    @Override
    public int save(Basket basket) {
        return 0;
    }

    @SneakyThrows
    @Override
    public void addUserBaskets(UUID userId, UUID basketId) {
        PreparedStatement addUserBasket = connection.prepareStatement(ADD_USER_BASKETS);
        addUserBasket.setString(1, userId.toString());
        addUserBasket.setString(2, basketId.toString());
        addUserBasket.execute();
    }

    @SneakyThrows
    @Override
    public void cleanBasket(String userId, String basketId) {
        PreparedStatement cleanBasket = connection.prepareStatement(CLEAN_USER_BASKET);
        cleanBasket.setString(1, userId);
        cleanBasket.setString(2, basketId);
        cleanBasket.execute();
    }

    @SneakyThrows
    @Override
    public UUID getBasketIdFromUserBasket(String userId) {
        PreparedStatement getBasketId = connection.prepareStatement(GET_BASKET_ID_FROM_USER_BASKET);
        getBasketId.setString(1, userId);
        getBasketId.execute();
        ResultSet resultSet = getBasketId.getResultSet();
        UUID basket_id = null;
        if (resultSet.next()){
            basket_id = UUID.fromString(resultSet.getString(1));
        }
        return basket_id;
    }


    @SneakyThrows
    @Override
    public Basket getById(UUID id) {
        PreparedStatement getBasket = connection.prepareStatement(GET_BASKET);
        getBasket.setString(1, id.toString());
        getBasket.execute();
        ResultSet resultSet = getBasket.getResultSet();
        Basket basket = null;
        if (resultSet.next()){
            basket = Basket.map(resultSet);
        }
        return basket;
    }

    @SneakyThrows
    @Override
    public void update(Basket basket) {
        PreparedStatement update = connection.prepareStatement(UPDATE);
        update.setInt(1, basket.getAmount());
        update.setDouble(2, basket.getTotalPrice());
        update.setString(3, basket.getId().toString());
        update.execute();
    }

    @SneakyThrows
    @Override
    public void cleanUserBasket(String userId) {
        PreparedStatement clean = connection.prepareStatement(CLEAN_BASKET);
        clean.setString(1, userId);
        clean.execute();
    }

    @SneakyThrows
    @Override
    public Optional<List<Basket>> getBasket(String chatId) {
        String userId = userRepository.findUserByChatId(chatId).get().getId().toString();
        PreparedStatement findBaskets = connection.prepareStatement(GET_BASKETS);
        findBaskets.setString(1, userId);
        findBaskets.execute();
        ResultSet resultSet = findBaskets.getResultSet();
        if (resultSet.next()){
            List<Basket> baskets = new LinkedList<>();
            baskets.add(Basket.map(resultSet));
            while (resultSet.next()){
                baskets.add(Basket.map(resultSet));
            }
            return Optional.of(baskets);
        }
        return Optional.empty();
    }
}
