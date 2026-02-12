# BrainJam

Collaborative puzzle rooms where up to 4 players split a puzzle into parallel tasks and race the clock together.

## Stack
- Backend: Spring Boot 3, JWT auth, JPA, PostgreSQL
- Frontend: React + Vite
- Realtime: WebSockets

## Features (current)
- Register/login (username or email)
- Create/join rooms via invite code
- Assign 1 task per participant on room start
- Task completion + team time (max completion time)
- Realtime updates on joins/starts/completions

## Local setup

### 1) Start Postgres
```bash
cd backend
docker compose up -d
```

### 2) Start backend
```bash
cd backend
./mvnw spring-boot:run
```
Backend runs on `http://localhost:8080`.

### 3) Start frontend
```bash
cd frontend
npm install
npm run dev
```
Frontend runs on `http://localhost:5173` by default.

Create `frontend/.env`:
```
VITE_API_BASE_URL=http://localhost:8080
VITE_WS_BASE_URL=ws://localhost:8080
```

## Notes
- If you recently introduced usernames, existing DB rows may need migration or a dev reset.
- Dev reset (clears DB):
```bash
cd backend
docker compose down -v
```

## Repo layout
- `backend/` Spring Boot API
- `frontend/` React UI
- `docs/` project docs
