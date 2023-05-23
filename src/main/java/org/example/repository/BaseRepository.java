package org.example.repository;

import java.util.List;
import java.util.UUID;

public interface BaseRepository<T> {

    int save(T t);

    T getById(UUID id);


    void update(T t);

}
