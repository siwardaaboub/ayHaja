import axios from 'axios';
import { AuthResponse, LoginRequest, RegisterRequest, User } from '../../types';

const authApiClient = axios.create({
  baseURL: '/api/auth',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

authApiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('Auth API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export const authApi = {
  register: async (request: RegisterRequest): Promise<AuthResponse> => {
    const response = await authApiClient.post<AuthResponse>('/register', request);
    const { token, user } = response.data;
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
    return response.data;
  },

  login: async (request: LoginRequest): Promise<AuthResponse> => {
    const response = await authApiClient.post<AuthResponse>('/login', request);
    const { token, user } = response.data;
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
    return response.data;
  },

  logout: (): void => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  getToken: (): string | null => {
    return localStorage.getItem('token');
  },

  getUser: (): User | null => {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  }
};

export default authApiClient;
