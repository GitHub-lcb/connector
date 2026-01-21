import { supabase } from '../lib/supabase.js'
import type { TransformationConfig } from './transformService.js'

export interface Route {
  id: string
  name: string
  source_path: string
  target_url: string
  method: string
  mapping_config: TransformationConfig
  status: 'active' | 'inactive'
  version: number
}

// Simple in-memory cache
let routesCache: Route[] = []
let lastFetch = 0
const CACHE_TTL = 60 * 1000 // 60 seconds

export const getRoutes = async (includeInactive = false): Promise<Route[]> => {
  const now = Date.now()
  // Only use cache for active-only requests
  if (!includeInactive && routesCache.length > 0 && now - lastFetch < CACHE_TTL) {
    return routesCache
  }

  let query = supabase.from('routes').select('*')
  
  if (!includeInactive) {
    query = query.eq('status', 'active')
  }

  const { data, error } = await query

  if (error) {
    console.error('Error fetching routes:', error)
    return includeInactive ? [] : routesCache
  }

  const routes = (data || []) as Route[]
  
  if (!includeInactive) {
    routesCache = routes
    lastFetch = now
  }

  return routes
}

export const getRouteByPath = async (path: string, method: string, includeInactive = false): Promise<Route | undefined> => {
  const routes = await getRoutes(includeInactive)
  return routes.find(r => r.source_path === path && r.method.toUpperCase() === method.toUpperCase())
}
