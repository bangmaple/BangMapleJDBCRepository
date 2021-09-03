/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bangmaple.helper.repository;

/**
 *
 * @author bangmaple
 */
public interface ICRUDRepository<T, ID> extends IRepository<T, ID> {
    void insert(T entity);
    void insertAll(Iterable<T> entity);
    void update(T entity, ID id);
    void updateAll(Iterable<T> entities, Iterable<ID> ids);
    void deleteById(ID id);
    void deleteAll();
    void deleteAllByIds(Iterable<? extends T> ids);
    boolean existsById(ID id);
    T findById(ID id);
    Iterable<T> findAll();
    Iterable<T> findAllByIds(Iterable<? extends T> ids);
    long count();
}