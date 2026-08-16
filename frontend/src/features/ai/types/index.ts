export interface AIMessage {
  id: string;
  conversationId: string;
  senderType: 'USER' | 'ASSISTANT' | 'SYSTEM';
  content: string;
  messageIndex: number;
  tokensUsed: number;
  processingTimeMs: number;
  createdAt: string;
}

export interface AIConversation {
  id: string;
  userId: string;
  sessionId: string;
  governorateId: string | null;
  startedAt: string;
  lastInteractionAt: string;
  isActive: boolean;
  createdAt: string;
  messages?: AIMessage[];
}

export interface ChatMessageRequest {
  content: string;
  governorateId?: string;
  sessionId?: string;
  context?: string;
}

export interface PromptTemplate {
  id: string;
  templateName: string;
  promptType: 'RISK_EXPLANATION' | 'RECOMMENDATION' | 'SUMMARY' | 'CHAT' | 'FORECAST_ANALYSIS';
  templateContent: string;
  description: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}
