import React from 'react';
import TemperatureChart from '../../../components/charts/TemperatureChart';
import { HealthTrend } from '../../../types';

interface HealthTrendsProps {
  trend: HealthTrend | null;
  loading: boolean;
}

const HealthTrendsSection: React.FC<HealthTrendsProps> = ({ trend, loading }) => {
  if (loading) {
    return (
      <div className="bg-white rounded-xl shadow-md p-6 mb-8">
        <h3 className="text-lg font-semibold text-gray-700 mb-4">Health Trends</h3>
        <div className="h-80 flex items-center justify-center">
          <p className="text-gray-500">Loading trends...</p>
        </div>
      </div>
    );
  }

  if (!trend || !trend.dataPoints || trend.dataPoints.length === 0) {
    return (
      <div className="bg-white rounded-xl shadow-md p-6 mb-8">
        <h3 className="text-lg font-semibold text-gray-700 mb-4">Health Trends</h3>
        <p className="text-gray-500">No trend data available</p>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-xl shadow-md p-6 mb-8">
      <h3 className="text-lg font-semibold text-gray-700 mb-4">
        Health Trends - {trend.governorateName}
      </h3>
      <div className="h-80">
        <TemperatureChart data={trend.dataPoints} />
      </div>
    </div>
  );
};

export default HealthTrendsSection;
