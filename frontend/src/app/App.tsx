import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import DashboardPage from '../features/dashboard/pages/DashboardPage';
import AlertsPage from '../features/alerts/pages/AlertsPage';
import AIAssistantPage from '../features/ai/pages/AIAssistantPage';
import AdminDashboardPage from '../features/admin/pages/AdminDashboardPage';
import LoginPage from '../features/auth/pages/LoginPage';
import RegisterPage from '../features/auth/pages/RegisterPage';
import { AuthProvider, useAuth } from '../features/auth/contexts/AuthContext';
import '../index.css';

const RequireAuth: React.FC<{ children: JSX.Element }> = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();
  const location = useLocation();

  if (loading) {
    return <div className="flex justify-center items-center h-screen">Loading...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return children;
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/" element={
              <RequireAuth>
                <DashboardPage />
              </RequireAuth>
            } />
            <Route path="/dashboard" element={
              <RequireAuth>
                <DashboardPage />
              </RequireAuth>
            } />
            <Route path="/alerts" element={
              <RequireAuth>
                <AlertsPage />
              </RequireAuth>
            } />
            <Route path="/ai" element={
              <RequireAuth>
                <AIAssistantPage />
              </RequireAuth>
            } />
            <Route path="/admin" element={
              <RequireAuth>
                <AdminDashboardPage />
              </RequireAuth>
            } />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
