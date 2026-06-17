classDiagram
direction BT
class TCPConnection {
  + TCPConnection(Socket) 
  + TCPConnection(String, int) 
  + sendString(String) void
  + awaitString() String
  + close() void
}

