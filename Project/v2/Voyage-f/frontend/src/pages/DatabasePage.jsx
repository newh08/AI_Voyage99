import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import API_BASE_URL from "../config";

function DatabasePage() {
  const [databases, setDatabases] = useState([]);
  const [dbName, setDbName] = useState("");
  const [loading, setLoading] = useState(false);

  // 📌 1️⃣ 데이터베이스 목록 불러오기
  useEffect(() => {
    fetchDatabases();
  }, []);

  const fetchDatabases = async () => {
    try {
        const response = await axios.get("http://localhost:8080/api/databases");
        console.log("✅ API Response:", response.data); // 여기에서 데이터를 확인
        setDatabases(response.data);
    } catch (error) {
        console.error("❌ Error fetching databases:", error);
    }
};


  // 📌 2️⃣ 새 데이터베이스 추가 (버튼 클릭 시 실행될 함수)
  const createDatabase = async () => {
    if (!dbName.trim()) {
        console.log("❌ Database name is empty. Aborting.");
        return; // 빈 값이면 실행 안 함
    }

    console.log("✅ Create button clicked. Sending request...");
    setLoading(true);

    try {
        const response = await axios.post("http://localhost:8080/api/databases", { dbName });
        console.log("✅ Database created:", response.data);

        setDbName(""); // 입력 필드 초기화
        fetchDatabases(); // 데이터베이스 목록 갱신
    } catch (error) {
        console.error("❌ Error creating database:", error);
    } finally {
        setLoading(false);
    }
};

  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold">📂 Database List</h1>

      {/* 📌 3️⃣ 데이터베이스 입력 & 생성 버튼 */}
      <div className="flex gap-2 my-4">
        <input
          type="text"
          placeholder="Enter Database Name"
          value={dbName}
          onChange={(e) => setDbName(e.target.value)}
          className="border p-2 rounded w-1/3"
        />
        <button
          onClick={createDatabase}
          disabled={loading}
          className={`px-4 py-2 rounded ${
            loading ? "bg-gray-400" : "bg-blue-500 hover:bg-blue-600"
          } text-white`}
        >
          {loading ? "Creating..." : "+ Create"}
        </button>
      </div>

      {/* 📌 4️⃣ 데이터베이스 목록 표시 */}
      <ul className="mt-4 border p-2 rounded">
        {databases.length === 0 ? (
          <p className="text-gray-500">No databases found.</p>
        ) : (
          databases?.map((db) => (
            <li key={db.id} className="p-2 border-b">
              <Link to={`/databases/${db.id}/tables`} className="text-blue-500 hover:underline">
                {db.dbName} &emsp;
              </Link>

            {/* 새 링크: seeAll - DB 전체 구조 보기 */}
              <Link to={`/databases/${db.id}/schema`} className="text-sm text-green-500 hover:underline ml-4">
               &emsp; seeAll
              </Link>
            </li>
          ))
        )}
      </ul>
    </div>
  );
}

export default DatabasePage;
