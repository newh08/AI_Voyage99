## SQL 생성기 V2

### 전체 Flow
- 데이터베이스의 메타데이터를 화면을 통해 저장한 후, 해당 메타데이터로 Text-to-SQL 을 진행
- 사용 모델 : gaussalgo/T5-LM-Large-text2sql-spider

![total_flow](https://github.com/user-attachments/assets/6f0d80e7-aa7f-42dd-9d13-f0fad9014c88)

### SQL 생성
- 모델 사용: [StreamLit 링크](https://github.com/newh08/AI_Voyage99/blob/main/Project/v2/Voyage-f/streamlit/streamlit_app.py)
- 다양한 데이터베이스에도 SQL 생성 가능


![sql_generate](https://github.com/user-attachments/assets/c9a137ae-f2c5-42a2-af1f-956d2792a32d)

### TODO
- 한글 입력 가능하도록 번역을 위한 모듈 추가
- GPT API 를 활용해 DataBase Metadata 를 바탕으로 모델을 평가할 Data 만들기
- GPT API 를 활용해 DataBase Metadata 를 바탕으로 학습 데이터 생성하기
- 생성한 학습 데이터로 파인튜닝을 통해 모델 성능 높이기


** 현재 EC2 인스턴스는 일시정지 상태입니다.**
