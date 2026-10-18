import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../auth/contexts/AuthContext';import StatCard from '../../../components/ui/StatCard';
import { DashboardSummary } from '../../../types';

interface DashboardSummaryProps {
  summary: DashboardSummary;
}

const DashboardSummarySection: React.FC<DashboardSummaryProps> = ({ summary }) => {
  const navigate = useNavigate();
  const { user } = useAuth();
const isAdmin = user?.role === 'ADMIN';

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <StatCard
  title="Total Users"
  value={summary.totalUsers.toLocaleString()}
  color="blue"
  onClick={isAdmin ? () => navigate('/admin') : undefined}
/>

      <StatCard
  title="Governorates"
  value={summary.totalGovernorates}
  color="green"
  onClick={() => {
    document.getElementById('governorates-map')?.scrollIntoView({
      behavior: 'smooth',
      block: 'start'
    });
  }}
/>

      <StatCard
        title="Active Alerts"
        value={summary.activeAlerts}
        color="red"
        onClick={() => navigate('/alerts')}
      />

      <StatCard
  title="High Risk Areas"
  value={summary.highRiskAreas}
  color="orange"
  onClick={() => {
    document.getElementById('risk-distribution')?.scrollIntoView({
      behavior: 'smooth',
      block: 'start'
    });
  }}
/>
    </div>
  );
};

export default DashboardSummarySection;