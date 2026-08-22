import React from 'react';

interface Governorate {
  name: string;
  nameAr?: string;
  centerLat: number;
  centerLon: number;
}

interface ClimateData {
  temperature?: number;
  feelsLike?: number;
  humidity?: number;
  windSpeed?: number;
  windDirection?: number;
  pressure?: number;
  uvIndex?: number;
  precipitation?: number;
  cloudCover?: number;
}

interface Forecast {
  forecastDate: string;
  temperatureMax?: number;
  temperatureMin?: number;
  precipitation?: number;
  windSpeed?: number;
  uvIndex?: number;
}

interface GovernorateDetail {
  governorate: Governorate;
  currentWeather: ClimateData;
  forecasts: Forecast[];
}

interface GovernorateDetailsProps {
  detail: GovernorateDetail | null;
}

const GovernorateDetails: React.FC<GovernorateDetailsProps> = ({ detail }) => {
  if (!detail) {
    return (
      <div className="rounded-xl bg-white p-6 shadow">
        <h2 className="mb-2 text-xl font-semibold text-gray-900">
          Governorate Details
        </h2>
        <p className="text-gray-500">
          Click a governorate on the map to view its climate information.
        </p>
      </div>
    );
  }

  const weather = detail.currentWeather;

  return (
    <div className="rounded-xl bg-white p-6 shadow">
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-gray-900">
          {detail.governorate.name}
        </h2>

        <p className="text-sm text-gray-500">
          {detail.governorate.centerLat.toFixed(4)},{' '}
          {detail.governorate.centerLon.toFixed(4)}
        </p>
      </div>

      <div className="grid grid-cols-2 gap-4 md:grid-cols-4">
        <div className="rounded-lg bg-gray-50 p-4">
          <p className="text-sm text-gray-500">Temperature</p>
          <p className="text-2xl font-bold text-gray-900">
            {weather.temperature ?? '--'} °C
          </p>
        </div>

        <div className="rounded-lg bg-gray-50 p-4">
          <p className="text-sm text-gray-500">Feels Like</p>
          <p className="text-2xl font-bold text-gray-900">
            {weather.feelsLike ?? '--'} °C
          </p>
        </div>

        <div className="rounded-lg bg-gray-50 p-4">
          <p className="text-sm text-gray-500">Humidity</p>
          <p className="text-2xl font-bold text-gray-900">
            {weather.humidity ?? '--'} %
          </p>
        </div>

        <div className="rounded-lg bg-gray-50 p-4">
          <p className="text-sm text-gray-500">Wind</p>
          <p className="text-2xl font-bold text-gray-900">
            {weather.windSpeed ?? '--'} m/s
          </p>
        </div>

        <div className="rounded-lg bg-gray-50 p-4">
          <p className="text-sm text-gray-500">Pressure</p>
          <p className="text-xl font-semibold text-gray-900">
            {weather.pressure ?? '--'} hPa
          </p>
        </div>

        <div className="rounded-lg bg-gray-50 p-4">
          <p className="text-sm text-gray-500">UV Index</p>
          <p className="text-xl font-semibold text-gray-900">
            {weather.uvIndex ?? '--'}
          </p>
        </div>

        <div className="rounded-lg bg-gray-50 p-4">
          <p className="text-sm text-gray-500">Precipitation</p>
          <p className="text-xl font-semibold text-gray-900">
            {weather.precipitation ?? '--'} mm
          </p>
        </div>

        <div className="rounded-lg bg-gray-50 p-4">
          <p className="text-sm text-gray-500">Cloud Cover</p>
          <p className="text-xl font-semibold text-gray-900">
            {weather.cloudCover ?? '--'} %
          </p>
        </div>
      </div>

      <div className="mt-8">
        <h3 className="mb-4 text-lg font-semibold text-gray-900">
          7-Day Forecast
        </h3>

        {detail.forecasts.length === 0 ? (
          <p className="text-gray-500">No forecast data available.</p>
        ) : (
          <div className="grid grid-cols-1 gap-3 md:grid-cols-2 lg:grid-cols-4">
            {detail.forecasts.map((forecast) => (
              <div
                key={forecast.forecastDate}
                className="rounded-lg border border-gray-200 p-4"
              >
                <p className="font-medium text-gray-900">
                  {forecast.forecastDate}
                </p>

                <p className="mt-2 text-sm text-gray-600">
                  {forecast.temperatureMin ?? '--'}°C →{' '}
                  {forecast.temperatureMax ?? '--'}°C
                </p>

                <p className="text-sm text-gray-600">
                  Rain: {forecast.precipitation ?? '--'} mm
                </p>

                <p className="text-sm text-gray-600">
                  Wind: {forecast.windSpeed ?? '--'} m/s
                </p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default GovernorateDetails;