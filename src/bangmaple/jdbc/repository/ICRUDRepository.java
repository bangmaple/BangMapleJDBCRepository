/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bangmaple.jdbc.repository;

/**
 * Interface for generic CRUD operations on a repository for a specific type.
 *
 * @author bangmaple
 */
public interface ICRUDRepository<T, ID> extends IRepository<T, ID> {

    /**
     * Inserts a given entity.
     *
     * @param entity must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal entity} is {@literal null}.
     * @throws java.sql.SQLException in case there is an error of the SQL Operations.
     */
    void insert(T entity);

    /**
     * Saves all given entities.
     *
     * @param entities must not be {@literal null} nor must it contain {@literal null}.
     * @throws IllegalArgumentException in case the given {@link Iterable entities} or one of its entities is
     *           {@literal null}.
     * @throws java.sql.SQLException in case there is an error of the SQL Operations.
     */
    void insertAll(Iterable<T> entities);

    /**
     * Updates a given entity.
     *
     * @param entity must not be {@literal null}.
     * @param id must not be {@literal null}..
     * @throws IllegalArgumentException in case the given {@literal entity} is {@literal null}.
     * @throws java.sql.SQLException in case there is an error of the SQL Operations.
     */
    void update(T entity, ID id);

    /**
     * Updates all given entities with corresponding id.
     *
     * @param entities must not be {@literal null} nor must it contain {@literal null}.
     * @param ids must not be {@literal null}. Must not contain {@literal null} elements.
     * @throws IllegalArgumentException in case the given {@link Iterable entities} or one of its entities is
     *           {@literal null}.
     * @throws java.sql.SQLException in case there is an error of the SQL Operations.
     */
    void updateAll(Iterable<T> entities, Iterable<ID> ids);

    /**
     * Deletes the entity with the given id.
     *
     * @param id must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}
     * @throws java.sql.SQLException in case there is an error of the SQL Operations.
     */
    void deleteById(ID id);

    /**
     * Deletes the entities.
     *
     * @throws IllegalArgumentException in case the given {@literal entities} or one of its entities is {@literal null}.
     * @throws java.sql.SQLException in case there is an error of the SQL Operations.
     */
    void deleteAll();

    /**
     * Deletes all instances of the type {@code T} with the given IDs.
     *
     * @param ids must not be {@literal null}. Must not contain {@literal null} elements.
     * @throws IllegalArgumentException in case the given {@literal ids} or one of its elements is {@literal null}.
     * @throws java.sql.SQLException in case there is an error of the SQL Operations.
     */
    void deleteAllByIds(Iterable<? extends ID> ids);

    /**
     * Returns whether an entity with the given id exists.
     *
     * @param id must not be {@literal null}.
     * @return {@literal true} if an entity with the given id exists, {@literal false} otherwise.
     * @throws IllegalArgumentException if {@literal id} is {@literal null}.
     * @throws java.sql.SQLException in case there is an error of the SQL Operations.
     */
    boolean existsById(ID id);

    /**
     * Retrieves an entity by its id.
     *
     * @param id must not be {@literal null}.
     * @return the entity with the given id or {@literal null} if none found.
     * @throws IllegalArgumentException if {@literal id} is {@literal null}.
     * @throws java.sql.SQLException in case there is an error of the SQL Operations.
     */
    T findById(ID id);

    /**
     * Returns all instances of the type.
     *
     * @return all entities
     *
     * @throws java.sql.SQLException in case there is an error of the SQL Operations.
     */
    Iterable<T> findAll();

    /**
     * Returns all instances of the type {@code T} with the given IDs.
     * <p>
     * If some or all ids are not found, no entities are returned for these IDs.
     * <p>
     * Note that the order of elements in the result is not guaranteed.
     *
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @return guaranteed to be not {@literal null}. The size can be equal or less than the number of given
     *         {@literal ids}.
     * @throws IllegalArgumentException in case the given {@link Iterable ids} or one of its items is {@literal null}.
     * @throws java.sql.SQLException in case there is an error of the SQL Operations.
     */
    Iterable<T> findAllByIds(Iterable<? extends ID> ids);

    /**
     * Returns the number of entities available.
     *
     * @return the number of entities.
     * @throws java.sql.SQLException in case there is an error of the SQL Operations.
     */
    long count();
}