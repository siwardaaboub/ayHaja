import axios from 'axios';
import { AIMessage, AIConversation, ChatMessageRequest, PromptTemplate } from '../types';

const apiClient = axios.create({
  baseURL: '/api/ai',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
});

apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('AI API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export const aiApi = {
  getUserConversations: async (): Promise<AIConversation[]> => {
    const response = await apiClient.get<AIConversation[]>('/conversations');
    return response.data;
  },

  sendMessage: async (request: ChatMessageRequest): Promise<AIMessage> => {
    const response = await apiClient.post<AIMessage>('/chat', request);
    return response.data;
  },

  getRecommendations: async (governorateId: string, governorateName: string): Promise<string> => {
    const response = await apiClient.get<string>('/recommendations', {
      params: { governorateId, governorateName }
    });
    return response.data;
  },

  getRiskExplanation: async (governorateId: string, governorateName: string): Promise<string> => {
    const response = await apiClient.get<string>('/risk-explanation', {
      params: { governorateId, governorateName }
    });
    return response.data;
  },

  getClimateSummary: async (governorateId: string, governorateName: string): Promise<string> => {
    const response = await apiClient.get<string>('/summary', {
      params: { governorateId, governorateName }
    });
    return response.data;
  },

  getAllPromptTemplates: async (): Promise<PromptTemplate[]> => {
    const response = await apiClient.get<PromptTemplate[]>('/templates');
    return response.data;
  },

  getPromptTemplateByName: async (name: string): Promise<PromptTemplate> => {
    const response = await apiClient.get<PromptTemplate>(`/templates/${name}`);
    return response.data;
  }
};

export default apiClient;
