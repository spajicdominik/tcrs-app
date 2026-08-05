// Shape returned by GET /api/v1/auth/me (all fields populated for the current user).
export interface CurrentUser {
    id : number,
    name : string,
    email : string,
    role : string,
    phoneNumber : string | null
}
