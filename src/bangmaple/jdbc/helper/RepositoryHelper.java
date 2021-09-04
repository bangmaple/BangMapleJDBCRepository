package bangmaple.jdbc.helper;

import bangmaple.jdbc.annotations.Column;
import bangmaple.jdbc.annotations.Id;
import bangmaple.jdbc.annotations.Table;
import bangmaple.jdbc.query.SQLQueryType;
import bangmaple.jdbc.repository.JdbcRepository;
import bangmaple.jdbc.utils.JdbcRepositoryParams;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface RepositoryHelper {
    Logger LOGGER = Logger.getGlobal();

    int BATCH_SIZE = 5;

    String SQL_BINDING_PARAMETER = "?";
    String SQL_QUERY_PARAMS_DELIMITER = ", ";


    static <T> void bindValueToSQLBindingParams(T entity, PreparedStatement prStm, SQLQueryType sqlType)
            throws IllegalAccessException, SQLException {
        if (entity instanceof String) {
            prStm.setObject(1, entity);
            return;
        }
        Field[] fields = entity.getClass().getDeclaredFields();
        int lastParamCount = fields.length;
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.get(entity) == null) {
                lastParamCount--;
            }
        }
        int size = fields.length;
        int index = 0;
        for (int i = index; i < size; i++) {
            fields[i].setAccessible(true);
            if (Objects.nonNull(fields[i].getAnnotation(Id.class))) {
                prStm.setObject(i + 1, fields[i].get(entity));
                if (sqlType.compareTo(SQLQueryType.DELETE) == 0) {
                    return;
                } else if (sqlType.compareTo(SQLQueryType.COUNT) == 0) {
                    prStm.setObject(i + 1, fields[i].get(entity));
                    return;
                } else if (sqlType.compareTo(SQLQueryType.UPDATE) == 0) {
                      prStm.setObject(i + 1, fields[i].get(entity));
                    prStm.setObject(lastParamCount, fields[i].get(entity));
                } else if (sqlType.compareTo(SQLQueryType.CREATE) == 0) {
                    prStm.setObject(i + 1, fields[i].get(entity));
                }
                continue;
            }
            if (fields[i].get(entity) != null) {
                if (i == lastParamCount) {
                    System.out.println(i + " " + fields[i].getName() + " " + fields[i].get(entity));
                        prStm.setObject(i, fields[i].get(entity));
                } else {
                    if (sqlType.compareTo(SQLQueryType.UPDATE) == 0) {
                        prStm.setObject(i, fields[i].get(entity));
                    } else {
                        prStm.setObject(i + 1, fields[i].get(entity));
                    }
                }
            }
        }
    }

    static String getParametersString(Field[] fields) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i == fields.length - 1) {
                result.append(getColumnNameFromField(fields[i]));
                break;
            }
            result.append(getColumnNameFromField(fields[i])).append(SQL_QUERY_PARAMS_DELIMITER);
        }
        return result.toString();
    }

    static String getIdFieldNameFromFields(Field[] fields) {
        for (Field field : fields) {
            if (Objects.nonNull(field.getAnnotation(Id.class))) {
                return field.getAnnotation(Column.class).value();
            }
        }
        throw new IllegalArgumentException();
    }

    static <T> T parseResultSetToDTO(ResultSet resultSet, Class<T> clazz) throws SQLException {
        T entity = null;
        try {
            Field[] fields = clazz.getDeclaredFields();
            entity = clazz.newInstance();
            for (Field field : fields) {
                field.setAccessible(true);
                String columnName = getTableColumnNameFromField(field);
                field.set(entity, resultSet.getObject(columnName));
            }
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }

    static <T> T parseFromTableDataToEntity(Field[] fields, ResultSet resultSet, T entity) throws SQLException,
            IllegalAccessException, InstantiationException {
        for (Field field : fields) {
            field.setAccessible(true);
            String columnName = getTableColumnNameFromField(field);
            field.set(entity, resultSet.getObject(columnName));
        }
        return entity;
    }

    @SuppressWarnings("unchecked")
    static <T> T parseFromTableDataToEntity(Field[] fields, ResultSet resultSet) throws SQLException,
            IllegalAccessException, InstantiationException {
        T entity = null;
        if (fields.length > 0) {
            entity = (T) fields[0].getDeclaringClass().newInstance();
            for (Field field : fields) {
                field.setAccessible(true);
                String columnName = getTableColumnNameFromField(field);
                field.set(entity, resultSet.getObject(columnName));
            }
        }
        return entity;
    }

    static <T> String getStringParamValuesFromEntity(T entity) {
        StringBuilder result = new StringBuilder();
        Field[] fields = entity.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            if (i == fields.length - 1) {
                result.append(SQL_BINDING_PARAMETER);
                break;
            }
            result.append(SQL_BINDING_PARAMETER).append(SQL_QUERY_PARAMS_DELIMITER);
        }
        return result.toString();
    }

    static String getTableColumnNameFromField(Field field) {
        return field.getAnnotation(Column.class).value();
    }

    static boolean shouldCommitBatch(int index) {
        return ((index + 1) % BATCH_SIZE == 0) && (index != 0);
    }

    static void commitBatchTransaction(Connection conn, PreparedStatement prStm) throws SQLException {
        prStm.executeBatch();
        conn.commit();
    }

    static void commitTransaction(Connection conn, PreparedStatement prStm) throws SQLException {
        prStm.executeBatch();
        conn.commit();
    }

    static void rollbackTransaction(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    static <T> String getDatabaseNameFromEntity(T entity) {
        return entity.getClass().getAnnotation(Table.class).catalog();
    }

    static <T, ID> void assignIdToEntity(T entity, ID id) throws IllegalAccessException {
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (Objects.nonNull(field.getAnnotation(Id.class))) {
                field.setAccessible(true);
                field.set(entity, id);
            }
        }
    }

    static <T, ID> void addToBatchThenCommitProperly(List<?> list, JdbcRepositoryParams<T, ID> params,
                                                     Connection conn, PreparedStatement prStm, SQLQueryType sqlType)
            throws SQLException, IllegalAccessException {
        int batchSizeCounter = 0;
        for (int i = 0; i < list.size(); i++) {
            if (JdbcRepository.DEBUG) {
                LOGGER.log(Level.INFO, params.getSqlQuery());
            }
            if (sqlType.compareTo(SQLQueryType.CREATE) == 0) {
                bindValueToSQLBindingParams(list.get(i), prStm, SQLQueryType.CREATE);
            } else if (sqlType.compareTo(SQLQueryType.DELETE) == 0) {
                bindValueToSQLBindingParams(list.get(i), prStm, SQLQueryType.DELETE);
            } else if (sqlType.compareTo(SQLQueryType.UPDATE) == 0) {
                bindValueToSQLBindingParams(list.get(i), prStm, SQLQueryType.UPDATE);
            }
            prStm.addBatch();
            if (shouldCommitBatch(++batchSizeCounter)) {
                commitBatchTransaction(conn, prStm);
            }
        }
        commitBatchTransaction(conn, prStm);
    }

    static <T, ID> List<T> addToBatchThenCommitProperlyForFind(List<?> list, JdbcRepositoryParams<T, ID> params,
                                                               Connection conn, PreparedStatement prStm, SQLQueryType sqlType)
            throws SQLException, IllegalAccessException, InstantiationException {
        ResultSet rs = null;
        List<T> result = null;
        int batchSizeCounter = 0;
        for (int i = 0; i < list.size(); i++) {
            if (JdbcRepository.DEBUG) {
                LOGGER.log(Level.INFO, params.getSqlQuery());
            }
            if (sqlType.compareTo(SQLQueryType.SELECT) == 0) {
                bindValueToSQLBindingParams(list.get(i), prStm, SQLQueryType.SELECT);
            }
            prStm.addBatch();
            result = new LinkedList<>();
            if (shouldCommitBatch(++batchSizeCounter)) {
                commitTransaction(conn, prStm);
            }
            rs = prStm.executeQuery();
            while (rs.next()) {
                list.add(parseFromTableDataToEntity(params.getFields(), rs));
            }
        }
        commitTransaction(conn, prStm);
        return result;
    }

    static Field getIdFieldFromEntityFields(Field[] fields) {
        Field idField = null;
        for (Field field : fields) {
            field.setAccessible(true);
            if (Objects.nonNull(field.getAnnotation(Id.class))) {
                idField = field;
            }
        }
        return idField;
    }

    static <T> String getUpdateQueryStringWithEntityFields(T entity, String originalQuery) throws IllegalAccessException {
        StringBuilder queryString = new StringBuilder(originalQuery);
        Field[] fields = entity.getClass().getDeclaredFields();
        Field idField = getIdFieldFromEntityFields(fields);
        for (int i = 1; i < fields.length; i++) {
            if (Objects.nonNull(fields[i].get(entity))) {
                if (i == fields.length - 1) {
                    queryString.append(getColumnNameFromField(fields[i])).append(" = ?")
                            .append(" WHERE ").append(getColumnNameFromField(idField)).append(" = ?");
                } else {
                    queryString.append(getColumnNameFromField(fields[i])).append(" = ?, ");
                }
            }
        }
        return queryString.toString();
    }

    static String getColumnNameFromField(Field field) {
        return field.getAnnotation(Column.class).value();
    }
}
