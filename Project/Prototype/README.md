# SQL 생성 AI 모델 Prototype

## 개요
- AI 모델 본격 설계하기 전, HuggingFace 에 이미 학습된 자연어 -> SQL 변환 모델을 사용해본다.
- 해당 모델의 학습 방식을 탐구하고 실제 사용할 모델에 대한 구체적인 청사진을 그려보기 위한 프로토타입 모델.


## 모델
- `google-t5/t5-base` : 어떤 NLP 작업이든 텍스트를 입력받아 텍스트를 생성하는 방식으로 처리하는 모델. (번역, 문서 요약, 수정, 질문생성, 질문 응답 등)
- `mrm8488/t5-base-finetuned-wikiSQL` : T5-base 모델의 파생모델로, WikiSQL 데이터셋에 대해 파인튜닝된 모델
  - 파라미터 : 220M
- `gaussalgo/T5-LM-Large-text2sql-spider` :  t5-base 보다 조금 더 큰 t5-large 모델의 파생모델로, Spider 데이터셋을 기반으로 학습된 대형 모델
  - 파라미터 : 770M
 

## 사전 학습에 사용된 데이터셋
- WikiSQL 데이터셋:
  - 자연어 질문과 SQL 쿼리 쌍으로 구성된 대규모 데이터셋으로, 테이블 데이터에 질의하는 시스템을 학습하는 데 사용
  - 단일 테이블에 대한 간단한 SELECT 쿼리와 조건절을 포함
    
- Spider 데이터셋:
  - 다양한 도메인의 복잡한 SQL 쿼리를 포함한 데이터셋.
  - 다중 테이블 조인, 서브쿼리, 그룹화 등을 포함한 복잡한 SQL 작업을 포함.
  - 200개 이상의 데이터베이스 스키마와 약 10,000개의 질문-SQL 쌍을 포함.
 

## 테스트 1 - Simple SQL
**Question** = Get the names of employees who earn more than 50000.

### DB Schema

#### employees Table

| id | name | salary |
|----|------|--------|
|  1 | John |  50000 |
|  2 | Jane |  60000 |
|  3 | Mike |  55000 |

#### 예상한 SQL:
```sql
SELECT name FROM employees WHERE salary > 50000;
```

#### 실제 답변 SQL - T5-Base 모델:
```sql
SELECT Employees FROM table WHERE Earnings ( $ ) > 50000
```

#### 실제 답변 SQL - T5-Large 모델:
```sql
SELECT name FROM employees WHERE salary > 50000
```

### 테스트 결과
- Base 모델은 조금 부정확한 결과가 나왔다. 특히 테이블은 전부 table 로 임의로 사용하는 듯 보인다.
- Large 모델은 아주 정확한 결과가 나왔다. 다만 맥북 m1 칩에선 31초가량의 시간이 걸렸다.

 
## 테스트 2 - 조금 복잡한 SQL
**Question** : For each item category where the stock quantity is less than 10, calculate the average price of the items in that category and the number of unique members who purchased those items.

### DB Schema

#### item Table

| **Item_ID** | **Item_Name**   | **Price** | **Category**       | **Stock_Quantity** | **Brand**    | **Added_Date** |
|-------------|-----------------|-----------|--------------------|--------------------|--------------|----------------|
| 1           | Laptop          | 1200.50   | Electronics        | 8                  | Dell         | 2024-01-01     |
| 2           | T-shirt         | 25.30     | Clothing           | 15                 | Nike         | 2024-02-10     |
| 3           | Coffee Machine  | 75.00     | Home & Garden      | 5                  | Philips      | 2023-12-25     |
| 4           | Headphones      | 50.00     | Electronics        | 3                  | Sony         | 2024-01-15     |
| 5           | Sofa Cushion    | 30.00     | Home & Garden      | 10                 | IKEA         | 2023-11-20     |


#### purchase Table
| **Purchase_ID** | **Purchase_Date** | **Quantity** | **Total_Price** | **Member_ID** | **Item_ID** |
|------------------|-------------------|--------------|-----------------|---------------|-------------|
| 101              | 2024-01-05       | 1            | 1200.50         | 201           | 1           |
| 102              | 2024-01-06       | 2            | 50.60           | 202           | 2           |
| 103              | 2024-01-10       | 1            | 75.00           | 203           | 3           |
| 104              | 2024-01-15       | 2            | 100.00          | 201           | 4           |
| 105              | 2024-01-20       | 3            | 90.00           | 202           | 5           |


#### member Table
| **Member_ID** | **Name**       | **Age** | **Gender** | **Email**               | **Phone**      | **Registration_Date** |
|---------------|----------------|---------|------------|-------------------------|----------------|------------------------|
| 201           | Alice Smith    | 30      | Female     | alice@example.com       | 555-1234       | 2023-12-01            |
| 202           | Bob Johnson    | 40      | Male       | bob@example.com         | 555-5678       | 2023-11-15            |
| 203           | Charlie Brown  | 35      | Male       | charlie@example.com     | 555-8765       | 2024-01-01            |

