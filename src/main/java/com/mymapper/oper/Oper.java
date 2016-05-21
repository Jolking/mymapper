package com.mymapper.oper;

public class Oper {
    private String symbol;

    public Oper(String symbol) {
        this.symbol = " " + symbol + " ";
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void convertSql(int index, StringBuilder sb) {
        sb.append(symbol);
    }

    public Object getValue() {
        return null;
    }
}