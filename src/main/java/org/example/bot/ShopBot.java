package org.example.bot;

import lombok.SneakyThrows;
import org.example.domain.model.user.UserState;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShopBot extends TelegramLongPollingBot {
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final ShopBotService botService = ShopBotService.getInstance();

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        executor.execute(() -> {
            if (update.hasCallbackQuery()) {
//            User user = update.getMessage().getFrom();
//            System.out.println(user.getFirstName() + " " + user.getLastName() + " is using");
                CallbackQuery callbackQuery = update.getCallbackQuery();
                Message message = callbackQuery.getMessage();
                String data = callbackQuery.getData();
                String userId = message.getChatId().toString();
                Integer messageId = message.getMessageId();

                UserState userState = botService.checkState(userId);

                SendMessage sendMessage = null;

                switch (userState) {
                    case PRODUCT -> {
                        if (data.equals("REGISTERED")) {
                            sendMessage = botService.getMenu(userId);
                            EditMessageText set = botService.clearInline(userId, messageId);
                            try {
                                execute(set);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        } else if (data.equals("PRODUCT_LIST")) {
                            EditMessageText edit = botService.getProducts(userId, messageId);
                            try {
                                execute(edit);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                            return;
                        } else if (data.equals("BASKET_LIST")) {
                            EditMessageText edit = botService.getBaskets(userId, messageId);
                            try {
                                execute(edit);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                            return;
                        } else {
                            EditMessageText edit = botService.addBasket(userId, data, messageId);
                            try {
                                execute(edit);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                            return;
                        }
                    }
                    case PRODUCT_LIST -> {
                        if (data.equals("CATEGORIES")) {
                            sendMessage = botService.getCategories(userId);
                            ReplyKeyboard replyMarkup = sendMessage.getReplyMarkup();
                            EditMessageText edit = new EditMessageText();
                            edit.setChatId(userId);
                            edit.setMessageId(messageId);
                            edit.setText("You are in categories");
                            edit.setReplyMarkup((InlineKeyboardMarkup) replyMarkup);
                            try {
                                execute(edit);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            EditMessageText edit = botService.getProduct(userId, data, messageId);
                            try {
                                execute(edit);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        return;
                    }
                    case CATEGORIES -> {
                        if (data.equals("MENU") || data.equals("REGISTERED")) {
                            sendMessage = botService.getMenu(userId);
                            EditMessageText set = botService.clearInline(userId, messageId);
                            try {
                                execute(set);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            sendMessage = botService.getProducts(userId, data);
                            ReplyKeyboard replyMarkup = sendMessage.getReplyMarkup();
                            EditMessageText set = new EditMessageText();
                            set.setChatId(userId);
                            set.setText("Here is you can choose one product");
                            set.setMessageId(messageId);
                            set.setReplyMarkup((InlineKeyboardMarkup) replyMarkup);
                            try {
                                execute(set);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                            return;
                        }
                    }
                    case BASKET_LIST -> {
                        if (data.equals("MENU") || data.equals("REGISTERED")) {
                            sendMessage = botService.getMenu(userId);
                            EditMessageText set = botService.clearInline(userId, messageId);
                            try {
                                execute(set);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        } else if (data.equals("REMOVE_ALL")) {
                            EditMessageText edit = botService.clearUserBasket(userId, messageId);
                            try {
                                execute(edit);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                            sendMessage = botService.getMenu(userId);
                        } else if (data.equals("ORDER_ALL")) {
                            sendMessage = botService.orderAll(userId);
                        } else {
                            EditMessageText edit = botService.getBasket(userId, data, messageId);

                            try {
                                execute(edit);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                            return;
                        }
                    }
                    case BASKET -> {
                        if (data.equals("REMOVE")) {
                            sendMessage = botService.clearBasket(userId);
                        } else if (data.equals("BASKET_LIST")) {
                            sendMessage = botService.getBaskets(userId);
                            ReplyKeyboard replyMarkup = sendMessage.getReplyMarkup();
                            EditMessageText edit = new EditMessageText();
                            edit.setChatId(userId);
                            edit.setMessageId(messageId);
                            edit.setText("You are in basket");
                            edit.setReplyMarkup((InlineKeyboardMarkup) replyMarkup);
                            try {
                                execute(edit);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                            return;
                        } else if (data.equals("MENU") || data.equals("REGISTERED")) {
                            sendMessage = botService.getMenu(userId);
                            EditMessageText set = botService.clearInline(userId, messageId);
                            try {
                                execute(set);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        } else if (data.equals("ORDER")) {
                            sendMessage = botService.orderProduct(userId);
                        } else {
                            EditMessageText edit = botService.changeProductAmountBasket(userId, data, messageId);
                            try {
                                execute(edit);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                            return;
                        }
                    }
                    case IDLE -> sendMessage = botService.getMenu(userId);
                }

                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

            } else {
                Message message = update.getMessage();
                String userId = message.getChatId().toString();
                String request = message.getText();
//            User user = message.getFrom();
//            System.out.println(user.getFirstName() + " " + user.getLastName() + " is using");

                UserState userState = botService.checkState(userId);

                SendMessage sendMessage = null;

                switch (userState) {
                    case START -> {
                        if (message.hasContact()) {
                            sendMessage = botService.registerUser(userId, message.getContact());
                        } else {
                            sendMessage = botService.shareContact(userId);
                        }
                    }
                    case REGISTERED -> {
                        UserState userState1 = botService.navigateMenu(request, userId);
                        switch (userState1) {
                            case GET_BALANCE -> sendMessage = botService.getBalance(userId);
                            case ADD_BALANCE -> sendMessage = botService.addBalance(userId);
                            case CATEGORIES -> {
                                sendMessage = botService.clearMenu(userId);
                                try {
                                    execute(sendMessage);
                                } catch (TelegramApiException e) {
                                    throw new RuntimeException(e);
                                }
                                sendMessage = botService.getCategories(userId);
                            }
                            case BASKET_LIST -> {
                                sendMessage = botService.clearMenu(userId);
                                try {
                                    execute(sendMessage);
                                } catch (TelegramApiException e) {
                                    throw new RuntimeException(e);
                                }
                                sendMessage = botService.getBaskets(userId);
                            }
                            case ORDERS_HISTORY -> sendMessage = botService.getOrders(userId);
                            default -> {
                                sendMessage = botService.clearMenu(userId);
                                try {
                                    execute(sendMessage);
                                } catch (TelegramApiException e) {
                                    throw new RuntimeException(e);
                                }
                                sendMessage = botService.getWrongMessage(userId);
                            }
                        }
                    }
                    case ADD_BALANCE -> {
                        sendMessage = botService.fillBalance(userId, request);
                    }
                    case IDLE, CATEGORIES, PRODUCT, PRODUCT_LIST, BASKET, BASKET_LIST -> {
                        sendMessage = botService.clearMenu(userId);
                        try {
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                        sendMessage = botService.getWrongMessage(userId);
                    }
                }

                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    @Override
    public String getBotToken() {
        return "6298982873:AAFRvpNUz4pzWYDprL9SaBCV_IDzOaGFmwo";
    }

    @Override
    public String getBotUsername() {
        return "http://t.me/g21_tech_shop_bot";
    }
}
