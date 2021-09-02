package bangmaple.helper;

import bangmaple.helper.annotations.Table;

import java.lang.reflect.Field;

import static bangmaple.helper.RepositoryHelper.getIdFieldNameFromFields;

public class JdbcRepositoryParams<T> {

    private Field[] fields;
    private String tableName;
    private String sqlColumnParams;
    private String sqlColumnParamValues;
    private String sqlQuery;

    private JdbcRepositoryParams() {}

    public JdbcRepositoryParams(Class<T> clazz, String sqlQuery) {
        this.fields = clazz.getDeclaredFields();
        this.tableName = clazz.getAnnotation(Table.class).name();
        String idFieldName = getIdFieldNameFromFields(fields);
        this.sqlColumnParams = RepositoryHelper.getParametersString(fields);
        this.sqlQuery = String.format(sqlQuery, sqlColumnParams, tableName, idFieldName);
    }

    public JdbcRepositoryParams(Class<T> clazz, T entity, String sqlQuery) throws IllegalAccessException {
        this.fields = clazz.getDeclaredFields();
        this.tableName = clazz.getAnnotation(Table.class).name();
        String idFieldName = getIdFieldNameFromFields(fields);
        this.sqlColumnParams = RepositoryHelper.getParametersString(fields);
        this.sqlColumnParamValues = RepositoryHelper.getStringParamValuesFromEntity(entity);
        this.sqlQuery = String.format(sqlQuery, tableName, sqlColumnParams, sqlColumnParamValues);
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
