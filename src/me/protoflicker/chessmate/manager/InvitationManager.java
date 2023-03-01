package me.protoflicker.chessmate.manager;

import me.protoflicker.chessmate.connection.ClientThread;
import me.protoflicker.chessmate.connection.PacketHandler;
import me.protoflicker.chessmate.data.DataManager;
import me.protoflicker.chessmate.data.table.InvitationsTable;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;
import me.protoflicker.chessmate.protocol.packet.game.invitation.*;
import me.protoflicker.chessmate.protocol.packet.game.invitation.info.GameInvitesRequestPacket;
import me.protoflicker.chessmate.protocol.packet.game.invitation.info.response.GameInvitesResponsePacket;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InvitationManager {


	public static void handleInvitePacket(ClientThread c, ClientPacket packet){
		GameInvitePacket p = (GameInvitePacket) packet;

		GameInvitation inv = p.getInvitation();
		if(inv == null || !LoginManager.isAuthorised(c, inv.getInviterId())){
			return;
		}

		byte[] invitationId = InvitationsTable.createInvitationAndGetId(inv);
		inv.setInvitationId(invitationId);

		Set<ClientThread> clients = LoginManager.getClientsById(inv.getInviteeId());
		for(ClientThread client : clients){
			client.sendPacket(new GameInvitedPacket(inv));
		}
	}


	public static void handleInviteAcceptPacket(ClientThread c, ClientPacket packet){
		GameInviteAcceptPacket p = (GameInviteAcceptPacket) packet;

		byte[] userId = LoginManager.getUserId(c);
		if(userId != null){
			GameInvitation inv = InvitationsTable.getInvitationById(p.getInvitationId());
			if(inv != null){
				if(inv.getInviteeId() == userId){
					DataManager.initGame(inv);
				}
			}
		}
	}


	public static void handleInviteDeclinePacket(ClientThread c, ClientPacket packet){
		GameInviteDeclinePacket p = (GameInviteDeclinePacket) packet;

		byte[] userId = LoginManager.getUserId(c);
		if(userId != null){
			InvitationsTable.removeReceivedInvitation(p.getInvitationId(), userId);
		}
	}


	public static void handleInvitesRequestPacket(ClientThread c, ClientPacket packet){
		GameInvitesRequestPacket p = (GameInvitesRequestPacket) packet;

		c.sendPacket(new GameInvitesResponsePacket(p.getUserId(),
				InvitationsTable.getInvitationsByInviteeId(p.getUserId())));
	}



	private static final Map<Class<?>, PacketHandler> packetHandlers = new HashMap<>();

	static {
		initHandlers();
	}

	public static void registerHandlers(ClientThread clientThread){
		clientThread.getPacketHandlers().putAll(packetHandlers);
	}

	public static void unregisterHandlers(ClientThread clientThread){
		Map<Class<?>, PacketHandler> h = clientThread.getPacketHandlers();
		for(Class<?> aClass : packetHandlers.keySet()){
			h.remove(aClass);
		}
	}

	private static void initHandlers(){
		packetHandlers.put(GameInvitePacket.class, InvitationManager::handleInvitePacket);
		packetHandlers.put(GameInviteAcceptPacket.class, InvitationManager::handleInviteAcceptPacket);
		packetHandlers.put(GameInviteDeclinePacket.class, InvitationManager::handleInviteDeclinePacket);
		packetHandlers.put(GameInvitesRequestPacket.class, InvitationManager::handleInvitesRequestPacket);
	}
}
