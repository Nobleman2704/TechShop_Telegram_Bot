package org.example.service;

import org.example.domain.DTO.BaseResponse;

import java.util.UUID;

public interface BaseService<CD, T> {
    BaseResponse save(CD cd);

    BaseResponse<T> getById(UUID id);

}
