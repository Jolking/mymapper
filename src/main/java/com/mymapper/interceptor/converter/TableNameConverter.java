package com.mymapper.interceptor.converter;

import com.mymapper.interceptor.Converter;
import com.mymapper.util.ReflectionUtil;

/**
 * Created by huang on 5/21/16.
 */
public class TableNameConverter extends Converter {
    private static final String TABLE_NAME = "TABLE_NAME";
    @Override
    protected void convert(Args args) {
        String originSql = args.boundSql.getSql();
        // 替换表名
        String newSql = originSql.replace("TABLE_NAME", args.table.tableName());
        ReflectionUtil.setFieldValue(args.boundSql, "sql", newSql);
    }

    @Override
    protected boolean shouldConvert(Args args) {
        return args.boundSql.getSql().contains(TABLE_NAME);
    }
}
