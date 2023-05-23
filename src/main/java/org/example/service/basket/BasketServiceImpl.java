package org.example.service.basket;

import org.example.domain.DTO.BaseResponse;
import org.example.domain.model.Basket;
import org.example.domain.model.product.Product;
import org.example.repository.basket.BasketRepository;
import org.example.repository.basket.BasketRepositoryImpl;
import org.example.service.product.ProductService;
import org.example.service.product.ProductServiceImpl;
import org.example.service.user.UserService;
import org.example.service.user.UserServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasketServiceImpl implements BasketService{
    private final UserService userService = UserServiceImpl.getInstance();
    private final ProductService productService = ProductServiceImpl.getInstance();
    private final BasketRepository basketRepository = BasketRepositoryImpl.getInstance();
    private BasketServiceImpl(){}
    private static BasketServiceImpl basketService;

    @Override
    public void addBasket(String userId, String data) {
        int amount = Integer.parseInt(data);
        UUID user_Id = userService.getByChatId(userId).getData().getId();
        Product product = productService.getProduct(user_Id.toString());
        Boolean isProductIdExistInBasket = basketRepository.checkBasket(user_Id.toString(), product.getId().toString());
        if (isProductIdExistInBasket) {
            Basket basket = new Basket();
            basket
                    .setProductId(product.getId())
                    .setUserId(user_Id)
                    .setAmount(amount)
                    .setTotalPrice(amount * product.getPrice())
                    .setProductPrice(product.getPrice())
                    .setProductName(product.getName());
            basketRepository.addBasket(basket);
        }else {
            basketRepository.addAmount(user_Id, amount, product.getId().toString());
        }
    }

    public static BasketServiceImpl getInstance(){
        return (basketService==null)?basketService = new BasketServiceImpl()
                :basketService;
    }
    @Override
    public BaseResponse save(Object o) {
        return null;
    }

    @Override
    public BaseResponse<String> getById(UUID basketId) {
        Basket basket = basketRepository.getById(basketId);
        basketRepository.addUserBaskets(basket.getUserId(), basketId);
        String data = "👨‍💻 Name:" + basket.getProductName() +
                ",\n💵 Price: " + basket.getProductPrice() +
                ",\n🔢 Amount: " + basket.getAmount() +
                ",\n🤑 Total Price: " + basket.getTotalPrice() +
                ",\n⌚ Date added to Basket: " + basket.getUpdatedDate() +
                "\n\n Click ➕ or ➖ to change amount";
        return new BaseResponse<String>()
                .setData(data);
    }

    @Override
    public void cleanBasket(String userId) {
        String user_Id = userService.getByChatId(userId).getData().getId().toString();
        UUID basketId = basketRepository.getBasketIdFromUserBasket(user_Id);
        basketRepository.cleanBasket(user_Id, basketId.toString());
    }

    @Override
    public UUID getBasketIdFromUserBasket(String userId) {
        return basketRepository.getBasketIdFromUserBasket(userId);
    }

    @Override
    public Basket findById(UUID basketId) {
        return basketRepository.getById(basketId);
    }

    @Override
    public void cleanBasket(UUID userId, UUID basketId) {
        basketRepository.cleanBasket(userId.toString(), basketId.toString());
    }

    @Override
    public BaseResponse changeProductAmount(String userId, String amount) {
        String user_Id = userService.getByChatId(userId).getData().getId().toString();
        UUID basket_Id = basketRepository.getBasketIdFromUserBasket(user_Id);
        Basket basket = basketRepository.getById(basket_Id);
        int digitOne = Integer.parseInt(amount);
        basket.setAmount(basket.getAmount() + digitOne);
        basket.setTotalPrice(basket.getAmount()*basket.getProductPrice());
        BaseResponse<String> byId = getById(basket_Id);
        if (basket.getAmount() == 0){
            String data = byId.getData() +
                    "\n\n\nSorry, minimum product amount is 1 🙂";
            return new BaseResponse<>()
                    .setData(data);
        }
        basketRepository.update(basket);
        return new BaseResponse<>().setData(byId.getData() +
                "\n\n\nAmount has been changed");
    }

    @Override
    public void cleanUserBasket(String userId) {
        String user_Id = userService.getByChatId(userId).getData().getId().toString();
        basketRepository.cleanUserBasket(user_Id);
    }

    @Override
    public BaseResponse<List<Basket>> getBasket(String userId) {
        Optional<List<Basket>> baskets = basketRepository.getBasket(userId);
        if (baskets.isEmpty()){
            return new BaseResponse<>("Not found", 404);
        }
        return new BaseResponse<>("found", 777, baskets.get());
    }
}
