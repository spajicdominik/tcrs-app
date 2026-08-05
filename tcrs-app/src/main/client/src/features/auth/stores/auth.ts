import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import type { CurrentUser } from "../types/CurrentUser.ts";
import { getCurrentUser, logout } from "../api/getCurrentUser.ts";

interface AuthState {
    user: CurrentUser | null;
    // 'idle'    -> /me has not been attempted yet
    // 'loading' -> /me in flight; routes must WAIT, not redirect
    status: 'idle' | 'loading' | 'authenticated' | 'anonymous';
}

const initialState: AuthState = {
    user: null,
    status: 'idle',
};

/** Resolves the session from the HttpOnly cookie. A 401 simply means "not logged in". */
export const fetchCurrentUser = createAsyncThunk(
    'auth/fetchCurrentUser',
    async () => await getCurrentUser(),
);

/** Clears the cookies server-side; local state is cleared either way. */
export const logoutUser = createAsyncThunk(
    'auth/logout',
    async () => await logout(),
);

const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        // used by the 401 interceptor when a token expires mid-session
        clearUser(state) {
            state.user = null;
            state.status = 'anonymous';
        },
    },
    extraReducers: (builder) => {
        builder
            .addCase(fetchCurrentUser.pending, (state) => {
                state.status = 'loading';
            })
            .addCase(fetchCurrentUser.fulfilled, (state, action) => {
                state.user = action.payload;
                state.status = 'authenticated';
            })
            .addCase(fetchCurrentUser.rejected, (state) => {
                state.user = null;
                state.status = 'anonymous';
            })
            // clear local state on logout whether or not the request succeeded
            .addCase(logoutUser.fulfilled, (state) => {
                state.user = null;
                state.status = 'anonymous';
            })
            .addCase(logoutUser.rejected, (state) => {
                state.user = null;
                state.status = 'anonymous';
            });
    },
});

export const { clearUser } = authSlice.actions;
export default authSlice.reducer;
