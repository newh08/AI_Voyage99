import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import HomePage from "./pages/HomePage";
import DatabasePage from "./pages/DatabasePage";
import TablePage from "./pages/TablePage";
import ColumnPage from "./pages/ColumnPage";
import ForeignKeyPage from "./pages/ForeignKeyPage";
import DBSchemaPage from "./pages/DBSchemaPage"; 

function App() {
  return (
    <Router>
      <div className="p-4">
        {/* 📌 네비게이션 메뉴 */}
        <nav className="mb-4">
          <Link className="mr-4" to="/">🏠 Home</Link>
          <Link className="mr-4" to="/databases">📂 Databases</Link>
        </nav>

        {/* 📌 라우트 설정 */}
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/databases" element={<DatabasePage />} />
          <Route path="/databases/:dbId/tables" element={<TablePage />} />
          <Route path="/tables/:tableId/columns" element={<ColumnPage />} />
          <Route path="/columns/:columnId/fk" element={<ForeignKeyPage />} />
          <Route path="/databases/:dbId/schema" element={<DBSchemaPage />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
