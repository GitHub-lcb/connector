import { createClient } from '@supabase/supabase-js'
import dotenv from 'dotenv'

dotenv.config()

// Prefer service key for backend operations to bypass RLS if needed, 
// but fall back to anon key if that's what is available.
const supabaseUrl = process.env.SUPABASE_URL || process.env.VITE_SUPABASE_URL
const supabaseKey = process.env.SUPABASE_SERVICE_KEY || process.env.VITE_SUPABASE_ANON_KEY

if (!supabaseUrl || !supabaseKey) {
  console.warn('Missing Supabase URL or Key. Make sure .env is configured.')
}

export const supabase = createClient(supabaseUrl || '', supabaseKey || '')
