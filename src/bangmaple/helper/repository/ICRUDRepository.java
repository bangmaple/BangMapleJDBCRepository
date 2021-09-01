/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bangmaple.helper.repository;

import java.sql.SQLException;

/**
 *
 * @author bangmaple
 */
public interface ICRUDRepository<T, ID> extends IRepository<T, ID> {
    void insert(T entity) throws SQLException;
    void insertAll(Iterable<T> entity);
    void update(T entity, ID id);
    void updateAll(Iterable<T> entities, Iterable<ID> ids);
    void delete(T entity);
    void deleteById(ID id);
    void deleteAll();
    void deleteAllById(Iterable<? extends T> ids);
    boolean existsById(ID id);
    T findById(ID id) throws SQLException;
    Iterable<T> findAll() throws SQLException;
    Iterable<T> findAllByIds(Iterable<ID> ids);
    long count();
}