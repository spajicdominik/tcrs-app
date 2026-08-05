import axios from "axios";
import { API_BASE_URL } from "../config/env";

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
  headers: {
    "Content-Type": "application/json",
  },
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (axios.isAxiosError(error)) {
      const message =
        (error.response?.data as { message?: string } | undefined)?.message ??
        error.message;
      error.message = message;

      // Session is gone (expired/invalid token). Announce it instead of importing the
      // store here, which would create a circular import (store -> slice -> api -> apiClient).
      // Routes.tsx listens and clears the user, which sends the app back to /authenticate.
      // /auth/me is excluded: a 401 there is the normal "not logged in" answer.
      const isSessionProbe = error.config?.url?.includes("/auth/me");
      if (error.response?.status === 401 && !isSessionProbe) {
        window.dispatchEvent(new CustomEvent("auth:unauthorized"));
      }
    }
    return Promise.reject(error);
  },
);
