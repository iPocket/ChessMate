package me.protoflicker.chessmate.protocol.packet.game.invitation.update;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;

public class GameInviteAcceptUnsuccessfulPacket extends Packet implements ServerPacket {

	@Getter
	private final byte[] invitationId;

	public GameInviteAcceptUnsuccessfulPacket(byte[] invitationId){
		this.invitationId = invitationId;
	}
}
