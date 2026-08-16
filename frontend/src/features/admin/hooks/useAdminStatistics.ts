import { AdminStatistics } from '../types';

export const useAdminStatistics = (data: AdminStatistics | null) => {
  const formatNumber = (num: number) => {
    return new Intl.NumberFormat().format(num);
  };

  const getRiskDistribution = () => {
    if (!data?.alertsBySeverity) return [];
    return Object.entries(data.alertsBySeverity).map(([key, value]) => ({
      name: key,
      value: value as number
    }));
  };

  const getUserDistribution = () => {
    if (!data?.usersByRole) return [];
    return Object.entries(data.usersByRole).map(([key, value]) => ({
      name: key,
      value: value as number
    }));
  };

  return {
    formatNumber,
    getRiskDistribution,
    getUserDistribution
  };
};
