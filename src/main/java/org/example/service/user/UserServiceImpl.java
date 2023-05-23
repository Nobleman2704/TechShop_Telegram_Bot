package org.example.service.user;

import org.example.domain.DTO.BaseResponse;
import org.example.domain.DTO.UserCreateDTO;
import org.example.domain.DTO.UserStateDTO;
import org.example.domain.model.user.User;
import org.example.domain.model.user.UserState;
import org.example.repository.user.UserRepository;
import org.example.repository.user.UserRepositoryImpl;
import org.example.util.BeanUtil;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import java.util.UUID;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository = UserRepositoryImpl.getInstance();
    private final ModelMapper modelMapper = BeanUtil.getModelMapper();
    private UserServiceImpl(){}
    private static UserServiceImpl userService;
    public static UserServiceImpl getInstance(){
        return (userService==null)?userService=new UserServiceImpl()
                :userService;
    }

    @Override
    public void noteProduct(String userId, String productId) {
        String user_Id = userRepository.findUserByChatId(userId).get().getId().toString();
        userRepository.noteProduct(user_Id, productId);
    }

    @Override
    public BaseResponse<User> getByChatId(String id) {
        return new BaseResponse<User>()
                .setData(userRepository.findUserByChatId(id).get());
    }

    @Override
    public void updateUser(User user) {
        userRepository.update(user);
    }

    @Override
    public BaseResponse save(UserCreateDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        user.setState(UserState.START);

        int save = userRepository.save(user);

        if(save == 200) {
            return new BaseResponse("Success", 200);
        }
        return new BaseResponse("Fail", 500);
    }

    @Override
    public BaseResponse<User> getById(UUID id) {
        return null;
    }

    @Override
    public BaseResponse<Double> getBalance(String userId) {
        Optional<User> user = userRepository.findUserByChatId(userId);
        return new BaseResponse<>("done", 777, user.get().getBalance());
    }

    @Override
    public BaseResponse fillBalance(String userId, String request) {
        try {
            double amount = Double.parseDouble(request);
            if (amount <= 0){
                return new BaseResponse("Number should be positive", 987);
            }else {
                Optional<User> userByChatId = userRepository.findUserByChatId(userId);
                User user = userByChatId.get();
                user.setBalance(user.getBalance() + amount);
                userRepository.update(user);
                return new BaseResponse("Amount has been added, your balance is " +
                        user.getBalance() + " $", 777);
            }

        }catch (NumberFormatException e){
            return new BaseResponse("You should enter only number", 254);
        }
    }

    @Override
    public BaseResponse<UserStateDTO> getUserState(String chatId) {

        Optional<User> userByChatId = userRepository.findUserByChatId(chatId);
        if(userByChatId.isPresent()) {
            User user = userByChatId.get();
            UserStateDTO userStateDTO = new UserStateDTO(user.getChatId(), user.getState());
            return new BaseResponse<>("success", 200, userStateDTO);
        }

        return new BaseResponse<>("fail", 404);
    }

    @Override
    public void updateState(UserStateDTO stateDTO) {
        userRepository.updateUserState(stateDTO.state(), stateDTO.chatId());
    }
}
