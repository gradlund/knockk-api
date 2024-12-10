/**
 * Subdirectory of the data package that provides mappers
 */
package com.knockk.api.data.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.knockk.api.entity.AdminEntity;

/**
 * Class that maps the Admin table
 * @author graceradlund
 */
public class AdminRowMapper implements RowMapper<AdminEntity> {

	/**
	 * Maps rows in the database to entities
	 */
	@Override
	public AdminEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new AdminEntity(rs.getObject("admin_id", java.util.UUID.class), rs.getString("username"), rs.getString("password"));
	}
}