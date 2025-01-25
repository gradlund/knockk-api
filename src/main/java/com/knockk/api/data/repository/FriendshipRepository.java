package com.knockk.api.data.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.knockk.api.entity.FriendshipEntity;

/**
 * Interface for the friendship repository
 * 
 * @author graceradlund
 */
public interface FriendshipRepository extends CrudRepository<FriendshipEntity, UUID> {

	/**
	 * Finds if the two residents are connected
	 * 
	 * @param residentId : id of the resident (user)
	 * @param friendId   : id of the friend
	 * @return a FriendshipEntity if one exists
	 */
	@Query(value = "SELECT * from \"Friendship\" WHERE (invitor_id = :residentId AND invitee_id = :friendId) OR (invitor_id = :friendId AND invitee_id = :residentId)")
	public Optional<FriendshipEntity> findByResidentIdAndNeighborId(UUID residentId, UUID friendId);

	// @Query(value = "INSERT INTO \"Friendship\" (invitor_id, invitee_id, accepted) " +
	// "VALUES (:invitorId, :inviteeId, :isAccepted) ON CONFLICT (invitor_id == invitorId AND invitee_id == inviteeId) " +
	// "ELSE DO UPDATE SET accepted = :isAccepted"
	// )
	@Query(value = "UPDATE \"Friendship\"" +
				"SET accepted = :isAccepted" + 
				"WHERE (invitor_id = :invitorId AND invitee_id = :inviteeId)" )
				//"   OR (invitor_id = '53b30260-1b0e-4ecd-88ab-eac6a16510a8' AND invitee_id = 'db0601ac-09bd-49a4-9940-70db17b18dd9');")
	public int updateFriendship(UUID invitorId, UUID inviteeId, boolean isAccepted);

	@Query(value = "INSERT INTO \"Friendship\"" +
				"(invitor_id, invitee_id, accepted ) VALUES (:invitorId, :inviteeId, :isAccepted) returning id" )
				//"   OR (invitor_id = '53b30260-1b0e-4ecd-88ab-eac6a16510a8' AND invitee_id = 'db0601ac-09bd-49a4-9940-70db17b18dd9');")
	public UUID addFriendship(UUID invitorId, UUID inviteeId, boolean isAccepted);

}