package org.example.data.repositories;

import org.example.database.Database;
import org.example.database.QueryBuilder;
import org.example.database.annotations.Column;
import org.example.database.annotations.ForeignKey;
import org.example.database.annotations.ID;
import org.example.database.annotations.Table;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class JdbcRepository<E, PK> {
    private final Connection connection;

    public JdbcRepository(Database database) {
        this.connection = database.getConnection();
    }

    public abstract Class<E> getModelClass();

    public E findById(PK id) {
        return findById(id, getModelClass());
    }

    public E findOne(QueryBuilder queryBuilder) {
        return findOne(queryBuilder, getModelClass());
    }

    public List<E> findAll(QueryBuilder queryBuilder) {
        return findAll(queryBuilder, getModelClass());
    }


    protected <T> T findOne(Class<T> tClass) {
        return findOne(new QueryBuilder(), tClass);
    }

    protected <T> T findOne(QueryBuilder query, Class<T> tClass) {
        Table table = getTable(tClass);
        query.setTable(table.name());
        Map<String, Field> routes = mapModel(tClass);

        try (Statement statement = connection.createStatement()) {
            String sql = query.build();

            try (ResultSet resultSet = statement.executeQuery(sql)) {
                long count = getRowsCount(resultSet);
                if (count == 0) {
                    return null;
                }

                resultSet.first();

                return parseRow(tClass, routes, resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected  <T, K> T findById(K id, Class<T> tClass) {
        QueryBuilder query = new QueryBuilder();

        query.addCondition(getIdColumnName(tClass), "=", id);
        return findOne(query, tClass);
    }

    protected <T> List<T> findAll(Class<T> tClass) {
        return findAll(new QueryBuilder(), tClass);
    }

    protected <T> List<T> findAll(QueryBuilder query, Class<T> tClass) {
        Table table = getTable(tClass);
        query.setTable(table.name());

        Map<String, Field> routes = mapModel(tClass);

        try (Statement statement = connection.createStatement()) {
            String selectSql = query.build();

            try (ResultSet resultSet = statement.executeQuery(selectSql)) {
                int rowsCount = getRowsCount(resultSet);
                resultSet.first();
                List<T> result = new ArrayList<>(rowsCount);

                do {
                    T newModel = parseRow(tClass, routes, resultSet);
                    result.add(newModel);
                } while (resultSet.next());

                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> Table getTable(Class<T> tClass) {
        Table table = tClass.getAnnotation(Table.class);
        if (table == null) {
            throw new IllegalArgumentException("Model class has no \"Table\" annotation");
        }
        return table;
    }

    private int getRowsCount(ResultSet resultSet) throws SQLException {
        int size = 0;
        if (resultSet != null) {
            resultSet.last();
            size = resultSet.getRow();
        }
        return size;
    }

    private <T> String getIdColumnName(Class<T> tClass) {
        for (Field field : tClass.getDeclaredFields()) {
            ID id = field.getAnnotation(ID.class);

            if (id != null) {
                field.setAccessible(true);
                return field.getName();
            }
        }

        return null;
    }

    private <T> Map<String, Field> mapModel(Class<T> modelClass) {
        Map<String, Field> routes = new HashMap<>();
        for (Field field : modelClass.getDeclaredFields()) {
            Column annotation = field.getAnnotation(Column.class);
            if (annotation != null) {
                field.setAccessible(true);
                routes.put(annotation.name(), field);
            }
        }
        return routes;
    }

    private <T> T parseRow(Class<T> tClass, Map<String, Field> routes, ResultSet resultSet) {
        T newObj;
        try {
            Constructor<T> constructor = tClass.getDeclaredConstructor();

            newObj = constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        for (String column : routes.keySet()) {
            Field field = routes.get(column);
            Object data = null;

            ForeignKey key = field.getAnnotation(ForeignKey.class);
            if (key == null) {
                try {
                    data = resultSet.getObject(column);
                } catch (SQLException ignored) {
                }
            } else {
                try {
                    int foreignKey = resultSet.getInt(column);

                    data = findById(foreignKey, field.getType());
                } catch (SQLException ignored) {
                }
            }

            if (data != null) {
                try {
                    field.set(newObj, data);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return newObj;
    }
}
