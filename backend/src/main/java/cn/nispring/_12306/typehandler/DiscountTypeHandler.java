package cn.nispring._12306.typehandler;

import cn.nispring._12306.model.DiscountType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(DiscountType.class)
public class DiscountTypeHandler extends BaseTypeHandler<DiscountType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, DiscountType parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public DiscountType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        return DiscountType.fromCode(code);
    }

    @Override
    public DiscountType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        return DiscountType.fromCode(code);
    }

    @Override
    public DiscountType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        return DiscountType.fromCode(code);
    }
}
