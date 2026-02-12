package com.puzzleroom.realtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.puzzleroom.room.Room;
import com.puzzleroom.room.RoomMemberRepository;
import com.puzzleroom.room.RoomRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoomSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper;
    private final RoomRepository rooms;
    private final RoomMemberRepository members;

    private final Map<UUID, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    private final Map<String, UUID> sessionToRoom = new ConcurrentHashMap<>();

    public RoomSocketHandler(ObjectMapper mapper, RoomRepository rooms, RoomMemberRepository members) {
        this.mapper = mapper;
        this.rooms = rooms;
        this.members = members;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        UUID roomId = sessionToRoom.remove(session.getId());
        if (roomId != null) {
            Set<WebSocketSession> sessions = roomSessions.get(roomId);
            if (sessions != null) {
                sessions.remove(session);
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, Object> payload = mapper.readValue(message.getPayload(), Map.class);
        String type = (String) payload.get("type");
        if (!"subscribe".equals(type)) return;

        String roomIdStr = (String) payload.get("roomId");
        if (roomIdStr == null) {
            session.close(new CloseStatus(1008, "roomId required"));
            return;
        }

        UUID roomId = UUID.fromString(roomIdStr);
        String userIdStr = (String) session.getAttributes().get("userId");
        if (userIdStr == null) {
            session.close(new CloseStatus(1008, "unauthorized"));
            return;
        }

        UUID userId = UUID.fromString(userIdStr);
        if (!canAccess(roomId, userId)) {
            session.close(new CloseStatus(1008, "forbidden"));
            return;
        }

        sessionToRoom.put(session.getId(), roomId);
        roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);

        session.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                "type", "subscribed",
                "roomId", roomId.toString(),
                "timestamp", Instant.now().toString()
        ))));
    }

    public void broadcast(UUID roomId, String type) {
        Set<WebSocketSession> sessions = roomSessions.get(roomId);
        if (sessions == null || sessions.isEmpty()) return;

        String msg;
        try {
            msg = mapper.writeValueAsString(Map.of(
                    "type", type,
                    "roomId", roomId.toString(),
                    "timestamp", Instant.now().toString()
            ));
        } catch (Exception e) {
            return;
        }

        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) continue;
            try {
                session.sendMessage(new TextMessage(msg));
            } catch (Exception ignored) {}
        }
    }

    private boolean canAccess(UUID roomId, UUID userId) {
        Room room = rooms.findById(roomId).orElse(null);
        if (room == null) return false;
        if (room.getOwnerId().equals(userId)) return true;
        return members.findByRoomIdAndUserId(roomId, userId).isPresent();
    }
}
