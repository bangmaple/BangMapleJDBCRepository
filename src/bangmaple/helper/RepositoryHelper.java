package bangmaple.helper;

import bangmaple.helper.annotations.Column;
import bangmaple.helper.annotations.Id;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public interface RepositoryHelper {

    String SQL_BINDING_PARAMETER = "?";
    String SQL_QUERY_PARAMS_DELIMETER = ", ";


    static <T> void bindValueToSQLBindingParams(Field[] fields, T entity, PreparedStatement prStm)
            throws IllegalAccessException, SQLException {
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            prStm.setObject(i + 1, fields[i].get(entity));
        }
    }

    static String getParametersString(Field[] fields) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i == fields.length - 1) {
                result.append(fields[i].getName());
                break;
            }
            result.append(fields[i].getName()).append(SQL_QUERY_PARAMS_DELIMETER);
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


    static <T> T parseFromTableDataToEntity(Field[] fields, ResultSet resultSet, Class<T> entity) throws SQLException,
            IllegalAccessException, InstantiationException {
        T t = entity.newInstance();
        for (Field field : fields) {
            field.setAccessible(true);
            String columnName = getTableColumnNameFromField(field);
            field.set(t, resultSet.getObject(columnName));
        }
        return t;
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
            result.append(SQL_BINDING_PARAMETER).append(SQL_QUERY_PARAMS_DELIMETER);
        }
        return result.toString();
    }

    static String getTableColumnNameFromField(Field field) {
        return field.getAnnotation(Column.class).value();
    }
}
