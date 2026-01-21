import { Router } from 'express'
import { supabase } from '../lib/supabase.js'
import axios from 'axios'
import { transformRequest } from '../services/transformService.js'

const router = Router()

// List routes
router.get('/routes', async (req, res) => {
  const { data, error } = await supabase
    .from('routes')
    .select('*')
    .order('created_at', { ascending: false })
  
  if (error) {
    return res.status(500).json({ error: error.message })
  }
  res.json(data)
})

// Get single route
router.get('/routes/:id', async (req, res) => {
  const { id } = req.params
  const { data, error } = await supabase
    .from('routes')
    .select('*')
    .eq('id', id)
    .single()
  
  if (error) {
    return res.status(404).json({ error: 'Route not found' })
  }
  res.json(data)
})

// Create route
router.post('/routes', async (req, res) => {
  const { name, source_path, target_url, method, mapping_config } = req.body
  
  // Basic validation
  if (!name || !source_path || !target_url || !method) {
    return res.status(400).json({ error: 'Missing required fields' })
  }

  const { data, error } = await supabase
    .from('routes')
    .insert({
      name,
      source_path,
      target_url,
      method,
      mapping_config: mapping_config || {},
      status: 'active'
    })
    .select()
    .single()
  
  if (error) {
    return res.status(500).json({ error: error.message })
  }
  res.status(201).json(data)
})

// Update route
router.put('/routes/:id', async (req, res) => {
  const { id } = req.params
  const updates = req.body
  
  // Remove immutable fields if present in body
  delete updates.id
  delete updates.created_at
  
  // First get old config for history
  const { data: oldData } = await supabase
    .from('routes')
    .select('*')
    .eq('id', id)
    .single()
  
  const { data, error } = await supabase
    .from('routes')
    .update({
      ...updates,
      updated_at: new Date().toISOString()
    })
    .eq('id', id)
    .select()
    .single()
  
  if (error) {
    return res.status(500).json({ error: error.message })
  }
  
  // Record history (Fire and forget)
  if (oldData) {
    supabase.from('config_history').insert({
      route_id: id,
      old_config: oldData,
      new_config: data,
    }).then(({ error }) => {
      if (error) console.error('Failed to save history', error)
    })
  }

  res.json(data)
})

// Delete route
router.delete('/routes/:id', async (req, res) => {
  const { id } = req.params
  const { error } = await supabase
    .from('routes')
    .delete()
    .eq('id', id)
  
  if (error) {
    return res.status(500).json({ error: error.message })
  }
  res.status(204).send()
})

// Get Logs
router.get('/logs', async (req, res) => {
  const { route_id, limit = 100 } = req.query
  let query = supabase
    .from('route_logs')
    .select('*, routes(name)')
    .order('created_at', { ascending: false })
    .limit(Number(limit))
  
  if (route_id) {
    query = query.eq('route_id', route_id)
  }
  
  const { data, error } = await query
  if (error) {
    return res.status(500).json({ error: error.message })
  }
  res.json(data)
})

export default router
