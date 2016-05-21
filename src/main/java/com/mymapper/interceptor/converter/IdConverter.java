package com.mymapper.interceptor.converter;

import com.mymapper.interceptor.Converter;
import com.mymapper.util.Logger;
import com.mymapper.util.ReflectionUtil;

/**
 * Created by huang on 3/30/16.
 */
public class IdConverter extends Converter {
    private static final Logger logger = Logger.newInstance(IdConverter.class);
    private static final String KEY = "ById";

    @Override
    protected void convert(Args args) {
        String originSql = args.boundSql.getSql();
        String beforeWhereSql = originSql.substring(0, originSql.indexOf("WHERE"));
        String newSql = convertSql(beforeWhereSql, args);
        ReflectionUtil.setFieldValue(args.boundSql, "sql", newSql);
    }

    private String convertSql(String beforeWhereSql, Args args) {
        StringBuilder sb = new StringBuilder();
        sb
            .append(beforeWhereSql)
            .append(" WHERE ")
            .append(args.table.id())
            .append(" = ?");
        return sb.toString();
    }

    @Override
    public boolean shouldConvert(Args args) {
        return args.methodName.endsWith(KEY);
    }
}
