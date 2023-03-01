package me.protoflicker.chessmate.protocol.packet.game.invitation;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;

public class GameInvitePacket extends Packet implements ClientPacket {

	@Getter
	private final GameInvitation invitation;

	public GameInvitePacket(GameInvitation invitation){
		this.invitation = invitation;
	}
}
