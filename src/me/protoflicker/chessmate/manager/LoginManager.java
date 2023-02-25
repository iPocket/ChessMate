package me.protoflicker.chessmate.manager;

import com.google.common.hash.Hashing;
import me.protoflicker.chessmate.connection.ClientThread;
import me.protoflicker.chessmate.connection.PacketHandler;
import me.protoflicker.chessmate.data.table.TokenTable;
import me.protoflicker.chessmate.data.table.UserTable;
import me.protoflicker.chessmate.protocol.chess.enums.AccountType;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;
import me.protoflicker.chessmate.protocol.packet.user.login.LoginByPasswordPacket;
import me.protoflicker.chessmate.protocol.packet.user.login.LoginByTokenPacket;
import me.protoflicker.chessmate.protocol.packet.user.login.response.LoginSuccessfulPacket;
import me.protoflicker.chessmate.protocol.packet.user.login.response.LoginThrottledPacket;
import me.protoflicker.chessmate.protocol.packet.user.login.response.LoginUnsuccessfulPacket;
import me.protoflicker.chessmate.protocol.packet.user.register.RegisterPacket;
import me.protoflicker.chessmate.protocol.packet.user.register.response.RegisterBadNamePacket;
import me.protoflicker.chessmate.protocol.packet.user.register.response.RegisterBadPasswordPacket;
import me.protoflicker.chessmate.protocol.packet.user.register.response.RegisterNameTakenPacket;
import me.protoflicker.chessmate.protocol.packet.user.register.response.RegisterSuccessfulPacket;
import me.protoflicker.chessmate.protocol.packet.user.setting.UserPasswordChangePacket;
import me.protoflicker.chessmate.protocol.packet.user.setting.response.UserPasswordChangeBadPasswordPacket;
import me.protoflicker.chessmate.protocol.packet.user.setting.response.UserPasswordChangeSuccessfulPacket;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

public abstract class LoginManager {

	private static final String SALT = "medicbag";

	private static final Map<Class<?>, PacketHandler> packetHandlers = new HashMap<>();

	static {
		initHandlers();
	}

	private static final Map<ClientThread, Long> nextAllowedToLogin = Collections.synchronizedMap(new WeakHashMap<>());

	private static final Map<ClientThread, byte[]> loggedIn = Collections.synchronizedMap(new WeakHashMap<>());

	private static String hashPassword(String password){
		return Hashing.sha256().hashString(password + SALT, StandardCharsets.UTF_8).toString();
	}

	public static boolean isPasswordCorrect(byte[] userId, String givenPassword){
		return hashPassword(givenPassword).equals(UserTable.getHashedPassword(userId));
	}


	private static void doSuccessfulLogin(ClientThread c, byte[] userId, boolean sendToken){
		String token = null;
		if(sendToken){
			token = TokenTable.createAndAddToken(userId);
		}

		c.sendPacket(new LoginSuccessfulPacket(userId, token));

		loggedIn.put(c, userId);
		UserTable.updateLastLogin(userId);
		unregisterHandlers(c);
	}

	public static byte[] getUserId(ClientThread c){
		return loggedIn.get(c);
	}

	public static boolean isAuthorised(ClientThread c, byte[] userId){
		return loggedIn.get(c) == userId;
	}

	public static boolean isUsernameValid(String username){
		return username.length() <= 32 && username.length() >= 2;
	}

	public static boolean isPasswordValid(String password){
		return password.length() >= 8;
	}
	
	
	public static void handleLoginByPassword(ClientThread c, ClientPacket packet){
		LoginByPasswordPacket p = (LoginByPasswordPacket) packet;
		
		if(loggedIn.containsKey(c)){
			return;
		}
		
		Long time = nextAllowedToLogin.get(c);
		if(time == null || System.currentTimeMillis() >= time){ //vulnerability: nullables could be exploited to just break things
			byte[] userId = UserTable.getUserIdByUsername(p.getUsername());
			if(userId != null){
				if(isPasswordCorrect(userId, hashPassword(p.getPassword()))){
					doSuccessfulLogin(c, userId, true);
					return;
				}
			}

			nextAllowedToLogin.put(c, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(3));
			c.sendPacket(new LoginUnsuccessfulPacket());
		} else {
			c.sendPacket(new LoginThrottledPacket());
		}
	}

	public static void handleLoginByToken(ClientThread c, ClientPacket packet){
		LoginByTokenPacket p = (LoginByTokenPacket) packet;

		if(loggedIn.containsKey(c)){
			return;
		}
		
		Long time = nextAllowedToLogin.get(c);
		if(time == null || System.currentTimeMillis() >= time){ //vulnerability: nullables could be exploited to just break things
			byte[] userId = TokenTable.getUserIdByToken(p.getToken());
			if(userId != null){
				doSuccessfulLogin(c, userId, false);
				return;
			}

			c.sendPacket(new LoginUnsuccessfulPacket());
			nextAllowedToLogin.put(c, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30));
		} else {
			c.sendPacket(new LoginThrottledPacket());
		}
	}


	public static void handleRegister(ClientThread c, ClientPacket packet){
		RegisterPacket p = (RegisterPacket) packet;

		if(loggedIn.containsKey(c)){
			return;
		}
		
		if(!isUsernameValid(p.getUsername())){
			c.sendPacket(new RegisterBadNamePacket(p.getUsername()));
			return;
		}

		if(!isPasswordValid(p.getPassword())){
			c.sendPacket(new RegisterBadPasswordPacket());
			return;
		}

		if(UserTable.getUserIdByUsername(p.getUsername()) != null){
			c.sendPacket(new RegisterNameTakenPacket(p.getUsername()));
			return;
		}

		UserTable.createUser(p.getUsername(), hashPassword(p.getPassword()), p.getBirthday(), AccountType.USER);
		c.sendPacket(new RegisterSuccessfulPacket(p.getUsername()));
	}

	public static void handleChangePassword(ClientThread c, ClientPacket packet){
		UserPasswordChangePacket p = (UserPasswordChangePacket) packet;

		byte[] userId = loggedIn.get(c);
		if(userId == null){
			return;
		}
		
		if(!isPasswordValid(p.getNewPassword())){
			c.sendPacket(new UserPasswordChangeBadPasswordPacket());
			return;
		}

		if(!isPasswordCorrect(userId, p.getOldPassword())){
			c.sendPacket(new UserPasswordChangeBadPasswordPacket()); //could consider throttling here, potential vulnerability
			return;
		}
		
		
		UserTable.setHashedPassword(userId, hashPassword(p.getNewPassword()));
		c.sendPacket(new UserPasswordChangeSuccessfulPacket());
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

	public static void initHandlers(){
		packetHandlers.put(LoginByPasswordPacket.class, LoginManager::handleLoginByPassword);
		packetHandlers.put(LoginByTokenPacket.class, LoginManager::handleLoginByToken);
		packetHandlers.put(RegisterPacket.class, LoginManager::handleRegister);

		packetHandlers.put(UserPasswordChangePacket.class, LoginManager::handleChangePassword);
	}
}
