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

export const getRoutes = async (): Promise<Route[]> => {
  const now = Date.now()
  if (routesCache.length > 0 && now - lastFetch < CACHE_TTL) {
    return routesCache
  }

  const { data, error } = await supabase
    .from('routes')
    .select('*')
    .eq('status', 'active')

  if (error) {
    console.error('Error fetching routes:', error)
    // Return cached routes if available even if expired, to avoid downtime
    return routesCache
  }

  if (data) {
    routesCache = data as Route[]
    lastFetch = now
  }

  return routesCache
}

export const getRouteByPath = async (path: string, method: string): Promise<Route | undefined> => {
  const routes = await getRoutes()
  // Simple exact match for now. 
  // For production, we might want regex or parameter matching (e.g. /users/:id)
  // Requirement says: "Interface Route Forwarding".
  // Let's assume exact match or prefix match?
  // "Receive warehouse system HTTP request... forward... from connector URL to cloud warehouse standard interface path"
  // Example: Connector /a/b -> Cloud /a/b
  // This implies the source_path might be the trigger.
  
  return routes.find(r => r.source_path === path && r.method.toUpperCase() === method.toUpperCase())
}
