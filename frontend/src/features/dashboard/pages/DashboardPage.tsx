import React, { useEffect, useState } from 'react';
import DashboardSummarySection from '../components/DashboardSummarySection';
import RiskDistributionSection from '../components/RiskDistributionSection';
import HealthTrendsSection from '../components/HealthTrendsSection';
import LoadingSpinner from '../../../components/ui/LoadingSpinner';
import TunisiaMap from '../../map/components/TunisiaMap';
import GovernorateDetails from '../../map/components/GovernorateDetails';
import { DashboardSummary, HealthTrend, RiskDistribution } from '../../../types';
import { dashboardApi } from '../../../services/api';

interface GovernorateDetail {
  governorate: {
    name: string;
    nameAr?: string;
    centerLat: number;
    centerLon: number;
  };
  currentWeather: {
    temperature?: number;
    feelsLike?: number;
    humidity?: number;
    windSpeed?: number;
    windDirection?: number;
    pressure?: number;
    uvIndex?: number;
    precipitation?: number;
    cloudCover?: number;
  };
  forecasts: Array<{
    forecastDate: string;
    temperatureMax?: number;
    temperatureMin?: number;
    precipitation?: number;
    windSpeed?: number;
    uvIndex?: number;
  }>;
}

const DashboardPage: React.FC = () => {
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [riskDistribution, setRiskDistribution] = useState<RiskDistribution[]>([]);
  const [healthTrend, setHealthTrend] = useState<HealthTrend | null>(null);
  const [selectedGovernorate, setSelectedGovernorate] =
    useState<GovernorateDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        setError(null);

        const [summaryData, riskData] = await Promise.all([
          dashboardApi.getDashboardSummary(),
          dashboardApi.getRiskDistribution(),
        ]);

        setSummary(summaryData);
        setRiskDistribution(riskData);

        if (riskData.length > 0) {
          const firstGovernorateId = riskData[0].governorateId;
          const trendData = await dashboardApi.getHealthTrends(
            firstGovernorateId,
            30
          );
          setHealthTrend(trendData);
        }
      } catch (err) {
        setError('Failed to load dashboard data. Please try again.');
        console.error('Dashboard data fetch error:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <p className="text-red-600 mb-4">{error}</p>
          <button
            onClick={() => window.location.reload()}
            className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">
            Climate Health Dashboard
          </h1>
          <p className="text-gray-600 mt-2">
            Real-time monitoring of climate-related health risks across Tunisia
          </p>
        </div>

        {summary && <DashboardSummarySection summary={summary} />}

       <div id="governorates-map" className="mt-8">
  <TunisiaMap
    onGovernorateSelected={setSelectedGovernorate}
  />
</div>

        <div className="mt-8">
          <GovernorateDetails detail={selectedGovernorate} />
        </div>

        <div
  id="risk-distribution"
  className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8 mt-8"
>
  <RiskDistributionSection data={riskDistribution} />
  <HealthTrendsSection trend={healthTrend} loading={false} />
</div>
      </div>
    </div>
  );
};

export default DashboardPage;