import { apiClient } from "../../../lib/apiClient";
import type { CurrentUser } from "../types/CurrentUser.ts";

// GET /api/v1/auth/me -> the logged-in user, resolved from the HttpOnly JWT cookie
export async function getCurrentUser(): Promise<CurrentUser> {
    const { data } = await apiClient.get<CurrentUser>("/auth/me");
    return data;
}

// POST /api/v1/auth/logout -> clears the jwt + refresh cookies server-side
export async function logout(): Promise<void> {
    await apiClient.post("/auth/logout", {});
}
