package com.mymapper.interceptor.converter;

import com.mymapper.interceptor.Converter;
import com.mymapper.oper.KVOper;
import com.mymapper.oper.Oper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 转换UPDATE_FIELD为: KEY1 = VALUE1, KEY2 = VALUE2<br/>
 * <pre>
 *     updateById: UPDATE t_user SET UPDATE_FIELD WHERE id = ${id}
 *     User user = new User();
 *     user.setAccount("account");
 *     user.setUsername("username");
 *     updateById(User.class, user, 1);
 * </pre>
 * 变为:
 * UPDATE t_user SET account='account', username='username' WHERE user_id = 1
 *
 * @author hjk
 */
public class UpdateFieldConverter extends Converter {
    public static final String UPDATE_FIELD = "UPDATE_FIELD";
    private static final String COMMA = ", ";

    @Override
    protected void convert(Converter.Args args) {
        if (args.entity != null) {
            convertByEntity(args);
        }
        // TODO 暂时取消WhereForm对更新字段的支持
    }

    private void convertByEntity(Args args) {
        List<Oper> list = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Object> entry : args.nonNullMap.entrySet()) {
            if (index++ > 0)
                list.add(new Oper(COMMA));
            list.add(new KVOper(entry.getKey(), entry.getValue()));
        }
        convertToSql(args, list, UPDATE_FIELD);
    }

    @Override
    public boolean shouldConvert(Args args) {
        return args.boundSql.getSql().contains(UPDATE_FIELD);
    }
}
