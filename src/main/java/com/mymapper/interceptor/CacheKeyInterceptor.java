package com.mymapper.interceptor;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;

@Intercepts(@Signature(
    type = Executor.class,
    method = "createCacheKey",
    args = {
        MappedStatement.class, Object.class, RowBounds.class, BoundSql.class
    }
))
public class CacheKeyInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 通过拦截createCacheKey方法改写key实现commonMapper的缓存
        // 或者通过拦截CachingExecutor来实现

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
