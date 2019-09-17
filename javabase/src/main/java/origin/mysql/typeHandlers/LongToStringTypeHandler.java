package origin.mysql.typeHandlers;

//import org.apache.ibatis.type.Alias;
//import org.apache.ibatis.type.BaseTypeHandler;
//import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * configure alias configruation in mysqlSqlSessionFactory
 * @author muqi.lmq
 * @date 04/01/2018.
 */
//@Alias(value = "longToStringTypeHandler")
public class LongToStringTypeHandler {
//        extends BaseTypeHandler<Long> {
//    @Override
//    public void setNonNullParameter(PreparedStatement ps, int i, Long parameter, JdbcType jdbcType)
//            throws SQLException {
//        ps.setString(i, parameter.toString());
//    }
//
//    @Override
//    public Long getNullableResult(ResultSet rs, String columnName)
//            throws SQLException {
//        String string = rs.getString(columnName);
//        return Long.valueOf(string);
//    }
//
//    @Override
//    public Long getNullableResult(ResultSet rs, int columnIndex)
//            throws SQLException {
//        return Long.valueOf(rs.getString(columnIndex));
//    }
//
//    @Override
//    public Long getNullableResult(CallableStatement cs, int columnIndex)
//            throws SQLException {
//        return Long.valueOf(cs.getString(columnIndex));
//    }
}
