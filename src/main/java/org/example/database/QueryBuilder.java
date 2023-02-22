package org.example.database;

import java.util.ArrayList;
import java.util.List;

public class QueryBuilder {
    private final List<String> columns = new ArrayList<>();
    private final List<Where> conditions = new ArrayList<>();
    private String namespace;
    private String table;

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void addColumn(String column) {
        this.columns.add(column);
    }

    public void addCondition(String column, String compare, Object value) {
        Where where = new Where(column, compare, value);
        this.conditions.add(where);
    }

    public String build() {
        StringBuilder query = new StringBuilder();

        query.append("select ");
        if (columns.isEmpty()) query.append("*");
        else query.append(String.join(", ", columns));

        query.append(" from ");
        if (namespace == null) query.append(String.format("%s", table));
        else query.append(String.format("%s.%s", namespace, table));

        for (Where condition : conditions) {
            if (conditions.indexOf(condition) != 0) {
                query.append(" and");
            }
            query.append(
                    String.format(" where %s %s \"%s\"", condition.column, condition.compare, condition.value.toString()));
        }

        return query.toString();
    }

    private record Where(String column, String compare, Object value) {
    }
}
