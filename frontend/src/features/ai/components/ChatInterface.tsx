import React, { useState, useRef, useEffect } from 'react';
import ChatMessage from './ChatMessage';
import LoadingSpinner from '../../../components/ui/LoadingSpinner';
import { AIMessage, ChatMessageRequest } from '../types';
import { aiApi } from '../services/api';
import { v4 as uuidv4 } from 'uuid';

const ChatInterface: React.FC = () => {
  const [messages, setMessages] = useState<AIMessage[]>([]);
  const [inputValue, setInputValue] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [sessionId] = useState(() => uuidv4());
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSendMessage = async () => {
    if (!inputValue.trim() || isLoading) return;

    const userMessage: AIMessage = {
      id: uuidv4(),
      conversationId: sessionId,
      senderType: 'USER',
      content: inputValue,
      messageIndex: messages.length,
      tokensUsed: 0,
      processingTimeMs: 0,
      createdAt: new Date().toISOString()
    };

    setMessages(prev => [...prev, userMessage]);
    setInputValue('');
    setIsLoading(true);
    setError(null);

    try {
      const request: ChatMessageRequest = {
        content: inputValue,
        sessionId: sessionId
      };

      const response = await aiApi.sendMessage(request);
      setMessages(prev => [...prev, response]);
    } catch (err) {
      setError('Failed to send message. Please try again.');
      console.error('Failed to send message:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  const handleQuickAction = async (action: string) => {
    setInputValue(action);
    await handleSendMessage();
  };

  const quickActions = [
    "Quelles sont les risques climatiques actuels en Tunisie?",
    "Recommandations pour une personne âgée",
    "Quel temps fera-t-il demain?",
    "Expliquer les risques de canicule"
  ];

  return (
    <div className="flex flex-col h-screen bg-gray-50">
      <div className="bg-white shadow-sm border-b px-4 py-3">
        <h1 className="text-xl font-bold text-gray-900">Assistant IA Climatique</h1>
        <p className="text-sm text-gray-600">Posez vos questions sur les risques climatiques et la santé</p>
      </div>

      <div className="flex-1 overflow-y-auto px-4 py-4">
        {messages.length === 0 ? (
          <div className="text-center py-12">
            <div className="mb-6">
              <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-3xl">🌤️</span>
              </div>
              <h2 className="text-xl font-semibold text-gray-900 mb-2">
                Bonjour! Comment puis-je vous aider?
              </h2>
              <p className="text-gray-600 max-w-md mx-auto">
                Je suis votre assistant IA pour les risques climatiques. 
                Posez-moi des questions sur les risques actuels, les recommandations 
                personnalisées ou les prévisions météorologiques.
              </p>
            </div>

            <div className="mt-8 grid grid-cols-1 sm:grid-cols-2 gap-3 max-w-2xl mx-auto">
              {quickActions.map((action, index) => (
                <button
                  key={index}
                  onClick={() => handleQuickAction(action)}
                  className="p-3 text-left bg-white border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors text-sm"
                >
                  {action}
                </button>
              ))}
            </div>
          </div>
        ) : (
          <div className="space-y-4">
            {messages.map((message) => (
              <ChatMessage key={message.id} message={message} />
            ))}
            <div ref={messagesEndRef} />
          </div>
        )}

        {isLoading && (
          <div className="flex justify-center py-4">
            <LoadingSpinner size="md" />
          </div>
        )}
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-600 px-4 py-2 text-sm">
          {error}
        </div>
      )}

      <div className="bg-white border-t px-4 py-3">
        <div className="max-w-3xl mx-auto">
          <textarea
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder="Tapez votre question..."
            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
            rows={1}
            disabled={isLoading}
          />
          <div className="flex justify-between items-center mt-2">
            <button
              onClick={() => handleQuickAction("Recommandations personnalisées")}
              className="text-xs text-blue-600 hover:text-blue-800"
            >
              Suggestions rapides
            </button>
            <button
              onClick={handleSendMessage}
              disabled={!inputValue.trim() || isLoading}
              className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              {isLoading ? 'Envoi...' : 'Envoyer'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ChatInterface;
