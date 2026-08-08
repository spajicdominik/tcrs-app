import type { UserResponse } from "../../users/types/UserResponse.ts";

// antd's Transfer requires a `key` on every record; the rest is the user from the API
export type PlayerRow = UserResponse & { key: string };
