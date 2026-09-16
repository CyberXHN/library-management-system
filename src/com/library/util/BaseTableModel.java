package com.library.util;

import javax.swing.table.AbstractTableModel;
import java.lang.reflect.Field;
import java.util.List;

public class BaseTableModel<T> extends AbstractTableModel {
    private List<T> dataList;
    private final String[] columns;
    private final String[] props;

    // 【严格还原原始契约顺序】(list, columns, props)，禁止调换顺序！
    public BaseTableModel(List<T> dataList, String[] columns, String[] props) {
        this.dataList = dataList;
        this.columns = columns;
        this.props = props;
    }

    @Override
    public int getRowCount() {
        // 恢复null保护，防止dataList为null空指针
        if(dataList == null){
            return 0;
        }
        return dataList.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        T item = dataList.get(rowIndex);
        String propPath = props[columnIndex];
        return getNestedValue(item, propPath);
    }

    // 新增：嵌套属性解析，支持 category.name / book.category.name
    private Object getNestedValue(Object rootObj, String propPath) {
        if (rootObj == null || propPath == null || propPath.isBlank()) {
            return null;
        }
        String[] propsSplit = propPath.split("\\.");
        Object current = rootObj;
        for (String prop : propsSplit) {
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

    // 【恢复原有方法，B/C模块依赖，绝对不能删！】
    public void setList(List<T> newList) {
        this.dataList = newList;
        fireTableDataChanged();
    }

    // 【恢复原有getRow方法】
    public T getRow(int rowIndex) {
        if(dataList == null || rowIndex <0 || rowIndex >= dataList.size()){
            return null;
        }
        return dataList.get(rowIndex);
    }

    public List<T> getDataList() {
        return dataList;
    }
}
