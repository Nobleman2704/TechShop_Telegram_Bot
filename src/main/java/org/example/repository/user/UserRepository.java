package org.example.repository.user;

import org.example.domain.model.user.User;
import org.example.domain.model.user.UserState;
import org.example.repository.BaseRepository;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User> {

    String NOTE_USER = "call note_user_product(?::uuid, ?::uuid);";
    String FIND_BY_CHAT_ID = "select * from users " +
            "where chat_id = ?";

    String INSERT = """
            insert into users(full_name, phone_number, chat_id, user_state)
            values(?, ?, ?, ?);""";

    String UPDATE_STATE = """
            update users set user_state = ?, updated = now()
            where chat_id = ?;""";

    String UPDATE_BALANCE = """
                              update users set balance = ?, updated = now()
                              where chat_id = ?""";

    Optional<User> findUserByChatId(String chatId);

    void updateUserState(UserState state,String chatId);

    void noteProduct(String userId, String productId);
}
