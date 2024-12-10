package com.knockk.api.data.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.knockk.api.entity.UserEntity;

/**
 * Class that maps the User table
 * @author graceradlund
 */
public class UserRowMapper implements RowMapper<UserEntity> {

	/**
	 * Maps rows in the database to entities
	 */
	@Override
	public UserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new UserEntity(rs.getObject("user_id", java.util.UUID.class), rs.getString("email"), rs.getString("password"));
	}
}
