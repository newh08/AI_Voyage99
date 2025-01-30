import base64
import streamlit as st
import os

from langchain_openai import ChatOpenAI
from langchain_core.messages import HumanMessage, AIMessage

st.title("Picture Comparing Bot")

model = ChatOpenAI(model="gpt-4o-mini")

# 세션 상태 초기화
if "messages" not in st.session_state:
    st.session_state["messages"] = []  # 질문 및 응답 기록
    st.session_state["uploaded_images"] = []  # 업로드된 이미지들

# 다중 파일 업로더 설정
images = st.file_uploader(
    "사진을 올려주세요! (여러 장 가능)",
    type=["png", "jpg", "jpeg"],
    accept_multiple_files=True,
)

# 이미지 업로드 처리
if images:
    st.session_state["uploaded_images"] = []
    st.write(f"업로드된 이미지 수: {len(images)}")

    # 각 이미지를 처리하여 Base64로 변환
    for idx, image in enumerate(images):
        st.image(image, caption=f"업로드된 이미지 {idx + 1}", use_container_width=True)
        image_base64 = base64.b64encode(image.read()).decode("utf-8")
        st.session_state["uploaded_images"].append(
            {
                "type": "image_url",
                "image_url": {"url": f"data:image/jpeg;base64,{image_base64}"},
            }
        )

# 추가 질문 입력 섹션
if st.session_state["uploaded_images"]:
    st.write("질문을 입력하세요!")
    user_input = st.text_input("질문:")
    if st.button("질문 보내기") and user_input:
        # HumanMessage 생성
        content_list = [{"type": "text", "text": user_input}]
        content_list.extend(st.session_state["uploaded_images"])
        message = HumanMessage(content=content_list)

        # 모델 호출
        try:
            result = model.invoke([message])
            response = result.content

            # 메시지 기록 저장
            st.session_state["messages"].append({"user": user_input, "bot": response})
        except Exception as e:
            st.error(f"오류 발생: {e}")

# 이전 대화 내용 출력
if st.session_state["messages"]:
    st.write("대화 기록:")
    for idx, msg in enumerate(st.session_state["messages"]):
        st.markdown(f"**질문 {idx + 1}:** {msg['user']}")
        st.markdown(f"**응답:** {msg['bot']}")
