package bangmaple.jdbc.repository;

import bangmaple.jdbc.paging.Page;
import bangmaple.jdbc.paging.Pageable;
import bangmaple.jdbc.paging.Sort;

interface IPagingAndSortingRepository<T, ID> extends ICRUDRepository<T ,ID> {
    Page<T> findAll(Pageable pageable);
    Iterable<T> findAll(Sort sort);
}
