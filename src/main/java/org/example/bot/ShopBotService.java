package org.example.bot;

import org.example.domain.DTO.BaseResponse;
import org.example.domain.DTO.UserCreateDTO;
import org.example.domain.DTO.UserStateDTO;
import org.example.domain.model.user.UserState;
import org.example.service.basket.BasketService;
import org.example.service.basket.BasketServiceImpl;
import org.example.service.order.OrderService;
import org.example.service.order.OrderServiceImpl;
import org.example.service.product.ProductService;
import org.example.service.product.ProductServiceImpl;
import org.example.service.user.UserService;
import org.example.service.user.UserServiceImpl;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.UUID;

public class ShopBotService {
    private static ShopBotService shopBotService;
    private ShopBotService(){}
    public static ShopBotService getInstance(){
        return (shopBotService==null)? shopBotService = new ShopBotService():shopBotService;
    }
    private final UserService userService = UserServiceImpl.getInstance();
    private final OrderService orderService = OrderServiceImpl.getInstance();
    private final BasketService basketService = BasketServiceImpl.getInstance();
    private final ProductService productService = ProductServiceImpl.getInstance();

    private final ReplyKeyboardService keyboardService = ReplyKeyboardService.getInstance();


    public SendMessage registerUser(String chatId, Contact contact){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        UserCreateDTO userCreateDTO = new UserCreateDTO(
                contact.getFirstName() + " " + contact.getLastName(),
                contact.getPhoneNumber(), chatId);
        BaseResponse response = userService.save(userCreateDTO);

        if(response.getStatus() == 200) {
            userService.updateState(new UserStateDTO(chatId, UserState.REGISTERED));
            sendMessage.setReplyMarkup(keyboardService.mainMenu());
        }

        sendMessage.setText(response.getMessage());
        return sendMessage;
    }

    public UserState checkState(String chatId) {
        BaseResponse<UserStateDTO> response = userService.getUserState(chatId);
        if(response.getStatus() == 200) {
            return response.getData().state();
        }
        return UserState.START;
    }

