import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import API_BASE_URL from "../config";

function DBSchemaPage() {
  const { dbId } = useParams();

  const [dbSchema, setDbSchema] = useState(null);
  const [loading, setLoading] = useState(false);

  // ✅ 테이블 추가
  const [newTableName, setNewTableName] = useState("");

  // ✅ 컬럼 추가(테이블별)
  const [newColumnNames, setNewColumnNames] = useState({});

  // ✅ FK 설정 사이드패널
  const [showFKPanel, setShowFKPanel] = useState(false);
  const [targetColumn, setTargetColumn] = useState(null);
  const [fkTable, setFkTable] = useState("");
  const [fkColumn, setFkColumn] = useState("");

  // ----------------------------------------
  // 1) DB 스키마 로딩
  // ----------------------------------------
  useEffect(() => {
    fetchDbSchema();
  }, [dbId]);

  const fetchDbSchema = async () => {
    setLoading(true);
    try {
      const response = await axios.get(`${API_BASE_URL}/databases/${dbId}/schema`);
      console.log("✅ DB Schema:", response.data);
      setDbSchema(response.data);
    } catch (error) {
      console.error("❌ Error fetching schema:", error);
    } finally {
      setLoading(false);
    }
  };

  // ----------------------------------------
  // 2) 테이블 추가/삭제
  // ----------------------------------------
  const addTable = async () => {
    if (!newTableName.trim()) {
      alert("Table name is empty.");
      return;
    }
    try {
      await axios.post(`${API_BASE_URL}/databases/${dbId}/tables`, {
        tableName: newTableName
      });
      setNewTableName("");
      fetchDbSchema();
    } catch (error) {
      console.error("❌ Error creating table:", error);
    }
  };

  const deleteTable = async (tableId) => {
    if (!window.confirm("Are you sure to delete this table?")) return;
    try {
      await axios.delete(`${API_BASE_URL}/api/tables/${tableId}`);
      fetchDbSchema();
    } catch (error) {
      console.error("❌ Error deleting table:", error);
      alert(error.response?.data || error.message);
    }
  };

  // ----------------------------------------
  // 3) 컬럼 추가/삭제
  // ----------------------------------------
  const handleColumnNameChange = (tableId, val) => {
    setNewColumnNames((prev) => ({
      ...prev,
      [tableId]: val
    }));
  };

  const addColumn = async (tableId) => {
    const colName = newColumnNames[tableId] || "";
    if (!colName.trim()) {
      alert("Column name is empty.");
      return;
    }
    try {
      await axios.post(`${API_BASE_URL}/api/tables/${tableId}/columns`, { columnName: colName });
      setNewColumnNames((prev) => ({ ...prev, [tableId]: "" }));
      fetchDbSchema();
    } catch (error) {
      console.error("❌ Error adding column:", error);
      alert(error.response?.data || error.message);
    }
  };

  const deleteColumn = async (columnId) => {
    if (!window.confirm("Are you sure to delete this column?")) return;
    try {
      await axios.delete(`${API_BASE_URL}/api/columns/${columnId}`);
      fetchDbSchema();
    } catch (error) {
      console.error("❌ Error deleting column:", error);
      alert(error.response?.data || error.message);
    }
  };

  // ----------------------------------------
  // 4) FK 설정/해제
  // ----------------------------------------
  const openFKPanel = (col) => {
    setTargetColumn(col);
    setFkTable("");
    setFkColumn("");
    setShowFKPanel(true);
  };

  const closeFKPanel = () => {
    setShowFKPanel(false);
    setTargetColumn(null);
    setFkTable("");
    setFkColumn("");
  };

  const saveFK = async () => {
    if (!fkTable || !fkColumn) {
      alert("Select table & column for FK");
      return;
    }
    try {
      await axios.post(`${API_BASE_URL}/api/columns/${targetColumn.id}/foreign-key`, {
        foreignTableId: fkTable,
        foreignColumnId: fkColumn
      });
      alert("FK set successfully!");
      closeFKPanel();
      fetchDbSchema();
    } catch (error) {
      console.error("❌ Error setting FK:", error);
      alert(error.response?.data || error.message);
    }
  };

  const removeFK = async (col) => {
    if (!window.confirm("Remove FK?")) return;
    try {
      await axios.post(`${API_BASE_URL}/api/columns/${col.id}/foreign-key`, {
        foreignTableId: null,
        foreignColumnId: null
      });
      fetchDbSchema();
    } catch (error) {
      console.error("❌ Error removing FK:", error);
      alert(error.response?.data || error.message);
    }
  };

  // ----------------------------------------
  //  Loading / Error
  // ----------------------------------------
  if (loading) return <p>Loading...</p>;
  if (!dbSchema) return <p>No Data</p>;

  const allTables = dbSchema.tables;

  return (
    <div className="p-4"> 
      <h1 className="text-2xl font-bold">DB: {dbSchema.dbName}</h1>

      {/* TABLE CREATE */}
      <div className="my-4 flex gap-2">
        <input
          type="text"
          placeholder="New Table Name"
          value={newTableName}
          onChange={(e) => setNewTableName(e.target.value)}
          className="border p-1 rounded"
        />
        <button onClick={addTable} className="bg-blue-500 text-white px-3 py-1 rounded">
          + Add Table
        </button>
      </div>

      {/* TABLE LIST */}
      {allTables.map((table) => (
        <div key={table.id} className="border p-2 mb-4">
          {/* TABLE HEADER */}
          <div className="flex items-center justify-between border-b p-2">
            <span style={{ fontSize: "24px", fontWeight: "bold" }}>{table.tableName}</span>
            <button onClick={() => deleteTable(table.id)} style={{ fontSize: "14px", color: "red", textDecoration: "underline" }}>
              Delete Table
            </button>
          </div>

          {/* COLUMN LIST */}
          <ul className="ml-4 mt-2">
            {table.columns.map((col) => {
              let fkInfo = null;
              if (col.foreignTableId && col.foreignColumnId) {
                const ft = allTables.find((t) => t.id === col.foreignTableId);
                const fc = ft?.columns.find((c) => c.id === col.foreignColumnId);

                if (ft && fc) {
                  fkInfo = (
                    <span className="text-sm text-gray-500">
                      {" "}
                      → FK to [{ft.tableName}, {fc.columnName}]
                    </span>
                  );
                } else {
                  fkInfo = (
                    <span className="text-sm text-gray-500">
                      {" "}
                      → FK to [{col.foreignTableId}, {col.foreignColumnId}]
                    </span>
                  );
                }
              }

              return (
                <li key={col.id} className="my-1">
                  <div className="flex items-center gap-3">
                    <span>
                      {col.columnName}
                      {fkInfo}
                    </span>
                    {!fkInfo && (
                      <button
                        onClick={() => openFKPanel(col)}
                        className="text-blue-500 underline"
                      >
                        Set FK
                      </button>
                    )}
                    {fkInfo && (
                      <button
                        className="text-purple-500 underline"
                        onClick={() => removeFK(col)}
                      >
                        Remove FK
                      </button>
                    )}
                    <button onClick={() => deleteColumn(col.id)} className="text-red-500 underline" style={{ fontSize: "14px", color: "red", textDecoration: "underline" }}>
                      Delete Column
                    </button>
                  </div>
                </li>
              );
            })}
          </ul>

          {/* ADD COLUMN (테이블 하단) */}
          <div className="mt-2 flex gap-2">
            <input
              type="text"
              placeholder="New column name"
              value={newColumnNames[table.id] || ""}
              onChange={(e) => handleColumnNameChange(table.id, e.target.value)}
              className="border p-1 rounded"
            />
            <button
              onClick={() => addColumn(table.id)}
              className="bg-green-500 text-white px-2 py-1 rounded"
            >
              {/* 요청하신 부분: 테이블명 표시 */}
              + Add Column to {table.tableName}
            </button>
          </div>
        </div>
      ))}

      {/* FK 설정 사이드패널 */}
      {showFKPanel && (
        <>
          {/* (옵션) 반투명 배경 → 클릭 시 닫을 수도 있음 */}
          <div
            className="fixed inset-0 bg-black bg-opacity-30 z-40"
            onClick={closeFKPanel}
          />
          <div className="fixed top-0 right-0 w-80 h-full bg-white shadow-lg z-50 p-4 overflow-auto">
            <div className="flex justify-between items-center mb-2">
              <h2 className="text-xl font-bold">
                Set FK for {targetColumn?.columnName}
              </h2>
              <button onClick={closeFKPanel} className="text-gray-600">X</button>
            </div>

            <p className="mb-2 text-sm text-gray-500">
              Select the table and column to reference
            </p>

            <select
              className="border p-1 mb-2 w-full"
              value={fkTable}
              onChange={(e) => {
                setFkTable(e.target.value);
                setFkColumn("");
              }}
            >
              <option value="">Select Table</option>
              {allTables.map((tbl) => (
                <option key={tbl.id} value={tbl.id}>
                  {tbl.tableName}
                </option>
              ))}
            </select>

            <select
              className="border p-1 mb-4 w-full"
              value={fkColumn}
              onChange={(e) => setFkColumn(e.target.value)}
              disabled={!fkTable}
            >
              <option value="">Select Column</option>
              {fkTable &&
                allTables
                  .find((t) => t.id.toString() === fkTable.toString())
                  ?.columns.map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.columnName}
                    </option>
                  ))}
            </select>

            <div className="flex justify-end gap-2">
              <button onClick={saveFK} className="bg-blue-500 text-white px-3 py-1 rounded">
                Save
              </button>
              <button
                onClick={closeFKPanel}
                className="bg-gray-400 text-white px-3 py-1 rounded"
              >
                Cancel
              </button>
            </div>
          </div>
        </>
      )}
    </div>
  );
}

export default DBSchemaPage;
