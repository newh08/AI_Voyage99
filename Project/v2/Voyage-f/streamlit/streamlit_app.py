import streamlit as st
import requests
import torch
from transformers import AutoTokenizer, AutoModelForSeq2SeqLM

# ✅ 백엔드 API URL 설정
BASE_URL = "http://localhost:8080/api"

# ✅ 디바이스 설정 (CUDA > MPS(Mac) > CPU)
device = torch.device("cuda" if torch.cuda.is_available() else "mps" if torch.backends.mps.is_available() else "cpu")

# ✅ Streamlit 캐시를 사용해 모델을 한 번만 로드
@st.cache_resource
def load_model():
    MODEL_NAME = "gaussalgo/T5-LM-Large-text2sql-spider"
    tokenizer = AutoTokenizer.from_pretrained(MODEL_NAME)
    model = AutoModelForSeq2SeqLM.from_pretrained(MODEL_NAME).to(device)  # 모델을 GPU/MPS로 이동
    return tokenizer, model

# ✅ 데이터베이스 목록 가져오기 함수
def fetch_databases():
    try:
        response = requests.get(f"{BASE_URL}/databases")
        if response.status_code == 200:
            return response.json()
        else:
            st.error("❌ 데이터베이스 목록을 가져오는 데 실패했습니다.")
            return []
    except requests.exceptions.RequestException as e:
        st.error(f"🚨 서버 연결 실패: {e}")
        return []

# ✅ 선택한 데이터베이스의 메타데이터 가져오기 함수
def fetch_database_metadata(db_id):
    try:
        response = requests.get(f"{BASE_URL}/databases/{db_id}/schema-text")
        if response.status_code == 200:
            return response.text  
        else:
            st.error("❌ 데이터베이스 메타데이터를 가져오는 데 실패했습니다.")
            return None
    except requests.exceptions.RequestException as e:
        st.error(f"🚨 서버 연결 실패: {e}")
        return None

# ✅ 모델 로드 (최적화 적용)
tokenizer, model = load_model()

# ✅ Streamlit UI 시작
st.title("💾 SQL Generator")
st.write("텍스트를 SQL로 변환하는 프로젝트")

# 📌 1️⃣ 데이터베이스 리스트 가져오기
db_list = fetch_databases()

if db_list:
    # ✅ 사용자가 데이터베이스 선택
    db_options = {db["id"]: db["dbName"] for db in db_list}
    selected_db_id = st.selectbox("📂 사용할 데이터베이스 선택", list(db_options.keys()), format_func=lambda x: db_options[x])

    # ✅ 선택된 데이터베이스 정보 표시
    st.write(f"**선택한 데이터베이스:** `{db_options[selected_db_id]}`")

    # 📌 2️⃣ 선택된 데이터베이스의 메타데이터 가져오기
    st.subheader("📊 데이터베이스 메타데이터")
    metadata = fetch_database_metadata(selected_db_id)

    if metadata:
        st.text_area("📜 데이터베이스 스키마", metadata, height=300)
    else:
        st.warning("⚠️ 메타데이터를 불러오지 못했습니다.")

    # 📌 3️⃣ SQL 변환할 텍스트 입력
    user_input = st.text_area("📝 변환할 텍스트 입력", placeholder="예: 고객 테이블에서 나이가 30 이상인 사람을 찾기")

    # 📌 4️⃣ SQL 생성 버튼
    if st.button("✅ SQL 생성"):
        if user_input.strip():
            # 📌 입력 텍스트 포맷
            input_text = f"Generate an SQL query using the schema below:\nSchema: {metadata}\nQuestion: {user_input}"
            
            # 📌 모델 실행을 위한 토크나이징
            inputs = tokenizer(input_text, return_tensors="pt", padding=True, truncation=True, max_length=512)

            # 📌 데이터를 GPU/MPS로 이동
            inputs = {key: value.to(device) for key, value in inputs.items()}

            # 📌 모델 추론 실행
            with torch.no_grad():
                outputs = model.generate(
                    input_ids=inputs["input_ids"],
                    attention_mask=inputs["attention_mask"],
                    max_length=512,
                    num_beams=5,  # 빔 서치
                    early_stopping=True
                )
            
            # 📌 결과 변환
            sql_query = tokenizer.decode(outputs[0], skip_special_tokens=True)

            # 📌 결과 출력
            st.success(f"🔍 생성된 SQL 쿼리:\n```sql\n{sql_query}\n```")
            
        else:
            st.warning("⚠️ 변환할 텍스트를 입력하세요.")

else:
    st.warning("⚠️ 데이터베이스가 없습니다. 백엔드를 확인하세요.")
