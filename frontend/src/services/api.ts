import {
  User,
  FoundReport,
  LostReport,
  MatchCandidate,
  Claim,
  PickupAppointment,
  CollectionReceipt,
  NotificationItem,
  AdminStats,
  CampusLocation,
  ItemCategory,
  AiVisionAnalysis
} from '../types';

const API_BASE = '/api';

// Helper to get auth token
function getAuthHeader(): HeadersInit {
  const token = localStorage.getItem('cf_token');
  return token ? { Authorization: `Bearer ${token}` } : {};
}

export const api = {
  // Authentication
  async login(email: string, password: string): Promise<{ token: string; user: User }> {
    const res = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Login failed');
    }
    const data = await res.json();
    localStorage.setItem('cf_token', data.token);
    return {
      token: data.token,
      user: {
        id: data.id,
        email: data.email,
        fullName: data.fullName,
        role: data.role,
        studentStaffId: data.studentStaffId,
        department: data.department
      }
    };
  },

  async register(userData: any): Promise<{ token: string; user: User }> {
    const res = await fetch(`${API_BASE}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(userData)
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Registration failed');
    }
    const data = await res.json();
    localStorage.setItem('cf_token', data.token);
    return {
      token: data.token,
      user: {
        id: data.id,
        email: data.email,
        fullName: data.fullName,
        role: data.role,
        studentStaffId: data.studentStaffId,
        department: data.department
      }
    };
  },

  async getCurrentUser(): Promise<User | null> {
    const token = localStorage.getItem('cf_token');
    if (!token) return null;
    const res = await fetch(`${API_BASE}/auth/me`, {
      headers: getAuthHeader()
    });
    if (!res.ok) return null;
    const data = await res.json();
    return {
      id: data.id,
      email: data.email,
      fullName: data.fullName,
      role: data.role,
      studentStaffId: data.studentStaffId,
      department: data.department
    };
  },

  logout() {
    localStorage.removeItem('cf_token');
  },

  // AI Services
  async analyzeImage(file: File, contextHint?: string): Promise<{ analysis: AiVisionAnalysis; imageUrl: string; thumbnailUrl: string }> {
    const formData = new FormData();
    formData.append('file', file);
    if (contextHint) formData.append('contextHint', contextHint);

    const res = await fetch(`${API_BASE}/ai/analyze-image`, {
      method: 'POST',
      headers: getAuthHeader(),
      body: formData
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.error || 'Failed to analyze image with AI');
    }
    return res.json();
  },

  async assistDescription(text: string): Promise<any> {
    const res = await fetch(`${API_BASE}/ai/assist-description`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...getAuthHeader() },
      body: JSON.stringify({ text })
    });
    if (!res.ok) throw new Error('AI description assistance failed');
    return res.json();
  },

  // Found Reports
  async getFoundReports(params?: { category?: string; locationId?: number; query?: string; status?: string }): Promise<FoundReport[]> {
    const query = new URLSearchParams();
    if (params?.category) query.set('category', params.category);
    if (params?.locationId) query.set('locationId', params.locationId.toString());
    if (params?.query) query.set('query', params.query);
    if (params?.status) query.set('status', params.status);

    const res = await fetch(`${API_BASE}/found?${query.toString()}`, {
      headers: getAuthHeader()
    });
    if (!res.ok) throw new Error('Failed to load found reports');
    return res.json();
  },

  async getFoundReportById(id: number): Promise<FoundReport> {
    const res = await fetch(`${API_BASE}/found/${id}`, {
      headers: getAuthHeader()
    });
    if (!res.ok) throw new Error('Found report not found');
    return res.json();
  },

  async createFoundReport(reportData: any): Promise<FoundReport> {
    const res = await fetch(`${API_BASE}/found`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...getAuthHeader() },
      body: JSON.stringify(reportData)
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Failed to submit found report');
    }
    return res.json();
  },

  async getMyFoundReports(): Promise<FoundReport[]> {
    const res = await fetch(`${API_BASE}/found/my`, {
      headers: getAuthHeader()
    });
    if (!res.ok) return [];
    return res.json();
  },

  // Lost Reports
  async getLostReports(params?: { category?: string; locationId?: number; query?: string }): Promise<LostReport[]> {
    const query = new URLSearchParams();
    if (params?.category) query.set('category', params.category);
    if (params?.locationId) query.set('locationId', params.locationId.toString());
    if (params?.query) query.set('query', params.query);

    const res = await fetch(`${API_BASE}/lost?${query.toString()}`, {
      headers: getAuthHeader()
    });
    if (!res.ok) throw new Error('Failed to load lost reports');
    return res.json();
  },

  async createLostReport(reportData: any): Promise<LostReport> {
    const res = await fetch(`${API_BASE}/lost`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...getAuthHeader() },
      body: JSON.stringify(reportData)
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Failed to submit lost report');
    }
    return res.json();
  },

  async getMyLostReports(): Promise<LostReport[]> {
    const res = await fetch(`${API_BASE}/lost/my`, {
      headers: getAuthHeader()
    });
    if (!res.ok) return [];
    return res.json();
  },

  // Matches
  async getMatchesForLostReport(lostReportId: number): Promise<MatchCandidate[]> {
    const res = await fetch(`${API_BASE}/matches/lost/${lostReportId}`, {
      headers: getAuthHeader()
    });
    if (!res.ok) return [];
    return res.json();
  },

  async getMyMatches(): Promise<MatchCandidate[]> {
    const res = await fetch(`${API_BASE}/matches/my`, {
      headers: getAuthHeader()
    });
    if (!res.ok) return [];
    return res.json();
  },

  async getAllMatches(): Promise<MatchCandidate[]> {
    const res = await fetch(`${API_BASE}/matches/all`, {
      headers: getAuthHeader()
    });
    if (!res.ok) return [];
    return res.json();
  },

  // Claims
  async submitClaim(claimData: { foundReportId: number; lostReportId?: number; claimantAnswers: string }): Promise<Claim> {
    const res = await fetch(`${API_BASE}/claims`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...getAuthHeader() },
      body: JSON.stringify(claimData)
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Failed to submit claim');
    }
    return res.json();
  },

  async getMyClaims(): Promise<Claim[]> {
    const res = await fetch(`${API_BASE}/claims/my`, {
      headers: getAuthHeader()
    });
    if (!res.ok) return [];
    return res.json();
  },

  async getAllClaims(): Promise<Claim[]> {
    const res = await fetch(`${API_BASE}/claims/admin`, {
      headers: getAuthHeader()
    });
    if (!res.ok) return [];
    return res.json();
  },

  async reviewClaim(claimId: number, reviewData: { status: string; adminNotes?: string; storageLocation?: string }): Promise<Claim> {
    const res = await fetch(`${API_BASE}/claims/${claimId}/review`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...getAuthHeader() },
      body: JSON.stringify(reviewData)
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Failed to review claim');
    }
    return res.json();
  },

  // Pickup Appointments
  async schedulePickup(pickupData: { claimId: number; scheduledDate: string; timeSlot: string; pickupLocation?: string }): Promise<PickupAppointment> {
    const res = await fetch(`${API_BASE}/pickup/schedule`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...getAuthHeader() },
      body: JSON.stringify(pickupData)
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Failed to schedule pickup');
    }
    return res.json();
  },

  async getMyPickups(): Promise<PickupAppointment[]> {
    const res = await fetch(`${API_BASE}/pickup/my`, {
      headers: getAuthHeader()
    });
    if (!res.ok) return [];
    return res.json();
  },

  async getPickupByClaim(claimId: number): Promise<PickupAppointment | null> {
    const res = await fetch(`${API_BASE}/pickup/claim/${claimId}`, {
      headers: getAuthHeader()
    });
    if (!res.ok) return null;
    return res.json();
  },

  async getPendingPickups(): Promise<PickupAppointment[]> {
    const res = await fetch(`${API_BASE}/pickup/pending`, {
      headers: getAuthHeader()
    });
    if (!res.ok) return [];
    return res.json();
  },

  async verifyPickup(verifyData: { qrToken?: string; otpCode?: string; staffNotes?: string }): Promise<CollectionReceipt> {
    const res = await fetch(`${API_BASE}/pickup/verify`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...getAuthHeader() },
      body: JSON.stringify(verifyData)
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Pickup verification failed');
    }
    return res.json();
  },

  async getReceipt(appointmentId: number): Promise<CollectionReceipt> {
    const res = await fetch(`${API_BASE}/pickup/receipt/${appointmentId}`, {
      headers: getAuthHeader()
    });
    if (!res.ok) throw new Error('Receipt not found');
    return res.json();
  },

  // Notifications
  async getNotifications(): Promise<NotificationItem[]> {
    const res = await fetch(`${API_BASE}/notifications`, {
      headers: getAuthHeader()
    });
    if (!res.ok) return [];
    return res.json();
  },

  async getUnreadNotificationCount(): Promise<number> {
    const res = await fetch(`${API_BASE}/notifications/unread-count`, {
      headers: getAuthHeader()
    });
    if (!res.ok) return 0;
    const data = await res.json();
    return data.unreadCount || 0;
  },

  async markNotificationAsRead(id: number): Promise<void> {
    await fetch(`${API_BASE}/notifications/${id}/read`, {
      method: 'PUT',
      headers: getAuthHeader()
    });
  },

  // Metadata
  async getLocations(): Promise<CampusLocation[]> {
    const res = await fetch(`${API_BASE}/locations`);
    if (!res.ok) return [];
    return res.json();
  },

  async getCategories(): Promise<ItemCategory[]> {
    const res = await fetch(`${API_BASE}/categories`);
    if (!res.ok) return [];
    return res.json();
  },

  // Admin
  async getAdminStats(): Promise<AdminStats> {
    const res = await fetch(`${API_BASE}/admin/stats`, {
      headers: getAuthHeader()
    });
    if (!res.ok) throw new Error('Failed to load admin stats');
    return res.json();
  },

  async moderateReport(reportId: number, action: string, notes?: string, storageLocation?: string): Promise<void> {
    const res = await fetch(`${API_BASE}/admin/moderate/${reportId}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...getAuthHeader() },
      body: JSON.stringify({ action, notes, storageLocation })
    });
    if (!res.ok) throw new Error('Report moderation failed');
  }
};
