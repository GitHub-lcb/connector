import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { createRoute, getRoute, updateRoute, type Route, type MappingRule, type TransformationStep, type SecurityConfig, type FieldType } from "@/lib/api";
import { ArrowLeft, Plus, Trash2, Save, Settings, X, Lock, Upload, FileJson, List, Download, Trash } from "lucide-react";

export default function RouteEditor() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isNew = !id || id === 'new';
  
  const [loading, setLoading] = useState(!isNew);
  const [saving, setSaving] = useState(false);
  const [editingIndex, setEditingIndex] = useState<number | null>(null);
  const [showBulkImport, setShowBulkImport] = useState(false);
  const [bulkJsonInput, setBulkJsonInput] = useState('');
  const [bulkImportError, setBulkImportError] = useState('');
  const [viewMode, setViewMode] = useState<'flat' | 'grouped'>('grouped');
  const [collapsedGroups, setCollapsedGroups] = useState<Set<string>>(new Set());
  
  const [formData, setFormData] = useState({
    name: '',
    sourcePath: '',
    targetUrl: '',
    method: 'POST',
    status: 'active',
    mappingConfig: {
      mappings: [] as MappingRule[],
      security: {
        type: 'NONE',
        publicKey: '',
        secretKey: '',
        encryptedField: ''
      } as SecurityConfig,
      headers: [] as { key: string, value: string, description?: string }[]
    }
  });

  useEffect(() => {
    if (!isNew && id) {
      getRoute(id).then(data => {
        setFormData({
          name: data.name,
          sourcePath: data.sourcePath,
          targetUrl: data.targetUrl,
          method: data.method,
          status: data.status,
          mappingConfig: {
            mappings: data.mappingConfig?.mappings || [],
            security: data.mappingConfig?.security || { type: 'NONE', publicKey: '', secretKey: '', encryptedField: '' },
            headers: data.mappingConfig?.headers || []
          }
        });
      }).catch(err => {
        console.error(err);
        alert("加载路由失败");
        navigate('/routes');
      }).finally(() => setLoading(false));
    }
  }, [id, isNew, navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      if (isNew) {
        await createRoute(formData as any);
      } else if (id) {
        await updateRoute(id, formData as any);
      }
      navigate('/routes');
    } catch (error) {
      console.error(error);
      alert("保存路由失败");
    } finally {
      setSaving(false);
    }
  };

  const addMapping = () => {
    setFormData(prev => ({
      ...prev,
      mappingConfig: {
        ...prev.mappingConfig,
        mappings: [...prev.mappingConfig.mappings, { source: '', target: '', sourceType: 'string', targetType: 'string' }]
      }
    }));
  };

  const updateMapping = (index: number, updates: Partial<MappingRule>) => {
    const newMappings = [...formData.mappingConfig.mappings];
    newMappings[index] = { ...newMappings[index], ...updates };
    setFormData(prev => ({
      ...prev,
      mappingConfig: {
        ...prev.mappingConfig,
        mappings: newMappings
      }
    }));
  };

  const removeMapping = (index: number) => {
    const newMappings = formData.mappingConfig.mappings.filter((_, i) => i !== index);
    setFormData(prev => ({
      ...prev,
      mappingConfig: {
        ...prev.mappingConfig,
        mappings: newMappings
      }
    }));
    if (editingIndex === index) setEditingIndex(null);
  };

  // Extract all field paths from a JSON object (including nested fields)
  const extractFieldPaths = (obj: any, prefix = ''): string[] => {
    const paths: string[] = [];
    
    if (Array.isArray(obj)) {
      // Mark as array and extract fields from first item
      if (prefix) {
        paths.push(prefix); // Add the array field itself
      }
      if (obj.length > 0) {
        // Extract fields from array items, mark with []
        const arrayItemPaths = extractFieldPaths(obj[0], prefix ? `${prefix}[]` : '[]');
        paths.push(...arrayItemPaths);
      }
    } else if (typeof obj === 'object' && obj !== null) {
      for (const key in obj) {
        const fullPath = prefix ? `${prefix}.${key}` : key;
        
        if (Array.isArray(obj[key])) {
          // This field is an array
          paths.push(fullPath);
          if (obj[key].length > 0) {
            paths.push(...extractFieldPaths(obj[key][0], `${fullPath}[]`));
          }
        } else if (typeof obj[key] === 'object' && obj[key] !== null) {
          // This field is an object, recurse
          paths.push(...extractFieldPaths(obj[key], fullPath));
        } else {
          // Leaf field
          paths.push(fullPath);
        }
      }
    }
    
    return paths;
  };

  // Bulk Import Functionality
  const handleBulkImport = () => {
    setBulkImportError('');
    try {
      const parsed = JSON.parse(bulkJsonInput);
      
      // Support different formats
      let newMappings: MappingRule[] = [];
      
      // Format 1: Array of mapping objects [{source: "a", target: "b"}, ...]
      if (Array.isArray(parsed) && parsed.length > 0 && parsed[0].source && parsed[0].target) {
        newMappings = parsed.map(item => ({
          source: item.source || '',
          target: item.target || '',
          defaultValue: item.defaultValue,
          transformations: item.transformations
        }));
      }
      // Format 2: Object mapping {"a": "test", "b": "value"}
      else if (typeof parsed === 'object' && !Array.isArray(parsed)) {
        // Check if it's a key-value mapping or a data structure to extract
        const firstValue = Object.values(parsed)[0];
        
        // If all values are strings/numbers/booleans, treat as mapping
        const isSimpleMapping = Object.values(parsed).every(v => 
          typeof v === 'string' || typeof v === 'number' || typeof v === 'boolean'
        );
        
        if (isSimpleMapping) {
          // Format: {"sourceField": "targetField"}
          newMappings = Object.entries(parsed).map(([source, target]) => ({
            source,
            target: String(target)
          }));
        } else {
          // Format: Extract all fields from JSON structure (source = target)
          const fieldPaths = extractFieldPaths(parsed);
          newMappings = fieldPaths.map(path => ({
            source: path,
            target: path,  // Default: same as source
            sourceType: 'string' as FieldType,
            targetType: 'string' as FieldType
          }));
        }
      }
      
      if (newMappings.length === 0) {
        setBulkImportError('未识别到有效的映射规则');
        return;
      }
      
      // Remove duplicates: check if source field already exists
      const existingSources = new Set(formData.mappingConfig.mappings.map(m => m.source));
      const uniqueNewMappings = newMappings.filter(m => !existingSources.has(m.source));
      
      const duplicateCount = newMappings.length - uniqueNewMappings.length;
      
      if (uniqueNewMappings.length === 0) {
        setBulkImportError(`所有字段已存在，跳过导入 (${duplicateCount} 个重复)`);
        return;
      }
      
      // Append to existing mappings
      setFormData(prev => ({
        ...prev,
        mappingConfig: {
          ...prev.mappingConfig,
          mappings: [...prev.mappingConfig.mappings, ...uniqueNewMappings]
        }
      }));
      
      // Show success message
      const message = duplicateCount > 0 
        ? `成功导入 ${uniqueNewMappings.length} 个字段，跳过 ${duplicateCount} 个重复字段`
        : `成功导入 ${uniqueNewMappings.length} 个字段`;
      
      alert(message);
      
      // Reset and close
      setBulkJsonInput('');
      setShowBulkImport(false);
      
    } catch (error) {
      setBulkImportError('JSON 格式错误: ' + (error as Error).message);
    }
  };

  const handleExportMappings = () => {
    const mappingsJson = JSON.stringify(formData.mappingConfig.mappings, null, 2);
    
    // Create a blob and download as file
    const blob = new Blob([mappingsJson], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `mappings-${formData.name || 'route'}-${Date.now()}.json`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
    
    // Also copy to clipboard for convenience
    navigator.clipboard.writeText(mappingsJson);
    alert('映射配置已导出为文件，并复制到剪贴板');
  };

  const handleClearAllMappings = () => {
    if (formData.mappingConfig.mappings.length === 0) {
      return;
    }
    
    const confirmed = window.confirm(
      `确定要删除所有 ${formData.mappingConfig.mappings.length} 个字段映射吗？此操作不可撤销。`
    );
    
    if (confirmed) {
      setFormData(prev => ({
        ...prev,
        mappingConfig: {
          ...prev.mappingConfig,
          mappings: []
        }
      }));
      setEditingIndex(null);
      alert('已清空所有字段映射');
    }
  };

  // Group mappings by top-level field
  const getGroupedMappings = () => {
    const groups: Record<string, { mappings: Array<{ mapping: MappingRule; index: number }>, prefix: string, isArray: boolean }> = {};
    
    formData.mappingConfig.mappings.forEach((mapping, index) => {
      // Handle array notation like "detail[]"
      const firstLevel = mapping.source.split(/[.\[\]]/)[0];
      const isArray = mapping.source.includes('[]') || mapping.source.match(new RegExp(`^${firstLevel}\\[\\]`));
      
      if (!groups[firstLevel]) {
        groups[firstLevel] = { mappings: [], prefix: firstLevel, isArray: false };
      }
      
      // Mark group as array if any mapping in this group is array-related
      if (isArray || mapping.source.startsWith(`${firstLevel}[]`)) {
        groups[firstLevel].isArray = true;
      }
      
      groups[firstLevel].mappings.push({ mapping, index });
    });
    
    return groups;
  };

  const toggleGroup = (groupName: string) => {
    const newCollapsed = new Set(collapsedGroups);
    if (newCollapsed.has(groupName)) {
      newCollapsed.delete(groupName);
    } else {
      newCollapsed.add(groupName);
    }
    setCollapsedGroups(newCollapsed);
  };

  // Get indentation level for a field path
  const getIndentLevel = (path: string) => {
    // Count dots and array brackets
    const dotCount = (path.match(/\./g) || []).length;
    const arrayCount = (path.match(/\[\]/g) || []).length;
    return dotCount + arrayCount;
  };

  // Check if a field path represents an array element
  const isArrayField = (path: string) => {
    return path.includes('[]');
  };

  // Get type label with emoji
  const getTypeLabel = (type?: FieldType) => {
    if (!type) return '';
    const typeMap: Record<FieldType, { label: string; emoji: string; color: string }> = {
      string: { label: '字符串', emoji: '📝', color: 'text-blue-600 bg-blue-50' },
      integer: { label: '整数', emoji: '🔢', color: 'text-green-600 bg-green-50' },
      decimal: { label: '小数', emoji: '💯', color: 'text-teal-600 bg-teal-50' },
      boolean: { label: '布尔', emoji: '✓', color: 'text-purple-600 bg-purple-50' },
      datetime: { label: '日期时间', emoji: '📅', color: 'text-orange-600 bg-orange-50' },
      date: { label: '日期', emoji: '📆', color: 'text-orange-600 bg-orange-50' },
      time: { label: '时间', emoji: '⏰', color: 'text-orange-600 bg-orange-50' },
      array: { label: '数组', emoji: '📋', color: 'text-pink-600 bg-pink-50' },
      object: { label: '对象', emoji: '📦', color: 'text-indigo-600 bg-indigo-50' },
      any: { label: '任意', emoji: '🔄', color: 'text-gray-600 bg-gray-50' }
    };
    return typeMap[type];
  };

  // Render field path with visual hierarchy
  const renderFieldPath = (path: string, isSource: boolean = true) => {
    // Handle array notation: detail[].quantity -> detail[].quantity
    const parts = path.split(/(\.)|(\[\])/g).filter(Boolean);
    
    return (
      <span className="font-mono text-sm flex items-center gap-0.5">
        {parts.map((part, i) => {
          if (part === '.') {
            return <span key={i} className="text-gray-400">.</span>;
          } else if (part === '[]') {
            return (
              <span key={i} className="inline-flex items-center">
                <span className="text-orange-600 font-bold">[</span>
                <List size={12} className="text-orange-600 mx-0.5" />
                <span className="text-orange-600 font-bold">]</span>
              </span>
            );
          } else {
            const isLast = i === parts.length - 1;
            return (
              <span key={i} className={isLast ? 'font-semibold text-blue-700' : 'text-gray-600'}>
                {part}
              </span>
            );
          }
        })}
      </span>
    );
  };

  // Get transformation description
  const getTransformationDesc = (type: string): string => {
    const descriptions: Record<string, string> = {
      string: '转换为字符串类型',
      number: '转换为数字类型',
      boolean: '转换为布尔类型',
      uppercase: '转换为大写',
      lowercase: '转换为小写',
      trim: '去除首尾空格',
      substring: '截取子串 (参数: 起始,结束)',
      concat: '拼接字符串 (参数: 要拼接的字符串)',
      replace: '替换字符串 (参数: 查找,替换)',
      split: '分割字符串 (参数: 分隔符)',
      join: '连接数组 (参数: 连接符)',
      base64_encode: 'Base64 编码',
      base64_decode: 'Base64 解码',
      json_parse: 'JSON 字符串解析为对象',
      json_stringify: '对象转换为 JSON 字符串',
      date_format: '日期格式化 (参数: 格式)',
      multiply: '乘法运算 (参数: 乘数)',
      divide: '除法运算 (参数: 除数)',
      add: '加法运算 (参数: 加数)',
      subtract: '减法运算 (参数: 减数)',
      round: '四舍五入 (参数: 小数位数)',
      floor: '向下取整',
      ceil: '向上取整',
      abs: '取绝对值',
      default_value: '设置默认值 (参数: 默认值)',
      regex_extract: '正则提取 (参数: 正则表达式)',
      regex_replace: '正则替换 (参数: 正则,替换值)'
    };
    return descriptions[type] || type;
  };

  // Transformation Helpers
  const addTransformation = (mappingIndex: number) => {
    const mapping = formData.mappingConfig.mappings[mappingIndex];
    const newTransformations = [...(mapping.transformations || []), { type: 'string', params: [] } as TransformationStep];
    updateMapping(mappingIndex, { transformations: newTransformations });
  };

  const updateTransformation = (mappingIndex: number, transIndex: number, updates: Partial<TransformationStep>) => {
    const mapping = formData.mappingConfig.mappings[mappingIndex];
    const newTransformations = [...(mapping.transformations || [])];
    newTransformations[transIndex] = { ...newTransformations[transIndex], ...updates };
    updateMapping(mappingIndex, { transformations: newTransformations });
  };

  const removeTransformation = (mappingIndex: number, transIndex: number) => {
    const mapping = formData.mappingConfig.mappings[mappingIndex];
    const newTransformations = (mapping.transformations || []).filter((_, i) => i !== transIndex);
    updateMapping(mappingIndex, { transformations: newTransformations });
  };

  const addHeader = () => {
    setFormData(prev => ({
      ...prev,
      mappingConfig: {
        ...prev.mappingConfig,
        headers: [...(prev.mappingConfig.headers || []), { key: '', value: '' }]
      }
    }));
  };

  const updateHeader = (index: number, key: string, value: string, description?: string) => {
    const newHeaders = [...(formData.mappingConfig.headers || [])];
    newHeaders[index] = { ...newHeaders[index], key, value, description };
    setFormData(prev => ({
      ...prev,
      mappingConfig: {
        ...prev.mappingConfig,
        headers: newHeaders
      }
    }));
  };

  const removeHeader = (index: number) => {
    const newHeaders = (formData.mappingConfig.headers || []).filter((_, i) => i !== index);
    setFormData(prev => ({
      ...prev,
      mappingConfig: {
        ...prev.mappingConfig,
        headers: newHeaders
      }
    }));
  };

  if (loading) return <div className="p-8 text-center">加载中...</div>;

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="flex items-center gap-4">
        <button onClick={() => navigate('/routes')} className="p-2 hover:bg-gray-100 rounded-full">
          <ArrowLeft size={20} />
        </button>
        <h2 className="text-2xl font-bold text-gray-800">{isNew ? '新建路由' : '编辑路由'}</h2>
      </div>

      <form onSubmit={handleSubmit} className="space-y-8">
        {/* Basic Info */}
        <div className="bg-white p-6 rounded-lg shadow space-y-4">
          <h3 className="text-lg font-medium text-gray-900 border-b pb-2">基本信息</h3>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700">路由名称</label>
              <input
                type="text"
                required
                value={formData.name}
                onChange={e => setFormData({...formData, name: e.target.value})}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm border p-2"
                placeholder="例如：订单同步"
              />
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700">HTTP 方法</label>
              <select
                value={formData.method}
                onChange={e => setFormData({...formData, method: e.target.value})}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm border p-2"
              >
                <option value="GET">GET</option>
                <option value="POST">POST</option>
                <option value="PUT">PUT</option>
                <option value="DELETE">DELETE</option>
                <option value="PATCH">PATCH</option>
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700">源路径 (连接器)</label>
              <input
                type="text"
                required
                value={formData.sourcePath}
                onChange={e => setFormData({...formData, sourcePath: e.target.value})}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm border p-2"
                placeholder="/api/v1/orders"
              />
              <p className="text-xs text-gray-500 mt-1">连接器监听的请求路径</p>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700">目标地址 (云仓)</label>
              <input
                type="url"
                required
                value={formData.targetUrl}
                onChange={e => setFormData({...formData, targetUrl: e.target.value})}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm border p-2"
                placeholder="https://cloud-wms.com/api/orders"
              />
            </div>
          </div>
        </div>

        {/* Security Config */}
        <div className="bg-white p-6 rounded-lg shadow space-y-4">
          <h3 className="text-lg font-medium text-gray-900 border-b pb-2 flex items-center gap-2">
            <Lock size={18} /> 安全配置
          </h3>
          
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700">加密方式</label>
              <select
                value={formData.mappingConfig.security?.type || 'NONE'}
                onChange={e => setFormData({
                  ...formData,
                  mappingConfig: {
                    ...formData.mappingConfig,
                    security: { ...formData.mappingConfig.security!, type: e.target.value as any }
                  }
                })}
                className="mt-1 block w-full md:w-1/3 rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm border p-2"
              >
                <option value="NONE">不加密</option>
                <option value="RSA">RSA 非对称加密</option>
                <option value="AES">AES 对称加密 (WIP)</option>
                <option value="HMAC">HMAC 签名 (WIP)</option>
              </select>
            </div>

            {formData.mappingConfig.security?.type === 'RSA' && (
              <>
                <div>
                  <label className="block text-sm font-medium text-gray-700">公钥 (Public Key)</label>
                  <textarea
                    rows={4}
                    value={formData.mappingConfig.security.publicKey || ''}
                    onChange={e => setFormData({
                      ...formData,
                      mappingConfig: {
                        ...formData.mappingConfig,
                        security: { ...formData.mappingConfig.security!, publicKey: e.target.value }
                      }
                    })}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm border p-2 font-mono text-xs"
                    placeholder="-----BEGIN PUBLIC KEY-----&#10;...&#10;-----END PUBLIC KEY-----"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700">加密后字段名</label>
                  <input
                    type="text"
                    value={formData.mappingConfig.security.encryptedField || ''}
                    onChange={e => setFormData({
                      ...formData,
                      mappingConfig: {
                        ...formData.mappingConfig,
                        security: { ...formData.mappingConfig.security!, encryptedField: e.target.value }
                      }
                    })}
                    className="mt-1 block w-full md:w-1/3 rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm border p-2"
                    placeholder="默认为 data (例如: { data: 'encrypted...' })"
                  />
                  <p className="text-xs text-gray-500 mt-1">指定加密后的字符串在请求体中的 Key。如果不填，默认包裹在 "data" 字段中。</p>
                </div>
              </>
            )}
          </div>
        </div>

        {/* Custom Headers */}
        <div className="bg-white p-6 rounded-lg shadow space-y-4">
          <div className="flex justify-between items-center border-b pb-2">
            <h3 className="text-lg font-medium text-gray-900">自定义请求头</h3>
            <button type="button" onClick={addHeader} className="text-sm text-blue-600 hover:text-blue-800 flex items-center gap-1">
              <Plus size={16} /> 添加 Header
            </button>
          </div>
          
          <div className="space-y-3">
            {(!formData.mappingConfig.headers || formData.mappingConfig.headers.length === 0) && (
              <p className="text-sm text-gray-500 italic">未配置自定义 Header。</p>
            )}
            
            {formData.mappingConfig.headers?.map((header, index) => (
              <div key={index} className="flex gap-4 items-center bg-gray-50 p-3 rounded border border-gray-200">
                <div className="flex-1">
                  <input
                    type="text"
                    placeholder="Header Key (e.g. X-API-Token)"
                    value={header.key}
                    onChange={e => updateHeader(index, e.target.value, header.value, header.description)}
                    className="block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm border p-2"
                  />
                </div>
                <div className="flex-1">
                  <input
                    type="text"
                    placeholder="Value"
                    value={header.value}
                    onChange={e => updateHeader(index, header.key, e.target.value, header.description)}
                    className="block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm border p-2"
                  />
                </div>
                <div className="flex-1">
                  <input
                    type="text"
                    placeholder="说明 (可选)"
                    value={header.description || ''}
                    onChange={e => updateHeader(index, header.key, header.value, e.target.value)}
                    className="block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm border p-2"
                  />
                </div>
                <button type="button" onClick={() => removeHeader(index)} className="text-red-500 hover:text-red-700 p-2" title="删除">
                  <Trash2 size={18} />
                </button>
              </div>
            ))}
          </div>
        </div>

        {/* Mappings */}
        <div className="bg-white p-6 rounded-lg shadow space-y-4">
          <div className="flex justify-between items-center border-b pb-2">
            <div className="flex items-center gap-4">
              <h3 className="text-lg font-medium text-gray-900">字段映射 (JSON)</h3>
              {formData.mappingConfig.mappings.length > 0 && (
                <div className="flex gap-1 bg-gray-100 rounded-md p-1">
                  <button
                    type="button"
                    onClick={() => setViewMode('flat')}
                    className={`px-3 py-1 text-xs rounded transition-colors ${
                      viewMode === 'flat'
                        ? 'bg-white text-blue-600 shadow-sm font-medium'
                        : 'text-gray-600 hover:text-gray-900'
                    }`}
                  >
                    平铺视图
                  </button>
                  <button
                    type="button"
                    onClick={() => setViewMode('grouped')}
                    className={`px-3 py-1 text-xs rounded transition-colors ${
                      viewMode === 'grouped'
                        ? 'bg-white text-blue-600 shadow-sm font-medium'
                        : 'text-gray-600 hover:text-gray-900'
                    }`}
                  >
                    分组视图
                  </button>
                </div>
              )}
            </div>
            <div className="flex gap-2">
              <button type="button" onClick={addMapping} className="text-sm text-blue-600 hover:text-blue-800 flex items-center gap-1">
                <Plus size={16} /> 添加字段
              </button>
              <button type="button" onClick={() => setShowBulkImport(!showBulkImport)} className="text-sm text-green-600 hover:text-green-800 flex items-center gap-1">
                <Upload size={16} /> 批量导入
              </button>
              {formData.mappingConfig.mappings.length > 0 && (
                <>
                  <button type="button" onClick={handleExportMappings} className="text-sm text-purple-600 hover:text-purple-800 flex items-center gap-1">
                    <Download size={16} /> 导出
                  </button>
                  <button type="button" onClick={handleClearAllMappings} className="text-sm text-red-600 hover:text-red-800 flex items-center gap-1">
                    <Trash size={16} /> 清空全部
                  </button>
                </>
              )}
            </div>
          </div>
          
          {/* Bulk Import Panel */}
          {showBulkImport && (
            <div className="bg-blue-50 p-4 rounded-lg border border-blue-200 space-y-3">
              <div className="flex justify-between items-start">
                <div>
                  <h4 className="font-medium text-sm text-gray-800">批量导入字段映射</h4>
                  <p className="text-xs text-gray-600 mt-1">支持三种格式：</p>
                  <ul className="text-xs text-gray-600 mt-1 space-y-1 list-disc list-inside">
                    <li>📋 <strong>字段提取</strong>: 粘贴JSON数据，自动提取所有字段（源=目标）</li>
                    <li>🔗 <strong>键值映射</strong>: <code className="bg-white px-1 rounded">{'{"源字段": "目标字段"}'}</code></li>
                    <li>📝 <strong>数组映射</strong>: <code className="bg-white px-1 rounded">{'[{"source": "a", "target": "b"}]'}</code></li>
                  </ul>
                </div>
                <button type="button" onClick={() => { setShowBulkImport(false); setBulkImportError(''); }} className="text-gray-400 hover:text-gray-600">
                  <X size={18} />
                </button>
              </div>
              
              <textarea
                value={bulkJsonInput}
                onChange={e => setBulkJsonInput(e.target.value)}
                placeholder='{&#10;  "receiptTime": "",&#10;  "receiptType": 32730,&#10;  "detail": [{&#10;    "quantity": 7232,&#10;    "skuCode": ""&#10;  }]&#10;}'
                className="w-full h-32 rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 text-sm font-mono border p-2"
              />
              
              {bulkImportError && (
                <div className="text-sm text-red-600 bg-red-50 p-2 rounded">
                  {bulkImportError}
                </div>
              )}
              
              <div className="flex justify-end gap-2">
                <button type="button" onClick={() => { setShowBulkImport(false); setBulkJsonInput(''); setBulkImportError(''); }} className="px-3 py-1.5 text-sm border border-gray-300 rounded hover:bg-gray-50">
                  取消
                </button>
                <button type="button" onClick={handleBulkImport} className="px-3 py-1.5 text-sm bg-green-600 text-white rounded hover:bg-green-700">
                  导入
                </button>
              </div>
            </div>
          )}
          
          <div className="space-y-3">
            {formData.mappingConfig.mappings.length === 0 && (
              <p className="text-sm text-gray-500 italic">未配置映射，请求体将原样转发。</p>
            )}
            
            {/* Flat View */}
            {viewMode === 'flat' && formData.mappingConfig.mappings.map((mapping, index) => (
              <div key={index} className="flex flex-col gap-2 bg-gray-50 p-3 rounded border border-gray-200">
                <div className="flex items-start gap-4">
                  <div className="flex-1 space-y-2">
                    <div className="text-xs text-gray-500 mb-1">源字段</div>
                    <input
                      type="text"
                      placeholder="源字段 (e.g. user.firstName)"
                      value={mapping.source}
                      onChange={e => updateMapping(index, { source: e.target.value })}
                      className="block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm border p-2 font-mono"
                    />
                    <select
                      value={mapping.sourceType || 'string'}
                      onChange={e => updateMapping(index, { sourceType: e.target.value as FieldType })}
                      className="block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 text-xs border p-1.5"
                    >
                      <option value="string">📝 字符串</option>
                      <option value="integer">🔢 整数</option>
                      <option value="decimal">💯 小数</option>
                      <option value="boolean">✓ 布尔</option>
                      <option value="datetime">📅 日期时间</option>
                      <option value="date">📆 日期</option>
                      <option value="time">⏰ 时间</option>
                      <option value="array">📋 数组</option>
                      <option value="object">📦 对象</option>
                      <option value="any">🔄 任意</option>
                    </select>
                  </div>
                  
                  <div className="text-gray-400 text-2xl mt-8">→</div>
                  
                  <div className="flex-1 space-y-2">
                    <div className="text-xs text-gray-500 mb-1">目标字段</div>
                    <input
                      type="text"
                      placeholder="目标字段 (e.g. customer_name)"
                      value={mapping.target}
                      onChange={e => updateMapping(index, { target: e.target.value })}
                      className="block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm border p-2 font-mono"
                    />
                    <select
                      value={mapping.targetType || 'string'}
                      onChange={e => updateMapping(index, { targetType: e.target.value as FieldType })}
                      className="block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 text-xs border p-1.5"
                    >
                      <option value="string">📝 字符串</option>
                      <option value="integer">🔢 整数</option>
                      <option value="decimal">💯 小数</option>
                      <option value="boolean">✓ 布尔</option>
                      <option value="datetime">📅 日期时间</option>
                      <option value="date">📆 日期</option>
                      <option value="time">⏰ 时间</option>
                      <option value="array">📋 数组</option>
                      <option value="object">📦 对象</option>
                      <option value="any">🔄 任意</option>
                    </select>
                  </div>
                  
                  <div className="flex gap-1 mt-8">
                    <button type="button" onClick={() => setEditingIndex(index === editingIndex ? null : index)} className={`p-2 rounded hover:bg-gray-200 ${editingIndex === index ? 'text-blue-600 bg-blue-100' : 'text-gray-500'}`} title="高级配置">
                      <Settings size={18} />
                    </button>
                    <button type="button" onClick={() => removeMapping(index)} className="text-red-500 hover:text-red-700 p-2" title="删除">
                      <Trash2 size={18} />
                    </button>
                  </div>
                </div>
                
                {/* Description */}
                <div className="mt-2">
                  <input
                    type="text"
                    placeholder="📝 字段描述 (可选)"
                    value={mapping.description || ''}
                    onChange={e => updateMapping(index, { description: e.target.value })}
                    className="block w-full rounded-md border-gray-200 shadow-sm focus:border-blue-400 focus:ring-blue-400 text-xs border p-2 bg-white text-gray-600 italic"
                  />
                </div>
                
                {/* Advanced Config Panel */}
                {editingIndex === index && (
                  <div className="mt-2 p-4 bg-white rounded border border-gray-200 space-y-4">
                    <h4 className="font-medium text-sm text-gray-700">高级配置</h4>
                    
                    {/* Default Value */}
                    <div>
                      <label className="block text-xs font-medium text-gray-500">默认值 (当源字段不存在时使用)</label>
                      <input
                        type="text"
                        value={mapping.defaultValue || ''}
                        onChange={e => updateMapping(index, { defaultValue: e.target.value })}
                        className="mt-1 block w-full rounded-md border-gray-300 shadow-sm sm:text-sm border p-2"
                        placeholder="Default Value"
                      />
                    </div>

                    {/* Transformations */}
                    <div>
                      <div className="flex justify-between items-center mb-2">
                        <label className="block text-xs font-medium text-gray-500">数据转换 (按顺序执行)</label>
                        <button type="button" onClick={() => addTransformation(index)} className="text-xs text-blue-600 hover:text-blue-800 flex items-center gap-1">
                          <Plus size={12} /> 添加转换
                        </button>
                      </div>
                      
                      {(!mapping.transformations || mapping.transformations.length === 0) && (
                        <p className="text-xs text-gray-400 italic">无转换规则</p>
                      )}

                      <div className="space-y-2">
                        {mapping.transformations?.map((trans, tIndex) => (
                          <div key={tIndex} className="bg-white p-3 rounded border border-gray-200">
                            <div className="flex gap-2 items-start">
                              <span className="text-xs text-gray-400 w-6 mt-2">{tIndex + 1}.</span>
                              <div className="flex-1 space-y-2">
                                <div className="flex gap-2 items-center">
                                  <select
                                    value={trans.type}
                                    onChange={e => updateTransformation(index, tIndex, { type: e.target.value as any })}
                                    className="flex-1 rounded-md border-gray-300 shadow-sm sm:text-xs border p-2"
                                  >
                                    <optgroup label="类型转换">
                                      <option value="string">🔤 转字符串</option>
                                      <option value="number">🔢 转数字</option>
                                      <option value="boolean">☑ 转布尔</option>
                                    </optgroup>
                                    <optgroup label="字符串操作">
                                      <option value="uppercase">⬆ 大写</option>
                                      <option value="lowercase">⬇ 小写</option>
                                      <option value="trim">✂ 去空格</option>
                                      <option value="substring">🔪 截取</option>
                                      <option value="concat">➕ 拼接</option>
                                      <option value="replace">🔄 替换</option>
                                      <option value="split">✂ 分割</option>
                                    </optgroup>
                                    <optgroup label="数学运算">
                                      <option value="multiply">✖ 乘法</option>
                                      <option value="divide">➗ 除法</option>
                                      <option value="add">➕ 加法</option>
                                      <option value="subtract">➖ 减法</option>
                                      <option value="round">🔰 四舍五入</option>
                                      <option value="floor">⬇ 向下取整</option>
                                      <option value="ceil">⬆ 向上取整</option>
                                      <option value="abs">📊 绝对值</option>
                                    </optgroup>
                                    <optgroup label="编码/解码">
                                      <option value="base64_encode">🔒 Base64编码</option>
                                      <option value="base64_decode">🔓 Base64解码</option>
                                      <option value="json_parse">📜 JSON解析</option>
                                      <option value="json_stringify">📝 JSON序列化</option>
                                    </optgroup>
                                    <optgroup label="日期处理">
                                      <option value="date_format">📅 日期格式化</option>
                                    </optgroup>
                                    <optgroup label="其他">
                                      <option value="join">🔗 连接数组</option>
                                      <option value="default_value">🎯 默认值</option>
                                      <option value="regex_extract">🔍 正则提取</option>
                                      <option value="regex_replace">🔄 正则替换</option>
                                    </optgroup>
                                  </select>
                                  <button 
                                    type="button" 
                                    onClick={() => removeTransformation(index, tIndex)} 
                                    className="text-red-400 hover:text-red-600 p-1.5"
                                    title="删除转换"
                                  >
                                    <X size={14} />
                                  </button>
                                </div>
                                
                                {/* Description */}
                                <div className="text-xs text-gray-500 italic">
                                  {getTransformationDesc(trans.type)}
                                </div>
                                
                                {/* Params Inputs based on type */}
                                {['substring', 'concat', 'replace', 'split', 'join', 'date_format', 'multiply', 'divide', 'add', 'subtract', 'round', 'default_value', 'regex_extract', 'regex_replace'].includes(trans.type) && (
                                  <input 
                                    type="text" 
                                    placeholder={
                                      trans.type === 'substring' ? '例: 0,10' :
                                      trans.type === 'replace' ? '例: 旧值,新值' :
                                      trans.type === 'split' ? '例: ,' :
                                      trans.type === 'join' ? '例: ,' :
                                      trans.type === 'concat' ? '例: 追加的字符串' :
                                      trans.type === 'date_format' ? '例: YYYY-MM-DD HH:mm:ss' :
                                      trans.type === 'multiply' ? '例: 100' :
                                      trans.type === 'divide' ? '例: 100' :
                                      trans.type === 'add' ? '例: 10' :
                                      trans.type === 'subtract' ? '例: 10' :
                                      trans.type === 'round' ? '例: 2' :
                                      trans.type === 'default_value' ? '例: 默认值' :
                                      trans.type === 'regex_extract' ? '例: \\d+' :
                                      trans.type === 'regex_replace' ? '例: \\d+,XXX' :
                                      '参数'
                                    }
                                    value={trans.params?.join(',') || ''}
                                    onChange={e => updateTransformation(index, tIndex, { params: e.target.value.split(',') })}
                                    className="w-full rounded-md border-gray-300 shadow-sm sm:text-xs border p-2"
                                  />
                                )}
                              </div>
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                  </div>
                )}
              </div>
            ))}
            
            {/* Grouped View */}
            {viewMode === 'grouped' && (() => {
              const groups = getGroupedMappings();
              return Object.entries(groups).map(([groupName, groupData]) => (
                <div key={groupName} className="border border-gray-300 rounded-lg overflow-hidden">
                  {/* Group Header */}
                  <div
                    className="bg-gradient-to-r from-blue-50 to-indigo-50 px-4 py-3 flex items-center justify-between cursor-pointer hover:from-blue-100 hover:to-indigo-100 transition-colors"
                    onClick={() => toggleGroup(groupName)}
                  >
                    <div className="flex items-center gap-3">
                      <span className="text-lg">{collapsedGroups.has(groupName) ? '▶' : '▼'}</span>
                      <div className="flex items-center gap-2">
                        <span className="font-semibold text-gray-800 text-base">{groupName}</span>
                        {groupData.isArray && (
                          <span className="inline-flex items-center gap-1 px-2 py-0.5 bg-orange-100 text-orange-700 rounded text-xs font-medium">
                            <List size={12} />
                            数组
                          </span>
                        )}
                        <span className="text-xs text-gray-500">({groupData.mappings.length} 个字段)</span>
                      </div>
                    </div>
                  </div>
                  
                  {/* Group Content */}
                  {!collapsedGroups.has(groupName) && (
                    <div className="bg-white divide-y divide-gray-200">
                      {groupData.mappings.map(({ mapping, index }) => {
                        const indentLevel = getIndentLevel(mapping.source);
                        return (
                          <div
                            key={index}
                            className="p-3 hover:bg-gray-50 transition-colors"
                            style={{ paddingLeft: `${1 + indentLevel * 1.5}rem` }}
                          >
                            <div className="flex items-center gap-4">
                              {/* Indent indicator */}
                              {indentLevel > 0 && (
                                <div className="flex items-center">
                                  <div className="w-6 h-px bg-gray-300"></div>
                                  {isArrayField(mapping.source) && (
                                    <span className="ml-1 text-orange-500 text-xs flex items-center gap-1">
                                      <List size={10} />
                                    </span>
                                  )}
                                </div>
                              )}
                              
                              <div className="flex-1 min-w-0 space-y-2">
                                <div className="text-xs text-gray-500 mb-1 flex items-center gap-2">
                                  <span>源字段</span>
                                  {isArrayField(mapping.source) && (
                                    <span className="inline-flex items-center gap-1 px-1.5 py-0.5 bg-orange-100 text-orange-600 rounded text-xs">
                                      <List size={10} />
                                      数组元素
                                    </span>
                                  )}
                                  {mapping.sourceType && (() => {
                                    const typeInfo = getTypeLabel(mapping.sourceType);
                                    return (
                                      <span className={`inline-flex items-center gap-1 px-1.5 py-0.5 rounded text-xs ${typeInfo.color}`}>
                                        <span>{typeInfo.emoji}</span>
                                        <span>{typeInfo.label}</span>
                                      </span>
                                    );
                                  })()}
                                </div>
                                <input
                                  type="text"
                                  value={mapping.source}
                                  onChange={e => updateMapping(index, { source: e.target.value })}
                                  className="block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 text-sm border p-2 font-mono"
                                />
                                <select
                                  value={mapping.sourceType || 'string'}
                                  onChange={e => updateMapping(index, { sourceType: e.target.value as FieldType })}
                                  className="block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 text-xs border p-1.5"
                                >
                                  <option value="string">📝 字符串</option>
                                  <option value="integer">🔢 整数</option>
                                  <option value="decimal">💯 小数</option>
                                  <option value="boolean">✓ 布尔</option>
                                  <option value="datetime">📅 日期时间</option>
                                  <option value="date">📆 日期</option>
                                  <option value="time">⏰ 时间</option>
                                  <option value="array">📋 数组</option>
                                  <option value="object">📦 对象</option>
                                  <option value="any">🔄 任意</option>
                                </select>
                              </div>
                              
                              <div className="text-gray-400 text-lg mt-10">→</div>
                              
                              <div className="flex-1 min-w-0 space-y-2">
                                <div className="text-xs text-gray-500 mb-1 flex items-center gap-2">
                                  <span>目标字段</span>
                                  {mapping.targetType && (() => {
                                    const typeInfo = getTypeLabel(mapping.targetType);
                                    return (
                                      <span className={`inline-flex items-center gap-1 px-1.5 py-0.5 rounded text-xs ${typeInfo.color}`}>
                                        <span>{typeInfo.emoji}</span>
                                        <span>{typeInfo.label}</span>
                                      </span>
                                    );
                                  })()}
                                </div>
                                <input
                                  type="text"
                                  value={mapping.target}
                                  onChange={e => updateMapping(index, { target: e.target.value })}
                                  className="block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 text-sm border p-2 font-mono"
                                />
                                <select
                                  value={mapping.targetType || 'string'}
                                  onChange={e => updateMapping(index, { targetType: e.target.value as FieldType })}
                                  className="block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 text-xs border p-1.5"
                                >
                                  <option value="string">📝 字符串</option>
                                  <option value="integer">🔢 整数</option>
                                  <option value="decimal">💯 小数</option>
                                  <option value="boolean">✓ 布尔</option>
                                  <option value="datetime">📅 日期时间</option>
                                  <option value="date">📆 日期</option>
                                  <option value="time">⏰ 时间</option>
                                  <option value="array">📋 数组</option>
                                  <option value="object">📦 对象</option>
                                  <option value="any">🔄 任意</option>
                                </select>
                              </div>
                              
                              <div className="flex gap-1 mt-10">
                                <button
                                  type="button"
                                  onClick={() => setEditingIndex(index === editingIndex ? null : index)}
                                  className={`p-2 rounded hover:bg-gray-200 transition-colors ${
                                    editingIndex === index ? 'text-blue-600 bg-blue-100' : 'text-gray-500'
                                  }`}
                                  title="高级配置"
                                >
                                  <Settings size={16} />
                                </button>
                                <button
                                  type="button"
                                  onClick={() => removeMapping(index)}
                                  className="text-red-500 hover:text-red-700 p-2 transition-colors"
                                  title="删除"
                                >
                                  <Trash2 size={16} />
                                </button>
                              </div>
                            </div>
                            
                            {/* Description */}
                            <div className="mt-2 px-1">
                              <input
                                type="text"
                                placeholder="📝 字段描述 (可选)"
                                value={mapping.description || ''}
                                onChange={e => updateMapping(index, { description: e.target.value })}
                                className="block w-full rounded-md border-gray-200 shadow-sm focus:border-blue-400 focus:ring-blue-400 text-xs border p-2 bg-white text-gray-600 italic"
                              />
                            </div>
                            
                            {/* Advanced Config */}
                            {editingIndex === index && (
                              <div className="mt-3 p-4 bg-gray-50 rounded border border-gray-200 space-y-4">
                                <h4 className="font-medium text-sm text-gray-700">高级配置</h4>
                                
                                <div>
                                  <label className="block text-xs font-medium text-gray-500">默认值</label>
                                  <input
                                    type="text"
                                    value={mapping.defaultValue || ''}
                                    onChange={e => updateMapping(index, { defaultValue: e.target.value })}
                                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm sm:text-sm border p-2"
                                    placeholder="Default Value"
                                  />
                                </div>

                                <div>
                                  <div className="flex justify-between items-center mb-2">
                                    <label className="block text-xs font-medium text-gray-500">数据转换</label>
                                    <button type="button" onClick={() => addTransformation(index)} className="text-xs text-blue-600 hover:text-blue-800 flex items-center gap-1">
                                      <Plus size={12} /> 添加转换
                                    </button>
                                  </div>
                                  
                                  {(!mapping.transformations || mapping.transformations.length === 0) && (
                                    <p className="text-xs text-gray-400 italic">无转换规则</p>
                                  )}

                                  <div className="space-y-2">
                                    {mapping.transformations?.map((trans, tIndex) => (
                                      <div key={tIndex} className="flex gap-2 items-center">
                                        <span className="text-xs text-gray-400 w-4">{tIndex + 1}.</span>
                                        <select
                                          value={trans.type}
                                          onChange={e => updateTransformation(index, tIndex, { type: e.target.value as any })}
                                          className="block w-32 rounded-md border-gray-300 shadow-sm sm:text-xs border p-1"
                                        >
                                          <option value="string">String</option>
                                          <option value="number">Number</option>
                                          <option value="boolean">Boolean</option>
                                          <option value="uppercase">Uppercase</option>
                                          <option value="lowercase">Lowercase</option>
                                          <option value="trim">Trim</option>
                                          <option value="substring">Substring</option>
                                          <option value="concat">Concat</option>
                                          <option value="replace">Replace</option>
                                          <option value="split">Split</option>
                                          <option value="join">Join</option>
                                          <option value="base64_encode">Base64 Enc</option>
                                          <option value="base64_decode">Base64 Dec</option>
                                          <option value="json_parse">JSON Parse</option>
                                          <option value="json_stringify">JSON Stringify</option>
                                        </select>
                                        
                                        {['substring', 'concat', 'replace', 'split', 'join'].includes(trans.type) && (
                                          <input 
                                            type="text" 
                                            placeholder={
                                              trans.type === 'substring' ? '0,5' :
                                              trans.type === 'replace' ? 'find,replace' : 
                                              'param'
                                            }
                                            value={trans.params?.join(',') || ''}
                                            onChange={e => updateTransformation(index, tIndex, { params: e.target.value.split(',') })}
                                            className="flex-1 rounded-md border-gray-300 shadow-sm sm:text-xs border p-1"
                                          />
                                        )}
                                        
                                        <button type="button" onClick={() => removeTransformation(index, tIndex)} className="text-red-400 hover:text-red-600">
                                          <X size={14} />
                                        </button>
                                      </div>
                                    ))}
                                  </div>
                                </div>
                              </div>
                            )}
                          </div>
                        );
                      })}
                    </div>
                  )}
                </div>
              ));
            })()}
            
            {/* Legacy flat rendering - kept for reference, remove after testing */}
            {false && formData.mappingConfig.mappings.map((mapping, index) => (
              <div key={index} className="flex flex-col gap-2 bg-gray-50 p-3 rounded border border-gray-200">
                <div className="flex items-center gap-4">
                  <div className="flex-1">
                    <input
                      type="text"
                      placeholder="源字段 (e.g. user.firstName)"
                      value={mapping.source}
                      onChange={e => updateMapping(index, { source: e.target.value })}
                      className="block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm border p-2"
                    />
                  </div>
                  <div className="text-gray-400">→</div>
                  <div className="flex-1">
                    <input
                      type="text"
                      placeholder="目标字段 (e.g. customer_name)"
                      value={mapping.target}
                      onChange={e => updateMapping(index, { target: e.target.value })}
                      className="block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm border p-2"
                    />
                  </div>
                  <button type="button" onClick={() => setEditingIndex(index === editingIndex ? null : index)} className={`p-2 rounded hover:bg-gray-200 ${editingIndex === index ? 'text-blue-600 bg-blue-100' : 'text-gray-500'}`} title="高级配置">
                    <Settings size={18} />
                  </button>
                  <button type="button" onClick={() => removeMapping(index)} className="text-red-500 hover:text-red-700 p-2" title="删除">
                    <Trash2 size={18} />
                  </button>
                </div>
                
                {/* Advanced Config Panel */}
                {editingIndex === index && (
                  <div className="mt-2 p-4 bg-white rounded border border-gray-200 space-y-4">
                    <h4 className="font-medium text-sm text-gray-700">高级配置</h4>
                    
                    {/* Default Value */}
                    <div>
                      <label className="block text-xs font-medium text-gray-500">默认值 (当源字段不存在时使用)</label>
                      <input
                        type="text"
                        value={mapping.defaultValue || ''}
                        onChange={e => updateMapping(index, { defaultValue: e.target.value })}
                        className="mt-1 block w-full rounded-md border-gray-300 shadow-sm sm:text-sm border p-2"
                        placeholder="Default Value"
                      />
                    </div>

                    {/* Transformations */}
                    <div>
                      <div className="flex justify-between items-center mb-2">
                        <label className="block text-xs font-medium text-gray-500">数据转换 (按顺序执行)</label>
                        <button type="button" onClick={() => addTransformation(index)} className="text-xs text-blue-600 hover:text-blue-800 flex items-center gap-1">
                          <Plus size={12} /> 添加转换
                        </button>
                      </div>
                      
                      {(!mapping.transformations || mapping.transformations.length === 0) && (
                        <p className="text-xs text-gray-400 italic">无转换规则</p>
                      )}

                      <div className="space-y-2">
                        {mapping.transformations?.map((trans, tIndex) => (
                          <div key={tIndex} className="bg-white p-3 rounded border border-gray-200">
                            <div className="flex gap-2 items-start">
                              <span className="text-xs text-gray-400 w-6 mt-2">{tIndex + 1}.</span>
                              <div className="flex-1 space-y-2">
                                <div className="flex gap-2 items-center">
                                  <select
                                    value={trans.type}
                                    onChange={e => updateTransformation(index, tIndex, { type: e.target.value as any })}
                                    className="flex-1 rounded-md border-gray-300 shadow-sm sm:text-xs border p-2"
                                  >
                                    <optgroup label="类型转换">
                                      <option value="string">🔤 转字符串</option>
                                      <option value="number">🔢 转数字</option>
                                      <option value="boolean">☑ 转布尔</option>
                                    </optgroup>
                                    <optgroup label="字符串操作">
                                      <option value="uppercase">⬆ 大写</option>
                                      <option value="lowercase">⬇ 小写</option>
                                      <option value="trim">✂ 去空格</option>
                                      <option value="substring">🔪 截取</option>
                                      <option value="concat">➕ 拼接</option>
                                      <option value="replace">🔄 替换</option>
                                      <option value="split">✂ 分割</option>
                                    </optgroup>
                                    <optgroup label="数学运算">
                                      <option value="multiply">✖ 乘法</option>
                                      <option value="divide">➗ 除法</option>
                                      <option value="add">➕ 加法</option>
                                      <option value="subtract">➖ 减法</option>
                                      <option value="round">🔰 四舍五入</option>
                                      <option value="floor">⬇ 向下取整</option>
                                      <option value="ceil">⬆ 向上取整</option>
                                      <option value="abs">📊 绝对值</option>
                                    </optgroup>
                                    <optgroup label="编码/解码">
                                      <option value="base64_encode">🔒 Base64编码</option>
                                      <option value="base64_decode">🔓 Base64解码</option>
                                      <option value="json_parse">📜 JSON解析</option>
                                      <option value="json_stringify">📝 JSON序列化</option>
                                    </optgroup>
                                    <optgroup label="日期处理">
                                      <option value="date_format">📅 日期格式化</option>
                                    </optgroup>
                                    <optgroup label="其他">
                                      <option value="join">🔗 连接数组</option>
                                      <option value="default_value">🎯 默认值</option>
                                      <option value="regex_extract">🔍 正则提取</option>
                                      <option value="regex_replace">🔄 正则替换</option>
                                    </optgroup>
                                  </select>
                                  <button 
                                    type="button" 
                                    onClick={() => removeTransformation(index, tIndex)} 
                                    className="text-red-400 hover:text-red-600 p-1.5"
                                    title="删除转换"
                                  >
                                    <X size={14} />
                                  </button>
                                </div>
                                
                                {/* Description */}
                                <div className="text-xs text-gray-500 italic">
                                  {getTransformationDesc(trans.type)}
                                </div>
                                
                                {/* Params Inputs based on type */}
                                {['substring', 'concat', 'replace', 'split', 'join', 'date_format', 'multiply', 'divide', 'add', 'subtract', 'round', 'default_value', 'regex_extract', 'regex_replace'].includes(trans.type) && (
                                  <input 
                                    type="text" 
                                    placeholder={
                                      trans.type === 'substring' ? '例: 0,10' :
                                      trans.type === 'replace' ? '例: 旧值,新值' :
                                      trans.type === 'split' ? '例: ,' :
                                      trans.type === 'join' ? '例: ,' :
                                      trans.type === 'concat' ? '例: 追加的字符串' :
                                      trans.type === 'date_format' ? '例: YYYY-MM-DD HH:mm:ss' :
                                      trans.type === 'multiply' ? '例: 100' :
                                      trans.type === 'divide' ? '例: 100' :
                                      trans.type === 'add' ? '例: 10' :
                                      trans.type === 'subtract' ? '例: 10' :
                                      trans.type === 'round' ? '例: 2' :
                                      trans.type === 'default_value' ? '例: 默认值' :
                                      trans.type === 'regex_extract' ? '例: \\d+' :
                                      trans.type === 'regex_replace' ? '例: \\d+,XXX' :
                                      '参数'
                                    }
                                    value={trans.params?.join(',') || ''}
                                    onChange={e => updateTransformation(index, tIndex, { params: e.target.value.split(',') })}
                                    className="w-full rounded-md border-gray-300 shadow-sm sm:text-xs border p-2"
                                  />
                                )}
                              </div>
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>

        <div className="flex justify-end gap-4">
          <button
            type="button"
            onClick={() => navigate('/routes')}
            className="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
          >
            取消
          </button>
          <button
            type="submit"
            disabled={saving}
            className="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 flex items-center gap-2 disabled:opacity-50"
          >
            <Save size={18} />
            {saving ? '保存中...' : '保存'}
          </button>
        </div>
      </form>
    </div>
  );
}
