import axios from 'axios'

export const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

export type TransformationType = 
  | 'uppercase' 
  | 'lowercase' 
  | 'trim'
  | 'substring' 
  | 'concat' 
  | 'replace' 
  | 'split' 
  | 'join'
  | 'base64_encode' 
  | 'base64_decode'
  | 'number'
  | 'string'
  | 'boolean'
  | 'json_parse'
  | 'json_stringify'

export interface TransformationStep {
  type: TransformationType
  params?: any[]
}

export interface ConditionRule {
  operator: 'exists' | 'not_exists' | 'equals' | 'not_equals' | 'contains' | 'gt' | 'lt'
  value?: any
}

export type FieldType = 'string' | 'integer' | 'decimal' | 'boolean' | 'datetime' | 'date' | 'time' | 'array' | 'object' | 'any'

export interface MappingRule {
  source: string
  target: string
  sourceType?: FieldType  // Source field type
  targetType?: FieldType  // Target field type
  description?: string    // Field mapping description
  defaultValue?: any
  transformations?: TransformationStep[]
  condition?: ConditionRule
}

export interface SecurityConfig {
  type: 'NONE' | 'RSA' | 'AES' | 'HMAC'
  publicKey?: string // for RSA
  secretKey?: string // for AES/HMAC
  encryptedField?: string
}

export interface HeaderConfig {
  key: string
  value: string
  description?: string
}

export interface Route {
  id: string
  name: string
  sourcePath: string
  targetUrl: string
  method: string
  status: 'active' | 'inactive'
  mappingConfig: {
    mappings: MappingRule[]
    security?: SecurityConfig
    headers?: HeaderConfig[]
  }
  createdAt: string
}

export interface RouteLog {
  id: string
  requestPath: string
  statusCode: number
  latencyMs: number
  errorMsg?: string
  createdAt: string
  routes?: {
    name: string
  }
}

export interface PageResult<T> {
  items: T[]
  total: number
  page: number
  size: number
  pages: number
}

export interface DashboardStats {
  totalRoutes: number
  activeRoutes: number
  totalRequests: number
  errorRate: number
}

export const getRoutes = () => api.get<Route[]>('/admin/routes').then(res => res.data)
export const getRoute = (id: string) => api.get<Route>(`/admin/routes/${id}`).then(res => res.data)
export const createRoute = (data: Partial<Route>) => api.post<Route>('/admin/routes', data).then(res => res.data)
export const updateRoute = (id: string, data: Partial<Route>) => api.put<Route>(`/admin/routes/${id}`, data).then(res => res.data)
export const deleteRoute = (id: string) => api.delete(`/admin/routes/${id}`).then(res => res.data)

export const getLogs = (params?: { page?: number; size?: number; routeId?: string }) => 
  api.get<PageResult<RouteLog>>('/admin/logs', { params }).then(res => res.data)

export const getDashboardStats = () => api.get<DashboardStats>('/admin/stats').then(res => res.data)
