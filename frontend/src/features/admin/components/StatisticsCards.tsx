import React from 'react';
import { useNavigate } from 'react-router-dom';
import { AdminStatistics } from '../types';
import StatCard from '../../../components/ui/StatCard';

interface StatisticsCardsProps {
  statistics: AdminStatistics | null;
}

const StatisticsCards: React.FC<StatisticsCardsProps> = ({ statistics }) => {
  const navigate = useNavigate();

  if (!statistics) {
    return null;
  }

  const scrollToUsers = () => {
    const usersSection = document.getElementById('users-management');

    if (usersSection) {
      usersSection.scrollIntoView({
        behavior: 'smooth',
        block: 'start'
      });
    }
  };

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <StatCard
        title="Total Users"
        value={statistics.totalUsers.toLocaleString()}
        color="blue"
        onClick={scrollToUsers}
      />

      <StatCard
        title="Total Admins"
        value={statistics.totalAdmins}
        color="purple"
        onClick={scrollToUsers}
      />

      <StatCard
        title="Active Alerts"
        value={statistics.activeAlerts}
        color="red"
        onClick={() => navigate('/alerts')}
      />

      <StatCard
        title="Climate Data Points"
        value={statistics.totalClimateDataPoints.toLocaleString()}
        color="green"
        onClick={() => navigate('/dashboard')}
      />
    </div>
  );
};

export default StatisticsCards;