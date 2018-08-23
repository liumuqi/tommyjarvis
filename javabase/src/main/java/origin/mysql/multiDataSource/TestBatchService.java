package origin.mysql.multiDataSource;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class TestBatchService {
    @Autowired
    @Qualifier(DataSourceConfiguration.SQL_SESSION_FACTORY_NAME)
    private int MAXSQL = 200;
    private int MAXFLUSH = 10;
    private SqlSessionFactory sqlSessionFactory;

    private int doInserts(List<Long> inserts) {
        int s = inserts.size();
        final SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        final TestDao testDao = sqlSession.getMapper(TestDao.class);
        try {
            for (int i = 1, j = 0; i <= inserts.size(); i++) {
                Long id = inserts.get(i - 1);
                if (i % MAXSQL == 0 || i == inserts.size()) {
                    testDao.insert(id);
                    j++;
                }
                if ((j != 0 && j % MAXFLUSH == 0) || i == inserts.size()) {
                    sqlSession.flushStatements();
                    sqlSession.clearCache();
                    j = 0;
                }
            }
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
        return s;
    }
}
