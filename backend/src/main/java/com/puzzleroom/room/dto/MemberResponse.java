package com.puzzleroom.room.dto;

import com.puzzleroom.room.RoomMember;
import java.util.UUID;

public class MemberResponse {
    public UUID id;
    public String username;
    public String role;

    public static MemberResponse from(RoomMember m) {
        MemberResponse r = new MemberResponse();
        r.id = m.getUser().getId();
        r.username = m.getUser().getUsername();
        r.role = m.getRole().name();
        return r;
    }
}
