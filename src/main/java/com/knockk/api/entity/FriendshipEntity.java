package com.knockk.api.entity;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entity class for the Friendship table.
 */
@Table("Friendship")
public class FriendshipEntity {
    @Id
    @Column("id")
    private UUID id;

    @Column("created_at")
    private Date createdAt;

    @Column("invitor_id")
    private UUID invitorId;

    @Column("invitee_id")
    private UUID inviteeId;

    @Column("accepted")
    private boolean accepted; //this will alter the response

    public UUID getInvitorId() {
        return invitorId;
    }

    public UUID getInviteeId() {
        return inviteeId;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public FriendshipEntity(UUID id, Date createdAt, UUID invitorId, UUID inviteeId, boolean accepted) {
        this.id = id;
        this.createdAt = createdAt;
        this.invitorId = invitorId;
        this.inviteeId = inviteeId;
        this.accepted = accepted;
    }
}
