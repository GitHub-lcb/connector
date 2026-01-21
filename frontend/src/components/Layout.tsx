import { Link, Outlet, useLocation } from "react-router-dom";
import { cn } from "@/lib/utils";

export default function Layout() {
  const location = useLocation();
  
  const navItems = [
    { name: "仪表盘", path: "/" },
    { name: "路由管理", path: "/routes" },
    { name: "在线测试", path: "/test" },
    { name: "日志监控", path: "/logs" },
  ];

  return (
    <div className="min-h-screen bg-gray-100 flex">
      {/* Sidebar */}
      <aside className="w-64 bg-white shadow-md flex flex-col">
        <div className="p-6 border-b">
          <h1 className="text-xl font-bold text-gray-800">连接器中间件</h1>
        </div>
        <nav className="flex-1 p-4 space-y-2">
          {navItems.map((item) => (
            <Link
              key={item.path}
              to={item.path}
              className={cn(
                "block px-4 py-2 rounded-md text-gray-600 hover:bg-gray-100 hover:text-gray-900 transition-colors",
                location.pathname === item.path && "bg-blue-50 text-blue-600 font-medium"
              )}
            >
              {item.name}
            </Link>
          ))}
        </nav>
      </aside>

      {/* Main Content */}
      <main className="flex-1 p-8 overflow-y-auto">
        <Outlet />
      </main>
    </div>
  );
}
