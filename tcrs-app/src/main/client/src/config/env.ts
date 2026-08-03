// Backend base URL. Override per-machine with VITE_API_BASE_URL in a .env file.
export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8083/api/v1";
