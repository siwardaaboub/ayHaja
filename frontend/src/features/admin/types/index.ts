export interface AdminUser {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: 'USER' | 'ADMIN';
  createdAt: string;
  updatedAt: string;
  hasHealthProfile: boolean;
}

export interface AdminStatistics {
  totalUsers: number;
  totalAdmins: number;
  totalGovernorates: number;
  totalClimateDataPoints: number;
  totalAlerts: number;
  activeAlerts: number;
  totalConversations: number;
  totalHealthProfiles: number;
  avgTemperature: number;
  avgHumidity: number;
  alertsByType: Record<string, number>;
  usersByRole: Record<string, number>;
  dataByGovernorate: Record<string, number>;
  alertsBySeverity: Record<string, number>;
}

export interface AdminLog {
  id: string;
  adminId: string;
  adminEmail: string;
  action: string;
  entityType: string;
  entityId: string | null;
  details: string;
  ipAddress: string;
  userAgent: string;
  createdAt: string;
}

export interface AlertRule {
  id: string;
  ruleName: string;
  alertType: 'HEATWAVE' | 'DROUGHT' | 'FIRE' | 'STORM' | 'FLOOD' | 'AIR_QUALITY';
  conditionField: string;
  operator: string;
  thresholdValue: number;
  durationMinutes: number;
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  isActive: boolean;
  cooldownMinutes: number;
  createdAt: string;
  updatedAt: string;
}
