package bangmaple.jdbc.paging;

import java.io.Serializable;

public class PageRequest extends AbstractPageRequest implements Serializable {

    private static final long serialVersionUID = -4541509938956089562L;

    private final Sort sort;

    /**
     * Creates a new {@link PageRequest} with sort parameters applied.
     *
     * @param page zero-based page index, must not be negative.
     * @param size the size of the page to be returned, must be greater than 0.
     * @param sort must not be {@literal null}, use {@link Sort#unsorted()} instead.
     */
    protected PageRequest(int page, int size, Sort sort) {

        super(page, size);

        this.sort = sort;
    }

    /**
     * Creates a new unsorted {@link PageRequest}.
     *
     * @param page zero-based page index, must not be negative.
     * @param size the size of the page to be returned, must be greater than 0.
     */
    public static PageRequest of(int page, int size) {
        return of(page, size, Sort.unsorted());
    }

    /**
     * Creates a new {@link PageRequest} with sort parameters applied.
     *
     * @param page zero-based page index.
     * @param size the size of the page to be returned.
     * @param sort must not be {@literal null}, use {@link Sort#unsorted()} instead.
      */
    public static PageRequest of(int page, int size, Sort sort) {
        return new PageRequest(page, size, sort);
    }

    /**
     * Creates a new {@link PageRequest} with sort direction and properties applied.
     *
     * @param page zero-based page index, must not be negative.
     * @param size the size of the page to be returned, must be greater than 0.
     * @param direction must not be {@literal null}.
     * @param properties must not be {@literal null}.
     */
    public static PageRequest of(int page, int size, Sort.Direction direction, String... properties) {
        return of(page, size, Sort.by(direction, properties));
    }

    /**
     * Creates a new {@link PageRequest} for the first page (page number {@code 0}) given {@code pageSize} .
     *
     * @param pageSize the size of the page to be returned, must be greater than 0.
     * @return a new {@link PageRequest}.
     */
    public static PageRequest ofSize(int pageSize) {
        return PageRequest.of(0, pageSize);
    }

    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return (Pageable) new PageRequest(getPageNumber() + 1, getPageSize(), getSort());
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public Pageable previous() {
        return (Pageable) (getPageNumber() == 0 ? this : new PageRequest(getPageNumber() - 1, getPageSize(), getSort()));
    }

    @Override
    public Pageable first() {
        return (Pageable) new PageRequest(0, getPageSize(), getSort());
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof PageRequest)) {
            return false;
        }

        PageRequest that = (PageRequest) obj;

        return super.equals(that) && this.sort.equals(that.sort);
    }

    /**
     * Creates a new {@link PageRequest} with {@link Sort.Direction} and {@code properties} applied.
     *
     * @param direction must not be {@literal null}.
     * @param properties must not be {@literal null}.
     * @return a new {@link PageRequest}.
     */
    public PageRequest withSort(Sort.Direction direction, String... properties) {
        return new PageRequest(getPageNumber(), getPageSize(), Sort.by(direction, properties));
    }

    /**
     * Creates a new {@link PageRequest} with {@link Sort} applied.
     *
     * @param sort must not be {@literal null}.
     * @return a new {@link PageRequest}.
     */
    public PageRequest withSort(Sort sort) {
        return new PageRequest(getPageNumber(), getPageSize(), sort);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + sort.hashCode();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Page request [number: %d, size %d, sort: %s]", getPageNumber(), getPageSize(), sort);
    }

}
