package me.protoflicker.chessmate.manager;

import com.google.common.hash.Hashing;
import me.protoflicker.chessmate.connection.ClientThread;
import me.protoflicker.chessmate.connection.PacketHandler;
import me.protoflicker.chessmate.console.Logger;
import me.protoflicker.chessmate.data.table.TokenTable;
import me.protoflicker.chessmate.data.table.UserTable;
import me.protoflicker.chessmate.protocol.chess.enums.AccountType;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;
import me.protoflicker.chessmate.protocol.packet.TestPacket;
import me.protoflicker.chessmate.protocol.packet.user.UserInfo;
import me.protoflicker.chessmate.protocol.packet.user.login.LoginByPasswordPacket;
import me.protoflicker.chessmate.protocol.packet.user.login.LoginByTokenPacket;
import me.protoflicker.chessmate.protocol.packet.user.login.response.LoginSuccessfulPacket;
import me.protoflicker.chessmate.protocol.packet.user.login.response.LoginThrottledPacket;
import me.protoflicker.chessmate.protocol.packet.user.login.response.LoginUnsuccessfulPacket;
import me.protoflicker.chessmate.protocol.packet.user.register.RegisterPacket;
import me.protoflicker.chessmate.protocol.packet.user.register.response.*;
import me.protoflicker.chessmate.protocol.packet.user.setting.UserLogoutPacket;
import me.protoflicker.chessmate.protocol.packet.user.setting.UserPasswordChangePacket;
import me.protoflicker.chessmate.protocol.packet.user.setting.response.UserPasswordChangeBadPasswordPacket;
import me.protoflicker.chessmate.protocol.packet.user.setting.response.UserPasswordChangeSuccessfulPacket;
import me.protoflicker.chessmate.protocol.packet.user.setting.response.UserPasswordChangeUnsuccessfulPacket;
import me.protoflicker.chessmate.protocol.packet.user.setting.response.UserPasswordChangeWrongPasswordPacket;
import org.mariadb.jdbc.plugin.authentication.standard.ed25519.Utils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public abstract class LoginManager {

	private static final String SALT = "medicbag";

	private static final Map<ClientThread, Long> nextAllowedToLogin = new ConcurrentHashMap<>();

	private static final Map<ClientThread, Long> nextAllowedToLoginByToken = new ConcurrentHashMap<>();

	private static final Map<ClientThread, byte[]> loggedIn = new ConcurrentHashMap<>();

	public static String hashPassword(String password){
		return Hashing.sha256().hashString(password + SALT, StandardCharsets.UTF_8).toString();
	}

	public static boolean isPasswordCorrect(byte[] userId, String givenPassword){
		return hashPassword(givenPassword).equals(UserTable.getHashedPassword(userId));
	}

	public static Set<byte[]> getOnlineUsers(){
		return new HashSet<>(loggedIn.values());
	}

	private static void doSuccessfulLogin(ClientThread c, byte[] userId, boolean sendToken, boolean register){
		String token = null;
		if(sendToken){
			token = TokenTable.createAndGetToken(userId);
		}

		UserTable.updateLastLogin(userId);

		UserInfo userInfo = UserTable.getUserInfo(userId);
		if(register){
			c.sendPacket(new RegisterSuccessfulPacket(userId, userInfo, token));
		} else {
			c.sendPacket(new LoginSuccessfulPacket(userId, userInfo, token));
		}

		assert userInfo != null;

		loggedIn.put(c, userId);

		Logger.getInstance().log((register ? "Registered" : "Logged in") + " as " + userInfo.getUsername() + " ("
				+ Utils.bytesToHex(userId)
				+ ") via " + (sendToken ? "password" : "token"));
	}

	public static byte[] getUserId(ClientThread c){
		return loggedIn.get(c);
	}

	public static boolean isAuthorised(ClientThread c, byte[] userId){
		return Arrays.equals(getUserId(c), userId);
	}

	public static Set<ClientThread> getClientsById(byte[] userId){
		Set<ClientThread> clients = new HashSet<>();
		for(Map.Entry<ClientThread, byte[]> entry : loggedIn.entrySet()){
			if(Arrays.equals(userId, entry.getValue())){
				clients.add(entry.getKey());
			}
		}

		return clients;
	}

	public static boolean isUsernameValid(String username){
		return TestPacket.isUsernameValid(username);
	}

	public static boolean isPasswordValid(String password){
		return TestPacket.isPasswordValid(password);
	}
	
	
	public static void handleLoginByPassword(ClientThread c, ClientPacket packet){
		LoginByPasswordPacket p = (LoginByPasswordPacket) packet;
		
		if(loggedIn.containsKey(c)){
			return;
		}

		if(p.getUsername() == null || p.getPassword() == null){
			c.sendPacket(new LoginUnsuccessfulPacket());
			return;
		}
		
		Long time = nextAllowedToLogin.get(c);
		if(time == null || System.currentTimeMillis() >= time){ //vulnerability: nullables could be exploited to just break things
			byte[] userId = UserTable.getUserIdByUsername(p.getUsername());
			if(userId != null){
				if(isPasswordCorrect(userId, p.getPassword())){
					doSuccessfulLogin(c, userId, true, false);
					return;
				}
			}

			nextAllowedToLogin.put(c, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(3));
			Logger.getInstance().log("Client failed to login as " + p.getUsername() + " via password");
			c.sendPacket(new LoginUnsuccessfulPacket());
		} else {
			Logger.getInstance().log("Client throttled to login as " + p.getUsername() + " via password");
			c.sendPacket(new LoginThrottledPacket());
		}
	}

	public static void handleLoginByToken(ClientThread c, ClientPacket packet){
		LoginByTokenPacket p = (LoginByTokenPacket) packet;

		if(loggedIn.containsKey(c)){
			return;
		}

		if(p.getToken() == null){
			c.sendPacket(new LoginUnsuccessfulPacket());
			return;
		}
		
		Long time = nextAllowedToLoginByToken.get(c);
		if(time == null || System.currentTimeMillis() >= time){ //vulnerability: nullables could be exploited to just break things
			byte[] userId = TokenTable.getUserIdByToken(p.getToken());
			if(userId != null){
				doSuccessfulLogin(c, userId, false, false);
				return;
			}

			Logger.getInstance().log("Client failed to login via token");
			c.sendPacket(new LoginUnsuccessfulPacket());
			nextAllowedToLoginByToken.put(c, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30));
		} else {
			Logger.getInstance().log("Client throttled to login via token");
			c.sendPacket(new LoginThrottledPacket());
		}
	}


	public static void handleRegister(ClientThread c, ClientPacket packet){
		RegisterPacket p = (RegisterPacket) packet;

		if(loggedIn.containsKey(c)){
			return;
		}

		if(p.getBirthday() == null){
			c.sendPacket(new RegisterUnsuccessfulPacket());
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

		doSuccessfulLogin(c, UserTable.getUserIdByUsername(p.getUsername()), true, true);
	}

	public static void handleChangePassword(ClientThread c, ClientPacket packet){
		UserPasswordChangePacket p = (UserPasswordChangePacket) packet;

		byte[] userId = loggedIn.get(c);
		if(userId == null){
			return;
		}

		if(p.getNewPassword() == null || p.getOldPassword() == null){
			c.sendPacket(new UserPasswordChangeUnsuccessfulPacket());
		}
		
		if(!isPasswordValid(p.getNewPassword())){
			Logger.getInstance().log("User failed to change password: bad password");
			c.sendPacket(new UserPasswordChangeBadPasswordPacket());
			return;
		}

		if(!isPasswordCorrect(userId, p.getOldPassword())){
			Logger.getInstance().log("User failed to change password: wrong password");
			c.sendPacket(new UserPasswordChangeWrongPasswordPacket()); //could consider throttling here, potential vulnerability
			return;
		}
		
		
		UserTable.setHashedPassword(userId, hashPassword(p.getNewPassword()));

		Logger.getInstance().log("User successfully changed password");

		c.sendPacket(new UserPasswordChangeSuccessfulPacket());
	}

	public static void handleLogout(ClientThread c, ClientPacket packet){
		UserLogoutPacket p = (UserLogoutPacket) packet;

		byte[] userId = loggedIn.get(c);
		if(userId == null){
			return;
		}

		TokenTable.removeTokenIfAuthorised(userId, p.getToken());

		Logger.getInstance().log("User logged out");

		c.tryClose();
	}

	public static void onDisconnect(ClientThread c){
		loggedIn.remove(c);
		nextAllowedToLogin.remove(c);
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

	public static void initHandlers(){
		packetHandlers.put(LoginByPasswordPacket.class, LoginManager::handleLoginByPassword);
		packetHandlers.put(LoginByTokenPacket.class, LoginManager::handleLoginByToken);
		packetHandlers.put(RegisterPacket.class, LoginManager::handleRegister);

		packetHandlers.put(UserPasswordChangePacket.class, LoginManager::handleChangePassword);
		packetHandlers.put(UserLogoutPacket.class, LoginManager::handleLogout);
	}
}
