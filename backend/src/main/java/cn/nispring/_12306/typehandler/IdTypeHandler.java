package cn.nispring._12306.typehandler;

import cn.nispring._12306.model.IdType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(IdType.class)
public class IdTypeHandler extends BaseTypeHandler<IdType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, IdType parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public IdType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        return IdType.fromCode(code);
    }

    @Override
    public IdType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        return IdType.fromCode(code);
    }

    @Override
    public IdType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        return IdType.fromCode(code);
    }
}
