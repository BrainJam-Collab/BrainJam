const BASE_URL = import.meta.env.VITE_API_BASE_URL;

export async function apiFetch(path, { token, ...options } = {}) {
  const headers = {
    "Content-Type": "application/json",
    ...(options.headers || {}),
  };

  if (token) headers.Authorization = `Bearer ${token}`;

  const res = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers,
  });

  // try parse json (some endpoints may return empty body on 401)
  const text = await res.text();
  const data = text ? safeJson(text) : null;

  if (!res.ok) {
    const msg = (data && (data.message || data.error)) || `Request failed: ${res.status}`;
    const err = new Error(msg);
    err.status = res.status;
    err.data = data;
    throw err;
  }

  return data;
}

function safeJson(text) {
  try { return JSON.parse(text); } catch { return null; }
}
