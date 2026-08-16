import React from 'react';
import { Line } from 'react-chartjs-2';
import { ChartJS, ChartOptions } from 'chart.js';
import { HealthDataPoint } from '../../types';

interface TemperatureChartProps {
  data: HealthDataPoint[];
  title?: string;
}

const TemperatureChart: React.FC<TemperatureChartProps> = ({ data, title = 'Temperature Trend' }) => {
  const chartData = {
    labels: data.map(d => new Date(d.date).toLocaleDateString()),
    datasets: [
      {
        label: 'Temperature (°C)',
        data: data.map(d => d.temperature),
        borderColor: 'rgb(255, 99, 132)',
        backgroundColor: 'rgba(255, 99, 132, 0.5)',
        tension: 0.3,
        fill: true
      },
      {
        label: 'Heatwave Risk',
        data: data.map(d => d.heatwaveRisk),
        borderColor: 'rgb(255, 159, 64)',
        backgroundColor: 'rgba(255, 159, 64, 0.5)',
        tension: 0.3,
        fill: true,
        yAxisID: 'y1'
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
        type: 'linear',
        display: true,
        position: 'left',
        title: {
          display: true,
          text: 'Temperature (°C)'
        }
      },
      y1: {
        type: 'linear',
        display: true,
        position: 'right',
        title: {
          display: true,
          text: 'Risk Level'
        },
        grid: {
          drawOnChartArea: false
        }
      }
    }
  };

  return <Line data={chartData} options={options} />;
};

export default TemperatureChart;
