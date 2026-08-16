import React from 'react';

interface RiskBadgeProps {
  level: string;
  size?: 'sm' | 'md' | 'lg';
}

const RiskBadge: React.FC<RiskBadgeProps> = ({ level, size = 'md' }) => {
  const sizeClasses = {
    sm: 'px-2 py-0.5 text-xs',
    md: 'px-3 py-1 text-sm',
    lg: 'px-4 py-2 text-base'
  };

  const levelClasses = {
    Critical: 'bg-red-100 text-red-800 border-red-200',
    High: 'bg-orange-100 text-orange-800 border-orange-200',
    Moderate: 'bg-yellow-100 text-yellow-800 border-yellow-200',
    Low: 'bg-green-100 text-green-800 border-green-200',
    Unknown: 'bg-gray-100 text-gray-800 border-gray-200'
  };

  const classes = levelClasses[level as keyof typeof levelClasses] || levelClasses.Unknown;

  return (
    <span
      className={`inline-flex items-center rounded-full font-medium border ${sizeClasses[size]} ${classes}`}
    >
      {level}
    </span>
  );
};

export default RiskBadge;
