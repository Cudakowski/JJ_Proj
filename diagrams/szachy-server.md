classDiagram
direction BT
class BackendMain {
  + BackendMain() 
  + main(String[]) void
}
class Bishop {
  + Bishop(Color, Coordinates) 
   Set~CoordinatesShift~ pieceMoves
}
class Board {
  + Board() 
  - Coordinates enPassantTarget
  + getPiece(Coordinates) Piece
  + revokeCastlingRight(Color, boolean) void
  + setupFromFEN(String) void
  + isSquareEmpty(Coordinates) boolean
  + hasCastlingRight(Color, boolean) boolean
  + copy() Board
  - setupKnights() void
  + movePiece(Coordinates, Coordinates) void
  + removePiece(Coordinates) void
  + setupDefaultPiecesPositions() void
  - setupRooks() void
  - setupBishops() void
  - setupQueens() void
  + setPiece(Coordinates, Piece) void
  - createPieceFromFenChar(char, Coordinates) Piece
  - setupPawns() void
  - setupKings() void
   Coordinates enPassantTarget
}
class BoardConsoleRenderer {
  + BoardConsoleRenderer() 
  + render(Board) void
  - getPieceSymbol(Piece) String
}
class BoardTest {
  + BoardTest() 
  + setUp() void
  + testInitialBoardSetup() void
}
class ClientHandler {
  + ClientHandler(Socket) 
  - GameSession currentSession
  - boolean isInGame
  - String playerLogin
  - commandCancelInvite(String[]) void
  + clearSession() void
  - processMessage(String) void
  - commandDecline(String[]) void
  + sendUpdatedUserList() void
  - clearAfterDisconnect() void
  - commandAccept(String[]) void
  - commandLogin(String[]) void
  + sendMessage(String) void
  + toString() String
  + pingAbsenceDisconnection() void
  - commandResumeGame(String, String) void
  + run() void
  - commandRegister(String[]) void
  + getlastPingTime() long
  - commandMove(String[]) void
  - commandGetUserList(String[]) void
  - commandInvite(String[]) void
   String playerLogin
   GameSession currentSession
   boolean isInGame
}
class Color {
<<enumeration>>
  - Color() 
  + valueOf(String) Color
  + values() Color[]
}
class Coordinates {
  + Coordinates(File, int) 
  + shift(CoordinatesShift) Coordinates
  + equals(Object) boolean
  + toString() String
  + hashCode() int
}
class CoordinatesParser {
  + CoordinatesParser() 
  + parse(String) Coordinates
  + toString(Coordinates) String
}
class CoordinatesShift {
  + CoordinatesShift(int, int) 
}
class DatabaseManager {
  + DatabaseManager() 
  + closePool() void
   Connection connection
}
class File {
<<enumeration>>
  - File() 
  + values() File[]
  + valueOf(String) File
}
class Game {
  + Game(Board) 
  - handleCastling(Coordinates, Coordinates) void
  - handlePromotion(Coordinates, Color) void
  + startGame() void
  - updateCastlingRights(Coordinates, Piece) void
}
class GameSession {
  + GameSession(ClientHandler, ClientHandler, String) 
  + GameSession(ClientHandler, ClientHandler, String, int) 
  - Color currentTurn
  - boolean gameOver
  + pauseGame(String) void
  + playerDisconnected(ClientHandler) void
  + startGame() void
  - colorToString(Color) String
  + boardToFEN() String
  + endGameSession() void
  - updateCastlingRights(Coordinates, Piece) void
  + applyMove(String, String, String) String
  + getColorByLogin(String) Color
  + checkTime() void
  - handleCastling(Coordinates, Coordinates) void
  + broadcast(String) void
  - pieceToFEN(Piece) char
  + getOpponent(ClientHandler) ClientHandler
   String initialFEN
   Color currentTurn
   String allLegalMovesFor
   boolean gameOver
}
class GameStateChecker {
  + GameStateChecker() 
  + isKingInCheck(Color, Board) boolean
  + hasLegalMoves(Color, Board) boolean
  + isCheckPate(Color, Board) boolean
  + shouldPromote(Piece, Coordinates) boolean
  + getLegalMoves(Piece, Coordinates, Board) Set~Coordinates~
  + promotePawn(Coordinates, Piece, Board) void
  - findKing(Color, Board) Coordinates
  + isCheckMate(Color, Board) boolean
}
class GamesTable {
  + GamesTable() 
  + getGameFen(int) String
  + isUserWhiteInGame(int, int) boolean
  + updateGameFen(int, String) boolean
  + setGameOver(int, Boolean) boolean
  + createNewGame(int, int, String) Integer
  + getPausedGamesForUser(int) String
}
class InputCoordinates {
  + InputCoordinates() 
  + inputPieceCoordinatesForColor(Color, Board) Coordinates
  + input() Coordinates
  + inputAvailableSquare(Set~Coordinates~) Coordinates
}
class King {
  + King(Color, Coordinates) 
  # isSquareAvailableForMove(Coordinates, Board) boolean
   Set~CoordinatesShift~ pieceMoves
}
class Knight {
  + Knight(Color, Coordinates) 
  # canJumpOverPieces() boolean
   Set~CoordinatesShift~ pieceMoves
}
class MovesTable {
  + MovesTable() 
  + saveMove(int, String, String, int, Integer) boolean
  + getMovesForGame(int) List~String[]~
}
class Pawn {
  + Pawn(Color, Coordinates) 
  # isSquareAvailableForMove(Coordinates, Board) boolean
   Set~CoordinatesShift~ pieceMoves
}
class Piece {
  + Piece(Color, Coordinates) 
  + getAvailableMoveSquares(Board) Set~Coordinates~
  # canJumpOverPieces() boolean
  # isPathClear(Coordinates, Board) boolean
  # isSquareAvailableForMove(Coordinates, Board) boolean
   Set~CoordinatesShift~ pieceMoves
}
class PiecesTable {
  + PiecesTable() 
  + getPieceId(char, boolean) Integer
}
class Queen {
  + Queen(Color, Coordinates) 
   Set~CoordinatesShift~ pieceMoves
}
class Rook {
  + Rook(Color, Coordinates) 
   Set~CoordinatesShift~ pieceMoves
}
class Server {
  + Server() 
  + startServer() void
  - startClock() void
  - startConsole() void
}
class StatsRepository {
  + StatsRepository() 
  + getGameHistory(int) String
  + getUserStats(int) String
}
class TestLogic {
  + TestLogic() 
  + test() void
}
class UsersTable {
  + UsersTable() 
  + registerUser(String, String) boolean
  + getUserId(String) Integer
  + checkLogin(String, String) boolean
}

Bishop  -->  Piece 
Board "1" *--> "pieces *" Coordinates 
Board "1" *--> "pieces *" Piece 
BoardTest "1" *--> "board 1" Board 
ClientHandler "1" *--> "currentSession 1" GameSession 
Coordinates "1" *--> "file 1" File 
Game "1" *--> "board 1" Board 
Game "1" *--> "renderer 1" BoardConsoleRenderer 
GameSession "1" *--> "board 1" Board 
GameSession "1" *--> "whitePlayer 1" ClientHandler 
GameSession "1" *--> "currentTurn 1" Color 
GameSession "1" *--> "enPassantTarget 1" Coordinates 
King  -->  Piece 
Knight  -->  Piece 
Pawn  -->  Piece 
Piece "1" *--> "color 1" Color 
Piece "1" *--> "coordinates 1" Coordinates 
Queen  -->  Piece 
Rook  -->  Piece 
Server "1" *--> "onlineUsers *" ClientHandler 
