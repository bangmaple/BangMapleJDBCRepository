package bangmaple.helper.repository;

import static bangmaple.helper.RepositoryHelper.*;

import bangmaple.helper.ConnectionManager;
import bangmaple.helper.JdbcRepositoryParams;
import bangmaple.helper.annotations.Table;
import bangmaple.helper.paging.Page;
import bangmaple.helper.paging.Pageable;
import bangmaple.helper.paging.Sort;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public abstract class JdbcRepository<T, ID> implements IPagingAndSortingRepository<T, ID> {

    private static final String ERROR_WHILE_CLOSING_CONNECTION = "There is an error while closing the connection from the database!";

    private Connection conn;
    private PreparedStatement prStm;
    private ResultSet rs;

    protected void closeConnection() throws SQLException {
        try {
            if (Objects.nonNull(rs)) {
                rs.close();
            }
            if (Objects.nonNull(prStm)) {
                prStm.close();
            }
            if (Objects.nonNull(conn)) {
                conn.close();
            }
        } catch (SQLException e) {
            throw new SQLException(ERROR_WHILE_CLOSING_CONNECTION);
        }
    }

    @Override
    public T findById(ID id) throws SQLException {
        Class<T> entity = getEntityInstance();
        T t = null;
        conn = ConnectionManager.getConnection();
        try {
            if (Objects.nonNull(conn)) {
                String queryString = "SELECT %s FROM %s WHERE %s = ?";
                JdbcRepositoryParams<T> params
                        = new JdbcRepositoryParams<>(entity, queryString);
                System.out.println(params.getSqlQuery());
                prStm = conn.prepareStatement(params.getSqlQuery());
                prStm.setObject(1, id);
                rs = prStm.executeQuery();
                if (rs.next()) {
                    t = parseFromTableDataToEntity(params.getFields(), rs, entity);
                }
            }
        } catch (SQLException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return t;
    }

    @Override
    public List<T> findAll() throws SQLException {
        Class<T> entity = getEntityInstance();
        List<T> list = new LinkedList<>();
        conn = ConnectionManager.getConnection();
        try {
            if (Objects.nonNull(conn)) {
                Field[] fields = entity.getDeclaredFields();
                String tableName = entity.getAnnotation(Table.class).name();
                String params = getParametersString(fields);
                String query = String.format("SELECT %s FROM %s", params, tableName);
                prStm = conn.prepareStatement(query);
                rs = prStm.executeQuery();
                while (rs.next()) {
                    list.add(parseFromTableDataToEntity(fields, rs, entity));
                }
            }
        } catch (SQLException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return list;
    }

    @Override
    public void insert(T entity) throws SQLException {
        conn = ConnectionManager.getConnection();
        try {
            if (Objects.nonNull(conn)) {
                String queryString = "INSERT INTO %s(%s) VALUES(%s)";
                JdbcRepositoryParams<T> params = new JdbcRepositoryParams<>(getEntityInstance(), entity, queryString);
                System.out.println(params.getSqlQuery());
                prStm = conn.prepareStatement(params.getSqlQuery());
                bindValueToSQLBindingParams(params.getFields(), entity, prStm);
                if (prStm.executeUpdate() < 1) {
                    throw new SQLException("Invalid SQL");
                }
            }
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    @Override
    public void insertAll(Iterable<T> entity) {

    }

    @Override
    public void update(T entity, ID id) {

    }

    @Override
    public void updateAll(Iterable<T> entities, Iterable<ID> ids) {

    }

    @Override
    public void delete(T entity) {

    }

    @Override
    public void deleteById(ID id) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void deleteAllById(Iterable<? extends T> ids) {

    }

    @Override
    public boolean existsById(ID id) {
        return false;
    }

    @Override
    public Iterable<T> findAllByIds(Iterable<ID> ids) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Iterable<T> findAll(Sort sort) {
        return null;
    }

                    /*Field[] fields = clazz.getDeclaredFields();
                String tableName = clazz.getAnnotation(Table.class).name();
                String params = RepositoryHelper.getParametersString(fields);
                String paramValues = RepositoryHelper.getStringParamValuesFromEntity(entity);
                String query = String.format("INSERT INTO %s(%s) VALUES(%s)", tableName, params, paramValues);*/

    @SuppressWarnings("unchecked")
    protected <T> Class<T> getEntityInstance() {
        System.out.println(getClass().getGenericSuperclass().getTypeName());
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

}
