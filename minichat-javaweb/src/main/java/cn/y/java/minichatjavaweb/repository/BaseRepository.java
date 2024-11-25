package cn.y.java.minichatjavaweb.repository;

import cn.y.java.minichatjavaweb.utils.JDBCUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaseRepository {

    public int executeUpdate(String sql, Object... params) throws SQLException {
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        // 添加sql参数
        addParams(statement, params);

        // 执行sql
        int rows = statement.executeUpdate();

        // 释放资源
        statement.close();
        // 如果是自动提交才放回连接池
        if (connection.getAutoCommit()) {
            JDBCUtil.release();
        }
        return rows;
    }

    public <T> List<T> executeQuery(String sql, Class<T> clazz, Object... params) throws SQLException {
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        // 添加sql参数
        addParams(statement, params);

        ResultSet resultSet = statement.executeQuery();
        // 获取结果元数据，就是列名
        ResultSetMetaData metaData = resultSet.getMetaData();
        // 获取列名个数
        int columnCount = metaData.getColumnCount();
        List<T> beanList = new ArrayList<>();
        while (resultSet.next()) {
            try {
                // 使用反射获取对象实例
                T bean = clazz.getDeclaredConstructor().newInstance();
                // 根据列名个数设置对象内的属性
                for (int i = 1; i <= columnCount; i++) {
                    // 获取当列对应的值
                    Object value = resultSet.getObject(i);
                    // 获取当列的label，就是sql内给列取的别名，没有就是列名
                    String columnLabel = metaData.getColumnLabel(i);
                    // 使用反射设置指定的属性
                    Field field = clazz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(bean, value);
                }
                beanList.add(bean);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        resultSet.close();
        statement.close();
        // 如果是自动提交才放回连接池
        if (connection.getAutoCommit()) {
            JDBCUtil.release();
        }
        return beanList;
    }

    public <T> T executeQueryOne(String sql, Class<T> clazz, Object... params) throws SQLException {
        List<T> resultList = executeQuery(sql, clazz, params);
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    private void addParams(PreparedStatement statement, Object[] params) throws SQLException {
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
        }
    }
}
