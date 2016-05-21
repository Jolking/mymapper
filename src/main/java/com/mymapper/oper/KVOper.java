package com.mymapper.oper;

public class KVOper extends Oper {
    private String key;
    private Object value;

    public KVOper(String key, String symbol, Object value) {
        super(symbol);
        this.key = key;
        this.value = value;
    }

    public KVOper(String key, Object value) {
        super("=");
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void convertSql(int i, StringBuilder sb) {
        sb.append(key)
          .append(getSymbol())
          .append(" ? ");
    }
}