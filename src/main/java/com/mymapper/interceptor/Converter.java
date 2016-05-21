package com.mymapper.interceptor;

import com.mymapper.WhereList;
import com.mymapper.annotation.Table;
import com.mymapper.oper.Oper;
import com.mymapper.util.ReflectionUtil;
import com.mymapper.util.StringUtil;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;

import java.util.List;
import java.util.Map;

/**
 * Created by huang on 3/30/16.
 */
public abstract class Converter {
    public static final String PREFIX = "____";
    private static final String AND = " AND ";

    protected abstract void convert(Args args);

    protected abstract boolean shouldConvert(Args args);

    protected <T extends Oper> void convertToSql(Args args,
                                                 List<Oper> list,
                                                 String replaced) {
        Map paramObj = (Map) args.boundSql.getParameterObject();
        String originSql = args.boundSql.getSql();
        int paramCount = StringUtil.countOfChar(originSql.substring(0, originSql.indexOf(replaced)), '?');

        StringBuilder sb = new StringBuilder();
        String property = null;
        ParameterMapping pm;
        Object value;
        int index = 0;
        for (Oper oper : list) {
            oper.convertSql(index++, sb);
            property = PREFIX + paramCount;
            value = oper.getValue();
            if (value != null) {
                pm = new ParameterMapping
                    .Builder(args.conf, property, value.getClass())
                    .javaType(value.getClass())
                    .build();
                paramObj.put(property, value);
                args.boundSql.setAdditionalParameter(property, value);
                args.parameterMappingList.add(paramCount++, pm);
            }
        }

        String newSql = originSql.replaceFirst(replaced, sb.toString());
        ReflectionUtil.setFieldValue(args.boundSql, "sql", newSql);
    }

    protected static class Args {
        public Object delegate;
        public BoundSql boundSql;
        public Class<?> type;
        public String methodName;
        public Configuration conf;
        public Table table;
        public Object entity;
        public Map<String, Object> nonNullMap;
        public WhereList whereList;
        public MappedStatement ms;
        public List parameterMappingList;
    }
}
