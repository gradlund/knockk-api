package com.knockk.api.util.model;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

// Models need getters if they are being sent in request
/**
 * Model used in the response when checking for a friendship.
 */
public class FriendshipModel {

    private UUID invitorId;
    private UUID inviteeId;
    private boolean isAccepted;

    @NotNull
    public UUID getInvitorId() {
        return invitorId;
    }

    @NotNull
    public UUID getInviteeId() {
        return inviteeId;
    }

    @NotNull
    public boolean isAccepted() {
        return isAccepted;
    }

    public FriendshipModel(UUID invitorId, UUID inviteeId, boolean isAccepted) {
        this.invitorId = invitorId;
        this.inviteeId = inviteeId;
        this.isAccepted = isAccepted;
    }
}
