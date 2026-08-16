import React, { useEffect, useState } from 'react';
import ActiveAlertsSection from '../components/ActiveAlertsSection';
import AlertCard from '../components/AlertCard';
import LoadingSpinner from '../../../components/ui/LoadingSpinner';
import { ClimateAlert } from '../types';
import { alertApi } from '../services/api';

const AlertsPage: React.FC = () => {
  const [allAlerts, setAllAlerts] = useState<ClimateAlert[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filter, setFilter] = useState<'all' | 'active' | 'resolved'>('active');

  useEffect(() => {
    fetchAllAlerts();
  }, []);

  const fetchAllAlerts = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await alertApi.getActiveAlerts();
      setAllAlerts(data);
    } catch (err) {
      setError('Failed to load alerts. Please try again.');
      console.error('Failed to fetch alerts:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleAcknowledge = async (alertId: string) => {
    try {
      await alertApi.acknowledgeAlert(alertId);
      setAllAlerts(allAlerts.map(alert =>
        alert.id === alertId ? { ...alert, isAcknowledged: true } : alert
      ));
    } catch (err) {
      console.error('Failed to acknowledge alert:', err);
    }
  };

  const handleResolve = async (alertId: string) => {
    try {
      await alertApi.resolveAlert(alertId);
      setAllAlerts(allAlerts.map(alert =>
        alert.id === alertId ? { ...alert, isResolved: true, resolvedAt: new Date().toISOString() } : alert
      ));
    } catch (err) {
      console.error('Failed to resolve alert:', err);
    }
  };

  const filteredAlerts = allAlerts.filter(alert => {
    if (filter === 'active') return !alert.isResolved;
    if (filter === 'resolved') return alert.isResolved;
    return true;
  });

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 py-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-center py-12">
            <LoadingSpinner size="lg" />
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 py-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center py-12">
            <p className="text-red-600 mb-4">{error}</p>
            <button
              onClick={fetchAllAlerts}
              className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
            >
              Retry
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Climate Health Alerts</h1>
          <p className="text-gray-600 mt-2">
            Monitor and manage climate-related health alerts across Tunisia
          </p>
        </div>

        <div className="mb-6 flex space-x-4">
          <button
            onClick={() => setFilter('active')}
            className={`px-4 py-2 rounded-lg font-medium transition-colors ${
              filter === 'active'
                ? 'bg-red-600 text-white'
                : 'bg-white text-gray-700 hover:bg-gray-100'
            }`}
          >
            Active Alerts
          </button>
          <button
            onClick={() => setFilter('resolved')}
            className={`px-4 py-2 rounded-lg font-medium transition-colors ${
              filter === 'resolved'
                ? 'bg-green-600 text-white'
                : 'bg-white text-gray-700 hover:bg-gray-100'
            }`}
          >
            Resolved Alerts
          </button>
          <button
            onClick={() => setFilter('all')}
            className={`px-4 py-2 rounded-lg font-medium transition-colors ${
              filter === 'all'
                ? 'bg-blue-600 text-white'
                : 'bg-white text-gray-700 hover:bg-gray-100'
            }`}
          >
            All Alerts
          </button>
        </div>

        <ActiveAlertsSection />

        {filteredAlerts.length > 0 && (
          <div className="space-y-4">
            {filteredAlerts.map(alert => (
              <AlertCard
                key={alert.id}
                alert={alert}
                onAcknowledge={handleAcknowledge}
                onResolve={handleResolve}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default AlertsPage;
