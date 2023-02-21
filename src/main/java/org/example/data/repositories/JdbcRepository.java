package org.example.data.repositories;

import org.example.database.Database;
import org.example.database.annotations.Column;
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

public abstract class JdbcRepository<T> {
    private final Connection connection;

    public JdbcRepository(Database database) {
        this.connection = database.getConnection();
    }

    public List<T> list(Class<T> tClass, String queryTable) {
        IsTable(tClass);
        List<T> result = new ArrayList<>();
        Map<String, Field> routes = mapModel(tClass);

        try (Statement statement = connection.createStatement()) {
            String selectSql = "select * from user1." + queryTable;

            try (ResultSet resultSet = statement.executeQuery(selectSql)) {
                while (resultSet.next()) {
                    T newModel = parseClass(tClass, routes, resultSet);
                    result.add(newModel);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    private void IsTable(Class<T> tClass) {
        Table table = tClass.getAnnotation(Table.class);
        if (table == null) {
            throw new IllegalArgumentException("Model class has no \"Table\" annotation");
        }
    }

    private Map<String, Field> mapModel(Class modelClass) {
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

    private T parseClass(Class<T> tClass, Map<String, Field> routes, ResultSet resultSet) throws SQLException {
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
            Object data = resultSet.getObject(column);
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
