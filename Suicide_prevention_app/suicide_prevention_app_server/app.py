from flask import Flask, request
import pandas as pd
from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity
import json

app = Flask(__name__)
model = SentenceTransformer('jhgan/ko-sroberta-multitask')

# 데이터 전처리
df = pd.read_csv('wellness_dataset.csv')
df['embedding'] = df['embedding'].apply(lambda x: json.loads(x.replace("'", "")))


@app.route("/")
def main():
    return "일단 만들긴 했는데~"


@app.route('/message', methods=['POST'])
def receive_message():
    userMessage = request.data.decode('utf-8')
    print("Received message: " + userMessage)

    text = userMessage

    embedding = model.encode(text)

    # 유사도 계산
    df['distance'] = df['embedding'].map(lambda x: cosine_similarity([embedding], [x]).squeeze())

    try:
        answer = df.loc[df['distance'].idxmax(), 'AI']
    except ValueError:
        # 유사도 계산 중 오류 발생 시 예외 처리
        answer = "죄송해요, 처리 중에 오류가 발생했습니다."

    return answer, 200


if __name__ == "__main__":
    app.run(host="0.0.0.0", port="8080", debug=True)
