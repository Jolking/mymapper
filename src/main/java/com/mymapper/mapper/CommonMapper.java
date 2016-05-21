package com.mymapper.mapper;

import com.mymapper.Page;
import com.mymapper.oper.Oper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by huang on 3/16/16.
 */

public interface CommonMapper {
    /**
     * 更具id查询所有字段
     *
     * @param type 要查询的表的实体类型, 需要有@Table注解
     * @param id   要查询的记录的id
     * @param <T>
     * @return
     */
    <T> T getById(@Param("_type") Class<T> type,
                  @Param("_id") Integer id);

    /**
     * 根据id查询fieldList中的字段, 返回类型为type<br/>
     *
     * @param type      要查询的表的实体类型, 需要有@Table注解
     * @param fieldList 想要查询的字段列表， null则查询所有字段
     * @param id        要查询的记录的id
     * @param <T>
     * @return
     */
    <T> T getFieldsById(@Param("_type") Class<T> type,
                        @Param("_fieldList") List<String> fieldList,
                        @Param("_id") Integer id);

    /**
     * 查询fieldList中的字段, 查询条件为entity中不为空的字段, 返回类型为type<br/>
     * <pre>
     *  User user = new User();
     *  user.setAccount("whereValue");
     *  queryFieldsByEntity(User.class, new FieldList("user_id"), user);
     * </pre>
     * 则产生: SELECT user_id FROM t_user WHERE account = 'whereValue'<br/>
     * 如果fieldList为null则查询所有字段<br/>
     * 如果entity为null则查询所有结果<br/>
     * <pre>
     *  queryFieldsByEntity(User.class, null, null);
     * </pre>
     * 则产生: SELECT * FROM t_user WHERE 1 = 1
     *
     * @param type      要查询的表的实体类型, 需要有@Table注解
     * @param fieldList 想要查询的字段列表， null则查询所有字段
     * @param entity    查询条件的容器
     * @param <T>
     * @return type类型
     */
    <T> T getFieldsByEntity(@Param("_type") Class<T> type,
                            @Param("_fieldList") List<String> fieldList,
                            @Param("_entity") T entity);

    <T> T getFieldsByEntity(@Param("_type") Class<T> type,
                            @Param("_fieldList") List<String> fieldList,
                            @Param("_entity") T entity,
                            @Param("_page") Page page);

    <T> List<T> queryFieldsByWhere(@Param("_type") Class<T> type,
                                   @Param("_fieldList") List<String> fieldList,
                                   @Param("_whereList") List<Oper> list);

    <T> List<T> queryFieldsByWhere(@Param("_type") Class<T> type,
                                   @Param("_fieldList") List<String> fieldList,
                                   @Param("_whereList") List<Oper> list,
                                   @Param("_page") Page page);

    /**
     * 使用方法和{@link #getFieldsByEntity(Class, List, Object)}, 但是返回的是列表
     */
    <T> List<T> queryFieldsByEntity(@Param("_type") Class<T> type,
                                    @Param("_fieldList") List<String> fieldList,
                                    @Param("_entity") T entity);

    <T> List<T> queryFieldsByEntity(@Param("_type") Class<T> type,
                                    @Param("_fieldList") List<String> fieldList,
                                    @Param("_entity") T entity,
                                    @Param("_page") Page page);

    /**
     * 根据id更新entity中的非空字段<br/>
     * <pre>
     *  User user = new User();
     *  user.setAccount("update_field");
     *  updateById(User.class, user, 2);
     * </pre>
     * 则产生: UPDATE t_user SET account = 'update_field' WHERE user_id = 2<br/>
     *
     * @param type   要更新的表的实体类型, 需要有@Table注解
     * @param entity 包含非空字段的实体
     * @param id     要更新的记录的id
     * @param <T>
     * @return
     */
    <T> Integer updateById(@Param("_type") Class<T> type,
                           @Param("_entity") T entity,
                           @Param("_id") Integer id);

    <T> Integer updateByWhere(@Param("_type") Class<T> type,
                              @Param("_entity") T entity,
                              @Param("_whereList") List<Oper> list);

    // TODO 添加根据entity更新， 并且区分whereEntity 和 updateEntity等
//    <T> Integer updateByEntity(@Param("_type") Class<T> type,
//                               @Param("_eneity") T entity,
//                               @Param(""));
    /**
     * 根据实体类型的非空字段创建一条记录
     * <pre>
     *  User user = new User();
     *  user.setAccount("create_row");
     *  create(User.class, user);
     * </pre>
     * 则产生: INSERT INTO t_user(account) VALUES('create_row')<br/>
     *
     * @param type   要创建的表的实体类型, 需要有@Table注解
     * @param entity 包含非空字段的实体
     * @param <T>
     * @return
     */
    <T> Integer create(@Param("_type") Class<T> type,
                       @Param("_entity") T entity);

    /**
     * 批量插入若需要返回主键则参数命名必须为list/collection/array其一
     *
     * @param type
     * @param list
     * @param <T>
     */
    <T> Integer batchCreate(@Param("_type") Class<T> type,
                         @Param("list") List<T> list);

    /**
     * 根据id删除一条记录
     * <pre>
     *  User user = new User();
     *  user.setUserId(3);
     *  deleteById(User.class, user);
     * </pre>
     * 则产生: DELETE FROM t_user WHERE user_id = 3<br/>
     *
     * @param type 要删除的表的实体类型, 需要有@Table注解
     * @param id   要删除的记录的id
     * @param <T>
     * @return
     */
    <T> Integer deleteById(@Param("_type") Class<T> type,
                           @Param("_id") Integer id);

    /**
     * 以entity中的非空字段为条件删除一条记录
     * <pre>
     *  User user = new User();
     *  user.setAccount('account');
     *  user.setUsername('username');
     *  deleteByEntity(User.class, user);
     * </pre>
     * 则产生: DELETE FROM t_user WHERE account = 'account' AND username = 'username'<br/>
     *
     * @param type   要删除的表的实体类型, 需要有@Table注解
     * @param entity 包含非空字段的实体
     * @param <T>
     * @return
     */
    <T> Integer deleteByEntity(@Param("_type") Class<T> type,
                               @Param("_entity") T entity);

    <T> Integer deleteByWhere(@Param("_type") Class<T> type,
                              @Param("_whereList") List<Oper> list);

    // TODO 仅在测试时使用
    Integer resetAutoIncrement();
}
