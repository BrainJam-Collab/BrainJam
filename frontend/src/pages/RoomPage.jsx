import { useEffect, useMemo, useRef, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { apiFetch } from "../api/client";
import { useAuth } from "../auth/useAuth";

function formatDuration(seconds) {
  if (seconds == null) return "—";
  const mins = Math.floor(seconds / 60);
  const secs = seconds % 60;
  return `${mins}m ${secs}s`;
}

export default function RoomPage() {
  const { roomId } = useParams();
  const { token, me } = useAuth();
  const navigate = useNavigate();
  const [room, setRoom] = useState(null);
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const refreshInFlight = useRef(false);

  async function refresh({ silent = false } = {}) {
    if (!token) return;
    if (refreshInFlight.current && silent) return;
    refreshInFlight.current = true;
    if (!silent) {
      setError("");
      setLoading(true);
    }
    try {
      const roomData = await apiFetch(`/rooms/${roomId}`, { token });
      const taskData = await apiFetch(`/rooms/${roomId}/tasks`, { token });
      setRoom(roomData);
      setTasks(taskData || []);
    } catch (err) {
      setError(err.message || "Failed to load room");
    } finally {
      if (!silent) setLoading(false);
      refreshInFlight.current = false;
    }
  }

  useEffect(() => {
    refresh();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [roomId, token]);

  useEffect(() => {
    if (!token) return;
    const base = import.meta.env.VITE_WS_BASE_URL || "ws://localhost:8080";
    const ws = new WebSocket(`${base}/ws/rooms?token=${encodeURIComponent(token)}`);

    ws.onopen = () => {
      ws.send(JSON.stringify({ type: "subscribe", roomId }));
    };
    ws.onmessage = (event) => {
      try {
        const msg = JSON.parse(event.data);
        if (msg.type === "refresh") {
          refresh({ silent: true });
        }
      } catch {
        // ignore bad payloads
      }
    };

    return () => {
      ws.close();
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [roomId, token]);

  async function handleStart() {
    if (!token) return;
    setBusy(true);
    setError("");
    try {
      const data = await apiFetch(`/rooms/${roomId}/start`, { method: "POST", token });
      setTasks(data || []);
      await refresh();
    } catch (err) {
      setError(err.message || "Failed to start room");
    } finally {
      setBusy(false);
    }
  }

  async function handleComplete(roomTaskId) {
    if (!token) return;
    setBusy(true);
    setError("");
    try {
      await apiFetch(`/room-tasks/${roomTaskId}/complete`, { method: "POST", token });
      await refresh();
    } catch (err) {
      setError(err.message || "Failed to complete task");
    } finally {
      setBusy(false);
    }
  }

  const isOwner = useMemo(() => {
    if (!room || !me) return false;
    return room.ownerId === me.id;
  }, [room, me]);

  const memberColors = useMemo(() => {
    const palette = [
      "#FF6B6B",
      "#6A5CFF",
      "#2ED9C3",
      "#FFD166",
    ];
    const map = new Map();
    (room?.members || []).forEach((m, idx) => {
      map.set(m.id, palette[idx % palette.length]);
    });
    return map;
  }, [room?.members]);

  const memberStatus = useMemo(() => {
    const status = new Map();
    (room?.members || []).forEach((m) => {
      status.set(m.id, { name: m.username, completed: false });
    });
    tasks.forEach((t) => {
      if (status.has(t.assignedToId)) {
        status.get(t.assignedToId).completed = t.status === "COMPLETED";
      }
    });
    return status;
  }, [room?.members, tasks]);

  const completionStats = useMemo(() => {
    const total = room?.members?.length || 0;
    let done = 0;
    memberStatus.forEach((value) => {
      if (value.completed) done += 1;
    });
    const percent = total === 0 ? 0 : Math.round((done / total) * 100);
    return { total, done, percent };
  }, [room?.members, memberStatus]);

  if (loading) {
    return (
      <div className="page">
        <div className="card">Loading room…</div>
      </div>
    );
  }

  if (!room) {
    return (
      <div className="page">
        <div className="card">Room not found.</div>
      </div>
    );
  }

  return (
    <div className="page">
      <section className="room-header">
        <div>
          <p className="eyebrow">Room {room.id}</p>
          <h1>{room.puzzle?.title}</h1>
          <p className="muted">{room.puzzle?.genre} · Status: {room.status}</p>
        </div>
        <div className="room-meta">
          <div className="stat">
            <span className="label">Invite code</span>
            <span className="value mono">{room.inviteCode}</span>
          </div>
          <div className="stat">
            <span className="label">Team time</span>
            <span className="value">{formatDuration(room.teamTimeSeconds)}</span>
          </div>
        </div>
      </section>

      {room.members?.length > 0 && (
        <section className="card progress-card sticker tilt-right">
          <div className="progress-header">
            <span>Start</span>
            <span>{completionStats.done} / {completionStats.total} solved</span>
            <span>Finish</span>
          </div>
          <div className="progress-bar">
            <div className="progress-fill" style={{ width: `${completionStats.percent}%` }} />
          </div>
          <div className="progress-legend">
            {room.members.map((m) => {
              const color = memberColors.get(m.id) || "#6A5CFF";
              const completed = memberStatus.get(m.id)?.completed;
              return (
                <div key={m.id} className={`legend-item ${completed ? "done" : ""}`}>
                  <span className="legend-dot" style={{ background: color }} />
                  <span>@{m.username}</span>
                </div>
              );
            })}
          </div>
        </section>
      )}

      {room.status === "COMPLETED" && (
        <div className="completion-overlay">
          <div className="completion-panel sticker tilt-left">
            <p className="eyebrow">Solved</p>
            <h2>Room complete</h2>
            <p className="muted">
              Total time: <strong>{formatDuration(room.teamTimeSeconds)}</strong>
            </p>
            <button className="primary" onClick={() => navigate("/")}>
              Back to home
            </button>
          </div>
        </div>
      )}

      <section className="grid">
        <div className="card sticker tilt-left">
          <h2>Lobby</h2>
          <div className="list">
            {room.members?.map((m) => (
              <div
                className="list-item member-card"
                key={m.id}
                style={{
                  borderColor: memberColors.get(m.id) || "#6A5CFF",
                  background: `${memberColors.get(m.id) || "#6A5CFF"}14`,
                }}
              >
                <div>
                  <div className="strong">@{m.username}</div>
                  <div className="muted small">{m.role}</div>
                </div>
              </div>
            ))}
          </div>
          {room.status === "OPEN" && (
            <div className="lobby-wait">
              <div className="spinner" />
              <div>
                <div className="strong">Finding more braincells...</div>
                <div className="muted small">Share the invite code and wait for the squad.</div>
              </div>
            </div>
          )}
          {isOwner && room.status === "OPEN" && (
            <button className="primary" onClick={handleStart} disabled={busy}>
              {busy ? "Starting..." : "Start room"}
            </button>
          )}
        </div>

        <div className="card sticker tilt-right">
          <h2>Your tasks</h2>
          {tasks.length === 0 ? (
            <p className="muted">Tasks will appear once the room starts.</p>
          ) : (
            <div className="list">
              {tasks.map((task) => {
                const isMine = me && task.assignedToId === me.id;
                const assigneeColor = memberColors.get(task.assignedToId) || "#E36B3D";
                return (
                  <div className="list-item" key={task.id}>
                    <div className="task-main">
                      <div className="strong">{task.title}</div>
                      <div className="muted small">{task.prompt}</div>
                      <div className="meta-row">
                        <span className="pill" style={{ background: `${assigneeColor}22`, color: assigneeColor }}>
                          {task.status}
                        </span>
                        <span className="muted small">Assigned to @{task.assignedToUsername}</span>
                      </div>
                    </div>
                    <div className="task-actions">
                      <div className="muted small">Duration: {formatDuration(task.durationSeconds)}</div>
                      {isMine && task.status === "ASSIGNED" && (
                        <button className="ghost" onClick={() => handleComplete(task.id)} disabled={busy}>
                          Mark complete
                        </button>
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </section>

      {error && <div className="error">{error}</div>}
    </div>
  );
}
