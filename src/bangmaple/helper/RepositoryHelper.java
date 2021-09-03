package bangmaple.helper;

import bangmaple.helper.annotations.Column;
import bangmaple.helper.annotations.Id;
import bangmaple.helper.annotations.Table;
import bangmaple.helper.query.SQLQueryType;
import bangmaple.helper.repository.JdbcRepository;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        Field[] fields = entity.getClass().getDeclaredFields();
        int lastParamCount = fields.length;
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.get(entity) == null) {
                lastParamCount--;
            }
        }
        for (int i = 0; i < fields.length; i++) {
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
                    prStm.setObject(lastParamCount + 1, fields[i].get(entity));
                } else if (sqlType.compareTo(SQLQueryType.CREATE) == 0) {
                    prStm.setObject(i + 1, fields[i].get(entity));
                }
                continue;
            }
            if (fields[i].get(entity) != null) {
                if (i == lastParamCount) {
                    prStm.setObject(i, fields[i].get(entity));
                } else {
                    prStm.setObject(i + 1, fields[i].get(entity));
                }
            }
        }
    }

    static String getParametersString(Field[] fields) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i == fields.length - 1) {
                result.append(fields[i].getName());
                break;
            }
            result.append(fields[i].getName()).append(SQL_QUERY_PARAMS_DELIMITER);
        }
        return result.toString();
    }

    static String getIdFieldNameFromFields(Field[] fields) {
        for (Field field : fields) {
            if (Objects.nonNull(field.getAnnotation(Id.class))) {
                return field.getName();
            }
        }
        throw new IllegalArgumentException();
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


    static <T> void addToBatchThenCommitProperly(List<T> list, JdbcRepositoryParams params, Connection conn, PreparedStatement prStm)
            throws SQLException, IllegalAccessException {
        int batchSizeCounter = 0;
        for (T entityInList : list) {
            if (JdbcRepository.DEBUG) {
                LOGGER.log(Level.INFO, params.getSqlQuery());
            }
            bindValueToSQLBindingParams(entityInList, prStm, SQLQueryType.CREATE);
            prStm.addBatch();
            if (shouldCommitBatch(++batchSizeCounter)) {
                commitBatchTransaction(conn, prStm);
            }
        }
        commitBatchTransaction(conn, prStm);
    }
}
