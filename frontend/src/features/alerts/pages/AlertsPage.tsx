import React, { useCallback, useEffect, useMemo, useState } from 'react';
import AlertCard from '../components/AlertCard';
import LoadingSpinner from '../../../components/ui/LoadingSpinner';
import { ClimateAlert } from '../types';
import { alertApi } from '../services/api';

type AlertFilter = 'all' | 'active' | 'resolved';

interface AlertStatistics {
  totalAlerts?: number;
  activeAlerts?: number;
  resolvedAlerts?: number;
  alertsBySeverity?: Record<string, number>;
  alertsByType?: Record<string, number>;
}

const AlertsPage: React.FC = () => {
  const [allAlerts, setAllAlerts] = useState<ClimateAlert[]>([]);
  const [statistics, setStatistics] = useState<AlertStatistics | null>(null);

  const [filter, setFilter] = useState<AlertFilter>('active');
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [currentPage, setCurrentPage] = useState(1);
  const alertsPerPage = 10;

  const fetchAlerts = useCallback(async (showLoader = true) => {
    try {
      if (showLoader) {
        setLoading(true);
      } else {
        setRefreshing(true);
      }

      setError(null);

      const [alerts, stats] = await Promise.all([
        alertApi.getAllAlerts(),
        alertApi.getAlertStatistics(),
      ]);

      setAllAlerts(alerts);
      setStatistics(stats);
    } catch (err) {
      console.error('Failed to fetch alerts:', err);
      setError('Failed to load alerts. Please try again.');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    fetchAlerts();
  }, [fetchAlerts]);

  const handleAcknowledge = async (alertId: string) => {
    try {
      const updatedAlert = await alertApi.acknowledgeAlert(alertId);

      setAllAlerts((currentAlerts) =>
        currentAlerts.map((alert) =>
          alert.id === alertId ? updatedAlert : alert
        )
      );

      await fetchAlerts(false);
    } catch (err) {
      console.error('Failed to acknowledge alert:', err);
    }
  };

  const handleResolve = async (alertId: string) => {
    try {
      const updatedAlert = await alertApi.resolveAlert(alertId);

      setAllAlerts((currentAlerts) =>
        currentAlerts.map((alert) =>
          alert.id === alertId ? updatedAlert : alert
        )
      );

      await fetchAlerts(false);
    } catch (err) {
      console.error('Failed to resolve alert:', err);
    }
  };

  const filteredAlerts = useMemo(() => {
    switch (filter) {
      case 'active':
        return allAlerts.filter((alert) => !alert.isResolved);

      case 'resolved':
        return allAlerts.filter((alert) => alert.isResolved);

      case 'all':
      default:
        return allAlerts;
    }
  }, [allAlerts, filter]);

  useEffect(() => {
    setCurrentPage(1);
  }, [filter]);

  const totalPages = Math.max(
    1,
    Math.ceil(filteredAlerts.length / alertsPerPage)
  );

  const paginatedAlerts = useMemo(() => {
    const startIndex = (currentPage - 1) * alertsPerPage;

    return filteredAlerts.slice(
      startIndex,
      startIndex + alertsPerPage
    );
  }, [filteredAlerts, currentPage]);

  useEffect(() => {
    if (currentPage > totalPages) {
      setCurrentPage(totalPages);
    }
  }, [currentPage, totalPages]);

  const totalAlerts =
    statistics?.totalAlerts ?? allAlerts.length;

  const activeAlerts =
    statistics?.activeAlerts ??
    allAlerts.filter((alert) => !alert.isResolved).length;

  const resolvedAlerts =
    statistics?.resolvedAlerts ??
    allAlerts.filter((alert) => alert.isResolved).length;

  const criticalAlerts =
    statistics?.alertsBySeverity?.CRITICAL ??
    allAlerts.filter(
      (alert) =>
        alert.severity === 'CRITICAL' && !alert.isResolved
    ).length;

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 py-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-center py-16">
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
          <div className="rounded-xl bg-white shadow-sm p-8 text-center">
            <div className="text-4xl mb-4">⚠️</div>

            <h2 className="text-lg font-semibold text-gray-900 mb-2">
              Unable to load alerts
            </h2>

            <p className="text-gray-600 mb-6">
              {error}
            </p>

            <button
              type="button"
              onClick={() => fetchAlerts()}
              className="inline-flex items-center justify-center px-5 py-2.5 rounded-lg bg-blue-600 text-white font-medium hover:bg-blue-700 transition-colors"
            >
              Try again
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">

        {/* Header */}
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-8">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">
              Climate Health Alerts
            </h1>

            <p className="text-gray-600 mt-2">
              Monitor and manage climate-related health alerts across Tunisia
            </p>
          </div>

          <button
            type="button"
            onClick={() => fetchAlerts(false)}
            disabled={refreshing}
            className="inline-flex items-center justify-center gap-2 px-4 py-2.5 bg-white border border-gray-200 text-gray-700 rounded-lg font-medium shadow-sm hover:bg-gray-50 disabled:opacity-60 disabled:cursor-not-allowed transition-colors"
          >
            <span className={refreshing ? 'animate-spin' : ''}>
              ↻
            </span>

            {refreshing ? 'Refreshing...' : 'Refresh'}
          </button>
        </div>

        {/* Statistics */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">

          <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-5">
            <p className="text-sm font-medium text-gray-500">
              Total Alerts
            </p>

            <p className="text-3xl font-bold text-gray-900 mt-2">
              {totalAlerts}
            </p>
          </div>

          <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-5">
            <p className="text-sm font-medium text-gray-500">
              Active
            </p>

            <p className="text-3xl font-bold text-orange-600 mt-2">
              {activeAlerts}
            </p>
          </div>

          <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-5">
            <p className="text-sm font-medium text-gray-500">
              Resolved
            </p>

            <p className="text-3xl font-bold text-green-600 mt-2">
              {resolvedAlerts}
            </p>
          </div>

          <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-5">
            <p className="text-sm font-medium text-gray-500">
              Critical
            </p>

            <p className="text-3xl font-bold text-red-600 mt-2">
              {criticalAlerts}
            </p>
          </div>

        </div>

        {/* Filters */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-2 mb-6">
          <div className="flex flex-wrap gap-2">

            <button
              type="button"
              onClick={() => setFilter('active')}
              className={`px-4 py-2.5 rounded-lg font-medium text-sm transition-colors ${
                filter === 'active'
                  ? 'bg-blue-600 text-white'
                  : 'text-gray-600 hover:bg-gray-100'
              }`}
            >
              Active {activeAlerts}
            </button>

            <button
              type="button"
              onClick={() => setFilter('resolved')}
              className={`px-4 py-2.5 rounded-lg font-medium text-sm transition-colors ${
                filter === 'resolved'
                  ? 'bg-green-600 text-white'
                  : 'text-gray-600 hover:bg-gray-100'
              }`}
            >
              Resolved {resolvedAlerts}
            </button>

            <button
              type="button"
              onClick={() => setFilter('all')}
              className={`px-4 py-2.5 rounded-lg font-medium text-sm transition-colors ${
                filter === 'all'
                  ? 'bg-gray-800 text-white'
                  : 'text-gray-600 hover:bg-gray-100'
              }`}
            >
              All {totalAlerts}
            </button>

          </div>
        </div>

        {/* Empty state */}
        {filteredAlerts.length === 0 ? (
          <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-12 text-center">
            <div className="text-5xl mb-4">
              {filter === 'resolved' ? '✓' : '🔔'}
            </div>

            <h2 className="text-lg font-semibold text-gray-900 mb-2">
              {filter === 'active'
                ? 'No active alerts'
                : filter === 'resolved'
                  ? 'No resolved alerts'
                  : 'No alerts found'}
            </h2>

            <p className="text-gray-500">
              {filter === 'active'
                ? 'There are currently no active climate health alerts.'
                : filter === 'resolved'
                  ? 'No alerts have been resolved yet.'
                  : 'There are no climate health alerts to display.'}
            </p>
          </div>
        ) : (
          <>
            {/* Alerts */}
            <div className="space-y-5">
              {paginatedAlerts.map((alert) => (
                <AlertCard
                  key={alert.id}
                  alert={alert}
                  onAcknowledge={handleAcknowledge}
                  onResolve={handleResolve}
                />
              ))}
            </div>

            {/* Pagination */}
            {totalPages > 1 && (
              <div className="flex flex-col sm:flex-row items-center justify-between gap-4 mt-8">

                <p className="text-sm text-gray-500">
                  Showing{' '}
                  {(currentPage - 1) * alertsPerPage + 1}
                  {' '}to{' '}
                  {Math.min(
                    currentPage * alertsPerPage,
                    filteredAlerts.length
                  )}
                  {' '}of {filteredAlerts.length} alerts
                </p>

                <div className="flex items-center gap-2">

                  <button
                    type="button"
                    onClick={() =>
                      setCurrentPage((page) => Math.max(1, page - 1))
                    }
                    disabled={currentPage === 1}
                    className="px-3 py-2 rounded-lg border border-gray-200 bg-white text-gray-700 hover:bg-gray-50 disabled:opacity-40 disabled:cursor-not-allowed"
                  >
                    Previous
                  </button>

                  <span className="px-3 py-2 text-sm text-gray-600">
                    Page {currentPage} of {totalPages}
                  </span>

                  <button
                    type="button"
                    onClick={() =>
                      setCurrentPage((page) =>
                        Math.min(totalPages, page + 1)
                      )
                    }
                    disabled={currentPage === totalPages}
                    className="px-3 py-2 rounded-lg border border-gray-200 bg-white text-gray-700 hover:bg-gray-50 disabled:opacity-40 disabled:cursor-not-allowed"
                  >
                    Next
                  </button>

                </div>
              </div>
            )}
          </>
        )}

      </div>
    </div>
  );
};

export default AlertsPage;