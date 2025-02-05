package com.knockk.api.data.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.knockk.api.entity.FriendshipEntity;

public class FriendshipMapper implements RowMapper<FriendshipEntity>{
    /**
	 * Maps rows in the database to entities
	 */
	@Override
	public FriendshipEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new FriendshipEntity(rs.getObject("id", java.util.UUID.class), rs.getObject("created_at", java.util.Date.class), rs.getObject("invitor_id", java.util.UUID.class), rs.getObject("invitee_id", java.util.UUID.class), rs.getBoolean("accepted"));
        ///return new FriendshipEntity(rs.getObject("invitor_id", java.util.UUID.class), rs.getObject("invitee_id", java.util.UUID.class), rs.getBoolean("accepted"));
	}
}
