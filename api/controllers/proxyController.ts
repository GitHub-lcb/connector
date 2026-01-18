import type { Request, Response } from 'express'
import axios, { type AxiosRequestConfig } from 'axios'
import { getRouteByPath } from '../services/routeService.js'
import { transformRequest } from '../services/transformService.js'
import { supabase } from '../lib/supabase.js'

export const proxyHandler = async (req: Request, res: Response) => {
  const start = Date.now()
  const { path, method, body, query, headers } = req

  // 1. Find Route
  // We need to strip /api/proxy or similar if we mount it there.
  // Assuming this handler is mounted at root or we check full path.
  // Let's use req.path directly.
  const route = await getRouteByPath(path, method)

  if (!route) {
    res.status(404).json({ error: 'Route not found' })
    return
  }

  try {
    // 2. Transform Body
    const transformedBody = transformRequest(body, route.mapping_config)

    // 3. Prepare Forward Request
    const config: AxiosRequestConfig = {
      method: route.method,
      url: route.target_url,
      params: query,
      data: transformedBody,
      headers: {
        'Content-Type': 'application/json',
        // Forward auth headers if needed, or other specific headers
        // Be careful not to forward host header
        ...(headers.authorization ? { Authorization: headers.authorization } : {}),
      },
      timeout: 5000, // 5s timeout
    }

    // 4. Send Request
    const response = await axios(config)

    // 5. Log Success
    const latency = Date.now() - start
    await logRequest(route.id, path, response.status, latency, null)

    // 6. Return Response
    res.status(response.status).json(response.data)

  } catch (error: any) {
    const latency = Date.now() - start
    const status = error.response?.status || 500
    const msg = error.message

    console.error('Proxy Error:', msg)
    await logRequest(route.id, path, status, latency, msg)

    res.status(status).json({
      error: 'Proxy Error',
      details: error.response?.data || msg
    })
  }
}

async function logRequest(routeId: string, path: string, status: number, latency: number, errorMsg: string | null) {
  // Fire and forget logging to avoid delaying response
  supabase.from('route_logs').insert({
    route_id: routeId,
    request_path: path,
    status_code: status,
    latency_ms: latency,
    error_msg: errorMsg
  }).then(({ error }) => {
    if (error) console.error('Failed to log request:', error)
  })
}
