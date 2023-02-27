package org.example.database;

import java.util.*;

public class QueryBuilder {
    private final List<String> columns = new ArrayList<>();
    private final List<Where> conditions = new ArrayList<>();

    private final Map<String, Object> postValues = new HashMap<>();

    private String namespace;
    private String table;
    private Querytype queryType;

    public QueryBuilder() {
        this(Querytype.SELECT);
    }

    public QueryBuilder(Querytype queryType) {
        this.queryType = queryType;
    }

    public void addColumn(String column) {
        this.columns.add(column);
    }

    public void addCondition(String column, String compare, Object value) {
        Where where = new Where(column, compare, value);
        this.conditions.add(where);
    }

    public void addPostValue(String key, Object value) {
        postValues.put(key, value);
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setQueryType(Querytype queryType) {
        this.queryType = queryType;
    }

    public String build() {
        StringBuilder query = switch (this.queryType) {
            case UPDATE -> buildUpdate();
            case SELECT -> buildSelect();
            case INSERT -> buildInsert();
            default -> new StringBuilder();
        };

        return query.toString();
    }

    public StringBuilder buildInsert() {
        StringBuilder query = new StringBuilder();

        query.append(String.format("insert into %s (", getDestination()));

        query.append(String.join(", ", postValues.keySet()));
        query.append(") values (\"");
        query.append(String.join("\", \"", postValues.values().stream().map(Object::toString).toList()));
        query.append("\")");

        return query;
    }

    public StringBuilder buildUpdate() {
        StringBuilder query = new StringBuilder();

        query.append(String.format("update %s set ", getDestination()));

        query.append(String.join(", ",
                postValues
                        .entrySet()
                        .stream()
                        .map(e -> String.format("%s=\"%s\"", e.getKey(), e.getValue()))
                        .toList()));

        if (!conditions.isEmpty()) {
            query.append(" where ");
            query.append(String.join(" and ",
                    conditions
                            .stream()
                            .map(e -> String.format("%s %s \"%s\"", e.column, e.compare, e.value))
                            .toList()));
        }

        return query;
    }

    public StringBuilder buildSelect() {
        StringBuilder query = new StringBuilder();

        query.append("select ");
        if (columns.isEmpty()) query.append("*");
        else query.append(String.join(", ", columns));

        query.append(" from ");
        query.append(getDestination());
        query.append(" where ");
        query.append(String.join(" and ",
                conditions
                        .stream()
                        .map(e -> String.format("%s %s \"%s\"", e.column, e.compare, e.value))
                        .toList()));
        return query;
    }

    private String getDestination() {
        if (namespace == null) return String.format("%s", table);
        else return String.format("%s.%s", namespace, table);
    }

    public enum Querytype {
        SELECT,
        INSERT,
        UPDATE,
        DELETE
    }

    private static class Where {
        private final String column;
        private final String compare;
        private final Object value;

        private Where(String column, String compare, Object value) {
            this.column = column;
            this.compare = compare;
            this.value = value;
        }

        public String column() {
            return column;
        }

        public String compare() {
            return compare;
        }

        public Object value() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Where) obj;
            return Objects.equals(this.column, that.column) &&
                    Objects.equals(this.compare, that.compare) &&
                    Objects.equals(this.value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(column, compare, value);
        }

        @Override
        public String toString() {
            return "Where[" +
                    "column=" + column + ", " +
                    "compare=" + compare + ", " +
                    "value=" + value + ']';
        }

    }
}
