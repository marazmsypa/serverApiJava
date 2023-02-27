package org.example.data.repositories.internal;

import org.example.database.Database;
import org.example.database.QueryBuilder;
import org.example.database.annotations.Column;
import org.example.database.annotations.ForeignKey;
import org.example.database.annotations.ID;
import org.example.database.annotations.Table;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class JdbcRepository<E, PK> {
    private final Connection connection;

    private final RepositoryUtils utils = new RepositoryUtils();

    public JdbcRepository(Database database) {
        this.connection = database.getConnection();
    }

    public abstract Class<E> getModelClass();

    //Группа методов для реализации POST запросов
    public E save(E entity) {
        Field field = utils.getIdField(getModelClass());

        try {
            if (field.get(entity) == null) {
                return insert(entity, new QueryBuilder(), getModelClass());
            } else {
                return update(entity, new QueryBuilder(), getModelClass());
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /*public <T> boolean addOne(QueryBuilder query, Class<T> tClass, T objectClass){
        Table table = getTable(tClass);
        query.setTable(table.name());
        query.addPostColumns(getPostColumns(tClass));
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


            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }*/

    // группа методов для реализации GET запросов
    public E findById(PK id) {
        return findById(id, getModelClass());
    }

    public E findOne(QueryBuilder queryBuilder) {
        return findOne(queryBuilder, getModelClass());
    }

    public List<E> findAll(QueryBuilder queryBuilder) {
        return findAll(queryBuilder, getModelClass());
    }


    // System calls

    protected <T> T insert(T data, QueryBuilder queryBuilder, Class<T> tClass)
            throws IllegalAccessException {
        Table table = utils.getTable(tClass);
        queryBuilder.setTable(table.name());
        queryBuilder.setQueryType(QueryBuilder.Querytype.INSERT);

        for (Field field : tClass.getDeclaredFields()) {
            field.setAccessible(true);

            Column column = field.getAnnotation(Column.class);
            String key = column.name();
            Object value = field.get(data);

            if (value != null) {
                ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);

                if (foreignKey == null) {
                    queryBuilder.addPostValue(key, value);
                } else {
                    Class<?> fClass = field.getType();
                    Field foreignField = utils.getIdField(fClass);

                    queryBuilder.addPostValue(key, foreignField.get(value));
                }
            }
        }

        queryBuilder.addColumn(utils.getIdColumnName(tClass));

        try (Statement statement = connection.createStatement()) {
            String query = queryBuilder.build();
            statement.executeQuery(query);

            try (ResultSet resultSet = statement.executeQuery("SELECT LAST_INSERT_ID() as id")) {
                resultSet.first();

                Object id = resultSet.getObject("id");

                Field idField = utils.getIdField(tClass);
                if (idField != null) {
                    idField.set(data, id);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return data;
    }

    protected <T> T update(T data, QueryBuilder queryBuilder, Class<T> tClass)
            throws IllegalAccessException {
        Table table = utils.getTable(tClass);
        queryBuilder.setTable(table.name());
        queryBuilder.setQueryType(QueryBuilder.Querytype.UPDATE);

        for (Field field : tClass.getDeclaredFields()) {
            field.setAccessible(true);

            Column column = field.getAnnotation(Column.class);
            String key = column.name();
            Object value = field.get(data);

            if (value != null) {
                ID id = field.getAnnotation(ID.class);
                if (id != null) {
                    queryBuilder.addCondition(key, "=", value);
                    continue;
                }

                ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);

                if (foreignKey == null) {
                    queryBuilder.addPostValue(key, value);
                } else {
                    Class<?> fClass = field.getType();
                    Field foreignField = utils.getIdField(fClass);

                    queryBuilder.addPostValue(key, foreignField.get(value));
                }
            }
        }

        try (Statement statement = connection.createStatement()) {
            String query = queryBuilder.build();
            statement.executeQuery(query);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return data;
    }

    protected <T> T findOne(Class<T> tClass) {
        return findOne(new QueryBuilder(QueryBuilder.Querytype.SELECT), tClass);
    }

    protected <T> T findOne(QueryBuilder query, Class<T> tClass) {
        Table table = utils.getTable(tClass);
        query.setTable(table.name());
        query.setQueryType(QueryBuilder.Querytype.SELECT);

        Map<String, Field> routes = utils.mapModel(tClass);

        try (Statement statement = connection.createStatement()) {
            String sql = query.build();

            try (ResultSet resultSet = statement.executeQuery(sql)) {
                long count = utils.getRowsCount(resultSet);
                if (count == 0) {
                    return null;
                }

                resultSet.first();

                return utils.parseRow(tClass, routes, resultSet, this::findById);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected <T, K> T findById(K id, Class<T> tClass) {
        QueryBuilder query = new QueryBuilder(QueryBuilder.Querytype.SELECT);
        query.addCondition(utils.getIdColumnName(tClass), "=", id);
        return findOne(query, tClass);
    }

    protected <T> List<T> findAll(Class<T> tClass) {
        return findAll(new QueryBuilder(QueryBuilder.Querytype.SELECT), tClass);
    }

    protected <T> List<T> findAll(QueryBuilder query, Class<T> tClass) {
        Table table = utils.getTable(tClass);
        query.setTable(table.name());

        Map<String, Field> routes = utils.mapModel(tClass);

        try (Statement statement = connection.createStatement()) {
            String selectSql = query.build();

            try (ResultSet resultSet = statement.executeQuery(selectSql)) {
                int rowsCount = utils.getRowsCount(resultSet);
                resultSet.first();
                List<T> result = new ArrayList<>(rowsCount);

                do {
                    T newModel = utils.parseRow(tClass, routes, resultSet, this::findById);
                    result.add(newModel);
                } while (resultSet.next());

                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
