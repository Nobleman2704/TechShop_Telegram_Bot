package org.example.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors (chain = true)
public class BaseResponse<T> {
    private String message;
    private int status;
    private T data;

    public BaseResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }
}
