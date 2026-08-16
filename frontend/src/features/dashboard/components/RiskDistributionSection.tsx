import React from 'react';
import RiskDistributionChart from '../../../components/charts/RiskDistributionChart';
import { RiskDistribution } from '../../../types';

interface RiskDistributionProps {
  data: RiskDistribution[];
}

const RiskDistributionSection: React.FC<RiskDistributionProps> = ({ data }) => {
  if (!data || data.length === 0) {
    return (
      <div className="bg-white rounded-xl shadow-md p-6 mb-8">
        <h3 className="text-lg font-semibold text-gray-700 mb-4">Risk Distribution</h3>
        <p className="text-gray-500">No risk data available</p>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-xl shadow-md p-6 mb-8">
      <h3 className="text-lg font-semibold text-gray-700 mb-4">Risk Distribution by Governorate</h3>
      <div className="h-80">
        <RiskDistributionChart data={data} />
      </div>
    </div>
  );
};

export default RiskDistributionSection;
