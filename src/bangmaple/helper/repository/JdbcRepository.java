package bangmaple.helper.repository;


import bangmaple.helper.ConnectionManager;
import bangmaple.helper.annotations.Column;
import bangmaple.helper.annotations.Id;
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

    public String getParametersString(Field[] fields) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i == fields.length - 1) {
                result.append(fields[i].getName());
                break;
            }
            result.append(fields[i].getName()).append(", ");
        }
        return result.toString();
    }

    public String getIdFieldNameFromFields(Field[] fields) {
        for (int i = 0; i < fields.length; i++) {
            if (Objects.nonNull(fields[i].getAnnotation(Id.class))) {
                return fields[i].getName();
            }
        }
        throw new IllegalArgumentException();
    }

    @SuppressWarnings("unchecked")
    public Class<T> getEntityInstance() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public T parseFromTableDataToEntity(Field[] fields) throws SQLException, IllegalAccessException, InstantiationException {
        Class<T> entity = getEntityInstance();
        T t = entity.newInstance();
        for (Field field : fields) {
            String column = field.getAnnotation(Column.class).value();
            field.setAccessible(true);
            field.set(t, rs.getObject(column));
        }
        return t;
    }

    private void closeConnection() throws SQLException {
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
                Field[] fields = entity.getDeclaredFields();
                String tableName = entity.getAnnotation(Table.class).name();
                String idFieldName = getIdFieldNameFromFields(fields);
                String params = getParametersString(fields);
                String query = String.format("SELECT %s FROM %s WHERE %s = ?", params, tableName, idFieldName);
                prStm = conn.prepareStatement(query);
                prStm.setObject(1, id);
                rs = prStm.executeQuery();
                if (rs.next()) {
                    t = parseFromTableDataToEntity(fields);
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
                    list.add(parseFromTableDataToEntity(fields));
                }
            }
        } catch (SQLException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return list;
    }

    private String getStringParamValuesFromEntity(T entity) throws IllegalAccessException {
        StringBuilder result = new StringBuilder();
        Field[] fields = entity.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            if (i == fields.length - 1) {
                result.append("?");
                break;
            }
            result.append("?").append(", ");
        }
        return result.toString();
    }

    @Override
    public void insert(T entity) throws SQLException {
        Class<T> clazz = getEntityInstance();
        conn = ConnectionManager.getConnection();
        try {
            if (Objects.nonNull(conn)) {
                Field[] fields = clazz.getDeclaredFields();
                String tableName = clazz.getAnnotation(Table.class).name();
                String params = getParametersString(fields);
                String paramValues = getStringParamValuesFromEntity(entity);
                String query = String.format("INSERT INTO %s(%s) VALUES(%s)", tableName, params, paramValues);
                System.out.println(query);
                prStm = conn.prepareStatement(query);
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setAccessible(true);
                    prStm.setObject(i + 1, fields[i].get(entity));
                }
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
}
