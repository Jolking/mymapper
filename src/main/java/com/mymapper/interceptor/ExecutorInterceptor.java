package com.mymapper.interceptor;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;

@Intercepts({
    @Signature(type = Executor.class, method = "query", args = {
        MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class
    }),
    @Signature(type = Executor.class, method = "update", args = {
        MappedStatement.class, Object.class
    })
})
public class ExecutorInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 通过复制一份newMs避免在并发环境下对原MappedStatement修改导致冲突
        MappedStatement oldMs = (MappedStatement) invocation.getArgs()[0];
        MappedStatement newMs = new MappedStatement.Builder(
            oldMs.getConfiguration(),
            oldMs.getId(),
            oldMs.getSqlSource(),
            oldMs.getSqlCommandType()
        )
            .resource(oldMs.getResource())
            .fetchSize(oldMs.getFetchSize())
            .timeout(oldMs.getTimeout())
            .statementType(oldMs.getStatementType())
            .resultSetType(oldMs.getResultSetType())
            .cache(oldMs.getCache())
            .resultMaps(oldMs.getResultMaps())
            .flushCacheRequired(oldMs.isFlushCacheRequired())
            .useCache(oldMs.isUseCache())
            .resultOrdered(oldMs.isResultOrdered())
            .keyGenerator(oldMs.getKeyGenerator())
            .databaseId(oldMs.getDatabaseId())
            .lang(oldMs.getLang())
            .build();
        invocation.getArgs()[0] = newMs;
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
