import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import axios from "axios";
import API_BASE_URL from "../config";

function ColumnPage() {
  const { tableId } = useParams();
  const [columns, setColumns] = useState([]);
  const [tableName, setTableName] = useState(""); // ✅ 테이블 이름 저장
  const [columnName, setColumnName] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchTableInfo();
    fetchColumns();
  }, [tableId]);

  const fetchTableInfo = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/tables/${tableId}`);
      console.log("✅ Table Info:", response.data); // 📌 API 응답 확인용 로그
      setTableName(response.data.tableName); // ✅ 테이블 이름 설정
    } catch (error) {
      console.error("❌ Error fetching table info:", error);
    }
  };

  const fetchColumns = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/tables/${tableId}/columns`);
      setColumns(response.data);
    } catch (error) {
      console.error("❌ Error fetching columns:", error);
    }
  };

  // 📌 컬럼 추가 기능
  const createColumn = async () => {
    if (!columnName.trim()) {
      alert("Please enter column name.");
      return;
    }

    setLoading(true);
    try {
      await axios.post(`${API_BASE_URL}/tables/${tableId}/columns`, { columnName });

      setColumnName("");
      fetchColumns(); // ✅ 컬럼 목록 갱신
    } catch (error) {
      console.error("❌ Error creating column:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-4">
      {/* ✅ 실제 테이블 이름 표시 */}
      <h1 className="text-2xl font-bold">
        📑 Columns in {tableName ? tableName : `Table ${tableId}`}
      </h1>

      {/* 📌 컬럼 추가 입력 폼 */}
      <div className="flex gap-2 my-4">
        <input
          type="text"
          placeholder="Enter Column Name"
          value={columnName}
          onChange={(e) => setColumnName(e.target.value)}
          className="border p-2 rounded w-1/3"
        />
        <button
          onClick={createColumn}
          disabled={loading}
          className={`px-4 py-2 rounded ${
            loading ? "bg-gray-400" : "bg-green-500 hover:bg-green-600"
          } text-white`}
        >
          {loading ? "Creating..." : "+ Create Column"}
        </button>
      </div>

      {/* 📌 컬럼 목록 표시 */}
      <ul className="mt-4 border p-2 rounded">
        {columns.length === 0 ? (
          <p className="text-gray-500">No columns found.</p>
        ) : (
          columns.map((column) => (
            <li key={column.id} className="p-2 border-b flex justify-between">
              {/* ✅ 컬럼명을 클릭하면 FK 설정 페이지로 이동 */}
              <Link to={`/columns/${column.id}/fk`} className="text-blue-500 hover:underline">
                {column.columnName}
              </Link>
            </li>
          ))
        )}
      </ul>
    </div>
  );
}

export default ColumnPage;
