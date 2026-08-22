import React, { useEffect, useMemo, useState } from 'react';
import {
  GeoJSON,
  MapContainer,
  TileLayer,
  useMap,
} from 'react-leaflet';
import type { Feature, FeatureCollection, Geometry } from 'geojson';
import type { Layer } from 'leaflet';
import apiClient from '../../../services/api';

interface Governorate {
  id: string;
  name: string;
  nameAr?: string;
  centerLat: number;
  centerLon: number;
  population?: number;
  areaKm2?: number;
  geometry: string;
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

interface TunisiaMapProps {
  onGovernorateSelected?: (detail: GovernorateDetail) => void;
}

function FitTunisiaBounds({
  governorates,
}: {
  governorates: Governorate[];
}) {
  const map = useMap();

  useEffect(() => {
    if (!governorates.length) return;

    const points = governorates
      .filter((g) => g.centerLat != null && g.centerLon != null)
      .map((g) => [g.centerLat, g.centerLon] as [number, number]);

    if (points.length > 0) {
      map.fitBounds(points, { padding: [20, 20] });
    }
  }, [governorates, map]);

  return null;
}

const TunisiaMap: React.FC<TunisiaMapProps> = ({
  onGovernorateSelected,
}) => {
  const [governorates, setGovernorates] = useState<Governorate[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchGovernorates = async () => {
      try {
        setLoading(true);
        setError(null);

        const response = await apiClient.get<Governorate[]>(
          '/map/governorates/with-geometry'
        );

        setGovernorates(response.data);
      } catch (err) {
        console.error('Map governorates error:', err);
        setError('Unable to load governorates.');
      } finally {
        setLoading(false);
      }
    };

    fetchGovernorates();
  }, []);

  const handleGovernorateClick = async (governorate: Governorate) => {
    try {
      setError(null);

      const response = await apiClient.get<GovernorateDetail>(
        `/map/governorates/${governorate.id}/detail`
      );

      onGovernorateSelected?.(response.data);
    } catch (err) {
      console.error('Governorate detail error:', err);
      setError('Unable to load governorate details.');
    }
  };

  const featureCollection = useMemo<FeatureCollection>(() => {
    const features: Feature<Geometry>[] = governorates
      .filter((governorate) => governorate.geometry)
      .map((governorate) => ({
        type: 'Feature',
        properties: {
          id: governorate.id,
          name: governorate.name,
        },
        geometry: JSON.parse(governorate.geometry),
      }));

    return {
      type: 'FeatureCollection',
      features,
    };
  }, [governorates]);

  const onEachFeature = (feature: Feature, layer: Layer) => {
    const governorateId = feature.properties?.id;

    const governorate = governorates.find(
      (g) => g.id === governorateId
    );

    if (!governorate) return;

    layer.bindTooltip(governorate.name, {
      sticky: true,
    });

    layer.on({
      click: () => handleGovernorateClick(governorate),
    });
  };

  if (loading) {
    return (
      <div className="flex min-h-[500px] items-center justify-center rounded-xl bg-white shadow">
        <p className="text-gray-600">Loading Tunisia map...</p>
      </div>
    );
  }

  if (error && governorates.length === 0) {
    return (
      <div className="flex min-h-[500px] items-center justify-center rounded-xl bg-white shadow">
        <p className="text-red-600">{error}</p>
      </div>
    );
  }

  return (
    <div>
      {error && (
        <div className="mb-4 rounded-lg bg-red-50 px-4 py-3 text-sm text-red-700">
          {error}
        </div>
      )}

      <div className="overflow-hidden rounded-xl bg-white shadow">
        <MapContainer
          center={[34, 9]}
          zoom={6}
          scrollWheelZoom
          className="h-[600px] w-full"
        >
          <TileLayer
            attribution="&copy; OpenStreetMap contributors"
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />

          <FitTunisiaBounds governorates={governorates} />

          <GeoJSON
            key={governorates.map((g) => g.id).join(',')}
            data={featureCollection}
            style={{
              color: '#2563eb',
              weight: 1.5,
              fillOpacity: 0.2,
            }}
            onEachFeature={onEachFeature}
          />
        </MapContainer>
      </div>
    </div>
  );
};

export default TunisiaMap;