import React from 'react';
import { Doughnut } from 'react-chartjs-2';
import { ChartOptions } from 'chart.js';

interface RiskPieChartProps {
  data: Record<string, number>;
  title?: string;
}

const RiskPieChart: React.FC<RiskPieChartProps> = ({ data, title = 'Risk Level Distribution' }) => {
  const chartData = {
    labels: Object.keys(data),
    datasets: [
      {
        label: 'Number of Areas',
        data: Object.values(data),
        backgroundColor: [
          'rgba(75, 192, 192, 0.8)',
          'rgba(255, 206, 86, 0.8)',
          'rgba(255, 159, 64, 0.8)',
          'rgba(255, 99, 132, 0.8)'
        ],
        borderColor: [
          'rgb(75, 192, 192)',
          'rgb(255, 206, 86)',
          'rgb(255, 159, 64)',
          'rgb(255, 99, 132)'
        ],
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
      legend: {
        position: 'right'
      }
    }
  };

  return <Doughnut data={chartData} options={options} />;
};

export default RiskPieChart;
