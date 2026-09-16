package com.library.util;

import javax.swing.table.AbstractTableModel;
import java.lang.reflect.Field;
import java.util.List;

public class BaseTableModel<T> extends AbstractTableModel {
    private final List<T> dataList;
    private final String[] columnProps;
    private final String[] columnNames;

    public BaseTableModel(List<T> dataList, String[] columnProps, String[] columnNames) {
        this.dataList = dataList;
        this.columnProps = columnProps;
        this.columnNames = columnNames;
    }

    @Override
    public int getRowCount() {
        return dataList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        T item = dataList.get(rowIndex);
        String propPath = columnProps[columnIndex];
        return getNestedValue(item, propPath);
    }

    // 核心新增：支持嵌套属性，例如 "book.category.name"
    private Object getNestedValue(Object rootObj, String propPath) {
        if (rootObj == null || propPath == null || propPath.isBlank()) {
            return null;
        }
        String[] props = propPath.split("\\.");
        Object current = rootObj;
        for (String prop : props) {
            if (current == null) {
                return null;
            }
            try {
                Field field = current.getClass().getDeclaredField(prop);
                field.setAccessible(true);
                current = field.get(current);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                return null;
            }
        }
        return current;
    }

    public List<T> getDataList() {
        return dataList;
    }
}
