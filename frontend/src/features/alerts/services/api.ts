import axios from 'axios';
import { AlertRule, ClimateAlert, AlertSubscription } from './types';

const apiClient = axios.create({
  baseURL: '/api/alerts',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('Alert API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export const alertApi = {
  getAllAlertRules: async (): Promise<AlertRule[]> => {
    const response = await apiClient.get<AlertRule[]>('/rules');
    return response.data;
  },

  getAlertRuleById: async (id: string): Promise<AlertRule> => {
    const response = await apiClient.get<AlertRule>(`/rules/${id}`);
    return response.data;
  },

  createAlertRule: async (rule: Partial<AlertRule>): Promise<AlertRule> => {
    const response = await apiClient.post<AlertRule>('/rules', rule);
    return response.data;
  },

  updateAlertRule: async (id: string, rule: Partial<AlertRule>): Promise<AlertRule> => {
    const response = await apiClient.put<AlertRule>(`/rules/${id}`, rule);
    return response.data;
  },

  deleteAlertRule: async (id: string): Promise<void> => {
    await apiClient.delete(`/rules/${id}`);
  },

  getActiveAlerts: async (): Promise<ClimateAlert[]> => {
    const response = await apiClient.get<ClimateAlert[]>('/active');
    return response.data;
  },

  getAlertsByGovernorate: async (governorateId: string): Promise<ClimateAlert[]> => {
    const response = await apiClient.get<ClimateAlert[]>(`/governorate/${governorateId}`);
    return response.data;
  },

  acknowledgeAlert: async (alertId: string): Promise<ClimateAlert> => {
    const response = await apiClient.put<ClimateAlert>(`/${alertId}/acknowledge`);
    return response.data;
  },

  resolveAlert: async (alertId: string): Promise<ClimateAlert> => {
    const response = await apiClient.put<ClimateAlert>(`/${alertId}/resolve`);
    return response.data;
  },

  getUserSubscriptions: async (userId: string): Promise<AlertSubscription[]> => {
    const response = await apiClient.get<AlertSubscription[]>(`/subscriptions/user/${userId}`);
    return response.data;
  },

  createSubscription: async (subscription: Partial<AlertSubscription>): Promise<AlertSubscription> => {
    const response = await apiClient.post<AlertSubscription>('/subscriptions', subscription);
    return response.data;
  },

  getAlertStatistics: async (): Promise<Record<string, any>> => {
    const response = await apiClient.get<Record<string, any>>('/statistics');
    return response.data;
  }
};

export default apiClient;
