package com.knockk.api.entity;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entity class for the Lease table.
 */
@Table("Lease")
public class LeaseEntity {
    // Make sure the column names match
    @Id
    @Column("lease_id")
    private UUID leaseId;

    @Column("start_date")
    private Date startDate;

    @Column("end")
    private Date endDate;

    @Column("fk_unit_id")
    private UUID unitId;

    public LeaseEntity(UUID leaseId, Date startDate, Date endDate, UUID unitId) {
        this.leaseId = leaseId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.unitId = unitId;
    }

}
