package com.knockk.api.model;

import java.util.UUID;

// Models need getters if they are being sent in request
/**
 * Model used in the response when checking for a friendship.
 */
public class FriendshipModel {

    private UUID invitorId;
    private UUID inviteeId;
    private boolean isAccepted;

    public UUID getInvitorId() {
        return invitorId;
    }

    public UUID getInviteeId() {
        return inviteeId;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public FriendshipModel(UUID invitorId, UUID inviteeId, boolean isAccepted) {
        this.invitorId = invitorId;
        this.inviteeId = inviteeId;
        this.isAccepted = isAccepted;
    }
}
