package bangmaple.jdbc.paging;

import java.io.Serializable;
import java.util.Arrays;

public class PageRequest extends AbstractPageRequest implements Serializable {

    private static final long serialVersionUID = -4541509938956089562L;

    private boolean ordering = true;
    private String[] properties = {};

    /**
     * Creates a new {@link PageRequest} with sort parameters applied.
     *
     * @param page zero-based page index, must not be negative.
     * @param size the size of the page to be returned, must be greater than 0.
     */
    protected PageRequest(int page, int size, boolean ordering, String... properties) {

        super(page, size);

        this.ordering = ordering;
        this.properties = properties;
    }

    /**
     * Creates a new unsorted {@link PageRequest}.
     *
     * @param page zero-based page index, must not be negative.
     * @param size the size of the page to be returned, must be greater than 0.
     */
    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size, Pageable.SORT_ASC, "");
    }

    /**
     * Creates a new {@link PageRequest} with sort parameters applied.
     *
     * @param page     zero-based page index.
     * @param size     the size of the page to be returned.
     * @param ordering Pageable.ASC (true) for Ascending order, Pageable.DESC (false) for Descending order.
     */
    public static PageRequest of(int page, int size, boolean ordering) {
        return new PageRequest(page, size, ordering, "");
    }

    /**
     * Creates a new {@link PageRequest} with sort direction and properties applied.
     *
     * @param page       zero-based page index, must not be negative.
     * @param size       the size of the page to be returned, must be greater than 0.
     * @param ordering   Pageable.ASC (true) for Ascending order, Pageable.DESC (false) for Descending order.
     * @param properties must not be {@literal null}.
     */
    public static PageRequest of(int page, int size, boolean ordering, String... properties) {
        return new PageRequest(page, size, ordering, properties);
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

    @Override
    public boolean isAscending() {
        return ordering == Pageable.SORT_ASC;
    }

    @Override
    public boolean isDescending() {
        return ordering == Pageable.SORT_DESC;
    }

    public String getProperties() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < properties.length; i++) {
            if (i == properties.length - 1) {
                result.append(properties[i]);
                break;
            }
            result.append(properties[i]).append(", ");
        }
        return result.toString();
    }

    @Override
    public String toString() {
        return String.format("Page request [number: %d, size %d]", getPageNumber(), getPageSize());
    }

}
