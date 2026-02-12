import { BrowserRouter, Routes, Route, Navigate, Link, useNavigate } from "react-router-dom";
import "./App.css";
import { useAuth } from "./auth/useAuth";
import HomePage from "./pages/HomePage.jsx";
import LoginPage from "./pages/LoginPage.jsx";
import RegisterPage from "./pages/RegisterPage.jsx";
import RoomPage from "./pages/RoomPage.jsx";

function RequireAuth({ children }) {
  const { token, loading } = useAuth();
  if (loading) return <div className="page"><div className="card">Loading...</div></div>;
  if (!token) return <Navigate to="/login" replace />;
  return children;
}

function Header() {
  const auth = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    auth.logout();
    navigate("/login");
  }

  return (
    <header className="site-header">
      <div className="brand">
        <Link to="/">BrainJam</Link>
      </div>
      <nav className="nav">
        {auth.token ? (
          <>
            <button className="ghost" onClick={handleLogout}>Logout</button>
          </>
        ) : (
          <>
            <Link className="nav-link" to="/login">Login</Link>
            <Link className="nav-link" to="/register">Register</Link>
          </>
        )}
      </nav>
    </header>
  );
}

function AppLayout({ children }) {
  return (
    <div className="app">
      <Header />
      <main className="site-main">{children}</main>
    </div>
  );
}

function App() {
  return (
    <BrowserRouter>
      <AppLayout>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route
            path="/rooms/:roomId"
            element={
              <RequireAuth>
                <RoomPage />
              </RequireAuth>
            }
          />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AppLayout>
    </BrowserRouter>
  );
}

export default App;
