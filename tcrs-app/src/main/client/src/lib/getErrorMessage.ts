import axios from "axios";

/**
 * Pulls a human-readable message out of an error, handling every shape the
 * TCRS backend can return:
 *  - ErrorResponse JSON: { status, error, message, ... }        -> message
 *  - bean-validation errors: { email: "...", password: "..." }  -> joined values
 *  - plain string body (e.g. JSON parse / auth errors)          -> the string
 *  - no response at all (network error / server down)           -> axios message
 */
export function getErrorMessage(error: unknown): string {
    if (axios.isAxiosError(error)) {
        const data = error.response?.data;

        if (typeof data === "string" && data.trim()) {
            return data;
        }

        if (data && typeof data === "object") {
            const message = (data as { message?: unknown }).message;
            if (typeof message === "string" && message) {
                return message;
            }
            const fieldErrors = Object.values(data as Record<string, unknown>)
                .filter((v): v is string => typeof v === "string");
            if (fieldErrors.length) {
                return fieldErrors.join(" ");
            }
        }

        return error.message;
    }

    return error instanceof Error ? error.message : "Something went wrong.";
}
