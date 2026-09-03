package cn.nispring.rail12306.typehandler;

import cn.nispring.rail12306.model.layout.Layout;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import tools.jackson.databind.ObjectMapper;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(Layout.class)
public class LayoutTypeHandler extends BaseTypeHandler<Layout> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Layout parameter, JdbcType jdbcType)
            throws SQLException {
        try {
            ps.setString(i, objectMapper.writeValueAsString(parameter));
        } catch (Exception e) {
            throw new SQLException("Failed to serialize Layout to JSON", e);
        }
    }

    @Override
    public Layout getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return json == null ? null : parse(json);
    }

    @Override
    public Layout getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return json == null ? null : parse(json);
    }

    @Override
    public Layout getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return json == null ? null : parse(json);
    }

    private Layout parse(String json) throws SQLException {
        try {
            return objectMapper.readValue(json, Layout.class);
        } catch (Exception e) {
            throw new SQLException("Failed to deserialize JSON to Layout", e);
        }
    }
}
