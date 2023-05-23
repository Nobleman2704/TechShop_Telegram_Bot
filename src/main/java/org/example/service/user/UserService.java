package org.example.service.user;

import org.example.domain.DTO.BaseResponse;
import org.example.domain.DTO.UserCreateDTO;
import org.example.domain.DTO.UserStateDTO;
import org.example.domain.model.user.User;
import org.example.service.BaseService;

public interface UserService extends BaseService<UserCreateDTO, User> {
    BaseResponse<UserStateDTO> getUserState(String chatId);

    void updateState(UserStateDTO stateDTO);

    BaseResponse<Double> getBalance(String userId);

    BaseResponse fillBalance(String userId, String request);

    void noteProduct(String userId, String productId);
    BaseResponse<User> getByChatId(String id);

    void updateUser(User user);
}
