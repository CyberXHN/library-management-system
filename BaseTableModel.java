package com.library.util;

import javax.swing.table.AbstractTableModel;
import java.lang.reflect.Field;
import java.util.List;

public class BaseTableModel<T> extends AbstractTableModel {
    private List<T> list;
    private String[] columns;
    private String[] props;

    public BaseTableModel(List<T> list, String[] columns, String[] props) {
        this.list = list;
        this.columns = columns;
        this.props = props;
    }

    public void setList(List<T> list) {
        this.list = list;
        fireTableDataChanged();
    }

    public T getRow(int index) {
        return list.get(index);
    }

    @Override
    public int getRowCount() {
        if(list == null){ return 0;}
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    public Object getValueAt(int row, int col) {
        try {
            Object obj = list.get(row);
            String path = props[col];
            String[] arr = path.split("\\.");
            Object cur = obj;
            for (String f : arr) {
                Field field = cur.getClass().getDeclaredField(f);
                field.setAccessible(true);
                cur = field.get(cur);
                if(cur == null) {return "";}
            }
            return cur;
        } catch (Exception e) {
            return "";
        }
    }
}
