export interface Governorate {
  id: string;
  name: string;
  nameAr: string;
  centerLat: number;
  centerLon: number;
  population: number;
  areaKm2: number;
  geometry: string;
}

export interface ClimateData {
  id: string;
  governorateId: string;
  governorateName: string;
  dateRecorded: string;
  temperature: number;
  feelsLike: number;
  humidity: number;
  pressure: number;
  windSpeed: number;
  windDirection: number;
  uvIndex: number;
  precipitation: number;
  cloudCover: number;
  dataSource: string;
  createdAt: string;
}

export interface ClimateForecast {
  id: string;
  governorateId: string;
  governorateName: string;
  forecastDate: string;
  temperatureMax: number;
  temperatureMin: number;
  precipitation: number;
  windSpeed: number;
  windDirection: number;
  uvIndex: number;
  confidence: number;
  dataSource: string;
  createdAt: string;
}

export interface HealthStatistic {
  id: string;
  governorateId: string;
  governorateName: string;
  statisticDate: string;
  temperature: number;
  humidity: number;
  uvIndex: number;
  airQualityIndex: number;
  heatwaveRisk: number;
  droughtRisk: number;
  fireRisk: number;
  climateRiskIndex: number;
  populationAffected: number;
  healthAdvisory: string;
  createdAt: string;
}

export interface DashboardSummary {
  totalUsers: number;
  totalGovernorates: number;
  activeAlerts: number;
  totalClimateDataPoints: number;
  avgTemperature: number;
  avgHumidity: number;
  highRiskAreas: number;
  riskDistribution: Record<string, number>;
  governorateUserCount: Record<string, number>;
}

export interface HealthDataPoint {
  date: string;
  temperature: number;
  humidity: number;
  uvIndex: number;
  heatwaveRisk: number;
  droughtRisk: number;
  fireRisk: number;
}

export interface HealthTrend {
  governorateName: string;
  dataPoints: HealthDataPoint[];
}

export interface RiskDistribution {
  governorateId: string;
  governorateName: string;
  centerLat: number;
  centerLon: number;
  heatwaveRisk: number;
  droughtRisk: number;
  fireRisk: number;
  climateRiskIndex: number;
  populationAffected: number;
  riskLevel: string;
  healthRecommendations: string[];
}

export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  createdAt: string;
  updatedAt: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface HealthProfile {
  id: string;
  userId: string;
  age: number;
  hypertension: boolean;
  asthma: boolean;
  diabetes: boolean;
  heartDisease: boolean;
  pregnancy: boolean;
  elderlyPerson: boolean;
  childrenAtHome: boolean;
  createdAt: string;
  updatedAt: string;
}
