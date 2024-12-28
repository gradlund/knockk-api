package com.knockk.api.data.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.knockk.api.entity.LeaseEntity;

/**
 * Interface for the lease repository.
 * 
 * @author graceradlund
 */
public interface LeaseRepository extends CrudRepository<LeaseEntity, UUID> {
    /**
     * Retrieves the unit id from the lease table
     * 
     * @param leaseId : id of the lease
     * @return the unit id if it exists
     */
    @Query(value = "SELECT fk_unit_id from \"Lease\" WHERE lease_id = :leaseId")
    Optional<UUID> findUnitByLeaseId(UUID leaseId);

    // Not used
    /**
     * Retrieves the lease id from the unit table
     * 
     * @param unitID : id of the unit
     * @return the lease id if it exists
     */
    // @Query(value = "SELECT lease_id FROM \"Lease\" where fk_unit_id = :unitId")
    // public Optional<UUID> findLeaseIdByUnitId(UUID unitID);
}
