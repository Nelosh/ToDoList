package lv.tsi.todolist.db;


import java.util.ArrayList;
import java.util.List;

public class SQLBuilder {

    private String SQL = "";

    public SQLBuilder SELECT(String... fields) {
        SQL += "SELECT " + commaSeparated(fields);
        return this;
    }

    public SQLBuilder DELETE() {
        SQL += "DELETE ";
        return this;
    }

    public SQLBuilder FROM(String table) {
        SQL += " FROM " + table;
        return this;
    }

    public SQLBuilder WHERE(SQLExpression expression) {
        SQL += " WHERE " + expression;
        return this;
    }

    public SQLBuilder ORDER_BY(String... fields) {
        SQL += " ORDER BY " + commaSeparated(fields);
        return this;
    }

    public SQLBuilder UPDATE(String table) {
        SQL += "UPDATE " + table;
        return this;
    }

    public SQLBuilder SET(SQLExpression... expressions) {
        SQL += " SET " + commaSeparated(expressions);
        return this;
    }

    public SQLBuilder INSERT_INTO(String table, String... fields) {
        SQL += "INSERT INTO " + table;
        if (fields.length == 0)
            return this;

        SQL += " (" + commaSeparated(fields) + ")";
        return this;
    }

    public SQLBuilder VALUES(String... values) {
        SQL += "VALUES (" + commaSeparated(quoted(values)) + ")";
        return this;
    }

    private <T> String commaSeparated(T[] values) {
        String result = "";
        for (T value: values) {
            result += value + ",";
        }
        return result.substring(0, result.length() - 1);
    }

    private <T> String[] quoted(T[] values) {
        List<String> result = new ArrayList<>();
        for (T value : values) {
            result.add("'" + value + "'");
        }
        return result.toArray(new String[]{});
    }

    @Override
    public String toString() {
        return SQL;
    }

    public static class SQLExpression {
        private String expression = "";

        public SQLExpression(String column, String op, String value) {
            expression = expr(column, op, value);
        }

        private String expr(String column, String op, String value) {
            return column + " " + op + " '" + value + "'";
        }

        public SQLExpression AND(String column, String op, String value) {
            expression += " AND " + expr(column, op, value);
            return this;
        }

        public SQLExpression OR(String column, String op, String value) {
            expression += " OR " + expr(column, op, value);
            return this;
        }

        @Override
        public String toString() {
            return expression;
        }

        public String enclosed() {
            return "(" + this.toString() + ")";
        }
    }
}
