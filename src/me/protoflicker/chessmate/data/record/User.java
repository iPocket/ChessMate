package me.protoflicker.chessmate.data.record;

import java.sql.Date;
import java.sql.Timestamp;

public record User(String userId, String username, String hashedPassword, Date birthday, Timestamp lastLogin) {

}
