import { useState, useEffect } from "react";
import { getRoutes, testInvokeRoute, type Route } from "@/lib/api";
import { Play, CheckCircle, XCircle, ArrowRight } from "lucide-react";
import axios from "axios";

export default function RouteTester() {
  const [routes, setRoutes] = useState<Route[]>([]);
  const [selectedRouteId, setSelectedRouteId] = useState<string>("");
  const [requestBody, setRequestBody] = useState<string>("{\n  \"test\": \"value\"\n}");
  const [response, setResponse] = useState<any>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    getRoutes().then(setRoutes).catch(console.error);
  }, []);

  const selectedRoute = routes.find(r => r.id === selectedRouteId);

  useEffect(() => {
    if (selectedRoute) {
      // Pre-fill a sample body based on mappings if possible, or just default
      const sample: Record<string, any> = {};
      selectedRoute.mappingConfig?.mappings?.forEach(m => {
        // Construct a simple nested object for display
        const parts = m.source.split('.');
        let current = sample;
        parts.forEach((part, i) => {
          if (i === parts.length - 1) {
            current[part] = "测试值";
          } else {
            current[part] = current[part] || {};
            current = current[part];
          }
        });
      });
      
      if (Object.keys(sample).length > 0) {
        setRequestBody(JSON.stringify(sample, null, 2));
      } else {
        setRequestBody("{\n  \"message\": \"Hello World\"\n}");
      }
    }
  }, [selectedRouteId]);

  const handleTest = async () => {
    if (!selectedRoute) return;
    
    setLoading(true);
    setResponse(null);
    setError(null);
    
    try {
      // Parse body
      let data;
      try {
        data = JSON.parse(requestBody);
      } catch (e) {
        setError("JSON 格式错误");
        setLoading(false);
        return;
      }

      // Use the test helper which calls /api/admin/test-invoke
      const startTime = Date.now();
      const res = await testInvokeRoute(selectedRoute.id, data);
      const endTime = Date.now();

      setResponse({
        status: res.status,
        statusText: res.statusText,
        data: res.data,
        latency: endTime - startTime
      });
    } catch (err: any) {
      console.error("Test Request Failed:", err);
      const errorMsg = err.response?.data?.error || err.message || "Unknown Error";
      const errorDetails = err.response?.data?.details ? JSON.stringify(err.response.data.details) : "";
      
      let friendlyMsg = errorMsg;
      if (errorMsg.includes("ECONNREFUSED")) {
        friendlyMsg = `无法连接到目标服务器 (${selectedRoute.targetUrl})。请检查目标服务是否已启动。`;
      }
      
      setError(friendlyMsg + (errorDetails ? `\nDetails: ${errorDetails}` : ""));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <h2 className="text-2xl font-bold text-gray-800">路由测试台</h2>
      <p className="text-gray-600">在此处模拟发送请求，验证路由转发和参数转换规则是否生效。</p>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Left: Request Config */}
        <div className="space-y-4">
          <div className="bg-white p-6 rounded-lg shadow space-y-4">
            <h3 className="text-lg font-medium text-gray-900 border-b pb-2">请求配置</h3>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">选择路由</label>
              <select
                value={selectedRouteId}
                onChange={(e) => setSelectedRouteId(e.target.value)}
                className="block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm border p-2"
              >
                <option value="">-- 请选择要测试的路由 --</option>
                {routes.map(r => (
                  <option key={r.id} value={r.id}>
                    {r.name} ({r.method} {r.sourcePath})
                  </option>
                ))}
              </select>
            </div>

            {selectedRoute && (
              <div className="bg-blue-50 p-3 rounded text-sm text-blue-800 space-y-1">
                <div className="flex gap-2">
                  <span className="font-semibold">目标地址:</span>
                  <span className="truncate" title={selectedRoute.targetUrl}>{selectedRoute.targetUrl}</span>
                </div>
                <div className="flex gap-2">
                  <span className="font-semibold">参数映射:</span>
                  <span>{selectedRoute.mappingConfig?.mappings?.length || 0} 个规则</span>
                </div>
              </div>
            )}

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">请求体 (JSON)</label>
              <textarea
                value={requestBody}
                onChange={(e) => setRequestBody(e.target.value)}
                rows={10}
                className="block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm border p-2 font-mono"
              />
            </div>

            <button
              onClick={handleTest}
              disabled={!selectedRoute || loading}
              className="w-full flex justify-center items-center gap-2 px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 disabled:opacity-50"
            >
              {loading ? "发送中..." : (
                <>
                  <Play size={16} /> 发送测试请求
                </>
              )}
            </button>
          </div>
        </div>

        {/* Right: Response */}
        <div className="space-y-4">
          <div className="bg-white p-6 rounded-lg shadow h-full flex flex-col">
            <h3 className="text-lg font-medium text-gray-900 border-b pb-2 mb-4">响应结果</h3>
            
            {error ? (
              <div className="flex items-start gap-2 text-red-600 bg-red-50 p-4 rounded">
                <XCircle size={20} className="mt-0.5" />
                <div>
                  <div className="font-semibold">测试失败</div>
                  <div className="text-sm">{error}</div>
                </div>
              </div>
            ) : response ? (
              <div className="space-y-4 flex-1 flex flex-col">
                <div className="flex items-center gap-4 text-sm">
                  <span className={`px-2 py-1 rounded font-bold ${
                    response.status >= 200 && response.status < 300 ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                  }`}>
                    {response.status} {response.statusText}
                  </span>
                  <span className="text-gray-500">耗时: {response.latency}ms</span>
                </div>

                <div className="flex-1 bg-gray-50 rounded p-4 border overflow-auto">
                  <pre className="text-xs text-gray-800 font-mono whitespace-pre-wrap">
                    {JSON.stringify(response.data, null, 2)}
                  </pre>
                </div>
              </div>
            ) : (
              <div className="flex-1 flex flex-col items-center justify-center text-gray-400">
                <ArrowRight size={48} className="mb-2 opacity-20" />
                <p>选择路由并发送请求后<br/>在此处查看结果</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
