import React from 'react';
import StatCard from '../../../components/ui/StatCard';
import RiskPieChart from '../../../components/charts/RiskPieChart';
import { DashboardSummary } from '../../../types';

interface DashboardSummaryProps {
  summary: DashboardSummary;
}

const DashboardSummarySection: React.FC<DashboardSummaryProps> = ({ summary }) => {
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <StatCard
        title="Total Users"
        value={summary.totalUsers.toLocaleString()}
        color="blue"
      />
      <StatCard
        title="Governorates"
        value={summary.totalGovernorates}
        color="green"
      />
      <StatCard
        title="Active Alerts"
        value={summary.activeAlerts}
        color="red"
      />
      <StatCard
        title="High Risk Areas"
        value={summary.highRiskAreas}
        color="orange"
      />
    </div>
  );
};

export default DashboardSummarySection;
