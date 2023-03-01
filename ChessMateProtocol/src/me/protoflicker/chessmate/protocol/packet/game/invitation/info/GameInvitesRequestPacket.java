package me.protoflicker.chessmate.protocol.packet.game.invitation.info;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;

public class GameInvitesRequestPacket extends Packet implements ClientPacket {

	@Getter
	private final byte[] userId;

	public GameInvitesRequestPacket(byte[] userId){
		this.userId = userId;
	}
}
