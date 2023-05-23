package org.example.service.order;

import org.example.domain.DTO.BaseResponse;
import org.example.domain.model.Basket;
import org.example.domain.model.Order;
import org.example.domain.model.user.User;
import org.example.repository.order.OrderRepository;
import org.example.repository.order.OrderRepositoryImpl;
import org.example.service.basket.BasketService;
import org.example.service.basket.BasketServiceImpl;
import org.example.service.user.UserService;
import org.example.service.user.UserServiceImpl;
import org.example.util.BeanUtil;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderServiceImpl implements OrderService {
    private final UserService userService = UserServiceImpl.getInstance();
    private final BasketService basketService = BasketServiceImpl.getInstance();
    private final OrderRepository orderRepository = OrderRepositoryImpl.getInstance();

    private OrderServiceImpl(){}
    private static OrderServiceImpl orderService;
    public static OrderServiceImpl getInstance(){
        return (orderService==null)?orderService=new OrderServiceImpl()
                :orderService;
    }

    @Override
    public BaseResponse<String> getOrders(String userId) {
        StringBuilder builder = new StringBuilder();
        Optional<List<Order>> optionalOrders = orderRepository.getOrders(userId);
        if (optionalOrders.isEmpty()){
            return new BaseResponse<>("You have not ordered anything yet", 404);
        }
        List<Order> orders = optionalOrders.get();
        for (Order order : orders) {
            String data = "👨‍💻 Name:" + order.getProductName() +
                    ",\n💵 Price: " + order.getProductPrice() +
                    ",\n🔢 Amount: " + order.getAmount() +
                    ",\n🤑 Total Price: " + order.getTotalPrice() +
                    ",\n⌚ Ordered date: " + order.getUpdatedDate() +
                    "\n****************************************\n";
            builder.append(data);
        }
        return new BaseResponse<>("found", 777, builder.toString());
    }

    @Override
    public BaseResponse orderProduct(String userId) {
        User user = userService.getByChatId(userId).getData();
        UUID basketId = basketService.getBasketIdFromUserBasket(user.getId().toString());
        Basket basket = basketService.findById(basketId);
        if (user.getBalance() < basket.getTotalPrice()){
            return new BaseResponse()
                    .setMessage("You do not have enough money");
        }else {
            user.setBalance(user.getBalance() - basket.getTotalPrice());
            basketService.cleanBasket(user.getId(), basket.getId());
            userService.updateUser(user);
            save(basket);
            return new BaseResponse()
                    .setMessage("Order is successfully completed");
        }

    }

    @Override
    public BaseResponse orderAll(String userId) {
        User user = userService.getByChatId(userId).getData();
        List<Basket> baskets = basketService.getBasket(userId).getData();
        Double totalAmount = 0d;
        for (Basket basket : baskets) {
            totalAmount+=basket.getTotalPrice();
        }
        if (user.getBalance() < totalAmount ){
            return new BaseResponse<>()
                    .setMessage("You do not have enough money");
        }
        for (Basket basket : baskets) {
            save(basket);
        }
        user.setBalance(user.getBalance() - totalAmount);
        userService.updateUser(user);
        basketService.cleanUserBasket(userId);
        return new BaseResponse<>()
                .setMessage("Ordering to all has successfully been completed");
    }


    @Override
    public BaseResponse save(Basket basket) {
        Order order = BeanUtil.getModelMapper().map(basket, Order.class);
        orderRepository.save(order);
        return new BaseResponse<>()
                .setData("saved");
    }

    @Override
    public BaseResponse getById(UUID id) {
        return null;
    }
}
