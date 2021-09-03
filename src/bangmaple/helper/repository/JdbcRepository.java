package bangmaple.helper.repository;

import static bangmaple.helper.RepositoryHelper.*;

import bangmaple.helper.ConnectionManager;
import bangmaple.helper.JdbcRepositoryParams;
import bangmaple.helper.annotations.Id;
import bangmaple.helper.annotations.Table;
import bangmaple.helper.paging.Page;
import bangmaple.helper.paging.Pageable;
import bangmaple.helper.paging.Sort;
import bangmaple.helper.query.SQLQueryType;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class JdbcRepository<T, ID> implements IPagingAndSortingRepository<T, ID> {
    private static final Logger LOGGER = Logger.getGlobal();
    public static boolean DEBUG = true;

    private static final String ERROR_WHILE_CLOSING_CONNECTION = "There is an error while closing the connection from the database!";

    private Connection conn;
    private PreparedStatement prStm;
    private ResultSet rs;

    public JdbcRepository() {
        System.out.println("die");
    }

    private static final String FIND_BY_ID_QUERY = "SELECT %s FROM %s WHERE %s = ?";
    private static final String FIND_ALL_QUERY = "SELECT %s FROM %s";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM %s WHERE %s = ?";
    private static final String INSERT_ONE_QUERY = "INSERT INTO %s(%s) VALUES(%s)";
    private static final String DELETE_ALL_QUERY = "DELETE FROM %s";
    private static final String IS_EXISTED_QUERY = "SELECT COUNT(%s) FROM %s WHERE %s = ?";
    private static final String COUNT_QUERY = "SELECT COUNT(%s) FROM %s";

    @Override
    @SuppressWarnings("unchecked")
    public T findById(ID id) {
        T result = null;
        conn = ConnectionManager.getConnection();
        try {
            T entity = getEntityInstance();
            conn.setCatalog(getDatabaseNameFromEntity(entity));
            if (Objects.nonNull(conn)) {
                JdbcRepositoryParams<T, ID> params
                        = new JdbcRepositoryParams<>(entity, FIND_BY_ID_QUERY, SQLQueryType.SELECT);
                if (DEBUG) {
                    LOGGER.log(Level.INFO, params.getSqlQuery());
                }
                prStm = conn.prepareStatement(params.getSqlQuery());
                prStm.setObject(1, id);
                rs = prStm.executeQuery();
                if (rs.next()) {
                    result = parseFromTableDataToEntity(params.getFields(), rs, entity);
                }
            }
        } catch (SQLException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return result;
    }

    @Override
    public List<T> findAll() {
        T entity = getEntityInstance();
        List<T> list = new LinkedList<>();
        conn = ConnectionManager.getConnection();
        try {
            if (Objects.nonNull(conn)) {
                conn.setCatalog(getDatabaseNameFromEntity(entity));
                JdbcRepositoryParams<T, ID> params = new JdbcRepositoryParams<>(entity, FIND_ALL_QUERY, SQLQueryType.SELECT);
                prStm = conn.prepareStatement(params.getSqlQuery());
                rs = prStm.executeQuery();
                while (rs.next()) {
                    list.add(parseFromTableDataToEntity(params.getFields(), rs, entity));
                }
            }
        } catch (SQLException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return list;
    }

    @Override
    public void insert(T entity) {
        conn = ConnectionManager.getConnection();
        try {
            if (Objects.nonNull(conn)) {
                conn.setCatalog(getDatabaseNameFromEntity(entity));
                JdbcRepositoryParams<T, ID> params = new JdbcRepositoryParams<>(entity, INSERT_ONE_QUERY, SQLQueryType.CREATE);
                prStm = conn.prepareStatement(params.getSqlQuery());
                bindValueToSQLBindingParams(entity, prStm, SQLQueryType.CREATE);
                prStm.executeUpdate();
            }
        } catch (SQLException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
    }

    @Override
    public void insertAll(Iterable<T> entities) {
        if (entities instanceof List) {
            List<T> list = (List<T>) entities;
            conn = ConnectionManager.getConnection();
            try {
                if (Objects.nonNull(conn) && !list.isEmpty()) {
                    T entity = list.get(0);
                    conn.setCatalog(getDatabaseNameFromEntity(entity));
                    conn.setAutoCommit(false);
                    JdbcRepositoryParams<T, ID> params
                            = new JdbcRepositoryParams<>(entity, INSERT_ONE_QUERY, SQLQueryType.CREATE);
                    prStm = conn.prepareStatement(params.getSqlQuery());
                    addToBatchThenCommitProperly(list, params, conn, prStm);
                }
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new RuntimeException(e);
            } finally {
                closeConnection();
            }
        }
    }

    @Override
    public void update(T entity, ID id) {
        conn = ConnectionManager.getConnection();
        try {
            conn.setCatalog(entity.getClass().getAnnotation(Table.class).catalog());
            String queryString = "UPDATE %s SET ";
            Field[] fields = entity.getClass().getDeclaredFields();
            Field idField = null;
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                if (fields[i].getAnnotation(Id.class) != null) {
                    idField = fields[i];
                }
                if (i == fields.length - 1) {
                    if (fields[i].get(entity) == null) {
                        queryString = queryString.substring(0, queryString.lastIndexOf(","));
                        queryString += " WHERE " + idField.getName() + " = ?";
                    } else {
                        queryString += fields[i].getName() + " = ? WHERE " + idField.getName() + " = ?";
                    }
                    break;
                }
                if (fields[i].get(entity) != null) {
                    queryString += fields[i].getName() + " = ?, ";
                }
            }
            System.out.println(queryString);
            if (Objects.nonNull(conn)) {
                JdbcRepositoryParams<T, ID> params
                        = new JdbcRepositoryParams<>(entity, queryString, id, SQLQueryType.UPDATE);
                System.out.println(params.getSqlQuery());
                prStm = conn.prepareStatement(params.getSqlQuery());
                bindValueToSQLBindingParams(entity, prStm, SQLQueryType.UPDATE);
                if (prStm.executeUpdate() < 0) {
                    throw new SQLException("Invalid SQL");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
    }

    @Override
    public void updateAll(Iterable<T> entities, Iterable<ID> ids) {

    }

    @Override
    public void deleteById(ID id) {
        conn = ConnectionManager.getConnection();
        T entity = getEntityInstance();
        try {
            assignIdToEntity(entity, id);
            if (Objects.nonNull(conn)) {
                conn.setCatalog(getDatabaseNameFromEntity(entity));
                JdbcRepositoryParams<T, ID> params
                        = new JdbcRepositoryParams<>(getEntityInstance(), DELETE_BY_ID_QUERY, id, SQLQueryType.DELETE);
                if (DEBUG) {
                    LOGGER.log(Level.INFO, params.getSqlQuery());
                }
                prStm = conn.prepareStatement(params.getSqlQuery());
                bindValueToSQLBindingParams(entity, prStm, SQLQueryType.DELETE);
                prStm.executeUpdate();
            }
        } catch (SQLException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
    }

    @Override
    public void deleteAll() {
        conn = ConnectionManager.getConnection();
        try {
            T entity = getEntityInstance();
            conn.setCatalog(getDatabaseNameFromEntity(entity));
            if (Objects.nonNull(conn)) {
                JdbcRepositoryParams<T, ID> params = new JdbcRepositoryParams<>(entity, DELETE_ALL_QUERY, SQLQueryType.DELETE);
                System.out.println(params.getSqlQuery());
                prStm = conn.prepareStatement(params.getSqlQuery());
                prStm.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
    }

    @Override
    public void deleteAllByIds(Iterable<? extends T> ids) {

    }

    @Override
    public boolean existsById(ID id) {
        boolean isExisted = false;
        conn = ConnectionManager.getConnection();
        T entity = getEntityInstance();
        try {
            assignIdToEntity(entity, id);
            if (Objects.nonNull(conn)) {
                conn.setCatalog(getDatabaseNameFromEntity(entity));
                JdbcRepositoryParams<T, ID> params = new JdbcRepositoryParams<>(entity, IS_EXISTED_QUERY, SQLQueryType.COUNT);
                prStm = conn.prepareStatement(params.getSqlQuery());
                bindValueToSQLBindingParams(entity, prStm, SQLQueryType.COUNT);
                rs = prStm.executeQuery();
                if (rs.next()) {
                    isExisted = rs.getLong(1) > 0;
                }
            }
        } catch (SQLException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return isExisted;
    }

    @Override
    public Iterable<T> findAllByIds(Iterable<? extends T> ids) {
        return null;
    }

    @Override
    public long count() {
        long size = 0;
        conn = ConnectionManager.getConnection();
        try {
            T entity = getEntityInstance();
            if (Objects.nonNull(conn)) {
                conn.setCatalog(getDatabaseNameFromEntity(entity));
                JdbcRepositoryParams<T, ID> params = new JdbcRepositoryParams<>(entity, COUNT_QUERY, SQLQueryType.COUNT);
                prStm = conn.prepareStatement(params.getSqlQuery());
                rs = prStm.executeQuery();
                if (rs.next()) {
                    size = rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return size;
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Iterable<T> findAll(Sort sort) {
        return null;
    }

    @SuppressWarnings("unchecked")
    protected Class<T> getEntityClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @SuppressWarnings("unchecked")
    protected T getEntityInstance() {
        try {
            return getEntityClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
    }

    protected void closeConnection() {
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
            throw new RuntimeException(ERROR_WHILE_CLOSING_CONNECTION);
        }
    }

}
