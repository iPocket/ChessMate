package me.protoflicker.chessmate.protocol.packet.game.invitation;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;

public class GameInvitedPacket extends Packet implements ServerPacket {

	@Getter
	private final GameInvitation invitation;

	public GameInvitedPacket(GameInvitation invitation){
		this.invitation = invitation;
	}
}
