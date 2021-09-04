package bangmaple.jdbc.paging;

public interface Pageable {

    boolean SORT_ASC = true;
    boolean SORT_DESC = false;

    /**
     * Creates a new {@link Pageable} for the first page (page number {@code 0}) given {@code pageSize} .
     *
     * @param pageSize the size of the page to be returned, must be greater than 0.
     * @return a new {@link Pageable}.
     * @since 2.5
     */
    static Pageable ofSize(int pageSize) {
        return null;
      //  return PageRequest.of(0, pageSize);
    }

    /**
     * Returns the page to be returned.
     *
     * @return the page to be returned.
     */
    int getPageNumber();

    /**
     * Returns the number of items to be returned.
     *
     * @return the number of items of that page
     */
    int getPageSize();

    /**
     * Returns the offset to be taken according to the underlying page and page size.
     *
     * @return the offset to be taken
     */
    long getOffset();

    boolean isAscending();

    boolean isDescending();

    String getProperties();

}
