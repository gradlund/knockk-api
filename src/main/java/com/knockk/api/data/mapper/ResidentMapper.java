package com.knockk.api.data.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.postgresql.util.PGobject;

import org.springframework.jdbc.core.RowMapper;

import com.knockk.api.data.Gender;
import com.knockk.api.entity.ResidentEntity;

/**
 * Class that maps the Resident table
 * @author graceradlund
 */
public class ResidentMapper implements RowMapper<ResidentEntity> {
	
	/**
	 * Maps rows in the database to entities
	 */
	//TODO: optionals for ResidentEntity
	@Override
	public ResidentEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
		System.out.println("resident mapper");
		System.out.println(rs.getString("first_name"));
		System.out.println(rs.getString("first_name").toString());
		//return new ResidentEntity();
//		//JSONB column
//		PGobject profile = (PGobject) rs.getObject("profile_photo");
//		PGobject background = (PGobject) rs.getObject("background_photo");
//		
//		String profilePhoto = null;
//		String backgroundPhoto = null;
//		
//		if(profile != null) {
//			profilePhoto = profile.getValue();
//		}
//		if(background != null) {
//			backgroundPhoto = background.getValue();
//		}
//		
		return new ResidentEntity(rs.getObject("resident_id", java.util.UUID.class), rs.getString("first_name"), rs.getString("last_name"), 
				//rs.getObject("gender", com.knockk.api.data.Gender.class), 
				rs.getShort("age"), rs.getString("hometown"), rs.getString("biography"), rs.getObject("profile_photo", org.postgresql.util.PGobject.class).toString(), 
				rs.getObject("background_photo", org.postgresql.util.PGobject.class).toString(), 
				rs.getString("instagram"), rs.getString("snapchat"), rs.getString("x"), rs.getString("facebook"), rs.getObject("fk_lease_id", java.util.UUID.class), rs.getBoolean("verified"));
	}
}
