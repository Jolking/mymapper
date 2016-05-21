package com.mymapper.interceptor.converter;

import com.mymapper.WhereList;
import com.mymapper.interceptor.Converter;
import com.mymapper.oper.KVOper;
import com.mymapper.oper.Oper;
import com.mymapper.util.ReflectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 若传入entity， 则转换WHERE_FIELD为: KEY1 = VALUE1 AND KEY2 = VALUE2<br/>
 * <pre>
 *     selectByWhere: SELECT XXX FROM t_user WHERE WHERE_FIELD
 *     User user = new User();
 *     user.setAccount("account");
 *     user.setUsername("username");
 *     selectByWhere(User.class, fieldList, user);
 * </pre>
 * 变为:
 * SELECT XXX FROM t_user WHERE account='account' AND username='username'<br/>
 * <p>
 * 若传入WhereForm， 则根据调用符号的顺序转换<br/>
 * <pre>
 *     selectByWhere: SELECT XXX FROM t_user WHERE WHERE_FIELD
 *     WhereForm form = new WhereForm("account", "account").or("user_id", "3");
 *     selectByWhere(User.class, fieldList, form);
 * </pre>
 * 变为:
 * SELECT XXX FROM t_user WHERE account='account' OR user_id = '3'<br/>
 *
 * @author hjk
 */
public class WhereFieldConverter extends Converter {
    public static final String WHERE_FIELD = "WHERE_FIELD";
    private static final String AND = " AND ";

    @Override
    protected void convert(Converter.Args args) {
        if (args.entity != null) {
            convertByEntity(args);
        } else if (args.whereList != null) {
            convertByForm(args);
        } else {
            String newSql = args.boundSql.getSql().replace("WHERE_FIELD", "1 " +
                "= 1");
            ReflectionUtil.setFieldValue(args.boundSql, "sql", newSql);
        }
    }

    private void convertByEntity(Args args) {
        List<Oper> list = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Object> entry : args.nonNullMap.entrySet()) {
            if (index++ > 0)
                list.add(new Oper(AND));
            list.add(new KVOper(entry.getKey(), entry.getValue()));
        }
        super.convertToSql(args, list, WHERE_FIELD);
    }

    private void convertByForm(Args args) {
        WhereList whereList = args.whereList;
        super.convertToSql(args, whereList, WHERE_FIELD);
    }

    @Override
    public boolean shouldConvert(Args args) {
        return args.boundSql.getSql().contains(WHERE_FIELD);
    }
}
