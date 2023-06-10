package com.knight.leaf.segment;

import com.knight.leaf.IDGen;
import com.knight.leaf.common.Result;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author knight
 * @desc
 * @date 2023/6/10
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class SpringIDGenServiceTest {

    @Autowired
    private IDGen idGen;

    @Test
    public void testGenId() {
        for (int i = 0; i < 100; i++) {
            Result result = idGen.get("leaf-segment-test");
            System.out.println(result);
        }
    }
}
