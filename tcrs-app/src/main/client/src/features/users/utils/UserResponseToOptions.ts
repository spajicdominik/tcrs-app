import type {UserResponse} from "../types/UserResponse.ts";
import type {PartnerOptions} from "../../reservations/types/PartnerOptions.ts";

export function userResponseToOptions(userResponse : UserResponse[]) {
    const options : PartnerOptions[] = [];

    for ( const user of userResponse ) {
        const option : PartnerOptions = {
            value : user.id,
            label : user.name
        }
        options.push(option);
    }
    return options;
}