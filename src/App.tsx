import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Layout from "@/components/Layout";
import Home from "@/pages/Home";
import RouteList from "@/pages/Admin/RouteList";
import RouteEditor from "@/pages/Admin/RouteEditor";
import RouteTester from "@/pages/Admin/RouteTester";
import Logs from "@/pages/Admin/Logs";

export default function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Home />} />
          <Route path="routes" element={<RouteList />} />
          <Route path="routes/new" element={<RouteEditor />} />
          <Route path="routes/:id" element={<RouteEditor />} />
          <Route path="test" element={<RouteTester />} />
          <Route path="logs" element={<Logs />} />
        </Route>
      </Routes>
    </Router>
  );
}
