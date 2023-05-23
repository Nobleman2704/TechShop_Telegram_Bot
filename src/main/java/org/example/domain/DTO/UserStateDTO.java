package org.example.domain.DTO;

import org.example.domain.model.user.UserState;

public record UserStateDTO(String chatId, UserState state) {
}

//@AllArgsConstructor
//@Getter
//public class UserStateDTO {
//    private final String chatId;
//    private final UserState state;
//
//}