    public SendMessage shareContact(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, "Please register yourself");
        sendMessage.setReplyMarkup(keyboardService.requestContact());
        return sendMessage;
    }
    public SendMessage getMenu(String chatId){
        SendMessage sendMessage = new SendMessage(chatId, "You are in menu");
        sendMessage.setReplyMarkup(keyboardService.mainMenu());
        userService.updateState(new UserStateDTO(chatId, UserState.REGISTERED));
        return sendMessage;
    }


    public SendMessage getWrongMessage(String userId) {
        userService.updateState(new UserStateDTO(userId, UserState.IDLE));
        SendMessage sendMessage = new SendMessage(userId, "You sent wrong request");
        sendMessage.setReplyMarkup(keyboardService.getWrongMessage());
        return sendMessage;
    }

    public UserState navigateMenu(String request, String userId) {
        UserState userState;
        switch (request){
            case "📋 Categories" -> userState = UserState.CATEGORIES;
            case "🧺 Basket" -> userState = UserState.BASKET_LIST;
            case "🗒️ Orders history" -> userState = UserState.ORDERS_HISTORY;
            case "💰️ Get balance" -> userState = UserState.GET_BALANCE;
            case "💸 Add balance" -> userState = UserState.ADD_BALANCE;
            default -> userState = UserState.IDLE;
        }
        userService.updateState(new UserStateDTO(userId, userState));
        return userState;
    }

    public SendMessage getBalance(String userId) {
        userService.updateState(new UserStateDTO(userId, UserState.REGISTERED));
        BaseResponse<Double> userBalance = userService.getBalance(userId);
        return new SendMessage(userId, "Your balance is " + userBalance.getData() + " $");
    }

    public SendMessage addBalance(String userId) {
        return new SendMessage(userId, "Please write number (number > 0) 🔢");
    }

    public SendMessage fillBalance(String userId, String request) {
        BaseResponse baseResponse = userService.fillBalance(userId, request);
        userService.updateState(new UserStateDTO(userId, UserState.REGISTERED));
        return new SendMessage(userId, baseResponse.getMessage());
    }

    public SendMessage getCategories(String userId) {
        userService.updateState(new UserStateDTO(userId, UserState.CATEGORIES));
        ReplyKeyboard replyKeyboard = keyboardService.getCategories();
        SendMessage sendMessage = new SendMessage(userId, "Choose one");
        sendMessage.setReplyMarkup(replyKeyboard);
        return sendMessage;
    }

    public SendMessage getOrders(String userId) {
        userService.updateState(new UserStateDTO(userId, UserState.REGISTERED));
        BaseResponse<String> userOrders = orderService.getOrders(userId);
        String response;
        if (userOrders.getStatus() == 777){
            response = userOrders.getData();
        }else response = userOrders.getMessage();
        return new SendMessage(userId, response);
    }

    public SendMessage getBaskets(String userId) {
        userService.updateState(new UserStateDTO(userId, UserState.BASKET_LIST));
        ReplyKeyboard replyKeyboard = keyboardService.getBaskets(userId);
        if (replyKeyboard != null){
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(userId);
            sendMessage.setText("Here is your basket");
            sendMessage.setReplyMarkup(replyKeyboard);
            return sendMessage;
        }else {
            SendMessage sendMessage = getMenu(userId);
            sendMessage.setText("basket is empty");
            return sendMessage;
        }
    }

    public SendMessage clearMenu(String userId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Menu cleared");
        sendMessage.setChatId(userId);
        sendMessage.setReplyMarkup(keyboardService.clearMenu());
        return sendMessage;
    }

    public SendMessage getProducts(String userId, String productType) {
        userService.updateState(new UserStateDTO(userId, UserState.PRODUCT_LIST));
        SendMessage sendMessage = new SendMessage(userId, "You can choose one");
        sendMessage.setReplyMarkup(keyboardService.getProducts(productType));
        return sendMessage;
    }

    public void updateUserState(String userId, UserState state) {
        userService.updateState(new UserStateDTO(userId, state));
    }

    public EditMessageText getProduct(String userId, String productId, Integer messageId) {
        userService.updateState(new UserStateDTO(userId, UserState.PRODUCT));
        userService.noteProduct(userId, productId);
        EditMessageText edit = new EditMessageText();
        BaseResponse<String> productInfo = productService.getById(UUID.fromString(productId));
        edit.setChatId(userId);
        edit.setText(productInfo.getData());
        edit.setMessageId(messageId);
        edit.setReplyMarkup(keyboardService.getProduct());
        return edit;
    }

    public EditMessageText clearInline(String userId, Integer messageId) {
        EditMessageText set = new EditMessageText();
        set.setChatId(userId);
        set.setText(".");
        set.setMessageId(messageId);
        set.setReplyMarkup(null);
        return set;
    }

    public EditMessageText getProducts(String userId, Integer messageId) {
        userService.updateState(new UserStateDTO(userId, UserState.PRODUCT_LIST));
        EditMessageText edit = new EditMessageText();
        edit.setChatId(userId);
        edit.setMessageId(messageId);
        edit.setText("Here they are");
        edit.setReplyMarkup(keyboardService
                .getProducts(productService
                        .getProductType(userId)));
        return edit;
    }

    public EditMessageText addBasket(String userId, String data, Integer messageId) {
        basketService.addBasket(userId, data);
        EditMessageText edit = new EditMessageText();
        edit.setChatId(userId);
        edit.setText("Product has been added to your basket 🧺");
        edit.setMessageId(messageId);
        edit.setReplyMarkup(keyboardService.addBasket());
        return edit;
    }

    public EditMessageText getBasket(String userId, String data, Integer messageId) {
        userService.updateState(new UserStateDTO(userId, UserState.BASKET));
        BaseResponse<String> basketInfo =  basketService.getById(UUID.fromString(data));
        EditMessageText edit = new EditMessageText();
        edit.setChatId(userId);
        edit.setMessageId(messageId);
        edit.setText(basketInfo.getData());
        edit.setReplyMarkup(keyboardService.getBasket());
        return edit;
    }

    public EditMessageText clearUserBasket(String userId, Integer messageId) {
        basketService.cleanUserBasket(userId);
        EditMessageText edit = new EditMessageText();
        edit.setMessageId(messageId);
        edit.setChatId(userId);
        edit.setText("Your basket has been cleaned");
        return edit;
    }

    public EditMessageText getBaskets(String userId, Integer messageId) {
        userService.updateState(new UserStateDTO(userId, UserState.BASKET_LIST));
        EditMessageText edit = new EditMessageText();
        edit.setChatId(userId);
        edit.setText("Your are in your basket");
        edit.setMessageId(messageId);
        edit.setReplyMarkup(keyboardService.getBaskets(userId));
        return edit;
    }

    public SendMessage clearBasket(String userId) {
        basketService.cleanBasket(userId);
        SendMessage message = getBaskets(userId);
        message.setText("Cleaned");
        return message;
    }

    public SendMessage orderProduct(String userId) {
        System.out.println("In ordering");
        BaseResponse orderResponse = orderService.orderProduct(userId);
        SendMessage message = getBaskets(userId);
        message.setText(orderResponse.getMessage());
        return message;
    }

    public EditMessageText changeProductAmountBasket(String userId, String data, Integer messageId) {
        BaseResponse<String> response = basketService.changeProductAmount(userId, data);
        EditMessageText edit = new EditMessageText();
        edit.setChatId(userId);
        edit.setMessageId(messageId);
        edit.setText(response.getData());
        edit.setReplyMarkup(keyboardService.getBasket());
        return edit;
    }

    public SendMessage orderAll(String userId) {
        BaseResponse response = orderService.orderAll(userId);
        SendMessage message = getBaskets(userId);
        message.setText(response.getMessage());
        return message;
    }
}
