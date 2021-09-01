package bangmaple.helper.repository;

import bangmaple.helper.paging.Page;
import bangmaple.helper.paging.Pageable;
import bangmaple.helper.paging.Sort;

interface IPagingAndSortingRepository<T, ID> extends ICRUDRepository<T ,ID> {
    Page<T> findAll(Pageable pageable);
    Iterable<T> findAll(Sort sort);
}
