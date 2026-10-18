import React from 'react';
import { ClimateAlert } from '../types';
import RiskBadge from '../../../components/ui/RiskBadge';

interface AlertCardProps {
  alert: ClimateAlert;
  onAcknowledge: (id: string) => void;
  onResolve: (id: string) => void;
}

const AlertCard: React.FC<AlertCardProps> = ({
  alert,
  onAcknowledge,
  onResolve,
}) => {

  const getSeverityBorder = () => {
    switch (alert.severity) {
      case 'CRITICAL':
        return 'border-l-red-500';
      case 'HIGH':
        return 'border-l-orange-500';
      case 'MEDIUM':
        return 'border-l-yellow-500';
      case 'LOW':
      default:
        return 'border-l-green-500';
    }
  };

  const formatDate = (dateString: string) => {
    if (!dateString) {
      return '—';
    }

    const date = new Date(dateString);

    if (Number.isNaN(date.getTime())) {
      return '—';
    }

    return date.toLocaleString('en-GB', {
      dateStyle: 'medium',
      timeStyle: 'short',
    });
  };

  return (
    <article
      className={`
        bg-white
        rounded-xl
        shadow-sm
        border
        border-gray-100
        border-l-4
        ${getSeverityBorder()}
        overflow-hidden
        transition-all
        duration-200
        hover:shadow-md
        ${alert.isResolved ? 'opacity-75' : ''}
      `}
    >
      <div className="p-5 sm:p-6">

        {/* Header */}
        <div className="flex flex-col lg:flex-row lg:items-start lg:justify-between gap-5">

          <div className="min-w-0 flex-1">

            <div className="flex flex-wrap items-center gap-3">


              <h3 className="text-lg sm:text-xl font-bold text-gray-900 break-words">
                {alert.title}
              </h3>

              <RiskBadge
                level={alert.severity}
                size="sm"
              />

              {alert.isResolved && (
                <span className="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-semibold bg-green-100 text-green-700">
                  Resolved
                </span>
              )}

            </div>

            {/* Description */}
            {alert.description && (
              <p className="mt-4 text-sm sm:text-base text-gray-600 leading-relaxed">
                {alert.description}
              </p>
            )}

          </div>

          {/* Actions */}
          <div className="flex flex-wrap lg:flex-col gap-2 lg:min-w-[120px]">

            {!alert.isResolved && !alert.isAcknowledged && (
              <button
                type="button"
                onClick={() => onAcknowledge(alert.id)}
                className="
                  inline-flex
                  items-center
                  justify-center
                  px-3
                  py-2
                  rounded-lg
                  bg-blue-50
                  text-blue-700
                  border
                  border-blue-100
                  text-sm
                  font-medium
                  hover:bg-blue-100
                  focus:outline-none
                  focus:ring-2
                  focus:ring-blue-500
                  transition-colors
                "
              >
                Acknowledge
              </button>
            )}

            {!alert.isResolved && (
              <button
                type="button"
                onClick={() => onResolve(alert.id)}
                className="
                  inline-flex
                  items-center
                  justify-center
                  px-3
                  py-2
                  rounded-lg
                  bg-green-50
                  text-green-700
                  border
                  border-green-100
                  text-sm
                  font-medium
                  hover:bg-green-100
                  focus:outline-none
                  focus:ring-2
                  focus:ring-green-500
                  transition-colors
                "
              >
                Resolve
              </button>
            )}

            {alert.isAcknowledged && !alert.isResolved && (
              <span className="inline-flex items-center justify-center px-3 py-2 rounded-lg bg-gray-100 text-gray-600 text-sm font-medium">
                ✓ Acknowledged
              </span>
            )}

          </div>
        </div>

        {/* Alert details */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mt-6 pt-5 border-t border-gray-100">

          <div className="min-w-0">
            <p className="text-xs font-medium uppercase tracking-wide text-gray-400">
              Governorate
            </p>

            <p className="mt-1 text-sm font-semibold text-gray-800 truncate">
              {alert.governorateName || '—'}
            </p>
          </div>

          <div>
            <p className="text-xs font-medium uppercase tracking-wide text-gray-400">
              Current
            </p>

            <p className="mt-1 text-sm font-semibold text-gray-800">
              {typeof alert.currentValue === 'number'
                ? alert.currentValue.toFixed(1)
                : '—'}
            </p>
          </div>

          <div>
            <p className="text-xs font-medium uppercase tracking-wide text-gray-400">
              Threshold
            </p>

            <p className="mt-1 text-sm font-semibold text-gray-800">
              {typeof alert.thresholdValue === 'number'
                ? alert.thresholdValue.toFixed(1)
                : '—'}
            </p>
          </div>

          <div className="min-w-0">
            <p className="text-xs font-medium uppercase tracking-wide text-gray-400">
              Triggered
            </p>

            <p className="mt-1 text-sm font-semibold text-gray-800">
              {formatDate(alert.triggeredAt)}
            </p>
          </div>

        </div>

        {/* Health recommendation */}
        {alert.healthRecommendations && (
          <div className="mt-5 rounded-lg bg-blue-50 border border-blue-100 p-4">

            <h4 className="text-sm font-semibold text-blue-900 mb-1.5">
              Health Recommendation
            </h4>

            <p className="text-sm text-blue-800 leading-relaxed">
              {alert.healthRecommendations}
            </p>

          </div>
        )}

        {/* Resolved information */}
        {alert.isResolved && alert.resolvedAt && (
          <div className="mt-4 text-xs text-gray-500">
            Resolved on {formatDate(alert.resolvedAt)}
          </div>
        )}

      </div>
    </article>
  );
};

export default AlertCard;