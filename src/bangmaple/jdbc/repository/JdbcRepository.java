package bangmaple.jdbc.repository;

import static bangmaple.jdbc.helper.RepositoryHelper.*;

import bangmaple.jdbc.utils.ConnectionManager;
import bangmaple.jdbc.utils.JdbcRepositoryParams;
import bangmaple.jdbc.paging.Page;
import bangmaple.jdbc.paging.Pageable;
import bangmaple.jdbc.paging.Sort;
import bangmaple.jdbc.query.SQLQueryType;

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
    public static boolean DEBUG = false;

    private static final String ERROR_WHILE_CLOSING_CONNECTION = "There is an error while closing the connection from the database!";

    protected Connection conn;
    protected PreparedStatement prStm;
    protected ResultSet rs;

    protected JdbcRepository() {
    }

    private static final String FIND_BY_ID_QUERY = "SELECT %s FROM %s WHERE %s = ?";
    private static final String FIND_ALL_QUERY = "SELECT %s FROM %s";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM %s WHERE %s = ?";
    private static final String INSERT_ONE_QUERY = "INSERT INTO %s(%s) VALUES(%s)";
    private static final String UPDATE_QUERY = "UPDATE %s SET ";
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
            if (Objects.nonNull(conn)) {
                conn.setCatalog(getDatabaseNameFromEntity(entity));
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
                    entity = getEntityInstance();
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
                    conn.setAutoCommit(false);
                    T entity = list.get(0);
                    conn.setCatalog(getDatabaseNameFromEntity(entity));
                    JdbcRepositoryParams<T, ID> params
                            = new JdbcRepositoryParams<>(entity, INSERT_ONE_QUERY, SQLQueryType.CREATE);
                    prStm = conn.prepareStatement(params.getSqlQuery());
                    addToBatchThenCommitProperly(list, params, conn, prStm, SQLQueryType.CREATE);
                }
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new RuntimeException(e);
            } finally {
                closeConnection();
            }
        } else {
            throw new RuntimeException("Currently only support List data structure!");
        }
    }

    @Override
    public void update(T entity, ID id) {
        conn = ConnectionManager.getConnection();
        try {
            if (Objects.nonNull(conn)) {
                conn.setCatalog(getDatabaseNameFromEntity(entity));
                String queryString = getUpdateQueryStringWithEntityFields(entity, UPDATE_QUERY);
                JdbcRepositoryParams<T, ID> params = new JdbcRepositoryParams<>(entity, id, queryString, SQLQueryType.UPDATE);
                if (DEBUG) {
                    LOGGER.log(Level.INFO, params.getSqlQuery());
                }
                prStm = conn.prepareStatement(params.getSqlQuery());
                bindValueToSQLBindingParams(entity, prStm, SQLQueryType.UPDATE);
                prStm.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
    }

    @Override
    public void updateAll(Iterable<T> entities, Iterable<ID> ids) {
        T entity = getEntityInstance();
        if (entities instanceof List) {
            List<T> listEntities = (List<T>)entities;
            List<ID> listIds = (List<ID>) ids;
            conn = ConnectionManager.getConnection();
            try {
                if (Objects.nonNull(conn)) {
                    conn.setCatalog(getDatabaseNameFromEntity(entity));
                    String queryString = getUpdateQueryStringWithEntityFields(entity, UPDATE_QUERY);
                    int size = listEntities.size();
                    for (int i = 0; i < size; i++) {
                        JdbcRepositoryParams<T, ID> params
                                = new JdbcRepositoryParams<>(listEntities.get(i), listIds.get(i), queryString, SQLQueryType.UPDATE);
                        if (DEBUG) {
                            LOGGER.log(Level.INFO, params.getSqlQuery());
                        }
                        prStm = conn.prepareStatement(params.getSqlQuery());
                        bindValueToSQLBindingParams(listEntities.get(i), prStm, SQLQueryType.UPDATE);
                        prStm.executeUpdate();
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                closeConnection();
            }
        } else {
            throw new RuntimeException("Currently only support List data structure!");
        }
    }

    @Override
    public void deleteById(ID id) {
        conn = ConnectionManager.getConnection();
        T entity = getEntityInstance();
        try {
            assignIdToEntity(entity, id);
            if (Objects.nonNull(conn)) {
                conn.setCatalog(getDatabaseNameFromEntity(entity));
                JdbcRepositoryParams<T, ID> params = new JdbcRepositoryParams<>(entity, DELETE_BY_ID_QUERY, SQLQueryType.DELETE);
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
            if (Objects.nonNull(conn)) {
                conn.setCatalog(getDatabaseNameFromEntity(entity));
                JdbcRepositoryParams<T, ID> params = new JdbcRepositoryParams<>(entity, DELETE_ALL_QUERY, SQLQueryType.DELETE);
                if (DEBUG) {
                    LOGGER.log(Level.INFO, params.getSqlQuery());
                }
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
    @SuppressWarnings("unchecked")
    public void deleteAllByIds(Iterable<? extends ID> ids) {
        if (ids instanceof List) {
            List<? extends ID> list = (List<? extends ID>) ids;
            conn = ConnectionManager.getConnection();
            try {
                if (Objects.nonNull(conn) && !list.isEmpty()) {
                    conn.setAutoCommit(false);
                    T entity = getEntityInstance();
                    conn.setCatalog(getDatabaseNameFromEntity(entity));
                    JdbcRepositoryParams<T, ID> params
                            = new JdbcRepositoryParams<>(entity, DELETE_BY_ID_QUERY, SQLQueryType.DELETE);
                    prStm = conn.prepareStatement(params.getSqlQuery());
                    addToBatchThenCommitProperly(list, params, conn, prStm, SQLQueryType.DELETE);
                }
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new RuntimeException(e);
            } finally {
                closeConnection();
            }
        } else {
            throw new RuntimeException("Currently only support List data structure!");
        }
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
    @SuppressWarnings("unchecked")
    public Iterable<T> findAllByIds(Iterable<? extends ID> ids) {
        List<T> result = null;
        if (ids instanceof List) {
            List<? extends ID> list = (List<? extends ID>) ids;
            conn = ConnectionManager.getConnection();
            try {
                if (Objects.nonNull(conn) && !list.isEmpty()) {
                    conn.setAutoCommit(false);
                    T entity = getEntityInstance();
                    conn.setCatalog(getDatabaseNameFromEntity(entity));
                    result = new LinkedList<>();
                    JdbcRepositoryParams<T, ID> params
                            = new JdbcRepositoryParams<>(entity, FIND_BY_ID_QUERY, SQLQueryType.SELECT);
                    for (ID id: list) {
                        if (DEBUG) {
                            LOGGER.log(Level.INFO, params.getSqlQuery());
                        }
                        prStm = conn.prepareStatement(params.getSqlQuery());
                        prStm.setObject(1, id);
                        rs = prStm.executeQuery();
                        if (rs.next()) {
                            result.add(parseFromTableDataToEntity(params.getFields(), rs, getEntityInstance()));
                        } else {
                            result.add(null);
                        }
                        closePreparedStatement();
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                closeConnection();
            }
        } else {
            throw new RuntimeException("Currently only support List data structure!");
        }
        return result;
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

    protected void closePreparedStatement() {
        try {
            if (Objects.nonNull(rs)) {
                rs.close();
            }
            if (Objects.nonNull(prStm)) {
                prStm.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(ERROR_WHILE_CLOSING_CONNECTION);
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
