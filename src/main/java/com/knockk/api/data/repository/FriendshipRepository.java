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
}