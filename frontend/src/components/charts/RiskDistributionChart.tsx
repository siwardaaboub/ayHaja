import React from 'react';
import { Bar } from 'react-chartjs-2';
import { ChartOptions } from 'chart.js';
import { RiskDistribution } from '../../types';

interface RiskDistributionChartProps {
  data: RiskDistribution[];
  title?: string;
}

const RiskDistributionChart: React.FC<RiskDistributionChartProps> = ({
  data,
  title = 'Risk Distribution by Governorate'
}) => {
  const chartData = {
    labels: data.map(d => d.governorateName),
    datasets: [
      {
        label: 'Heatwave Risk',
        data: data.map(d => d.heatwaveRisk),
        backgroundColor: 'rgba(255, 99, 132, 0.8)',
        borderColor: 'rgb(255, 99, 132)',
        borderWidth: 1
      },
      {
        label: 'Drought Risk',
        data: data.map(d => d.droughtRisk),
        backgroundColor: 'rgba(153, 102, 255, 0.8)',
        borderColor: 'rgb(153, 102, 255)',
        borderWidth: 1
      },
      {
        label: 'Fire Risk',
        data: data.map(d => d.fireRisk),
        backgroundColor: 'rgba(255, 159, 64, 0.8)',
        borderColor: 'rgb(255, 159, 64)',
        borderWidth: 1
      }
    ]
  };

  const options: ChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      title: {
        display: true,
        text: title
      },
      tooltip: {
        mode: 'index',
        intersect: false
      }
    },
    scales: {
      y: {
        beginAtZero: true,
        title: {
          display: true,
          text: 'Risk Level'
        }
      }
    }
  };

  return <Bar data={chartData} options={options} />;
};

export default RiskDistributionChart;
