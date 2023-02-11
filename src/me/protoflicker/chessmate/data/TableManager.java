package me.protoflicker.chessmate.data;

public abstract class TableManager {
//	public static List<byte[]> getGameIdsByUser(byte[] userId){
//		List<byte[]> gameIds = new ArrayList<>();
//
//		String statement =
//				"""
//				SELECT g.gameId
//				FROM `Participations` p
//				WHERE userId = ?
//				INNER JOIN `Games` g
//					ON p.gameId = g.gameId
//				ORDER BY p.result ASC;
//				""";
//
//		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
//			s.setBytes(1, userId);
//			ResultSet r = s.executeQuery();
//			while(r.next()){
//				gameIds.add(r.getBytes(1));
//			}
//			r.close();
//
//			return gameIds;
//		} catch (SQLException e){
//			throw new RuntimeException(e);
//		}
//	}
}
