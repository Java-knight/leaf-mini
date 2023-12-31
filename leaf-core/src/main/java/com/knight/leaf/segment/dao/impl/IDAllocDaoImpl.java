package com.knight.leaf.segment.dao.impl;

import com.knight.leaf.segment.dao.IDAllocDao;
import com.knight.leaf.segment.dao.IDAllocMapper;
import com.knight.leaf.segment.model.LeafAlloc;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.util.List;

/**
 * 连接池 & mapper
 * @author knight
 * @desc
 * @date 2023/6/9
 */
public class IDAllocDaoImpl implements IDAllocDao {

    private SqlSessionFactory sqlSessionFactory;

    public IDAllocDaoImpl(DataSource dataSource) {
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment env = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(env);
        configuration.addMapper(IDAllocMapper.class);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    @Override
    public List<LeafAlloc> getAllLeafAllocs() {
        SqlSession sqlSession = sqlSessionFactory.openSession(false);
        try {
            return sqlSession.selectList("com.knight.leaf.segment.dao.IDAllocMapper.getAllLeafAllocs");
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public LeafAlloc updateMaxIdAndGetLeafAlloc(String tag) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            sqlSession.update("com.knight.leaf.segment.dao.IDAllocMapper.updateMaxId", tag);
            LeafAlloc result = sqlSession.selectOne("com.knight.leaf.segment.dao.IDAllocMapper.getLeafAlloc", tag);
            sqlSession.commit();
            return result;
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public LeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(LeafAlloc leafAlloc) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            sqlSession.update("com.knight.leaf.segment.dao.IDAllocMapper.updateMaxIdByCustomStep", leafAlloc);
            LeafAlloc result = sqlSession.selectOne("com.knight.leaf.segment.dao.IDAllocMapper.getLeafAlloc", leafAlloc.getKey());
            sqlSession.commit();
            return result;
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public List<String> getAllTags() {
        SqlSession sqlSession = sqlSessionFactory.openSession(false);
        try {
            return sqlSession.selectList("com.knight.leaf.segment.dao.IDAllocMapper.getAllTags");
        } finally {
            sqlSession.close();
        }
    }
}
