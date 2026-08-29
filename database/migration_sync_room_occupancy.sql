-- 用实际在住学生数回写 rooms.current_count，避免选宿 CAS 与展示长期漂移。
-- 学生档案/房间列表的 occupancy 已改为实时 COUNT，本脚本只校正列值。

UPDATE rooms r
SET current_count = (
    SELECT COUNT(*) FROM students s WHERE s.room_id = r.id AND s.status = 1
);
