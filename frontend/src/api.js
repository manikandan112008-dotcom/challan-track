const API_ROOT = import.meta.env.VITE_API_BASE_URL || '/api';

async function request(path, options = {}) {
  const response = await fetch(`${API_ROOT}${path}`, {
    ...options,
    headers: {
      ...(options.body ? { 'Content-Type': 'application/json' } : {}),
      ...options.headers,
    },
  });

  if (!response.ok) {
    let message = `Request failed (${response.status})`;
    try {
      const body = await response.json();
      message = body.message || body.error || message;
    } catch {
      // Keep the status-based message when the API returns no JSON body.
    }
    throw new Error(message);
  }

  if (response.status === 204) return null;
  return response.json();
}

const send = (method, body) => ({ method, body: JSON.stringify(body) });

export const api = {
  getVehicles: () => request('/vehicles'),
  getVehicle: (id) => request(`/vehicles/${id}`),
  createVehicle: (body) => request('/vehicles', send('POST', body)),
  updateVehicle: (id, body) => request(`/vehicles/${id}`, send('PUT', body)),
  deleteVehicle: (id) => request(`/vehicles/${id}`, { method: 'DELETE' }),
  getViolationTypes: () => request('/violation-types'),
  createViolationType: (body) => request('/violation-types', send('POST', body)),
  updateViolationType: (id, body) => request(`/violation-types/${id}`, send('PUT', body)),
  deleteViolationType: (id) => request(`/violation-types/${id}`, { method: 'DELETE' }),
  createChallan: (body) => request('/challans', send('POST', body)),
  getVehicleChallans: (vehicleId) => request(`/vehicles/${vehicleId}/challans`),
  getRepeatViolations: (vehicleId) => request(`/vehicles/${vehicleId}/repeat-violations`),
  getOverdueChallans: () => request('/challans/unpaid/older-than-30-days'),
  createPayment: (body) => request('/payments', send('POST', body)),
};

export async function loadWorkspace() {
  const [vehicles, violationTypes] = await Promise.all([
    api.getVehicles(),
    api.getViolationTypes(),
  ]);
  const historyResults = await Promise.allSettled(
    vehicles.map((vehicle) => api.getVehicleChallans(vehicle.id)),
  );
  const challans = historyResults.flatMap((result) =>
    result.status === 'fulfilled' ? result.value : [],
  );
  const historyErrors = historyResults.filter((result) => result.status === 'rejected').length;
  return { vehicles, violationTypes, challans, historyErrors };
}