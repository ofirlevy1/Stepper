package DataTypes;

import java.util.ArrayList;

public class Relation {
    private String[][] matrix;
    private String[] columnNames;
    private int rows;
    private int cols;

    public Relation(int rows, int cols, String ... columnNames) {
        this.rows = rows;
        this.cols = cols;
        this.matrix = new String[rows][cols];
        if (columnNames.length != cols) {
            throw new RuntimeException("'Relation' constructor was called with " + cols + " columns, and " + columnNames.length + " column names!");
        }
        this.columnNames = columnNames.clone();
    }

    public void set(int row, int col, String value) {
        matrix[row][col] = value;
    }

    public String get(int row, int col) {
        return matrix[row][col];
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public String[] getColumnNames() {
        return columnNames.clone();
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames.clone();
    }

    public boolean isEmpty() {
        return rows == 0;
    }
}
