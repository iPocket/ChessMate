package me.protoflicker.chessmate.protocol.packet.game.invitation.info.response;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;
import me.protoflicker.chessmate.protocol.packet.game.invitation.GameInvitation;

import java.util.List;

public class GameInvitesResponsePacket extends Packet implements ServerPacket {

	@Getter
	private final byte[] userId;

	@Getter
	private final List<GameInvitation> invitations;

	public GameInvitesResponsePacket(byte[] userId, List<GameInvitation> invitations){
		this.userId = userId;
		this.invitations = invitations;
	}
}
