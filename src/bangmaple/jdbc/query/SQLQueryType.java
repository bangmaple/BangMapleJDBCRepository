package bangmaple.jdbc.query;

public enum SQLQueryType {
    CREATE("CREATE"),
    DELETE("DELETE"),
    UPDATE("UPDATE"),
    COUNT("COUNT"),
    SELECT("SELECT");

    private String type;

    SQLQueryType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
