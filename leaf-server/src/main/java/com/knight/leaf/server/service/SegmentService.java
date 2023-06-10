package com.knight.leaf.server.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.knight.leaf.IDGen;
import com.knight.leaf.common.PropertyFactory;
import com.knight.leaf.common.Result;
import com.knight.leaf.common.ZeroIDGen;
import com.knight.leaf.segment.SegmentIDGenImpl;
import com.knight.leaf.segment.dao.impl.IDAllocDaoImpl;
import com.knight.leaf.server.common.LeafConstants;
import com.knight.leaf.server.exception.InitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Properties;

/**
 * rpc接口, 分段服务
 * @desc
 * @author knight
 * @date 2023/6/10
 */
@Service("SegmentService")
public class SegmentService {

    private Logger logger = LoggerFactory.getLogger(SegmentService.class);

    private IDGen idGen;

    private DruidDataSource dataSource;

    public SegmentService() throws SQLException, InitException {
        Properties properties = PropertyFactory.getProperties();
        boolean flag = Boolean.parseBoolean(properties.getProperty(LeafConstants.LEAF_SEGMENT_ENABLE, "true"));
        if (flag) {  // 开启分段服务
            // config dataSource
            dataSource = new DruidDataSource();
            dataSource.setUrl(properties.getProperty(LeafConstants.LEAF_JDBC_URL));
            dataSource.setUsername(properties.getProperty(LeafConstants.LEAF_JDBC_USERNAME));
            dataSource.setPassword(properties.getProperty(LeafConstants.LEAF_JDBC_PASSWORD));
            dataSource.setDriverClassName(LeafConstants.LEAF_DRIVER_CLASS_NAME);
            dataSource.setValidationQuery(LeafConstants.LEAF_VALIDATION_QUERY);

            // config dao
            IDAllocDaoImpl idAllocDao = new IDAllocDaoImpl(dataSource);

            // config id gen
            idGen = new SegmentIDGenImpl();
            ((SegmentIDGenImpl) idGen).setIdAllocDao(idAllocDao);
            if (idGen.init()) {
                logger.info("Segment Service Init Successfully");
            } else {
                throw new InitException("Segment Service Init Fail");
            }
        } else {  // 默认服务[zeroIdGen]
            idGen = new ZeroIDGen();
            logger.info("Zero ID Gen Service Init Successfully");
        }
    }

    public Result getId(String key) {
        return idGen.get(key);
    }

    public SegmentIDGenImpl getIdGen() {
        if (idGen instanceof SegmentIDGenImpl) {
            return (SegmentIDGenImpl) idGen;
        }
        return null;
    }
}
