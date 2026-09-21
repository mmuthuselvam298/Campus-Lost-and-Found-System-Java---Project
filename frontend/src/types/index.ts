export type Role = 'ROLE_STUDENT' | 'ROLE_STAFF' | 'ROLE_ADMIN';

export interface User {
  id: number;
  email: string;
  fullName: string;
  role: Role;
  studentStaffId?: string;
  department?: string;
}

export interface AuthState {
  token: string | null;
  user: User | null;
}

export interface CampusLocation {
  id: number;
  name: string;
  zone: string;
  description: string;
  floorCount: number;
  commonItems?: string;
  mapLatitude?: number;
  mapLongitude?: number;
}

export interface ItemCategory {
  id: number;
  name: string;
  icon: string;
  description: string;
}

export interface FoundReport {
  id: number;
  referenceId: string;
  finderId?: number;
  finderName: string;
  title: string;
  category: string;
  color?: string;
  brand?: string;
  material?: string;
  distinctiveFeatures?: string;
  visibleText?: string;
  publicDescription?: string;
  hasPrivateDetails: boolean;
  privateVerificationDetails?: string | null;
  locationId: number;
  locationName: string;
  locationZone?: string;
  specificArea?: string;
  foundDate: string;
  possessionStatus: 'FINDER_HOLDING' | 'HANDED_TO_OFFICE' | 'STORED_IN_OFFICE';
  status: 'SUBMITTED' | 'VERIFIED' | 'CLAIM_PENDING' | 'READY_FOR_PICKUP' | 'COLLECTED' | 'RETURNED' | 'EXPIRED';
  storageLocation?: string;
  primaryImageUrl?: string;
  thumbnailImageUrl?: string;
  aiConfidence?: number;
  createdAt: string;
}

export interface LostReport {
  id: number;
  referenceId: string;
  ownerId?: number;
  ownerName: string;
  title: string;
  category: string;
  color?: string;
  brand?: string;
  distinctiveFeatures?: string;
  description: string;
  locationId: number;
  locationName: string;
  locationZone?: string;
  specificArea?: string;
  lostDate: string;
  status: string;
  referenceImageUrl?: string;
  rewardInfo?: string;
  contactPreference?: string;
  createdAt: string;
}

export interface AiVisionAnalysis {
  category: string;
  subcategory: string;
  color: string;
  material: string;
  brand: string;
  visibleText: string;
  distinctiveFeatures: string;
  observedFeatures: string[];
  inferredFeatures: string[];
  confidenceScore: number;
  confidenceLevel: string;
  verificationQuestions: string[];
  locationSuggestion: string;
  locationReason: string;
  imageQuality: {
    adequate: boolean;
    lightingCondition: string;
    clarity: string;
    suggestion: string;
  };
}

export interface MatchCandidate {
  id: number;
  lostReportId: number;
  lostReferenceId: string;
  lostTitle: string;
  lostCategory: string;
  lostLocation: string;
  lostDate: string;
  lostImageUrl?: string;

  foundReportId: number;
  foundReferenceId: string;
  foundTitle: string;
  foundCategory: string;
  foundLocation: string;
  foundDate: string;
  foundImageUrl?: string;

  overallScore: number;
  categoryScore: number;
  colorScore: number;
  brandScore: number;
  locationScore: number;
  timeScore: number;
  semanticScore: number;
  matchReasons: string[];
  status: string;
  createdAt: string;
}

export interface Claim {
  id: number;
  foundReportId: number;
  foundReferenceId: string;
  foundTitle: string;
  foundCategory: string;
  foundImageUrl?: string;
  claimantId: number;
  claimantName: string;
  claimantEmail: string;
  claimantAnswers: string;
  consistencyScore: 'HIGH' | 'MEDIUM' | 'LOW';
  consistencyAnalysis: string;
  status: 'SUBMITTED' | 'UNDER_REVIEW' | 'VERIFICATION_REQUIRED' | 'APPROVED' | 'REJECTED' | 'COLLECTED';
  adminNotes?: string;
  createdAt: string;
  pickupScheduled: boolean;
  pickupAppointmentId?: number;
}

export interface PickupAppointment {
  id: number;
  claimId: number;
  itemTitle: string;
  itemCategory: string;
  itemReferenceId: string;
  itemImageUrl?: string;
  storageLocation?: string;
  scheduledDate: string;
  timeSlot: string;
  pickupLocation: string;
  qrToken: string;
  qrCodeBase64?: string;
  otpCode: string;
  status: string;
  receiptNumber?: string;
  collectedAt?: string;
}

export interface CollectionReceipt {
  receiptNumber: string;
  itemReferenceId: string;
  itemTitle: string;
  category: string;
  claimantName: string;
  claimantEmail: string;
  claimantStudentId: string;
  staffName: string;
  pickupLocation: string;
  storageLocation: string;
  collectedAt: string;
  verificationMethod: string;
  digitalSignature: string;
}

export interface NotificationItem {
  id: number;
  title: string;
  message: string;
  type: string;
  linkUrl?: string;
  isRead: boolean;
  createdAt: string;
}

export interface AdminStats {
  activeLostReports: number;
  activeFoundReports: number;
  potentialMatches: number;
  pendingClaims: number;
  itemsReturned: number;
  itemsAwaitingPickup: number;
  recoveryRatePercentage: number;
  averageTimeToReturnDays: number;
  reportsByLocation: Record<string, number>;
  reportsByCategory: Record<string, number>;
  recentActivities: Array<{
    id: number;
    actorEmail: string;
    action: string;
    entityType: string;
    entityId: string;
    details: string;
    timestamp: string;
  }>;
}
