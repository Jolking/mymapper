package com.mymapper.interceptor;

import com.mymapper.Page;
import com.mymapper.util.ReflectionUtil;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by huang on 3/28/16.
 */

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare",
    args = {Connection.class, Integer.class})})
public class PageInterceptor implements Interceptor {
    private static String pagingPrefix;
    private Pattern limitPattern = Pattern.compile("\\(\\(([^\\)]+)\\).*\\)");

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (invocation.getTarget() instanceof RoutingStatementHandler) {
            Object wrapped;
            Object target = invocation.getTarget();
            while (null != (wrapped = ReflectionUtil.getFieldValue(target, "h", true))) {
                target = wrapped;
            }
            while (null != (wrapped = ReflectionUtil.getFieldValue(target, "target"))) {
                target = wrapped;
            }

            BoundSql boundSql = ReflectionUtil.getFieldValue(
                target,
                "delegate.boundSql",
                true
            );
            Page page = getPageObj(boundSql.getParameterObject());
            if (page == null)
                return invocation.proceed();

            MappedStatement mappedStatement = ReflectionUtil.getFieldValue(target, "delegate.mappedStatement", true);
            String id = mappedStatement.getId().substring(mappedStatement
                .getId().lastIndexOf(".") + 1);
            if (id.startsWith(pagingPrefix)) {
                //拦截到的prepare方法参数是一个Connection对象
                Connection connection = (Connection) invocation.getArgs()[0];
                //获取当前要执行的Sql语句，也就是我们直接在Mapper映射语句中写的Sql语句
                String sql = boundSql.getSql();
                //给当前的page参数对象设置总记录数
                setTotalCount(page, mappedStatement, boundSql, connection);
                //获取分页Sql语句
                String pageSql = getPageSql(page, sql);
                //利用反射设置当前BoundSql对应的sql属性为我们建立好的分页Sql语句
                ReflectionUtil.setFieldValue(boundSql, "sql", pageSql);
//                metaStatementHandler.setValue("boundSql.sql", pageSql);
            }
        }
        return invocation.proceed();
    }

    private Page getPageObj(Object object) {
        if (object instanceof Page)
            return (Page) object;
        if (object instanceof MapperMethod.ParamMap) {
            for (Object value : ((MapperMethod.ParamMap) object).values()) {
                if (value instanceof Page)
                    return (Page) value;
            }
        }
        return null;
    }

    /**
     * 给当前的参数对象page设置总记录数
     *
     * @param page            Mapper映射语句对应的参数对象
     * @param mappedStatement Mapper映射语句
     * @param boundSql
     * @param connection      当前的数据库连接
     */
    private void setTotalCount(Page page,
                               MappedStatement mappedStatement,
                               BoundSql boundSql,
                               Connection connection) {
        String sql = boundSql.getSql();
        Matcher matcher = limitPattern.matcher(sql);
        String countSql = null;
        if (matcher.find()) {
            countSql = "SELECT COUNT(*) FROM (" + matcher.group().replace("LIMIT_FIELD", "") + ") AS c";
        } else {
            countSql = "SELECT COUNT(*) FROM (" + sql + ") AS c";
        }
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        BoundSql countBoundSql = new BoundSql(mappedStatement
            .getConfiguration(), countSql, parameterMappings, boundSql.getParameterObject());
        ParameterHandler parameterHandler = new DefaultParameterHandler
            (mappedStatement, boundSql.getParameterObject(), countBoundSql);
        ResultSet rs = null;
        try (PreparedStatement pstmt = connection.prepareStatement(countSql)) {
            parameterHandler.setParameters(pstmt);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int totalRecord = rs.getInt(1);
                page.setTotalCount(totalRecord);
                page.setNumberOfPage(
                    (int) Math.ceil((double) totalRecord / page.getPageSize())
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据page对象获取对应的分页查询Sql语句
     *
     * @param page 分页对象
     * @param sql  原sql语句
     * @return
     */
    private String getPageSql(Page page, String sql) {
        return getMysqlPageSql(page, sql);
    }

    private String getMysqlPageSql(Page page, String sql) {
        int offset = (page.getCurrentPage() - 1) * page.getPageSize();
        StringBuilder sb = new StringBuilder();
        sb.append(" LIMIT ")
          .append(offset)
          .append(",")
          .append(page.getPageSize());
        if (sql.contains("LIMIT_FIELD")) {
            return sql.replace("LIMIT_FIELD", sb.toString());
        } else {
            return sql + sb.toString();
        }
    }

    @Override
    public Object plugin(Object target) {
        return target instanceof StatementHandler ?
               Plugin.wrap(target, this) :
               target;
    }

    @Override
    public void setProperties(Properties properties) {
        pagingPrefix = properties.getProperty("pagingPrefix");
    }
}
