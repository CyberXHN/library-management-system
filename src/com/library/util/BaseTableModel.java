package com.library.util;

import javax.swing.table.AbstractTableModel;
import java.lang.reflect.Field;
import java.util.List;

public class BaseTableModel<T> extends AbstractTableModel {
    private List<T> dataList;
    private String[] columnNames;
    private String[] props;

    // 契约要求：三参数构造器 list, columns, props
    public BaseTableModel(List<T> dataList, String[] columnNames, String[] props) {
        this.dataList = dataList;
        this.columnNames = columnNames;
        this.props = props;
    }

    public void setList(List<T> newList) {
        this.dataList = newList;
        fireTableDataChanged();
    }

    public T getRow(int rowIndex) {
        return dataList.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    // 反射取值，契约要求
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        T item = dataList.get(rowIndex);
        String fieldName = props[columnIndex];
        try {
            Field field = item.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(item);
        } catch (Exception e) {
            return "";
        }
    }
}
