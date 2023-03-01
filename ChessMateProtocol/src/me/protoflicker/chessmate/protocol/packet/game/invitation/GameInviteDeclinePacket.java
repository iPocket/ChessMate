package me.protoflicker.chessmate.protocol.packet.game.invitation;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;

public class GameInviteDeclinePacket extends Packet implements ClientPacket {

	@Getter
	private final byte[] invitationId;

	public GameInviteDeclinePacket(byte[] invitationId){
		this.invitationId = invitationId;
	}
}
