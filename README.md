MyMapper
===

### 简介
MyBatis的通用mapper实现的一种思路。
在mapper.xml中创建SQL模板，
然后通过MyBatis的Interceptor机制对SQL拦截并修改
如本项目中CommonMapper类的queryFieldsByWhere，
SQL模板如下:
```
<select id = "queryFieldsByWhere" resultType = "Object">
    SELECT
    <if test = "_fieldList == null">
        *
    </if>
    <if test = "_fieldList != null">
        <foreach collection = "_fieldList" item = "field" separator = ",">
            ${field}
        </foreach>
    </if>
    FROM TABLE_NAME WHERE WHERE_FIELD
</select>
```
通过动态修改TABLE_NAME和WHERE_FIELD的值实现对任意单表和任意字段的任意查询。

可以通过扩展converter实现多表的操作。

具体思路实现见CommonMapper.java类和common-mapper.xml文件
用法可以直接看测试文件CommonMapperTest.java


运行CommonMapperTest.java前，
请先执行src/main/resources目录下的mymapper.sql创建数据库，
并在src/main/resources目录下创建jdbc.properties文件
内容如下：
```
driver = com.mysql.jdbc.Driver
url=jdbc:mysql:///mymapper?allowMultiQueries=true
username = mysql用户名
password = mysql连接密码
```

### 联系我
Email: keepgohjk@gmail.com

### License
MIT ([http://www.opensource.org/licenses/mit-license.php](http://www.opensource.org/licenses/mit-license.php))