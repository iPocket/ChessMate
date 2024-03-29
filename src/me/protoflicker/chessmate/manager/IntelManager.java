package me.protoflicker.chessmate.manager;

import me.protoflicker.chessmate.connection.ClientThread;
import me.protoflicker.chessmate.connection.PacketHandler;
import me.protoflicker.chessmate.data.table.UserTable;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;
import me.protoflicker.chessmate.protocol.packet.user.UserInfo;
import me.protoflicker.chessmate.protocol.packet.user.info.UserIdRequestPacket;
import me.protoflicker.chessmate.protocol.packet.user.info.UserInfoRequestPacket;
import me.protoflicker.chessmate.protocol.packet.user.info.UsersInfoRequestPacket;
import me.protoflicker.chessmate.protocol.packet.user.info.UsersOnlineRequestPacket;
import me.protoflicker.chessmate.protocol.packet.user.info.response.UserIdResponsePacket;
import me.protoflicker.chessmate.protocol.packet.user.info.response.UserInfoResponsePacket;
import me.protoflicker.chessmate.protocol.packet.user.info.response.UsersInfoResponsePacket;
import me.protoflicker.chessmate.protocol.packet.user.info.response.UsersOnlineResponsePacket;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IntelManager {

	public static void handleUserIdRequest(ClientThread c, ClientPacket packet){
		UserIdRequestPacket p = (UserIdRequestPacket) packet;

		byte[] id = UserTable.getUserIdByUsername(p.getUsername());

		c.sendPacket(new UserIdResponsePacket(p.getUsername(), id));
	}

	public static void handleUserInfoRequest(ClientThread c, ClientPacket packet){
		UserInfoRequestPacket p = (UserInfoRequestPacket) packet;

		UserInfo info = UserTable.getUserInfo(p.getUserId());

		if(info != null){
			info = new UserInfo(info.getUserId(), info.getUsername(), null, info.getAccountType(), info.getLastLogin());
		}

		c.sendPacket(new UserInfoResponsePacket(p.getUserId(), info));
	}


	public static void handleUsersInfoRequest(ClientThread c, ClientPacket packet){
		UsersInfoRequestPacket p = (UsersInfoRequestPacket) packet;
		Map<byte[], UserInfo> infos = new HashMap<>();

		for(byte[] userId : p.getUserIds()){
			UserInfo info = UserTable.getUserInfo(userId);

			if(info != null){
				info = new UserInfo(info.getUserId(), info.getUsername(), null, info.getAccountType(), info.getLastLogin());
			}

			infos.put(userId, info);
		}

		c.sendPacket(new UsersInfoResponsePacket(infos));
	}


	public static void handleUsersOnlineRequest(ClientThread c, ClientPacket packet){
		UsersOnlineRequestPacket p = (UsersOnlineRequestPacket) packet;

		Set<byte[]> online = LoginManager.getOnlineUsers();
		c.sendPacket(new UsersOnlineResponsePacket(online));
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
		packetHandlers.put(UserIdRequestPacket.class, IntelManager::handleUserIdRequest);
		packetHandlers.put(UserInfoRequestPacket.class, IntelManager::handleUserInfoRequest);
		packetHandlers.put(UsersInfoRequestPacket.class, IntelManager::handleUsersInfoRequest);
		packetHandlers.put(UsersOnlineRequestPacket.class, IntelManager::handleUsersOnlineRequest);
	}
}
