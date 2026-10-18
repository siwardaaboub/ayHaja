export type AlertType =
  | 'HEATWAVE'
  | 'DROUGHT'
  | 'FIRE'
  | 'STORM'
  | 'FLOOD'
  | 'AIR_QUALITY';

export type Severity =
  | 'LOW'
  | 'MEDIUM'
  | 'HIGH'
  | 'CRITICAL';

export interface AlertRule {
  id: string;
  ruleName: string;
  alertType: AlertType;
  conditionField: string;
  operator: string;
  thresholdValue: number;
  durationMinutes: number;
  severity: Severity;
  isActive: boolean;
  cooldownMinutes: number;
  createdAt: string;
  updatedAt: string;
}

export interface ClimateAlert {
  id: string;
  governorateId: string;
  governorateName: string;
  alertType: AlertType;
  severity: Severity;
  title: string;
  description: string;
  currentValue: number;
  thresholdValue: number;
  triggeredAt: string;
  expiresAt: string;
  isAcknowledged: boolean;
  isResolved: boolean;
  resolvedAt: string | null;
  healthRecommendations: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface AlertSubscription {
  id: string;
  userId: string;
  governorateId: string | null;
  alertType: AlertType;
  minSeverity: Severity;
  isActive: boolean;
  notificationMethod: string;
  createdAt: string;
}