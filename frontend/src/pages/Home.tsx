import { Link } from "react-router-dom";
import { ArrowRight, Activity, Settings, FileText, BarChart3, Globe, AlertTriangle } from "lucide-react";
import { useEffect, useState } from "react";
import { getDashboardStats, type DashboardStats } from "@/lib/api";

export default function Home() {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getDashboardStats()
      .then(setStats)
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  const statsCards = [
    {
      label: "总请求数",
      value: stats?.totalRequests || 0,
      icon: BarChart3,
      color: "bg-blue-100 text-blue-600",
    },
    {
      label: "活跃路由",
      value: `${stats?.activeRoutes || 0} / ${stats?.totalRoutes || 0}`,
      icon: Globe,
      color: "bg-green-100 text-green-600",
    },
    {
      label: "错误率",
      value: stats ? `${(stats.errorRate * 100).toFixed(2)}%` : "0%",
      icon: AlertTriangle,
      color: "bg-red-100 text-red-600",
    },
  ];

  return (
    <div className="space-y-8">
      <div className="bg-white p-8 rounded-lg shadow-sm border border-gray-100">
        <h1 className="text-3xl font-bold text-gray-900 mb-4">欢迎使用连接器中间件</h1>
        <p className="text-gray-600 max-w-2xl">
          这是一个高性能的 API 路由转发和参数转换系统。您可以轻松配置路由规则，将请求转发到云仓或其他系统，并实时监控请求日志。
        </p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {statsCards.map((stat, index) => (
          <div key={index} className="bg-white p-6 rounded-lg border border-gray-100 shadow-sm flex items-center space-x-4">
            <div className={`p-3 rounded-lg ${stat.color}`}>
              <stat.icon size={24} />
            </div>
            <div>
              <p className="text-sm font-medium text-gray-500">{stat.label}</p>
              <h3 className="text-2xl font-bold text-gray-900">
                {loading ? "-" : stat.value}
              </h3>
            </div>
          </div>
        ))}
      </div>

      {/* Quick Actions */}
      <h2 className="text-xl font-bold text-gray-800 pt-4">快捷操作</h2>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Link to="/routes/new" className="group">
          <div className="bg-blue-50 p-6 rounded-lg border border-blue-100 hover:shadow-md transition-all h-full">
            <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mb-4 text-blue-600 group-hover:bg-blue-600 group-hover:text-white transition-colors">
              <Settings size={24} />
            </div>
            <h3 className="text-lg font-semibold text-gray-900 mb-2">配置路由</h3>
            <p className="text-gray-600 text-sm mb-4">创建新的 API 转发规则，设置源路径、目标地址和参数映射。</p>
            <div className="flex items-center text-blue-600 font-medium text-sm">
              立即创建 <ArrowRight size={16} className="ml-1" />
            </div>
          </div>
        </Link>

        <Link to="/routes" className="group">
          <div className="bg-green-50 p-6 rounded-lg border border-green-100 hover:shadow-md transition-all h-full">
            <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center mb-4 text-green-600 group-hover:bg-green-600 group-hover:text-white transition-colors">
              <Activity size={24} />
            </div>
            <h3 className="text-lg font-semibold text-gray-900 mb-2">管理规则</h3>
            <p className="text-gray-600 text-sm mb-4">查看和管理现有的路由规则，支持编辑、删除和状态切换。</p>
            <div className="flex items-center text-green-600 font-medium text-sm">
              查看列表 <ArrowRight size={16} className="ml-1" />
            </div>
          </div>
        </Link>

        <Link to="/logs" className="group">
          <div className="bg-purple-50 p-6 rounded-lg border border-purple-100 hover:shadow-md transition-all h-full">
            <div className="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center mb-4 text-purple-600 group-hover:bg-purple-600 group-hover:text-white transition-colors">
              <FileText size={24} />
            </div>
            <h3 className="text-lg font-semibold text-gray-900 mb-2">请求日志</h3>
            <p className="text-gray-600 text-sm mb-4">实时监控 API 请求状态、响应时间和错误信息。</p>
            <div className="flex items-center text-purple-600 font-medium text-sm">
              查看日志 <ArrowRight size={16} className="ml-1" />
            </div>
          </div>
        </Link>
      </div>
    </div>
  );
}
