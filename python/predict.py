"""
predict.py — XGBoost JSON 모델 버전
Spring Boot ProcessBuilder 호출용

사용법:
    python predict.py "{\"vibration\":0.35,\"oil_pressure\":0.72,\"discharge_temp\":0.28,\"motor_current\":0.41,\"cycle\":45,\"delta_T_weighted\":0.31,\"FDR\":1.26}"

출력:
    {"RUL_pred": 148.5, "freshness_grade": "Green", "prob_Green": 1.0, "prob_Yellow": 0.0, "prob_Red": 0.0, "warning": "정상 운전 중 — 신선도 유지"}
"""

import sys
import json
import os
import numpy as np
from xgboost import XGBRegressor, XGBClassifier

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
REG_PATH = os.path.join(BASE_DIR, 'model', 'xgb_reg.json')
CLF_PATH = os.path.join(BASE_DIR, 'model', 'xgb_clf.json')

FEATURE_COLS = [
    'vibration', 'oil_pressure', 'discharge_temp', 'motor_current',
    'vibration_roll_mean', 'oil_pressure_roll_mean',
    'discharge_temp_roll_mean', 'motor_current_roll_mean',
    'vibration_roll_std', 'oil_pressure_roll_std',
    'discharge_temp_roll_std', 'motor_current_roll_std',
    'delta_T_weighted', 'FDR', 'cycle',
]

LABEL_MAP   = {0: 'Green', 1: 'Yellow', 2: 'Red'}
WARNING_MAP = {
    'Green':  '정상 운전 중 — 신선도 유지',
    'Yellow': '열화 진행 중 — 사전 점검 권고',
    'Red':    '고장 임박 — 즉시 조치 필요',
}

def load_models():
    if not os.path.exists(REG_PATH):
        raise FileNotFoundError(f"회귀 모델 없음: {REG_PATH}")
    if not os.path.exists(CLF_PATH):
        raise FileNotFoundError(f"분류 모델 없음: {CLF_PATH}")
    reg = XGBRegressor()
    clf = XGBClassifier()
    reg.load_model(REG_PATH)   # pkl 아닌 json 로드
    clf.load_model(CLF_PATH)
    return reg, clf

def parse_input(json_str):
    data = json.loads(json_str)
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
    reg, clf = load_models()
    X = parse_input(json_str)
    rul_pred    = float(np.clip(reg.predict(X)[0], 0, None))
    grade_idx   = int(clf.predict(X)[0])
    grade_proba = clf.predict_proba(X)[0]
    grade_name  = LABEL_MAP[grade_idx]
    return {
        "RUL_pred":        round(rul_pred, 1),
        "freshness_grade": grade_name,
        "prob_Green":      round(float(grade_proba[0]), 4),
        "prob_Yellow":     round(float(grade_proba[1]), 4),
        "prob_Red":        round(float(grade_proba[2]), 4),
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
