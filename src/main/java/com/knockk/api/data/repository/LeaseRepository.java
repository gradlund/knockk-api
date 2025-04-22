package com.knockk.api.data.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.knockk.api.util.entity.LeaseEntity;

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

    /**
     * Retrieves the id of a lease given details of the lease
     * 
     * @param address      : building address of the lease
     * @param buildingName : building name on the lease
     * @param floor        : floor number on the lease
     * @param room         : room number on the lease
     * @param startDate    : start date of the lease
     * @param endDate      : end date of the lease
     * @return the id of the lease
     */
    @Query(value = "SELECT \"Lease\".lease_id from \"Building\"" +
            "  INNER JOIN \"Unit\" ON \"Unit\".fk_building_id = \"Building\".building_id" +
            "  INNER JOIN \"Lease\" ON \"Lease\".fk_unit_id = \"Unit\".unit_id" +
            "  where \"Building\".address = :address AND \"Building\".name = :buildingName AND \"Unit\".floor = :floor AND \"Unit\".room = :room AND \"Lease\".start_date = CAST(:startDate AS DATE) AND \"Lease\".end_date = CAST(:endDate AS DATE)")
    Optional<UUID> findLeaseId(String address, String buildingName, int floor, int room, String startDate,
            String endDate);
}
