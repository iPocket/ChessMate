package me.protoflicker.chessmate.protocol.packet.game.invitation.update;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;
import me.protoflicker.chessmate.protocol.packet.game.invitation.GameInvitation;

public class GameInviteSuccessfulPacket extends Packet implements ServerPacket {

	@Getter
	private final GameInvitation invitation;

	public GameInviteSuccessfulPacket(GameInvitation invitation){
		this.invitation = invitation;
	}
}
