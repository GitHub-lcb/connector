import axios from 'axios'

const api = axios.create({
  baseURL: '/api/admin',
  headers: {
    'Content-Type': 'application/json'
  }
})

// Test Helper
export const testInvokeRoute = (routeId: string, body: any) => 
  api.post('/test-invoke', { routeId, body }).then(res => res.data)

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

export interface MappingRule {
  source: string
  target: string
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

export const getRoutes = () => api.get<Route[]>('/routes').then(res => res.data)
export const getRoute = (id: string) => api.get<Route>(`/routes/${id}`).then(res => res.data)
export const createRoute = (data: Partial<Route>) => api.post<Route>('/routes', data).then(res => res.data)
export const updateRoute = (id: string, data: Partial<Route>) => api.put<Route>(`/routes/${id}`, data).then(res => res.data)
export const deleteRoute = (id: string) => api.delete(`/routes/${id}`).then(res => res.data)

export const getLogs = (params?: { page?: number; size?: number; routeId?: string }) => 
  api.get<PageResult<RouteLog>>('/logs', { params }).then(res => res.data)

export const getDashboardStats = () => api.get<DashboardStats>('/stats').then(res => res.data)
