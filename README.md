# BrainJam

Collaborative puzzle rooms where up to 4 players split a puzzle into parallel tasks and race the clock together.

<img width="3024" height="1964" alt="image" src="https://github.com/user-attachments/assets/585eb9bd-a740-454f-af1f-a76d6f71dfd6" />


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

<img width="3024" height="1964" alt="image" src="https://github.com/user-attachments/assets/81ca0038-5076-4a46-a1cc-ca694d69be03" />

<img width="3024" height="1964" alt="image" src="https://github.com/user-attachments/assets/aca039ff-2d45-4744-b96b-ba24e3491b66" />

<img width="3024" height="1964" alt="image" src="https://github.com/user-attachments/assets/da5a2dab-fa56-488d-a651-48519bb31c3c" />


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
