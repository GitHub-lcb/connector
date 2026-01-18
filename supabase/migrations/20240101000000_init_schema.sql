-- Create routes table
CREATE TABLE IF NOT EXISTS routes (
  id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  name VARCHAR NOT NULL,
  source_path VARCHAR NOT NULL UNIQUE,
  target_url VARCHAR NOT NULL,
  method VARCHAR NOT NULL,
  mapping_config JSONB DEFAULT '{}'::jsonb,
  status VARCHAR DEFAULT 'active',
  version INT DEFAULT 1,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Create route_logs table
CREATE TABLE IF NOT EXISTS route_logs (
  id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  route_id UUID REFERENCES routes(id),
  request_path VARCHAR NOT NULL,
  status_code INT,
  latency_ms INT,
  error_msg TEXT,
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Create config_history table
CREATE TABLE IF NOT EXISTS config_history (
  id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  route_id UUID REFERENCES routes(id),
  old_config JSONB,
  new_config JSONB,
  changed_by UUID,
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Enable RLS
ALTER TABLE routes ENABLE ROW LEVEL SECURITY;
ALTER TABLE route_logs ENABLE ROW LEVEL SECURITY;
ALTER TABLE config_history ENABLE ROW LEVEL SECURITY;

-- Policies for routes
-- Allow public read access to routes so the connector middleware can fetch them without auth (or use service role)
-- For simplicity in this demo, we allow public read. In production, middleware should use service key.
CREATE POLICY "Enable read access for anon" ON routes FOR SELECT TO anon USING (true);
CREATE POLICY "Enable read access for authenticated" ON routes FOR SELECT TO authenticated USING (true);
CREATE POLICY "Enable all access for authenticated users" ON routes FOR INSERT TO authenticated WITH CHECK (true);
CREATE POLICY "Enable update for authenticated users" ON routes FOR UPDATE TO authenticated USING (true);
CREATE POLICY "Enable delete for authenticated users" ON routes FOR DELETE TO authenticated USING (true);

-- Policies for route_logs
CREATE POLICY "Enable insert for anon" ON route_logs FOR INSERT TO anon WITH CHECK (true);
CREATE POLICY "Enable all access for authenticated" ON route_logs FOR ALL TO authenticated USING (true);

-- Policies for config_history
CREATE POLICY "Enable all access for authenticated" ON config_history FOR ALL TO authenticated USING (true);

-- Grant permissions
GRANT SELECT ON routes TO anon;
GRANT ALL ON routes TO authenticated;
GRANT ALL ON routes TO service_role;

GRANT INSERT ON route_logs TO anon;
GRANT ALL ON route_logs TO authenticated;
GRANT ALL ON route_logs TO service_role;

GRANT ALL ON config_history TO authenticated;
GRANT ALL ON config_history TO service_role;
