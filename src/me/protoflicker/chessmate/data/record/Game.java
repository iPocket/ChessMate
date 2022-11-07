package me.protoflicker.chessmate.data.record;

import java.sql.Timestamp;

public record Game(String gameId, Timestamp startTime, Timestamp endTime, String moves) {

}
