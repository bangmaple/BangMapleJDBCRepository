package bangmaple.jdbc.repository;

import bangmaple.jdbc.paging.Page;
import bangmaple.jdbc.paging.Pageable;
import bangmaple.jdbc.paging.Sort;

/**
 * Extension of {@link ICRUDRepository} to provide additional methods to retrieve entities using the pagination and
 * sorting abstraction.
 *
 * @author bangmaple
 * @see Sort
 * @see Pageable
 * @see Page
 */
interface IPagingAndSortingRepository<T, ID> extends ICRUDRepository<T ,ID> {

    /**
     * Returns all entities sorted by the given options.
     *
     * @param sort
     * @return all entities sorted by the given options
     */
    Iterable<T> findAll(Sort sort);

    /**
     * Returns a {@link Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
     *
     * @param pageable
     * @return a page of entities
     */
    Page<T> findAll(Pageable pageable);
}
