import { useState, useEffect } from "react";
import { getRoutes, type Route, type FieldType, api } from "@/lib/api";
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

  // Generate mock value based on field name and type
  const generateMockValue = (fieldPath: string, fieldType?: FieldType): any => {
    const parts = fieldPath.split('.');
    const leafKey = parts[parts.length - 1].replace('[]', '');

    switch (fieldType) {
      case 'string':
        return leafKey;
      case 'integer':
        return 12345;
      case 'decimal':
        return 123.45;
      case 'boolean':
        return true;
      case 'datetime':
        return new Date().toISOString();
      case 'date':
        return new Date().toISOString().split('T')[0];
      case 'time':
        return new Date().toTimeString().split(' ')[0];
      case 'array':
        return [];
      case 'object':
        return {};
      default:
        return leafKey;
    }
  };

  useEffect(() => {
    if (selectedRoute) {
      const sample: Record<string, any> = {};
      const allFields: Array<{path: string[], isArray: boolean, mockValue: any, fieldType?: FieldType}> = [];
      
      // Collect all field information
      selectedRoute.mappingConfig?.mappings?.forEach(m => {
        const source = m.source;
        // Use defaultValue if set, otherwise generate based on key/type
        const mockValue = (m.defaultValue !== undefined && m.defaultValue !== "") 
          ? m.defaultValue 
          : generateMockValue(source, m.sourceType);
        
        if (source.includes('[]')) {
          // Array field: detail[].quantity
          const arrayPath = source.split('[]')[0];
          const subPath = source.split('[]')[1]?.substring(1);
          
          if (subPath) {
            const fullPath = arrayPath.split('.').concat(subPath.split('.'));
            allFields.push({path: fullPath, isArray: true, mockValue, fieldType: m.sourceType});
          }
        } else {
          // Regular field (or array field without [])
          const fullPath = source.split('.');
          // Check if this field is explicitly marked as array type
          const isArrayType = m.sourceType === 'array';
          allFields.push({path: fullPath, isArray: isArrayType, mockValue, fieldType: m.sourceType});
        }
      });
      
      // Determine which paths are parent paths (should be objects/arrays, not values)
      const parentPaths = new Set<string>();
      allFields.forEach(field => {
        for (let i = 0; i < field.path.length - 1; i++) {
          const parentPath = field.path.slice(0, i + 1).join('.');
          parentPaths.add(parentPath);
        }
      });
      
      // Group array fields by their array path
      const arrayGroups = new Map<string, Array<{subPath: string[], mockValue: any}>>();
      allFields.forEach(field => {
        if (field.isArray) {
          // Find the array root by checking mappings
          const matchingMapping = selectedRoute.mappingConfig?.mappings?.find(m => {
            if (!m.source.includes('[]')) return false;
            const arrayPath = m.source.split('[]')[0];
            const subPath = m.source.split('[]')[1]?.substring(1);
            if (!subPath) return false;
            const fullPath = arrayPath.split('.').concat(subPath.split('.'));
            return fullPath.join('.') === field.path.join('.');
          });
          
          if (matchingMapping) {
            const arrayPath = matchingMapping.source.split('[]')[0];
            if (!arrayGroups.has(arrayPath)) {
              arrayGroups.set(arrayPath, []);
            }
            const subPath = matchingMapping.source.split('[]')[1]?.substring(1);
            if (subPath) {
              arrayGroups.get(arrayPath)!.push({subPath: subPath.split('.'), mockValue: field.mockValue});
            }
          }
        }
      });
      
      // Build the structure
      // First create array structures
      arrayGroups.forEach((fields, arrayPath) => {
        const arrayParts = arrayPath.split('.');
        let current = sample;
        
        // Navigate to parent
        for (let i = 0; i < arrayParts.length - 1; i++) {
          if (!current[arrayParts[i]] || typeof current[arrayParts[i]] !== 'object') {
            current[arrayParts[i]] = {};
          }
          current = current[arrayParts[i]];
        }
        
        // Create array with one sample item
        const arrayField = arrayParts[arrayParts.length - 1];
        const arrayItem: Record<string, any> = {};
        
        // Fill in array item fields
        fields.forEach(({subPath, mockValue}) => {
          let target = arrayItem;
          for (let i = 0; i < subPath.length - 1; i++) {
            if (!target[subPath[i]]) {
              target[subPath[i]] = {};
            }
            target = target[subPath[i]];
          }
          target[subPath[subPath.length - 1]] = mockValue;
        });
        
        current[arrayField] = [arrayItem];
      });
      
      // Then create regular fields (skip those inside arrays)
      allFields.forEach(field => {
        if (field.isArray && !field.path.join('.').includes('[]')) {
          // This is a standalone array field (e.g., "detail" with type=array)
          // Not an array element field (e.g., "detail[].quantity")
          const pathStr = field.path.join('.');
          
          // Check if already processed as part of array group
          if (arrayGroups.has(pathStr)) return;
          
          // Create empty array or array with sample data
          let current = sample;
          for (let i = 0; i < field.path.length - 1; i++) {
            const part = field.path[i];
            if (!current[part] || typeof current[part] !== 'object') {
              current[part] = {};
            }
            current = current[part];
          }
          
          const lastPart = field.path[field.path.length - 1];
          // Generate array with one sample item based on field type
          if (field.fieldType === 'array') {
            // Check if there's a default value
            if (Array.isArray(field.mockValue)) {
              current[lastPart] = field.mockValue;
            } else {
              // Create array with one item containing the key name
              current[lastPart] = [lastPart];
            }
          } else {
            current[lastPart] = field.mockValue;
          }
          return;
        }
        
        // Skip array element fields (already handled)
        const pathStr = field.path.join('.');
        if (pathStr.includes('[]') || field.isArray) return;
        
        // Check if this field is inside an array
        let isInsideArray = false;
        arrayGroups.forEach((_, arrayPath) => {
          if (pathStr.startsWith(arrayPath + '.')) {
            isInsideArray = true;
          }
        });
        
        if (isInsideArray) return; // Skip, already handled in array creation
        
        // Check if this path is a parent path (should not be set to a value)
        if (parentPaths.has(pathStr)) return;
        
        // Set the value
        let current = sample;
        for (let i = 0; i < field.path.length - 1; i++) {
          const part = field.path[i];
          
          // Don't overwrite arrays!
          if (Array.isArray(current[part])) {
            // This part is already an array, skip this field
            return;
          }
          
          if (!current[part] || typeof current[part] !== 'object') {
            current[part] = {};
          }
          current = current[part];
        }
        
        const lastPart = field.path[field.path.length - 1];
        // Don't overwrite arrays!
        if (Array.isArray(current[lastPart])) {
          return;
        }
        current[lastPart] = field.mockValue;
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

      // Call the proxy directly
      const startTime = Date.now();
      
      // Use the source path directly. 
      // api instance adds /api prefix which is needed for Vite proxy to reach backend.
      // Backend ProxyController will handle matching.
      const res = await api.request({
        method: selectedRoute.method,
        url: selectedRoute.sourcePath,
        data: data,
        headers: {
          'X-Connector-Test': 'true'
        },
        validateStatus: () => true // Always resolve so we can see error responses
      });
      
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