#### 예상한 SQL: 
```sql
SELECT 
    T1.Category,
    AVG(T1.Price),
    COUNT(DISTINCT T2.Member_ID) 
FROM 
    item AS T1
JOIN 
    purchase AS T2 ON  T1.Item_ID = T2.Item_ID
WHERE 
    T1.Stock_Quantity < 10
GROUP BY 
    T1.Category;
```

#### 실제 답변 SQL - T5-Small 모델:
```sql
SELECT COUNT Member_ID FROM table WHERE Stock Quantity  10
```

#### 실제 답변 SQL - T5-Large 모델:
```sql
SELECT 
    avg(T1.Price), 
    count(*) 
FROM 
    item AS T1 
JOIN 
    purchase AS T2 ON T1.Item_ID = T2.Item_ID 
WHERE 
    T2.stock_quantity < 10 
GROUP BY 
    T1.Category;
```

### 테스트 결과
- Base 모델은 아주 부정확한 결과가 나왔다.
- Large 모델은 꽤 정확한 결과가 나왔다. 다만 stock_quantity가 T2 가 아닌 T1 에서 찾아야했는데, DB 스키마에 대한 이해? 검색? 이 부족해보였다


## 테스트 3 - 아주 복잡한 SQL

question = "For items with an average rating of 4 or higher, belonging to a specific category, and purchased most recently, retrieve the shipment information including the buyer's name, shipment date, and item name."

### DB Schema
item, purchase, member Table 동일,
아래 테이블 추가
#### shipment Table
| **Shipment_ID** | **Purchase_ID** | **Shipment_Date** | **Courier** |
|------------------|-----------------|-------------------|-------------|
| 1                | 101             | 2024-01-07       | FedEx       |
| 2                | 102             | 2024-01-08       | UPS         |
| 3                | 103             | 2024-01-12       | DHL         |
| 4                | 104             | 2024-01-16       | USPS        |
| 5                | 105             | 2024-01-21       | FedEx       |

#### review Table
| **Review_ID** | **Member_ID** | **Item_ID** | **Rating** | **Review_Text**                     | **Review_Date** |
|---------------|---------------|-------------|-----------|-------------------------------------|-----------------|
| 1             | 201           | 1           | 5         | Excellent product, highly recommend!| 2024-01-10      |
| 2             | 202           | 2           | 4         | Comfortable and stylish.            | 2024-01-12      |
| 3             | 203           | 3           | 3         | Average performance.                | 2024-01-15      |
| 4             | 201           | 4           | 5         | Great sound quality!                | 2024-01-18      |
| 5             | 202           | 5           | 4         | Good value for money.               | 2024-01-20      |

#### 예상한 SQL: 
```sql
SELECT 
    member.Name AS Buyer_Name,
    shipment.Shipment_Date AS Shipment_Date,
    item.Item_Name AS Item_Name
FROM 
    review
JOIN 
    member ON review.Member_ID = member.Member_ID
JOIN 
    item ON review.Item_ID = item.Item_ID
JOIN 
    purchase ON item.Item_ID = purchase.Item_ID
JOIN 
    shipment ON purchase.Purchase_ID = shipment.Purchase_ID
WHERE 
    review.Rating >= 4
    AND item.Category = 'Electronics'
ORDER BY 
    purchase.Purchase_Date DESC
LIMIT 1;
```

#### 실제 답변 SQL - T5-Large 모델:
```sql
SELECT 
    T3.shipment_info
FROM 
    purchase AS T1
JOIN 
    shipment AS T2 
    ON T1.purchase_id = T2.purchase_id
JOIN 
    item AS T3 
    ON T2.item_id = T3.item_id
ORDER BY 
    T2.shipment_date DESC
LIMIT 1;
```

#### 마지막 review 테이블만 사용하는 질문 (입력해준 테이블 전체를 보는지 확인하기)
question = "Find all reviews with a rating of 4 or higher, including the review text and the review date."

#### 예상한 SQL: 
```sql
SELECT 
    Review_Text, 
    Review_Date 
FROM 
    review
WHERE 
    Rating >= 4;
```

#### 실제 답변 SQL - T5-Large 모델:
```sql
SELECT 
    T2.review_text, 
    T2.review_date 
FROM 
    review AS T1
JOIN 
    item AS T2 
ON 
    T1.item_id = T2.item_id
WHERE 
    T1.rating >= 4;
```


### 테스트 결과
- 데이터베이스가 많아지니 테이블의 컬럼을 이해하지 못하는듯 보인다. 그다지 좋지 않은 결과가 나온다.


## 최종 결론
- T5-Large 모델은 어느정도 좋은 성능을 보여준다.
- 그러나 테이블 5개만 넘어가도 테이블 컬럼명을 헷갈리는 경향이 있다.

### 모델 설계에 고민할 부분
  1. 데이터베이스 스키마를 모델에 잘 이해시키는 방법 -> 특정 데이터베이스로 학습 데이터를 만들어 파인튜닝하기?
  2. 한국어 -> 영어 번역이 앞단에 필요해 보인다.
     
