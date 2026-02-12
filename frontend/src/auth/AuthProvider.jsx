import React, { useEffect, useMemo, useState } from "react";
import { AuthContext } from "./authContext";
import { getToken, setToken as persistToken, clearToken } from "./authStorage";
import { apiFetch } from "../api/client";

export function AuthProvider({ children }) {
  const [token, setToken] = useState(getToken());
  const [me, setMe] = useState(null);
  const [loading, setLoading] = useState(true);

  async function refreshMe(nextToken = token) {
    if (!nextToken) {
      setMe(null);
      setLoading(false);
      return;
    }
    try {
      const data = await apiFetch("/me", { token: nextToken });
      setMe(data);
    } catch {
      clearToken();
      setToken("");
      setMe(null);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    refreshMe();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const value = useMemo(() => ({
    token,
    me,
    loading,

    async login(identifier, password) {
      const data = await apiFetch("/auth/login", {
        method: "POST",
        body: JSON.stringify({ identifier, password }),
      });
      persistToken(data.token);
      setToken(data.token);
      setLoading(true);
      await refreshMe(data.token);
    },

    async register(username, email, password) {
      const data = await apiFetch("/auth/register", {
        method: "POST",
        body: JSON.stringify({ username, email, password }),
      });
      persistToken(data.token);
      setToken(data.token);
      setLoading(true);
      await refreshMe(data.token);
    },

    logout() {
      clearToken();
      setToken("");
      setMe(null);
    },
  }), [token, me, loading]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
