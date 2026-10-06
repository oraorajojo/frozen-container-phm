-- local 프로필 전용 샘플 데이터 (서버 시작할 때마다 실행되므로 INSERT IGNORE로 중복 방지)
-- 회원가입·컨테이너 등록에 branchId가 필요해서 지점만 미리 넣어 둠
INSERT IGNORE INTO branch (branch_id, name, address, status, created_at, updated_at) VALUES
    (1, '강남점', '서울특별시 강남구', 'ACTIVE', NOW(), NOW()),
    (2, '부산점', '부산광역시 해운대구', 'ACTIVE', NOW(), NOW());
