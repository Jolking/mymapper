package com.mymapper.mapper;

import com.mymapper.FieldList;
import com.mymapper.Page;
import com.mymapper.WhereList;
import com.mymapper.entity.User;
import com.mymapper.util.Logger;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huang on 4/4/16.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CommonMapperTest {
    private static final Logger logger = Logger.newInstance(CommonMapperTest.class);
    private static SqlSessionFactory sqlSessionFactory;
    private static Reader reader;

    @BeforeClass
    public static void before() {
        try {
            reader = Resources.getResourceAsReader("mybatis-config.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

            try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
                CommonMapper commonMapper = sqlSession.getMapper(CommonMapper.class);
                WhereList whereList = new WhereList();
                whereList.eq(1, 1);
                commonMapper.deleteByWhere(User.class, whereList);
                commonMapper.resetAutoIncrement();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void A_createEntity() throws Exception {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            CommonMapper commonMapper = sqlSession.getMapper(CommonMapper.class);
            User user = new User();
            user.setUsername("创建第一个用户");
            commonMapper.create(User.class, user);
            Assert.assertNotNull(user.getUserId());
            logger.info(user);
        }
    }

    @Test
    public void B_getById() throws Exception {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            CommonMapper commonMapper = sqlSession.getMapper(CommonMapper.class);
            User user = commonMapper.getById(User.class, 1);
            Assert.assertNotNull(user);
            logger.info(user);
        }
    }

    @Test
    public void C_batchCreate() throws Exception {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            CommonMapper commonMapper = sqlSession.getMapper(CommonMapper.class);
            List<User> userList = new ArrayList<>();
            userList.add(new User("批量创建用户1"));
            userList.add(new User("批量创建用户2"));
            userList.add(new User("批量创建用户3"));
            userList.add(new User("批量创建用户4"));

            Assert.assertEquals(4, (long) commonMapper.batchCreate(User.class, userList));
        }
    }

    @Test
    public void D_getFieldsById() throws Exception {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            CommonMapper commonMapper = sqlSession.getMapper(CommonMapper.class);
            FieldList fieldList = new FieldList("username");
            User user = commonMapper.getFieldsById(User.class, fieldList, 1);
            Assert.assertNull(user.getUserId());
            Assert.assertEquals("创建第一个用户", user.getUsername());
        }
    }

    @Test
    public void E_getFieldsByEntity() throws Exception {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            CommonMapper commonMapper = sqlSession.getMapper(CommonMapper.class);
            List<String> fieldList = new ArrayList<>();
            fieldList.add("user_id");

            User user = new User();
            user.setUsername("批量创建用户1");
            user = commonMapper.getFieldsByEntity(User.class, fieldList, user);
            Assert.assertEquals(2, (long) user.getUserId());
        }
    }

    @Test
    public void F_updateById() throws Exception {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            CommonMapper commonMapper = sqlSession.getMapper(CommonMapper.class);
            User user = new User();
            user.setDeleted(true);
            commonMapper.updateById(User.class, user, 3);
            user = commonMapper.getById(User.class, 3);
            Assert.assertTrue(user.isDeleted());
        }
    }

    @Test
    public void G_queryFieldsByWhere() throws Exception {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            CommonMapper commonMapper = sqlSession.getMapper(CommonMapper.class);
            WhereList whereList = new WhereList();
            whereList
                .eq("username", "批量创建用户1")
                .or()
                .eq("username", "批量创建用户3")
                .or()
                .isTrue("deleted");
            List<User> userList = commonMapper.queryFieldsByWhere(User.class, null, whereList);
            Assert.assertEquals(3, userList.size());
        }
    }

    @Test
    public void H_deleteById() throws Exception {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            CommonMapper commonMapper = sqlSession.getMapper(CommonMapper.class);
            Assert.assertEquals(1, (long) commonMapper.deleteById(User.class, 4));
        }
    }

    @Test
    public void I_queryFieldsByEntity() throws Exception {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            CommonMapper commonMapper = sqlSession.getMapper(CommonMapper.class);
            User user = new User();
            List<User> userList = commonMapper.queryFieldsByEntity(User.class, null, user);
            Assert.assertEquals(3, userList.size());
        }
    }

    @Test
    public void J_queryFieldsByEntityWithPage() throws Exception {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            CommonMapper commonMapper = sqlSession.getMapper(CommonMapper.class);
            Page page = new Page();
            page.setPageSize(2);
            User user = new User();
            List<User> userList = commonMapper.queryFieldsByEntity(
                User.class, null, user, page
            );

            Assert.assertEquals(2, userList.size());
            Assert.assertEquals(1, (long) userList.get(0).getUserId());
        }
    }

    @Test
    public void K_deleteByWhere() throws Exception {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            CommonMapper commonMapper = sqlSession.getMapper(CommonMapper.class);
            User user = new User();
            Assert.assertEquals(3, (long) commonMapper.deleteByEntity(User.class, user));
        }
    }
}