package cn.nispring._12306.typehandler;

import cn.nispring._12306.model.SeatType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(SeatType.class)
public class SeatTypeHandler extends BaseTypeHandler<SeatType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, SeatType parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.getCode());
    }

    @Override
    public SeatType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String code = rs.getString(columnName);
        return code == null ? null : SeatType.fromCode(code);
    }

    @Override
    public SeatType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String code = rs.getString(columnIndex);
        return code == null ? null : SeatType.fromCode(code);
    }

    @Override
    public SeatType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String code = cs.getString(columnIndex);
        return code == null ? null : SeatType.fromCode(code);
    }
}
