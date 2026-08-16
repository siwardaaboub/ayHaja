import React, { useEffect, useState } from 'react';
import UsersTable from '../components/UsersTable';
import StatisticsCards from '../components/StatisticsCards';
import AdminLogsTable from '../components/AdminLogsTable';
import LoadingSpinner from '../../../components/ui/LoadingSpinner';
import { AdminUser, AdminStatistics, AdminLog } from '../types';
import { adminApi } from '../services/api';

const AdminDashboardPage: React.FC = () => {
  const [users, setUsers] = useState<AdminUser[]>([]);
  const [statistics, setStatistics] = useState<AdminStatistics | null>(null);
  const [logs, setLogs] = useState<AdminLog[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      setError(null);

      const [usersData, statsData, logsData] = await Promise.all([
        adminApi.getAllUsers(),
        adminApi.getStatistics(),
        adminApi.getAdminLogs()
      ]);

      setUsers(usersData);
      setStatistics(statsData);
      setLogs(logsData);
    } catch (err) {
      setError('Failed to load admin data. Please try again.');
      console.error('Failed to fetch admin data:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleRoleChange = async (userId: string, role: string) => {
    try {
      await adminApi.assignRole(userId, role);
      setUsers(users.map(user =>
        user.id === userId ? { ...user, role: role as 'USER' | 'ADMIN' } : user
      ));
    } catch (err) {
      console.error('Failed to update user role:', err);
    }
  };

  const handleDeleteUser = async (userId: string) => {
    try {
      await adminApi.deleteUser(userId);
      setUsers(users.filter(user => user.id !== userId));
    } catch (err) {
      console.error('Failed to delete user:', err);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <p className="text-red-600 mb-4">{error}</p>
          <button
            onClick={fetchData}
            className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Admin Dashboard</h1>
          <p className="text-gray-600 mt-2">
            Manage users, monitor system statistics, and review activity logs
          </p>
        </div>

        <StatisticsCards statistics={statistics} />

        <div className="bg-white rounded-xl shadow-md p-6 mb-8">
          <h2 className="text-xl font-bold text-gray-900 mb-4">Users Management</h2>
          <UsersTable
            users={users}
            onRoleChange={handleRoleChange}
            onDelete={handleDeleteUser}
          />
        </div>

        <AdminLogsTable logs={logs} loading={false} />
      </div>
    </div>
  );
};

export default AdminDashboardPage;
