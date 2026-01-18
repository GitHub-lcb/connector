import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { createRoute, getRoute, updateRoute, type Route, type MappingRule, type TransformationStep, type SecurityConfig } from "@/lib/api";
import { ArrowLeft, Plus, Trash2, Save, Settings, X, Lock } from "lucide-react";

export default function RouteEditor() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isNew = !id || id === 'new';
  
  const [loading, setLoading] = useState(!isNew);
  const [saving, setSaving] = useState(false);
  const [editingIndex, setEditingIndex] = useState<number | null>(null);
  
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
        mappings: [...prev.mappingConfig.mappings, { source: '', target: '' }]
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
            <h3 className="text-lg font-medium text-gray-900">字段映射 (JSON)</h3>
            <button type="button" onClick={addMapping} className="text-sm text-blue-600 hover:text-blue-800 flex items-center gap-1">
              <Plus size={16} /> 添加字段
            </button>
          </div>
          
          <div className="space-y-3">
            {formData.mappingConfig.mappings.length === 0 && (
              <p className="text-sm text-gray-500 italic">未配置映射，请求体将原样转发。</p>
            )}
            
            {formData.mappingConfig.mappings.map((mapping, index) => (
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
                            
                            {/* Params Inputs based on type */}
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
