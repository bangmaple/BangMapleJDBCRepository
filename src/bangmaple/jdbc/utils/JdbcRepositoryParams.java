package bangmaple.jdbc.utils;

import bangmaple.jdbc.annotations.Table;
import bangmaple.jdbc.helper.RepositoryHelper;
import bangmaple.jdbc.query.SQLQueryType;

import java.lang.reflect.Field;

import static bangmaple.jdbc.helper.RepositoryHelper.getIdFieldNameFromFields;
import static bangmaple.jdbc.helper.RepositoryHelper.getStringParamValuesFromEntity;

public class JdbcRepositoryParams<T, ID> {

    private Field[] fields;
    private String tableName;
    private String sqlColumnParams;
    private String sqlColumnParamValues;
    private String sqlQuery;

    private JdbcRepositoryParams() {}

    public JdbcRepositoryParams(T entity, String sqlQuery, SQLQueryType sqlType) {
        postConstruct(entity, null, sqlQuery, sqlType);
    }

    public JdbcRepositoryParams(T entity, ID id, String sqlQuery,  SQLQueryType sqlType) {
        postConstruct(entity, id, sqlQuery, sqlType);
    }

    public Field[] getFields() {
        return fields;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    public void postConstruct(T entity, ID id, String sqlQuery, SQLQueryType sqlType) {
        this.fields = entity.getClass().getDeclaredFields();
        this.tableName = entity.getClass().getAnnotation(Table.class).name();
        String idFieldName = getIdFieldNameFromFields(fields);
        this.sqlColumnParams = RepositoryHelper.getParametersString(fields);
        if (sqlType.compareTo(SQLQueryType.DELETE) == 0) {
            if (sqlQuery.split("%").length > 1) {
                this.sqlQuery = String.format(sqlQuery, tableName, idFieldName);
            } else {
                this.sqlQuery = String.format(sqlQuery, tableName);
            }
        } else if (sqlType.compareTo(SQLQueryType.COUNT) == 0) {
            this.sqlQuery = String.format(sqlQuery, idFieldName, tableName, idFieldName);
        } else if (sqlType.compareTo(SQLQueryType.SELECT) == 0) {
            this.sqlQuery = String.format(sqlQuery, sqlColumnParams, tableName, idFieldName);
        } else if (sqlType.compareTo(SQLQueryType.CREATE) == 0) {
            String paramValues = getStringParamValuesFromEntity(entity);
            this.sqlQuery = String.format(sqlQuery, tableName, sqlColumnParams, paramValues);
        } else if (sqlType.compareTo(SQLQueryType.UPDATE) == 0) {
            this.sqlQuery = String.format(sqlQuery, tableName, idFieldName, "'" + id + "'");
        }
    }
}
