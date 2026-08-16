import React from 'react';
import { AdminStatistics } from '../types';
import StatCard from '../../../components/ui/StatCard';

interface StatisticsCardsProps {
  statistics: AdminStatistics | null;
}

const StatisticsCards: React.FC<StatisticsCardsProps> = ({ statistics }) => {
  if (!statistics) {
    return null;
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <StatCard
        title="Total Users"
        value={statistics.totalUsers.toLocaleString()}
        color="blue"
      />
      <StatCard
        title="Total Admins"
        value={statistics.totalAdmins}
        color="purple"
      />
      <StatCard
        title="Active Alerts"
        value={statistics.activeAlerts}
        color="red"
      />
      <StatCard
        title="Climate Data Points"
        value={statistics.totalClimateDataPoints.toLocaleString()}
        color="green"
      />
    </div>
  );
};

export default StatisticsCards;
