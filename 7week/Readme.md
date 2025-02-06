https://wandb.ai/newh008/huggingface/runs/e8878vct?nw=nwusernewh08

## 모델 학습시켜보기

- GPT로 Text to SQL 학습 데이터 생성해 gaussalgo/T5-LM-Large-text2sql-spider 모델을 학습시켰습니다.
- 학습 목적은 제공된 데이터베이스에 대해 쿼리를 더 잘 생성할 수 있도록 하기 위함입니다.
- 데이터는 총 440개로 9:1 비율로 train, Validation 에 사용했습니다.

  ### 학습 과정
- gaussalgo/T5-LM-Large-text2sql-spider 모델은 T5 모델을 이미 SQL 구문으로 파인튜닝한 모델로 해당 모델을 파인튜닝할 때 사용한 방식을 참고했습니다
- SQL 구문에 대한 평가는 문장의 유사도를 측정할 수 있는 BLEU, ROUGE 를 사용했습니다.

  <img width="639" alt="image" src="https://github.com/user-attachments/assets/c95b35e0-16d8-42d6-aac2-f47f1f707248" />

- 학습시 초반에 정확도가 크게 증가하는것이 확인됩니다.

- TBU
