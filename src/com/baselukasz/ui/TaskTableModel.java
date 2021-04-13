package com.baselukasz.ui;

import com.baselukasz.core.Task;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class TaskTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    public static final int OBJECT_COL = -1;
    private static final int TITLE_COL = 0;
    private static final int STATUS_COL = 1;

    private String[] columnNames = { "Tytul", "Status zadania"};

    private List<Task> tasks;

    public TaskTableModel(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public int getRowCount() {
        return tasks.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int kol){
        return columnNames[kol];
    }

    @Override
    public Object getValueAt(int wie, int kol) {
        Task zad = tasks.get(wie);

        switch (kol){
            case TITLE_COL:
                return zad.getTitle();
            case STATUS_COL:
                if(zad.isStatus()){
                    return "tak";
                }
                else {
                    return "nie";
                }
            case OBJECT_COL:
                return zad;
            default:
                return zad.getTitle();
        }
    }

    @Override
    public Class<? extends Object> getColumnClass(int c){
        return getValueAt(0, c).getClass();
    }
}
