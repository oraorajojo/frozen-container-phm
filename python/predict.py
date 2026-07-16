"""
predict.py — XGBoost JSON 모델 버전 (food_type 대응)
Spring Boot ProcessBuilder 호출용

변경 사항 (기존 대비):
  - RUL 회귀 모델은 food_type과 무관하게 1개 공용 모델 사용 (xgb_reg.json)
  - 신선도 분류 모델은 food_type별로 3개 모델 사용
      Seafood -> model/xgb_clf_seafood.json
      Meat    -> model/xgb_clf_meat.json
      Frozen  -> model/xgb_clf_frozen.json
    (각 모델의 클래스 순서는 xgb_clf_{food}_meta.json 에 저장됨.
     Frozen은 학습 데이터에 Red 등급이 없어 2-클래스 모델임에 주의)
  - 입력 JSON에 "food_type" 필드가 반드시 포함되어야 함 (Seafood / Meat / Frozen)

사용법:
    python predict.py "{\"food_type\":\"Seafood\",\"vibration\":0.35,\"oil_pressure\":0.72,\"discharge_temp\":0.28,\"motor_current\":0.41,\"cycle\":45,\"delta_T_weighted\":0.31,\"FDR\":1.26}"

출력 (예시):
    {"RUL_pred": 148.5, "food_type": "Seafood", "freshness_grade": "Green", "prob_Green": 1.0, "prob_Yellow": 0.0, "prob_Red": 0.0, "warning": "정상 운전 중 — 신선도 유지"}
"""

import sys
import json
import os
import numpy as np
from xgboost import XGBRegressor, XGBClassifier

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
MODEL_DIR = os.path.join(BASE_DIR, 'model')
REG_PATH = os.path.join(MODEL_DIR, 'xgb_reg.json')

FOOD_MODEL_MAP = {
    "Seafood": "seafood",
    "Meat": "meat",
    "Frozen": "frozen",
}

FEATURE_COLS = [
    'vibration', 'oil_pressure', 'discharge_temp', 'motor_current',
    'vibration_roll_mean', 'oil_pressure_roll_mean',
    'discharge_temp_roll_mean', 'motor_current_roll_mean',
    'vibration_roll_std', 'oil_pressure_roll_std',
    'discharge_temp_roll_std', 'motor_current_roll_std',
    'delta_T_weighted', 'FDR', 'cycle',
]

WARNING_MAP = {
    'Green':  '정상 운전 중 — 신선도 유지',
    'Yellow': '열화 진행 중 — 사전 점검 권고',
    'Red':    '고장 임박 — 즉시 조치 필요',
}


def load_reg():
    if not os.path.exists(REG_PATH):
        raise FileNotFoundError(f"회귀 모델 없음: {REG_PATH}")
    reg = XGBRegressor()
    reg.load_model(REG_PATH)
    return reg


def load_clf(food_type):
    if food_type not in FOOD_MODEL_MAP:
        raise ValueError(f"지원하지 않는 food_type: {food_type} (허용값: {list(FOOD_MODEL_MAP.keys())})")
    fname = FOOD_MODEL_MAP[food_type]
    clf_path = os.path.join(MODEL_DIR, f'xgb_clf_{fname}.json')
    meta_path = os.path.join(MODEL_DIR, f'xgb_clf_{fname}_meta.json')
    if not os.path.exists(clf_path):
        raise FileNotFoundError(f"분류 모델 없음: {clf_path}")
    if not os.path.exists(meta_path):
        raise FileNotFoundError(f"분류 모델 메타데이터 없음: {meta_path}")
    clf = XGBClassifier()
    clf.load_model(clf_path)
    with open(meta_path, 'r', encoding='utf-8') as f:
        meta = json.load(f)
    return clf, meta['classes']  # classes: 인덱스 순서대로 등급명 리스트 (예: ["Green","Yellow","Red"] 또는 ["Green","Yellow"])


def parse_input(data):
    for sensor in ['vibration', 'oil_pressure', 'discharge_temp', 'motor_current']:
        if f'{sensor}_roll_mean' not in data:
            data[f'{sensor}_roll_mean'] = data.get(sensor, 0.0)
        if f'{sensor}_roll_std' not in data:
            data[f'{sensor}_roll_std'] = 0.0
    missing = [f for f in FEATURE_COLS if f not in data]
    if missing:
        raise ValueError(f"입력값 누락: {missing}")
    return np.array([[data[f] for f in FEATURE_COLS]])


def predict(json_str):
    data = json.loads(json_str)

    food_type = data.get('food_type')
    if not food_type:
        raise ValueError("입력값 누락: food_type (Seafood / Meat / Frozen 중 하나 필요)")

    X = parse_input(data)

    reg = load_reg()
    rul_pred = float(np.clip(reg.predict(X)[0], 0, None))

    clf, classes = load_clf(food_type)
    grade_idx = int(clf.predict(X)[0])
    grade_proba = clf.predict_proba(X)[0]
    grade_name = classes[grade_idx]

    # 항상 Green/Yellow/Red 3개 확률 키를 반환 (해당 식품 모델에 없는 등급은 0.0)
    proba_map = {g: 0.0 for g in ['Green', 'Yellow', 'Red']}
    for i, g in enumerate(classes):
        proba_map[g] = round(float(grade_proba[i]), 4)

    return {
        "RUL_pred":        round(rul_pred, 1),
        "food_type":       food_type,
        "freshness_grade": grade_name,
        "prob_Green":      proba_map['Green'],
        "prob_Yellow":     proba_map['Yellow'],
        "prob_Red":        proba_map['Red'],
        "warning":         WARNING_MAP[grade_name],
    }


if __name__ == '__main__':
    if len(sys.argv) < 2:
        print(json.dumps({"error": "입력 JSON 없음"}, ensure_ascii=False))
        sys.exit(1)
    try:
        print(json.dumps(predict(sys.argv[1]), ensure_ascii=False))
        sys.exit(0)
    except Exception as e:
        print(json.dumps({"error": str(e)}, ensure_ascii=False))
        sys.exit(1)
