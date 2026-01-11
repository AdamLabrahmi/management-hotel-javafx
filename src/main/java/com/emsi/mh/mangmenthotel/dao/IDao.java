package com.emsi.mh.mangmenthotel.dao;

import java.util.List;

public interface IDao<T, ID> {
    void create(T entity);

    T findById(ID id);

    List<T> findAll();

    void update(T entity);

    void delete(T entity);

    void deleteById(ID id);
}
