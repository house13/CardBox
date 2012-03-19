package com.hextilla.cardbox.lobby.invite;

import com.threerings.parlor.client.Invitation;

public interface InvitationListener
{
	/** Invoked by the invitation director when you get an invitation */
	public void invitationIncoming(Invitation invite);
}
