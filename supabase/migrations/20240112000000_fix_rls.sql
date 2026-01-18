-- 修复权限问题：允许开发环境下的匿名访问（绕过 RLS 限制）
-- Fix permission issues: Allow anonymous access in development (Bypass RLS)

-- 1. Routes 表权限
DROP POLICY IF EXISTS "Enable read access for anon" ON routes;
DROP POLICY IF EXISTS "Enable all access for anon" ON routes;
CREATE POLICY "Enable all access for anon" ON routes FOR ALL TO anon USING (true) WITH CHECK (true);

-- 2. Route Logs 表权限
DROP POLICY IF EXISTS "Enable insert for anon" ON route_logs;
DROP POLICY IF EXISTS "Enable all access for anon on logs" ON route_logs;
CREATE POLICY "Enable all access for anon on logs" ON route_logs FOR ALL TO anon USING (true) WITH CHECK (true);

-- 3. Config History 表权限
DROP POLICY IF EXISTS "Enable all access for anon on history" ON config_history;
CREATE POLICY "Enable all access for anon on history" ON config_history FOR ALL TO anon USING (true) WITH CHECK (true);
