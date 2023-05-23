package org.example.bot;

import org.example.domain.DTO.BaseResponse;
import org.example.domain.model.Basket;
import org.example.domain.model.product.Product;
import org.example.domain.model.product.ProductType;
import org.example.domain.model.user.UserState;
import org.example.service.basket.BasketService;
import org.example.service.basket.BasketServiceImpl;
import org.example.service.product.ProductService;
import org.example.service.product.ProductServiceImpl;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ReplyKeyboardService {
    private final BasketService basketService = BasketServiceImpl.getInstance();
    private final ProductService productService = ProductServiceImpl.getInstance();
    private ReplyKeyboardService() {
    }

    private static ReplyKeyboardService replyKeyboardService;

    public static ReplyKeyboardService getInstance() {
        if (replyKeyboardService == null) {
            replyKeyboardService = new ReplyKeyboardService();
        }
        return replyKeyboardService;
    }

    public ReplyKeyboardMarkup mainMenu() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("📋 Categories");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("🧺 Basket");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("🗒️ Orders history");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("💰️ Get balance");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("💸 Add balance");
        keyboardRows.add(row);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup requestContact() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardButton button = new KeyboardButton("Please share phone number 📞");
        button.setRequestContact(true);
        keyboardRow.add(button);
        markup.setResizeKeyboard(true);
        markup.setKeyboard(List.of(keyboardRow));
        return markup;
    }

    public ReplyKeyboard getWrongMessage() {
        InlineKeyboardButton button = new InlineKeyboardButton("Click here to go back to menu");
        button.setCallbackData("REGISTERED");
        InlineKeyboardMarkup replyKeyboard = new InlineKeyboardMarkup();
        replyKeyboard.setKeyboard(List.of(List.of(button)));
        return replyKeyboard;
    }

    public ReplyKeyboard getCategories() {
        List<InlineKeyboardButton> buttons = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton("💻 PC");
        button.setCallbackData(ProductType.PC.toString());
        buttons.add(button);
        button = new InlineKeyboardButton("🧑‍💻 Laptop");
        button.setCallbackData(ProductType.LAPTOP.toString());
        buttons.add(button);

        List<InlineKeyboardButton> buttons1 = new LinkedList<>();

        button = new InlineKeyboardButton("📱 Phone");
        button.setCallbackData(ProductType.PHONE.toString());
        buttons1.add(button);
        button = new InlineKeyboardButton("🖥️ Monitor");
        button.setCallbackData(ProductType.MONITOR.toString());
        buttons1.add(button);

        List<InlineKeyboardButton> buttons2 = new LinkedList<>();
        button = new InlineKeyboardButton("⬅️ Back to Menu");
        button.setCallbackData(UserState.REGISTERED.toString());
        buttons2.add(button);

        List<List<InlineKeyboardButton>> inlineButtons = List.of(buttons, buttons1, buttons2);
        InlineKeyboardMarkup categories = new InlineKeyboardMarkup();
        categories.setKeyboard(inlineButtons);
        return categories;
    }

    public InlineKeyboardMarkup getBaskets(String userId) {
        BaseResponse<List<Basket>> baseResponse = basketService.getBasket(userId);
        if (baseResponse.getStatus() == 404) {
            return null;
        }
        List<Basket> baskets = baseResponse.getData();
        List<List<InlineKeyboardButton>> buttons = new LinkedList<>();
        for (Basket basket : baskets) {
            buttons.add(createBasketButton(basket));
        }
        List<InlineKeyboardButton> buttons1 = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton("🔙 Back");
        button.setCallbackData(UserState.REGISTERED.toString());
        buttons1.add(button);
        button = new InlineKeyboardButton("Order all");
        button.setCallbackData("ORDER_ALL");
        buttons1.add(button);
        button = new InlineKeyboardButton("Remove all");
        button.setCallbackData("REMOVE_ALL");
        buttons1.add(button);
        buttons.add(buttons1);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(buttons);

        return inlineKeyboardMarkup;
    }

    private List<InlineKeyboardButton> createBasketButton(Basket basket) {
        InlineKeyboardButton button = new InlineKeyboardButton(basket.getProductName());
        button.setCallbackData(basket.getId().toString());
        return List.of(button);
    }

    public ReplyKeyboardRemove clearMenu() {
        ReplyKeyboardRemove replyKeyboard = new ReplyKeyboardRemove();
        replyKeyboard.setRemoveKeyboard(true);
        return replyKeyboard;
    }

    public InlineKeyboardMarkup getProducts(String productType) {
        BaseResponse<List<Product>> products = productService.getProducts(productType);
        List<Product> products1 = products.getData();
        List<List<InlineKeyboardButton>> buttons = new LinkedList<>();
        for (Product product : products1) {
            buttons.add(createProductButton(product));
        }
        List<InlineKeyboardButton> buttons1 = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton("⬅️ Back");
        button.setCallbackData(UserState.CATEGORIES.toString());
        buttons1.add(button);
        buttons.add(buttons1);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(buttons);
        return inlineKeyboardMarkup;
    }

    private List<InlineKeyboardButton> createProductButton(Product product) {
        InlineKeyboardButton button = new InlineKeyboardButton(product.getName());
        button.setCallbackData(product.getId().toString());
        return List.of(button);
    }

    public InlineKeyboardMarkup getProduct() {
        InlineKeyboardMarkup inline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new LinkedList<>();
        List<InlineKeyboardButton> rows = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton("1️⃣");
        button.setCallbackData("1");
        rows.add(button);
        button = new InlineKeyboardButton("2️⃣");
        button.setCallbackData("2");
        rows.add(button);
        button = new InlineKeyboardButton("3️⃣");
        button.setCallbackData("3");
        rows.add(button);
        buttons.add(rows);
        rows = new LinkedList<>();
        button = new InlineKeyboardButton("4️⃣");
        button.setCallbackData("4");
        rows.add(button);
        button = new InlineKeyboardButton("5️⃣");
        button.setCallbackData("5");
        rows.add(button);
        button = new InlineKeyboardButton("6️⃣");
        button.setCallbackData("6");
        rows.add(button);
        buttons.add(rows);
        rows = new LinkedList<>();
        button = new InlineKeyboardButton("7️⃣");
        button.setCallbackData("7");
        rows.add(button);
        button = new InlineKeyboardButton("8️⃣");
        button.setCallbackData("8");
        rows.add(button);
        button = new InlineKeyboardButton("9️⃣");
        button.setCallbackData("9");
        rows.add(button);
        buttons.add(rows);
        rows = new LinkedList<>();
        button = new InlineKeyboardButton("🏠 Go to Menu");
        button.setCallbackData(UserState.REGISTERED.toString());
        rows.add(button);
        button = new InlineKeyboardButton("🔟");
        button.setCallbackData("10");
        rows.add(button);
        button = new InlineKeyboardButton("🔙 Back");
        button.setCallbackData(UserState.PRODUCT_LIST.toString());
        rows.add(button);
        buttons.add(rows);
        inline.setKeyboard(buttons);
        return inline;
    }

    public InlineKeyboardMarkup addBasket() {
        InlineKeyboardMarkup inline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new LinkedList<>();
        List<InlineKeyboardButton> rows = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton("Go to your Basket 🧺");
        button.setCallbackData(UserState.BASKET_LIST.toString());
        rows.add(button);
        buttons.add(rows);
        rows = new LinkedList<>();
        button = new InlineKeyboardButton("Go to Menu 🏠");
        button.setCallbackData(UserState.REGISTERED.toString());
        rows.add(button);
        button = new InlineKeyboardButton("🔙 Back");
        button.setCallbackData(UserState.PRODUCT_LIST.toString());
        rows.add(button);
        buttons.add(rows);
        inline.setKeyboard(buttons);
        return inline;
    }

    public InlineKeyboardMarkup getBasket() {
        InlineKeyboardMarkup inline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new LinkedList<>();
        List<InlineKeyboardButton> rows = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton("➖");
        button.setCallbackData("-1");
        rows.add(button);
        button = new InlineKeyboardButton("➕");
        button.setCallbackData("1");
        rows.add(button);
        buttons.add(rows);
        rows = new LinkedList<>();
        button = new InlineKeyboardButton("Remove from Basket");
        button.setCallbackData("REMOVE");
        rows.add(button);
        button = new InlineKeyboardButton("Order product");
        button.setCallbackData("ORDER");
        rows.add(button);
        buttons.add(rows);
        rows = new LinkedList<>();
        button = new InlineKeyboardButton("Go to Menu 🏠");
        button.setCallbackData(UserState.REGISTERED.toString());
        rows.add(button);
        button = new InlineKeyboardButton("🔙 Back");
        button.setCallbackData(UserState.BASKET_LIST.toString());
        rows.add(button);
        buttons.add(rows);
        inline.setKeyboard(buttons);
        return inline;
    }
}
