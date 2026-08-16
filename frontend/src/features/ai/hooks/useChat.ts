import { AIMessage } from '../types';

export interface ChatState {
  messages: AIMessage[];
  isLoading: boolean;
  error: string | null;
  sessionId: string | null;
}

export const useChat = () => {
  const [state, setState] = React.useState<ChatState>({
    messages: [],
    isLoading: false,
    error: null,
    sessionId: null
  });

  const addMessage = (message: AIMessage) => {
    setState(prev => ({
      ...prev,
      messages: [...prev.messages, message]
    }));
  };

  const setLoading = (loading: boolean) => {
    setState(prev => ({ ...prev, isLoading: loading }));
  };

  const setError = (error: string | null) => {
    setState(prev => ({ ...prev, error }));
  };

  const clearMessages = () => {
    setState({
      messages: [],
      isLoading: false,
      error: null,
      sessionId: null
    });
  };

  return {
    messages: state.messages,
    isLoading: state.isLoading,
    error: state.error,
    sessionId: state.sessionId,
    addMessage,
    setLoading,
    setError,
    clearMessages
  };
};
