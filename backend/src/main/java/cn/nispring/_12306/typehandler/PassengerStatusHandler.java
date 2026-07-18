package cn.nispring._12306.typehandler;

import cn.nispring._12306.model.PassengerStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(PassengerStatus.class)
public class PassengerStatusHandler extends BaseTypeHandler<PassengerStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, PassengerStatus parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public PassengerStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        return PassengerStatus.fromCode(code);
    }

    @Override
    public PassengerStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        return PassengerStatus.fromCode(code);
    }

    @Override
    public PassengerStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        return PassengerStatus.fromCode(code);
    }
}
