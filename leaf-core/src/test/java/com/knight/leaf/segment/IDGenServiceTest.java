package com.knight.leaf.segment;

import com.alibaba.druid.pool.DruidDataSource;
import com.knight.leaf.IDGen;
import com.knight.leaf.common.PropertyFactory;
import com.knight.leaf.common.Result;
import com.knight.leaf.segment.dao.IDAllocDao;
import com.knight.leaf.segment.dao.impl.IDAllocDaoImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

public class IDGenServiceTest {
    IDGen idGen;
    DruidDataSource dataSource;
    @Before
    public void before() throws IOException, SQLException {
        // Load Db Config
        Properties properties = PropertyFactory.getProperties();

        // Config dataSource
        dataSource = new DruidDataSource();
        dataSource.setUrl(properties.getProperty("jdbc.url"));
        dataSource.setUsername(properties.getProperty("jdbc.username"));
        dataSource.setPassword(properties.getProperty("jdbc.password"));
        dataSource.setDriverClassName(properties.getProperty("jdbc.driver-class-name"));
        dataSource.setValidationQuery(properties.getProperty("druid.validation-query"));
        dataSource.init();

        // Config Dao
        IDAllocDao dao = new IDAllocDaoImpl(dataSource);

        // Config ID Gen
        idGen = new SegmentIDGenImpl();
        ((SegmentIDGenImpl) idGen).setIdAllocDao(dao);
        idGen.init();
    }
    @Test
    public void testGetId() {
        for (int i = 0; i < 100; ++i) {
            Result res = idGen.get("leaf-segment-test");
            System.out.println(res);
        }
    }
    @After
    public void after() {
       dataSource.close();
    }

}
