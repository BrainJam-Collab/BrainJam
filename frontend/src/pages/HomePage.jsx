import { useEffect, useMemo, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { apiFetch } from "../api/client";
import { useAuth } from "../auth/useAuth";

export default function HomePage() {
  const { token, me, loading } = useAuth();
  const navigate = useNavigate();
  const [puzzles, setPuzzles] = useState([]);
  const [puzzleId, setPuzzleId] = useState("");
  const [ownerParticipates, setOwnerParticipates] = useState(true);
  const [inviteCode, setInviteCode] = useState("");
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    let active = true;
    async function load() {
      try {
        const data = await apiFetch("/puzzles");
        if (!active) return;
        setPuzzles(data || []);
        if (data && data.length > 0) setPuzzleId(data[0].id);
      } catch (err) {
        if (!active) return;
        setError(err.message || "Failed to load puzzles");
      }
    }
    load();
    return () => {
      active = false;
    };
  }, []);

  const genres = useMemo(() => {
    const counts = {};
    puzzles.forEach((p) => {
      counts[p.genre] = (counts[p.genre] || 0) + 1;
    });
    return Object.entries(counts);
  }, [puzzles]);

  async function handleCreate(e) {
    e.preventDefault();
    if (!token) return;
    setError("");
    setBusy(true);
    try {
      const room = await apiFetch("/rooms", {
        method: "POST",
        token,
        body: JSON.stringify({ puzzleId, ownerParticipates }),
      });
      navigate(`/rooms/${room.id}`);
    } catch (err) {
      setError(err.message || "Failed to create room");
    } finally {
      setBusy(false);
    }
  }

  async function handleJoin(e) {
    e.preventDefault();
    if (!token) return;
    setError("");
    setBusy(true);
    try {
      const room = await apiFetch("/rooms/join", {
        method: "POST",
        token,
        body: JSON.stringify({ inviteCode }),
      });
      navigate(`/rooms/${room.id}`);
    } catch (err) {
      setError(err.message || "Failed to join room");
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="page">
      <section className="hero">
        <div>
          <p className="eyebrow">Collaborative puzzle rooms</p>
          <h1>BrainJam</h1>
          <p className="muted">
            Squad up, split the puzzle, and race the timer. One jam. Four tabs open.
          </p>
          {!token && (
            <div className="hero-actions">
              <Link className="primary" to="/register">Create an account</Link>
              <Link className="ghost" to="/login">I already have one</Link>
            </div>
          )}
          {!token && (
            <p className="muted small" style={{ marginTop: "12px" }}>
              Sign in to start creating or joining rooms.
            </p>
          )}
        </div>
        <div className="hero-card sticker tilt-right">
          <h3>Pick your chaos</h3>
          <div className="pill-grid">
            {genres.length === 0 && <span className="pill">Loading...</span>}
            {genres.map(([genre, count]) => (
              <span key={genre} className="pill">{genre} · {count}</span>
            ))}
          </div>
          <div className="divider" />
          <div className="stat">
            <span className="label">Puzzles seeded</span>
            <span className="value">{puzzles.length}</span>
          </div>
        </div>
      </section>

      {loading ? (
        <div className="card">Loading your account…</div>
      ) : token ? (
        <section className="grid">
          <div className="card sticker tilt-left">
            <h2>Create a room</h2>
            <p className="muted">Pick a puzzle. Decide if you’re playing.</p>
            <form className="form" onSubmit={handleCreate}>
              <label>
                Puzzle
                <select value={puzzleId} onChange={(e) => setPuzzleId(e.target.value)} required>
                  {puzzles.map((p) => (
                    <option key={p.id} value={p.id}>{p.title} · {p.genre}</option>
                  ))}
                </select>
              </label>
              <label className="toggle">
                <input
                  type="checkbox"
                  checked={ownerParticipates}
                  onChange={(e) => setOwnerParticipates(e.target.checked)}
                />
                <span>Owner participates in the room</span>
              </label>
              <button className="primary" type="submit" disabled={busy || !puzzleId}>
                {busy ? "Creating..." : "Create room"}
              </button>
            </form>
          </div>

          <div className="card sticker tilt-right">
            <h2>Join a room</h2>
            <p className="muted">Drop the invite code and jump in.</p>
            <form className="form" onSubmit={handleJoin}>
              <label>
                Invite code
                <input
                  type="text"
                  value={inviteCode}
                  onChange={(e) => setInviteCode(e.target.value.toUpperCase())}
                  placeholder="ABC12345"
                  required
                />
              </label>
              <button className="primary" type="submit" disabled={busy || !inviteCode}>
                {busy ? "Joining..." : "Join room"}
              </button>
            </form>
          </div>

          <div className="card sticker tilt-left">
            <h2>Signed in</h2>
            <p className="muted">@{me?.username}</p>
            <div className="stat-list">
              <div className="stat">
                <span className="label">Max players</span>
                <span className="value">4</span>
              </div>
              <div className="stat">
                <span className="label">Your role</span>
                <span className="value">Owner or member</span>
              </div>
            </div>
          </div>
        </section>
      ) : null}

      {error && <div className="error">{error}</div>}
    </div>
  );
}
