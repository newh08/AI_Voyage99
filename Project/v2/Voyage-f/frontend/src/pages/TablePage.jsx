import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import axios from "axios";
import API_BASE_URL from "../config";

function TablePage() {
  const { dbId } = useParams();
  const [tables, setTables] = useState([]);
  const [databaseName, setDatabaseName] = useState(""); // ✅ databaseName을 올바르게 정의
  const [tableName, setTableName] = useState("");
  const [loading, setLoading] = useState(false);

  // 📌 1️⃣ 데이터베이스 정보 가져오기
  useEffect(() => {
    fetchDatabaseInfo();
    fetchTables();
  }, []);

  const fetchDatabaseInfo = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/databases/${dbId}`);
      setDatabaseName(response.data.dbName); // ✅ 데이터베이스 이름 설정
    } catch (error) {
      console.error("❌ Error fetching database info:", error);
    }
  };

  const fetchTables = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/databases/${dbId}/tables`);
      setTables(response.data);
    } catch (error) {
      console.error("❌ Error fetching tables:", error);
    }
  };

  // 📌 2️⃣ 새 테이블 추가
  const createTable = async () => {
    if (!tableName.trim()) {
      console.log("❌ Table name is empty.");
      return;
    }
    setLoading(true);

    try {
      const response = await axios.post(`${API_BASE_URL}/databases/${dbId}/tables`, {
        tableName,
      });

      console.log("✅ Table created:", response.data);
      setTableName(""); // 입력 필드 초기화
      fetchTables(); // 테이블 목록 갱신
    } catch (error) {
      console.error("❌ Error creating table:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-4">
      {/* ✅ 실제 데이터베이스 이름 표시 */}
      <h1 className="text-2xl font-bold">
        📑 Tables in {databaseName ? databaseName : `Database ${dbId}`}
      </h1>

      {/* 📌 3️⃣ 테이블 입력 & 추가 버튼 */}
      <div className="flex gap-2 my-4">
        <input
          type="text"
          placeholder="Enter Table Name"
          value={tableName}
          onChange={(e) => setTableName(e.target.value)}
          className="border p-2 rounded w-1/3"
        />
        <button
          onClick={createTable}
          disabled={loading}
          className={`px-4 py-2 rounded ${
            loading ? "bg-gray-400" : "bg-green-500 hover:bg-green-600"
          } text-white`}
        >
          {loading ? "Creating..." : "+ Create Table"}
        </button>
      </div>

      {/* 📌 4️⃣ 테이블 목록 표시 */}
      <ul className="mt-4 border p-2 rounded">
        {tables.length === 0 ? (
          <p className="text-gray-500">No tables found.</p>
        ) : (
          tables.map((table) => (
            <li key={table.id} className="p-2 border-b">
              {/* ✅ 테이블명을 클릭하면 컬럼 생성 페이지로 이동 */}
              <Link to={`/tables/${table.id}/columns`} className="text-blue-500 hover:underline">
                {table.tableName}
              </Link>
            </li>
          ))
        )}
      </ul>
    </div>
  );
}

export default TablePage;
