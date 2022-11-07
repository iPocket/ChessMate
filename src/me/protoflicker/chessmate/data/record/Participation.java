package me.protoflicker.chessmate.data.record;

import me.protoflicker.chessmate.data.record.enums.GameSide;
import me.protoflicker.chessmate.data.record.enums.Result;

public record Participation(String participationId, String userId, String gameId, GameSide gameSide, Result result) {

}

