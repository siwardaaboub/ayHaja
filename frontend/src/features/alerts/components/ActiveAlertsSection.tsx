import React, { useEffect, useState } from 'react';
import AlertCard from '../components/AlertCard';
import LoadingSpinner from '../../../components/ui/LoadingSpinner';
import { ClimateAlert } from '../types';
import { alertApi } from '../services/api';

const ActiveAlertsSection: React.FC = () => {
  const [alerts, setAlerts] = useState<ClimateAlert[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchAlerts();
  }, []);

  const fetchAlerts = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await alertApi.getActiveAlerts();
      setAlerts(data);
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
      setAlerts(alerts.map(alert =>
        alert.id === alertId ? { ...alert, isAcknowledged: true } : alert
      ));
    } catch (err) {
      console.error('Failed to acknowledge alert:', err);
    }
  };

  const handleResolve = async (alertId: string) => {
    try {
      await alertApi.resolveAlert(alertId);
      setAlerts(alerts.map(alert =>
        alert.id === alertId ? { ...alert, isResolved: true, resolvedAt: new Date().toISOString() } : alert
      ));
    } catch (err) {
      console.error('Failed to resolve alert:', err);
    }
  };

  if (loading) {
    return (
      <div className="bg-white rounded-xl shadow-md p-6 mb-8">
        <h2 className="text-2xl font-bold text-gray-900 mb-4">Active Alerts</h2>
        <div className="flex justify-center py-8">
          <LoadingSpinner size="lg" />
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-white rounded-xl shadow-md p-6 mb-8">
        <h2 className="text-2xl font-bold text-gray-900 mb-4">Active Alerts</h2>
        <div className="text-center py-8">
          <p className="text-red-600 mb-4">{error}</p>
          <button
            onClick={fetchAlerts}
            className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  const activeAlerts = alerts.filter(alert => !alert.isResolved);

  return (
    <div className="bg-white rounded-xl shadow-md p-6 mb-8">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-2xl font-bold text-gray-900">Active Alerts</h2>
        <span className="bg-red-100 text-red-800 text-xs font-medium px-2.5 py-0.5 rounded-full">
          {activeAlerts.length} Active
        </span>
      </div>

      {activeAlerts.length === 0 ? (
        <div className="text-center py-8">
          <div className="text-6xl mb-4">✅</div>
          <h3 className="text-lg font-semibold text-gray-700 mb-2">No Active Alerts</h3>
          <p className="text-gray-500">All climate health risks are currently within normal levels.</p>
        </div>
      ) : (
        <div className="space-y-4">
          {activeAlerts.map(alert => (
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
  );
};

export default ActiveAlertsSection;
