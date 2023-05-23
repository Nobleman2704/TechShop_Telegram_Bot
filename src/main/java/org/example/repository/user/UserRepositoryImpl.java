package org.example.repository.user;

import lombok.SneakyThrows;
import org.example.domain.model.user.User;
import org.example.domain.model.user.UserState;
import org.example.util.BeanUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserRepositoryImpl implements UserRepository {
    private final Connection connection = BeanUtil.getConnection();
    private static UserRepositoryImpl userRepository;

    @SneakyThrows
    @Override
    public void noteProduct(String userId, String productId) {
        PreparedStatement noteProduct = connection.prepareStatement(NOTE_USER);
        noteProduct.setString(1, userId);
        noteProduct.setString(2, productId);
        noteProduct.execute();
    }

    public static UserRepositoryImpl getInstance() {
        return (userRepository==null)?userRepository = new UserRepositoryImpl()
                :userRepository;
    }

    private UserRepositoryImpl() {}

    @SneakyThrows
    @Override
    public int save(User user) {
        PreparedStatement insertStatement = connection.prepareStatement(INSERT);
        insertStatement.setString(1, user.getFullName());
        insertStatement.setString(2, user.getPhoneNumber());
        insertStatement.setString(3, user.getChatId());
        insertStatement.setString(4, user.getState().toString());
        insertStatement.execute();
        return 200;
    }

    @Override
    public User getById(UUID id) {
        return null;
    }

    @SneakyThrows
    @Override
    public void update(User user) {
        PreparedStatement updateBalance = connection.prepareStatement(UPDATE_BALANCE);
        updateBalance.setDouble(1, user.getBalance());
        updateBalance.setString(2, user.getChatId());
        updateBalance.execute();
    }

    @SneakyThrows
    @Override
    public Optional<User> findUserByChatId(String chatId) {
        PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CHAT_ID);
        preparedStatement.setString(1, chatId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return Optional.of(User.map(resultSet));
        }
        return Optional.empty();
    }

    @SneakyThrows
    @Override
    public void updateUserState(UserState state, String chatId) {
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_STATE);
        preparedStatement.setString(1, state.toString());
        preparedStatement.setString(2, chatId);
        preparedStatement.execute();
    }
}
