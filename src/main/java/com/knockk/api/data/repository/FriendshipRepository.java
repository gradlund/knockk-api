package com.knockk.api.data.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.knockk.api.util.entity.FriendshipEntity;

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

	/**
	 * Updates a friendship.
	 * 
	 * @param invitorId  : id of the resident who sent the friend request
	 * @param inviteeId  : id of the resident receiving the friend request
	 * @param isAccepted : if the friendship is accepted or no
	 * @return the number of rows updated
	 */
	@Query(value = "UPDATE \"Friendship\"" +
			"SET accepted = :isAccepted" +
			"WHERE (invitor_id = :invitorId AND invitee_id = :inviteeId)")
	public int updateFriendship(UUID invitorId, UUID inviteeId, boolean isAccepted);

	/**
	 * Creates a friendship.
	 * 
	 * @param invitorId  : id of the resident who sent the friend request
	 * @param inviteeId  : id of the resident receiving the friend request
	 * @param isAccepted : if the friendship is accepted or no
	 * @return the id of the friendship
	 */
	@Query(value = "INSERT INTO \"Friendship\"" +
			"(invitor_id, invitee_id, accepted ) VALUES (:invitorId, :inviteeId, :isAccepted) returning id")
	public UUID addFriendship(UUID invitorId, UUID inviteeId, boolean isAccepted);

	/**
	 * Deletes a friendship.
	 * 
	 * Modifying annotation indicates the data will be modified
	 * 
	 * @param residentId : id of the resident who wants to delete the friendship
	 * @param friendId   : id of the friend the resident no longer wants to be
	 *                   friends with
	 * @return the number of rows deleted (should only by 1)
	 */
	@Modifying
	@Query(value = "DELETE FROM \"Friendship\"" +
			" WHERE (invitor_id = :residentId AND invitee_id = :friendId) OR (invitor_id = :friendId AND invitee_id = :residentId)")
	public int deleteFriendship(UUID residentId, UUID friendId);

}