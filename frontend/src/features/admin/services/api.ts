import axios from 'axios';
import { AdminUser, AdminStatistics, AdminLog, AlertRule } from '../types';

const apiClient = axios.create({
  baseURL: '/api/admin',
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
    console.error('Admin API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export const adminApi = {
  getAllUsers: async (): Promise<AdminUser[]> => {
    const response = await apiClient.get<AdminUser[]>('/users');
    return response.data;
  },

  assignRole: async (userId: string, role: string): Promise<AdminUser> => {
    const response = await apiClient.put<AdminUser>(`/users/${userId}/role`, {}, {
      params: { role }
    });
    return response.data;
  },

  deleteUser: async (userId: string): Promise<void> => {
    await apiClient.delete(`/users/${userId}`);
  },

  getStatistics: async (): Promise<AdminStatistics> => {
    const response = await apiClient.get<AdminStatistics>('/statistics');
    return response.data;
  },

  getAdminLogs: async (params?: {
    adminId?: string;
    startDate?: string;
    endDate?: string;
  }): Promise<AdminLog[]> => {
    const response = await apiClient.get<AdminLog[]>('/logs', { params });
    return response.data;
  },

  

  getAllAlertRules: async (): Promise<AlertRule[]> => {
    const response = await apiClient.get<AlertRule[]>('/alerts/rules');
    return response.data;
  },

  deleteAlertRule: async (ruleId: string): Promise<void> => {
    await apiClient.delete(`/alerts/rules/${ruleId}`);
  },

  resolveAlert: async (alertId: string): Promise<void> => {
    await apiClient.put(`/alerts/${alertId}/resolve`);
  }
};

export default apiClient;
