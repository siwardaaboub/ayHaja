import React from 'react';
import { AIMessage } from '../types';

interface ChatMessageProps {
  message: AIMessage;
}

const ChatMessage: React.FC<ChatMessageProps> = ({ message }) => {
  const isUser = message.senderType === 'USER';
  const isAssistant = message.senderType === 'ASSISTANT';
  const isSystem = message.senderType === 'SYSTEM';

  const getMessageStyle = () => {
    if (isUser) {
      return 'bg-blue-600 text-white ml-auto';
    }
    if (isAssistant) {
      return 'bg-gray-100 text-gray-900';
    }
    return 'bg-purple-100 text-purple-800 mx-auto';
  };

  const formatTime = (dateString: string) => {
    return new Date(dateString).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  return (
    <div className={`max-w-3xl mx-auto mb-4 p-4 rounded-lg ${getMessageStyle()} ${
      isSystem ? 'text-center' : ''
    }`}>
      <div className="flex items-start space-x-3">
        {!isUser && (
          <div className="flex-shrink-0">
            <div className="w-8 h-8 rounded-full bg-gradient-to-r from-blue-500 to-purple-600 flex items-center justify-center">
              <span className="text-white text-sm font-bold">AI</span>
            </div>
          </div>
        )}
        <div className="flex-1">
          <div className="prose prose-sm max-w-none">
            {message.content.split('\n').map((line, index) => (
              <p key={index} className="mb-2 last:mb-0">
                {line}
              </p>
            ))}
          </div>
          <div className="flex items-center justify-between mt-2 text-xs opacity-70">
            <span>{formatTime(message.createdAt)}</span>
            {message.tokensUsed && (
              <span>{message.tokensUsed} tokens</span>
            )}
          </div>
        </div>
        {isUser && (
          <div className="flex-shrink-0">
            <div className="w-8 h-8 rounded-full bg-blue-600 flex items-center justify-center">
              <span className="text-white text-sm font-bold">U</span>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ChatMessage;
