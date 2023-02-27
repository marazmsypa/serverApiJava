package org.example.data.repositories.internal;

import org.example.database.annotations.Column;
import org.example.database.annotations.ForeignKey;
import org.example.database.annotations.ID;
import org.example.database.annotations.Table;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class RepositoryUtils {
    RepositoryUtils() {

    }
    public <T> Table getTable(Class<T> tClass) {
        Table table = tClass.getAnnotation(Table.class);
        if (table == null) {
            throw new IllegalArgumentException("Model class has no \"Table\" annotation");
        }
        return table;
    }

    public int getRowsCount(ResultSet resultSet) throws SQLException {
        int size = 0;
        if (resultSet != null) {
            resultSet.last();
            size = resultSet.getRow();
        }
        return size;
    }

    public <T> Field getIdField(Class<T> tClass) {
        for (Field field : tClass.getDeclaredFields()) {
            ID id = field.getAnnotation(ID.class);

            if (id != null) {
                field.setAccessible(true);
                return field;
            }
        }

        return null;
    }

    public <T> String getIdColumnName(Class<T> tClass) {
        Field field = getIdField(tClass);

        if (field == null) return null;

        Column column = field.getAnnotation(Column.class);
        return column.name();
    }

    public <T> Map<String, Field> mapModel(Class<T> modelClass) {
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

    public <T> T parseRow(Class<T> tClass,
                           Map<String, Field> routes,
                           ResultSet resultSet,
                           OnSearchForeign onSearchForeign) {
        T newObj = createInstance(tClass);

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

                    data = onSearchForeign.onSearch(foreignKey, field.getType());
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

    public <T> T createInstance(Class<T> tClass) {
        try {
            Constructor<T> constructor = tClass.getDeclaredConstructor();

            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    interface OnSearchForeign {
        Object onSearch(int id, Class<?> tClass);
    }
}
