import { useState, useEffect } from "react";
import axios from "axios";
import API_BASE_URL from "../config";

function ForeignKeyModal({ columnId, onClose }) {
  const [tables, setTables] = useState([]);
  const [selectedTable, setSelectedTable] = useState("");
  const [columns, setColumns] = useState([]);
  const [selectedColumn, setSelectedColumn] = useState("");
  const [loading, setLoading] = useState(false);
 

  // 📌 1️⃣ DB에서 테이블 목록 불러오기
  useEffect(() => {
    fetchTables();
  }, []);

  const fetchTables = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/tables`);
      setTables(response.data);
    } catch (error) {
      console.error("❌ Error fetching tables:", error);
    }
  };

  // 📌 2️⃣ 특정 테이블 선택 시 해당 테이블의 컬럼 불러오기
  const fetchColumns = async (tableId) => {
    if (!tableId) return;
    try {
      const response = await axios.get(`${API_BASE_URL}/tables/${tableId}/columns`);
      setColumns(response.data);
    } catch (error) {
      console.error("❌ Error fetching columns:", error);
    }
  };

  // 📌 3️⃣ FK 설정 요청
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
      onClose(); // 모달 닫기
    } catch (error) {
      console.error("❌ Error setting foreign key:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
      <div className="bg-white p-4 rounded shadow-lg w-96">
        <h2 className="text-xl font-bold mb-4">🔗 Set Foreign Key</h2>

        {/* 📌 4️⃣ 테이블 선택 */}
        <select
          value={selectedTable}
          onChange={(e) => {
            setSelectedTable(e.target.value);
            fetchColumns(e.target.value);
          }}
          className="border p-2 rounded w-full mb-2"
        >
          <option value="">Select Table</option>
          {tables.map((table) => (
            <option key={table.id} value={table.id}>{table.tableName}</option>
          ))}
        </select>

        {/* 📌 5️⃣ 컬럼 선택 */}
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

        {/* 📌 6️⃣ 저장 & 취소 버튼 */}
        <div className="flex justify-end gap-2">
          <button onClick={handleSave} className="bg-blue-500 text-white px-4 py-2 rounded" disabled={loading}>
            {loading ? "Saving..." : "Save"}
          </button>
          <button onClick={onClose} className="bg-gray-400 text-white px-4 py-2 rounded">Cancel</button>
        </div>
      </div>
    </div>
  );
}

export default ForeignKeyModal;
