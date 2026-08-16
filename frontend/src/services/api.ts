import axios from 'axios';
import {
  DashboardSummary,
  HealthStatistic,
  HealthTrend,
  RiskDistribution
} from '../types';

const apiClient = axios.create({
  baseURL: '/api',
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
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export const dashboardApi = {
  getDashboardSummary: async (): Promise<DashboardSummary> => {
    const response = await apiClient.get<DashboardSummary>('/dashboard/summary');
    return response.data;
  },

  getAllHealthStatistics: async (): Promise<HealthStatistic[]> => {
    const response = await apiClient.get<HealthStatistic[]>('/dashboard/statistics');
    return response.data;
  },

  getHealthStatisticById: async (id: string): Promise<HealthStatistic> => {
    const response = await apiClient.get<HealthStatistic>(`/dashboard/statistics/${id}`);
    return response.data;
  },

  getHealthTrends: async (governorateId: string, days: number = 30): Promise<HealthTrend> => {
    const response = await apiClient.get<HealthTrend>(
      `/dashboard/trends/${governorateId}?days=${days}`
    );
    return response.data;
  },

  getRiskDistribution: async (): Promise<RiskDistribution[]> => {
    const response = await apiClient.get<RiskDistribution[]>('/dashboard/risk-distribution');
    return response.data;
  },

  getActiveRiskAreas: async (): Promise<RiskDistribution[]> => {
    const response = await apiClient.get<RiskDistribution[]>('/dashboard/active-risk-areas');
    return response.data;
  }
};

export default apiClient;
