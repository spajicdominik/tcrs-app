import { apiClient } from "../../../lib/apiClient";
import type { ReservationResponse } from "../types/ReservationResponse";

export async function getActiveReservations(): Promise<ReservationResponse[]> {
  const response = await apiClient.get<ReservationResponse[]>("/reservation");
  return response.data;
}
