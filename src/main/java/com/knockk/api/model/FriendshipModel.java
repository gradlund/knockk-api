package com.knockk.api.model;

import java.util.UUID;

// Models need getters if they are being sent in request
/**
 * Model used in the response when checking for a friendship.
 */
public class FriendshipModel {

    private UUID invitorId;
    private UUID inviteeId;
    private boolean isPending;

    public UUID getInvitorId() {
        return invitorId;
    }

    public UUID getInviteeId() {
        return inviteeId;
    }

    public boolean isPending() {
        return isPending;
    }

    public FriendshipModel(UUID invitorId, UUID inviteeId, boolean isPending) {
        this.invitorId = invitorId;
        this.inviteeId = inviteeId;
        this.isPending = isPending;
    }
}
