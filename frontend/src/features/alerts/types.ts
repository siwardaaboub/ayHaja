import { AlertRule, ClimateAlert, AlertSubscription } from '../../types';

export interface AlertType {
  HEATWAVE: 'HEATWAVE';
  DROUGHT: 'DROUGHT';
  FIRE: 'FIRE';
  STORM: 'STORM';
  FLOOD: 'FLOOD';
  AIR_QUALITY: 'AIR_QUALITY';
}

export interface Severity {
  LOW: 'LOW';
  MEDIUM: 'MEDIUM';
  HIGH: 'HIGH';
  CRITICAL: 'CRITICAL';
}

export interface AlertRule {
  id: string;
  ruleName: string;
  alertType: AlertType[keyof AlertType];
  conditionField: string;
  operator: string;
  thresholdValue: number;
  durationMinutes: number;
  severity: Severity[keyof Severity];
  isActive: boolean;
  cooldownMinutes: number;
  createdAt: string;
  updatedAt: string;
}

export interface ClimateAlert {
  id: string;
  governorateId: string;
  governorateName: string;
  alertType: AlertType[keyof AlertType];
  severity: Severity[keyof Severity];
  title: string;
  description: string;
  currentValue: number;
  thresholdValue: number;
  triggeredAt: string;
  expiresAt: string;
  isAcknowledged: boolean;
  isResolved: boolean;
  resolvedAt: string | null;
  healthRecommendations: string;
  createdAt: string;
  updatedAt: string;
}

export interface AlertSubscription {
  id: string;
  userId: string;
  governorateId: string | null;
  alertType: AlertType[keyof AlertType];
  minSeverity: Severity[keyof Severity];
  isActive: boolean;
  notificationMethod: string;
  createdAt: string;
}
