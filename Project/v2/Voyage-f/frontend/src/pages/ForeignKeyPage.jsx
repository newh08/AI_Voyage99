import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import API_BASE_URL from "../config";

function ForeignKeyPage() {
  const { columnId } = useParams();
  const navigate = useNavigate();
  const [columnName, setColumnName] = useState(""); // ✅ 컬럼 이름 저장
  const [databaseId, setDatabaseId] = useState(""); // ✅ 해당 컬럼이 속한 데이터베이스 ID 저장
  const [tables, setTables] = useState([]);
  const [selectedTable, setSelectedTable] = useState("");
  const [columns, setColumns] = useState([]);
  const [selectedColumn, setSelectedColumn] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchColumnInfo();
  }, [columnId]);

  // 📌 컬럼 정보 가져오기 (해당 컬럼이 속한 테이블 및 데이터베이스 ID도 가져옴)
  const fetchColumnInfo = async () => {
    console.log("📌 columnId received in ForeignKeyPage:", columnId); // ✅ columnId 확인
  
    try {
      const response = await axios.get(`${API_BASE_URL}/columns/${columnId}`);
      console.log("✅ Column Info API Response:", response.data); // ✅ API 응답 확인
  
      setColumnName(response.data.columnName);
      setDatabaseId(response.data.databaseId);
  
      if (response.data.databaseId) {
        console.log("✅ Fetching tables for Database ID:", response.data.databaseId);
        fetchTables(response.data.databaseId); // ✅ 같은 데이터베이스의 테이블만 가져오기
      } else {
        console.error("❌ Database ID is undefined");
      }
    } catch (error) {
      console.error("❌ Error fetching column info:", error);
    }
  };
  
  
  

  // 📌 같은 데이터베이스에 속한 테이블 목록 가져오기
  const fetchTables = async (dbId) => {
    try {
      const response = await axios.get(`${API_BASE_URL}/databases/${dbId}/tables`);
      setTables(response.data);
    } catch (error) {
      console.error("❌ Error fetching tables:", error);
    }
  };

  // 📌 선택된 테이블의 컬럼 목록 가져오기
  const fetchColumns = async (tableId) => {
    if (!tableId) return;
    try {
      const response = await axios.get(`${API_BASE_URL}/tables/${tableId}/columns`);
      setColumns(response.data);
    } catch (error) {
      console.error("❌ Error fetching columns:", error);
    }
  };

  // 📌 FK 설정 저장
  const handleSave = async () => {
    if (!selectedTable || !selectedColumn) {
      alert("Please select both table and column for the foreign key.");
      return;
    }

    setLoading(true);
    try {
      await axios.post(`${API_BASE_URL}/columns/${columnId}/foreign-key`, {
        foreignTableId: selectedTable,
        foreignColumnId: selectedColumn
      });
      alert("Foreign key set successfully!");
      navigate(-1); // 이전 페이지로 이동
    } catch (error) {
      console.error("❌ Error setting foreign key:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-4">
      {/* ✅ 실제 컬럼 이름 표시 */}
      <h1 className="text-2xl font-bold">🔗 Set Foreign Key for {columnName || `Column ${columnId}`}</h1>

      {/* 📌 테이블 선택 (같은 데이터베이스에 속한 테이블만 표시) */}
      <select
        value={selectedTable}
        onChange={(e) => {
          setSelectedTable(e.target.value);
          fetchColumns(e.target.value);
        }}
        className="border p-2 rounded w-full mb-2"
      >
        <option value="">Select Table</option>
        {tables.length === 0 ? (
          <option value="" disabled>No tables found in this database</option>
        ) : (
          tables.map((table) => (
            <option key={table.id} value={table.id}>{table.tableName}</option>
          ))
        )}
      </select>

      {/* 📌 컬럼 선택 */}
      <select
        value={selectedColumn}
        onChange={(e) => setSelectedColumn(e.target.value)}
        className="border p-2 rounded w-full mb-4"
        disabled={!selectedTable}
      >
        <option value="">Select Column</option>
        {columns.map((column) => (
          <option key={column.id} value={column.id}>{column.columnName}</option>
        ))}
      </select>

      {/* 📌 저장 & 취소 버튼 */}
      <div className="flex justify-end gap-2">
        <button onClick={handleSave} className="bg-blue-500 text-white px-4 py-2 rounded" disabled={loading}>
          {loading ? "Saving..." : "Save"}
        </button>
        <button onClick={() => navigate(-1)} className="bg-gray-400 text-white px-4 py-2 rounded">Cancel</button>
      </div>
    </div>
  );
}

export default ForeignKeyPage;
