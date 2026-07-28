import { apiClient } from "../../../lib/apiClient";
import type { CreateReservationRequest } from "../types/CreateReservationRequest";
import type { ReservationResponse } from "../types/ReservationResponse";

export async function createReservation(
  request: CreateReservationRequest,
): Promise<ReservationResponse> {
  const response = await apiClient.post<ReservationResponse>(
    "/reservation",
    request,
  );
  return response.data;
}
