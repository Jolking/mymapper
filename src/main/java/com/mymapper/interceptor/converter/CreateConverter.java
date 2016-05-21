package com.mymapper.interceptor.converter;

import com.mymapper.interceptor.Converter;
import com.mymapper.oper.Oper;
import com.mymapper.util.ReflectionUtil;
import com.mymapper.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 转换CREATE_KEY为字段, CREATE_VALUE为值<br/>
 * <pre>
 *     create: INSERT INTO  t_user(CREATE_KEY) VALUES(CREATE_VALUE)
 *     User user = new User();
 *     user.setAccount("account");
 *     user.setUsername("username");
 *     create(User.class, user);
 * </pre>
 * 变为:
 * INSERT INTO  t_user(account, username) VALUES('account', 'username')
 *
 * @author hjk
 */
public class CreateConverter extends Converter {
    private static final String CREATE_KEY = "CREATE_KEY";
    private static final String CREATE_VALUE = "CREATE_VALUE";
    private static final String COMMA = ", ";

    @Override
    protected void convert(Converter.Args args) {
        if (args.entity != null) {
            List<Oper> list = convertToCreateOper(args.nonNullMap);
            convertByEntity(args, list, null);
        } else {
            try {
                List list = (List) ((Map) args.boundSql.getParameterObject()
                ).get("list");
                List<List<Oper>> batchList = new ArrayList<>();
                for (Object o : list) {
                    args.nonNullMap = ReflectionUtil.getNonNullMap(args.type, o);
                    batchList.add(convertToCreateOper(args.nonNullMap));
                }
                convertByEntity(args, list, batchList);
            } catch (Exception e) {
            }
        }
    }

    private List<Oper> convertToCreateOper(Map<String, Object> entityMap) {
        List<Oper> list = entityMap.entrySet().stream()
            .map(entry -> new CreateOper(COMMA, entry.getValue()))
            .collect(Collectors.toList());
        return list;
    }

    private void convertByEntity(Args args, List<Oper> list, List<List<Oper>> batchList) {
        String newSql;
        newSql = convertSql(args, list, batchList);
        ReflectionUtil.setFieldValue(args.boundSql, "sql", newSql);

        if (batchList == null) {
            String[] keyProperties = new String[1];
            keyProperties[0] = "_entity." + StringUtil.camel2Underline(args.table.id());
            ReflectionUtil.setFieldValue(args.ms, "keyProperties", keyProperties);
        } else {
            String[] keyProperties = new String[1];
            keyProperties[0] = StringUtil.camel2Underline(args.table.id());
            ReflectionUtil.setFieldValue(args.ms, "keyProperties", keyProperties);
        }
    }

    private String convertSql(Args args, List<Oper> list, List<List<Oper>> batchList) {
        StringBuilder keySb;
        keySb = buildKeySQL(args.nonNullMap);
        if (batchList != null) {
            batchList.forEach(l -> super.convertToSql(args, l, CREATE_VALUE));
        } else {
            super.convertToSql(args, list, CREATE_VALUE);
        }
        return args.boundSql.getSql().replace(CREATE_KEY, keySb.toString());
    }

    private StringBuilder buildKeySQL(Map<String, Object> map) {
        StringBuilder keySb = new StringBuilder();
        int index = 0;
        for (String key : map.keySet()) {
            if (index++ > 0) {
                keySb.append(COMMA);
            }
            keySb.append(key);
        }
        return keySb;
    }

    @Override
    public boolean shouldConvert(Args args) {
        return args.boundSql.getSql().contains(CREATE_KEY) &&
            args.boundSql.getSql().contains(CREATE_VALUE);
    }

    protected class CreateOper extends Oper {
        private Object value;

        public CreateOper(String symbol, Object value) {
            super(symbol);
            this.value = value;
        }

        @Override
        public Object getValue() {
            return value;
        }

        @Override
        public void convertSql(int index, StringBuilder sb) {
            if (index++ > 0) {
                sb.append(getSymbol());
            }
            sb.append(" ? ");
        }
    }
}
