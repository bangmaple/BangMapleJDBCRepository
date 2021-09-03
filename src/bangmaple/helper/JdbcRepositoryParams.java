package bangmaple.helper;

import bangmaple.helper.annotations.Id;
import bangmaple.helper.annotations.Table;
import bangmaple.helper.query.SQLQueryType;

import java.lang.reflect.Field;
import java.util.Objects;

import static bangmaple.helper.RepositoryHelper.getIdFieldNameFromFields;
import static bangmaple.helper.RepositoryHelper.getStringParamValuesFromEntity;

public class JdbcRepositoryParams<T, ID> {

    private Field[] fields;
    private String tableName;
    private String sqlColumnParams;
    private String sqlColumnParamValues;
    private String sqlQuery;

    private JdbcRepositoryParams() {

    }


    public JdbcRepositoryParams(T entity, String sqlQuery, SQLQueryType sqlType) {
        this.fields = entity.getClass().getDeclaredFields();
        this.tableName = entity.getClass().getAnnotation(Table.class).name();
        String idFieldName = getIdFieldNameFromFields(fields);
        this.sqlColumnParams = RepositoryHelper.getParametersString(fields);
        Field idField = null;
        for (Field field : fields) {
            if (Objects.nonNull(field.getAnnotation(Id.class))) {
                idField = field;
            }
        }
        if (sqlType.compareTo(SQLQueryType.DELETE) == 0) {
            this.sqlQuery = String.format(sqlQuery, tableName);
        } else if (sqlType.compareTo(SQLQueryType.COUNT) == 0) {
            this.sqlQuery = String.format(sqlQuery, idField.getName(), tableName, idField.getName());
        } else if (sqlType.compareTo(SQLQueryType.SELECT) == 0) {
            this.sqlQuery = String.format(sqlQuery, sqlColumnParams, tableName, idFieldName);
        } else if (sqlType.compareTo(SQLQueryType.CREATE) == 0) {
            String paramValues = getStringParamValuesFromEntity(entity);
            this.sqlQuery = String.format(sqlQuery, tableName, sqlColumnParams, paramValues);
        }

    }

    public JdbcRepositoryParams(T entity, String sqlQuery, ID id, SQLQueryType sqlType) {
        this.fields = entity.getClass().getDeclaredFields();
        this.tableName = entity.getClass().getAnnotation(Table.class).name();
        String idFieldName = getIdFieldNameFromFields(fields);
        this.sqlColumnParams = RepositoryHelper.getParametersString(fields);
        if (sqlType.compareTo(SQLQueryType.UPDATE) == 0) {
            this.sqlQuery = String.format(sqlQuery, tableName);
        } else {
            this.sqlQuery = String.format(sqlQuery, tableName, idFieldName, "'" + id + "'");
        }
    }

    public Field[] getFields() {
        return fields;
    }

    public void setFields(Field[] fields) {
        this.fields = fields;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSqlColumnParams() {
        return sqlColumnParams;
    }

    public void setSqlColumnParams(String sqlColumnParams) {
        this.sqlColumnParams = sqlColumnParams;
    }

    public String getSqlColumnParamValues() {
        return sqlColumnParamValues;
    }

    public void setSqlColumnParamValues(String sqlColumnParamValues) {
        this.sqlColumnParamValues = sqlColumnParamValues;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }
}
