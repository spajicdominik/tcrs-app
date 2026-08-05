export interface UserResponse {
    id : number,
    name : string,
    // only sent for the current user (/auth/me); null in listings like /users
    email : string | null,
    role : string | null
}
