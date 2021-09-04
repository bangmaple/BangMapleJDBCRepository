package bangmaple.jdbc.repository;

import bangmaple.jdbc.paging.Pageable;

/**
 * Extension of {@link ICRUDRepository} to provide additional methods to retrieve entities using the pagination and
 * sorting abstraction.
 *
 * @author bangmaple
 * @see Pageable
 */
interface IPagingAndSortingRepository<T, ID> extends ICRUDRepository<T ,ID> {

    /**
     * Returns all entities sorted by the given options.
     *
     * @param ordering @return all entities sorted by the given options
     */
    Iterable<T> findAll(boolean ordering);

    /**
     * Returns entities meeting the paging restriction provided in the {@code Pageable} object.
     *
     * @param pageable
     * @return a page of entities
     */
    Iterable<T> findAll(Pageable pageable);
}
