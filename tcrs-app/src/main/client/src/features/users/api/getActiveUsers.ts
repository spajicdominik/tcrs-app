import { apiClient } from "../../../lib/apiClient";
import type {UserResponse} from "../types/UserResponse.ts";

export async function getActiveUsers(): Promise<UserResponse[]> {
    const response = await apiClient.get<UserResponse[]>("/users");
    return response.data;
}
