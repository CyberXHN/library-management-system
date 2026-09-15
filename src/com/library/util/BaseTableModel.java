package com.library.util;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseTableModel<T> extends AbstractTableModel {

    protected List<T> dataList;
    protected String[] columnNames;

    public BaseTableModel(List<T> dataList, String[] columnNames) {
        this.dataList = dataList == null ? new ArrayList<>() : dataList;
        this.columnNames = columnNames;
    }

    // 审查报告要求新增的方法
    public List<?> getList(){
        return new ArrayList<>(dataList);
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

    public void setData(List<T> newData){
        this.dataList = newData == null ? new ArrayList<>() : newData;
        fireTableDataChanged();
    }

    public T getRowItem(int rowIndex){
        return dataList.get(rowIndex);
    }
}

