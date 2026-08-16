import React from 'react';
import { ClimateAlert } from '../types';
import RiskBadge from '../../../components/ui/RiskBadge';

interface AlertCardProps {
  alert: ClimateAlert;
  onAcknowledge: (id: string) => void;
  onResolve: (id: string) => void;
}

const AlertCard: React.FC<AlertCardProps> = ({ alert, onAcknowledge, onResolve }) => {
  const getAlertTypeIcon = (type: string) => {
    switch (type) {
      case 'HEATWAVE': return '🌡️';
      case 'DROUGHT': return '🏜️';
      case 'FIRE': return '🔥';
      case 'STORM': return '⛈️';
      case 'FLOOD': return '🌊';
      case 'AIR_QUALITY': return '💨';
      default: return '⚠️';
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString();
  };

  return (
    <div className={`bg-white rounded-xl shadow-md p-6 border-l-4 ${
      alert.severity === 'CRITICAL' ? 'border-red-500' :
      alert.severity === 'HIGH' ? 'border-orange-500' :
      alert.severity === 'MEDIUM' ? 'border-yellow-500' :
      'border-green-500'
    } ${alert.isResolved ? 'opacity-60' : ''}`}>
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <div className="flex items-center space-x-3 mb-3">
            <span className="text-2xl">{getAlertTypeIcon(alert.alertType)}</span>
            <h3 className="text-lg font-bold text-gray-900">{alert.title}</h3>
            <RiskBadge level={alert.severity} size="sm" />
          </div>

          <p className="text-gray-700 mb-3">{alert.description}</p>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-4 text-sm">
            <div>
              <span className="text-gray-500">Governorate:</span>
              <span className="font-medium">{alert.governorName}</span>
            </div>
            <div>
              <span className="text-gray-500">Current:</span>
              <span className="font-medium">{alert.currentValue?.toFixed(1)}</span>
            </div>
            <div>
              <span className="text-gray-500">Threshold:</span>
              <span className="font-medium">{alert.thresholdValue?.toFixed(1)}</span>
            </div>
            <div>
              <span className="text-gray-500">Triggered:</span>
              <span className="font-medium">{formatDate(alert.triggeredAt)}</span>
            </div>
          </div>

          {alert.healthRecommendations && (
            <div className="bg-blue-50 rounded-lg p-4 mb-4">
              <h4 className="font-semibold text-blue-900 mb-2">Health Recommendations:</h4>
              <p className="text-blue-800">{alert.healthRecommendations}</p>
            </div>
          )}
        </div>

        <div className="flex flex-col space-y-2 ml-4">
          {!alert.isAcknowledged && !alert.isResolved && (
            <button
              onClick={() => onAcknowledge(alert.id)}
              className="px-3 py-1 bg-blue-100 text-blue-800 rounded-lg hover:bg-blue-200 transition-colors text-sm"
            >
              Acknowledge
            </button>
          )}
          {!alert.isResolved && (
            <button
              onClick={() => onResolve(alert.id)}
              className="px-3 py-1 bg-green-100 text-green-800 rounded-lg hover:bg-green-200 transition-colors text-sm"
            >
              Resolve
            </button>
          )}
          {alert.isAcknowledged && (
            <span className="text-xs text-gray-500">Acknowledged</span>
          )}
          {alert.isResolved && (
            <span className="text-xs text-gray-500">Resolved</span>
          )}
        </div>
      </div>
    </div>
  );
};

export default AlertCard;
