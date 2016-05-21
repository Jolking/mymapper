package com.mymapper.interceptor;

import com.mymapper.WhereList;
import com.mymapper.annotation.Table;
import com.mymapper.util.ClassUtil;
import com.mymapper.util.Logger;
import com.mymapper.util.ReflectionUtil;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by huang on 3/29/16.
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare",
    args = {Connection.class, Integer.class})})
public class MapperInterceptor implements Interceptor {
    private static final Logger logger = Logger.newInstance(MapperInterceptor.class);
    private static List<Converter> converterChain = new ArrayList<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Converter.Args args = new Converter.Args();
        args.delegate = getOriginTarget(invocation);
        args.boundSql = ReflectionUtil.getFieldValue(
            args.delegate,
            "delegate.boundSql",
            true
        );
        args.type = getType(args.boundSql.getParameterObject());
        if (args.type == null || !args.type.isAnnotationPresent(Table.class)) {
            return invocation.proceed();
        }
        args.ms = ReflectionUtil.getFieldValue(args.delegate,
            "delegate.mappedStatement", true);
        args.methodName = args.ms.getId().substring(args.ms.getId()
            .lastIndexOf("" + ".") + 1);

        args.table = args.type.getAnnotation(Table.class);
        args.conf = ReflectionUtil.getFieldValue(args.delegate,
            "delegate.configuration", true);
        try {
            List oldList = ReflectionUtil.getFieldValue(
                args.boundSql.getParameterMappings(), "list", true
            );
            List newList = null;
            if (oldList == null)
                oldList = args.boundSql.getParameterMappings();
            if (oldList != null) {
                newList = new ArrayList<>(oldList);
                ReflectionUtil.setFieldValue(
                    args.boundSql, "parameterMappings", newList
                );
            }
            args.parameterMappingList = newList;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        args.entity = getFromParameterObject(args, "_entity");
        if (args.entity != null) {
            args.nonNullMap = ReflectionUtil.getNonNullMap(
                args.type, args.entity
            );
        } else {
            args.whereList = (WhereList) getFromParameterObject(args,
                "_whereList");
        }

        executeConverter(args);

        // 修改返回值类型
        if (args.ms.getResultMaps().size() == 1) {
            ReflectionUtil.setFieldValue(
                args.ms.getResultMaps().get(0), "type", args.type
            );
        }

        return invocation.proceed();
    }

    private Object getFromParameterObject(Converter.Args args, String key) {
        try {
            return ((Map) args.boundSql.getParameterObject()).get(key);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取最原始的代理对象
     *
     * @param invocation
     * @return
     * @throws Throwable
     */
    private Object getOriginTarget(Invocation invocation) throws Throwable {
        Object wrapped;
        Object target = invocation.getTarget();
        while (null != (wrapped = ReflectionUtil.getFieldValue(target, "h", true))) {
            target = wrapped;
        }
        while (null != (wrapped = ReflectionUtil.getFieldValue(target, "target"))) {
            target = wrapped;
        }

        return target;
    }

    private Class getType(Object object) {
        if (!(object instanceof MapperMethod.ParamMap) ||
            2 > ((MapperMethod.ParamMap) object).size()) {
            return null;
        }
        try {
            Class type = (Class<?>) ((MapperMethod.ParamMap) object).get("_type");
            return type.isAnnotationPresent(Table.class) ? type : null;
        } catch (Exception e) {
            logger.info("本次拦截的目标不属于通用mapper拦截的部分");
        }
        return null;
    }

    private void executeConverter(Converter.Args args) {
        converterChain.stream()
            .filter(converter ->
                converter.shouldConvert(args)
            )
            .forEach(converter -> {
                converter.convert(args);
            });
    }

    @Override
    public Object plugin(Object target) {
        return target instanceof StatementHandler ?
               Plugin.wrap(target, this) :
               target;
    }

    @Override
    public void setProperties(Properties properties) {
        loadConverter(properties.getProperty("converterPath"));
    }

    private void loadConverter(String converterPath) {
        if (converterPath == null) {
            return;
        }
        List<Class<?>> classList = ClassUtil.getClassList(converterPath);
        try {
            for (Class<?> c : classList) {
                if (!c.isInterface() && Converter.class.isAssignableFrom(c)) {
                    logger.debug("扫描到Converter: " + c);
                    converterChain.add((Converter) c.newInstance());
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
