package com.dormitory.mapper;

import com.dormitory.model.InspectionRecord;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface InspectionRecordMapper {

    @Select("SELECT ir.*, r.room_number, b.name as building_name, u.nickname as inspector_name " +
            "FROM inspection_records ir " +
            "LEFT JOIN rooms r ON ir.room_id = r.id " +
            "LEFT JOIN buildings b ON ir.building_id = b.id " +
            "LEFT JOIN users u ON ir.inspector_id = u.id " +
            "WHERE ir.id = #{id}")
    InspectionRecord findById(Long id);

    @Select("SELECT ir.*, r.room_number, b.name as building_name, u.nickname as inspector_name " +
            "FROM inspection_records ir " +
            "LEFT JOIN rooms r ON ir.room_id = r.id " +
            "LEFT JOIN buildings b ON ir.building_id = b.id " +
            "LEFT JOIN users u ON ir.inspector_id = u.id " +
            "ORDER BY ir.inspection_time DESC")
    List<InspectionRecord> findAll();

    @Select("SELECT ir.*, r.room_number, b.name as building_name, u.nickname as inspector_name " +
            "FROM inspection_records ir " +
            "LEFT JOIN rooms r ON ir.room_id = r.id " +
            "LEFT JOIN buildings b ON ir.building_id = b.id " +
            "LEFT JOIN users u ON ir.inspector_id = u.id " +
            "ORDER BY ir.inspection_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<InspectionRecord> findAllPaginated(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM inspection_records")
    int count();

    @Select("SELECT ir.*, r.room_number, b.name as building_name, u.nickname as inspector_name " +
            "FROM inspection_records ir " +
            "LEFT JOIN rooms r ON ir.room_id = r.id " +
            "LEFT JOIN buildings b ON ir.building_id = b.id " +
            "LEFT JOIN users u ON ir.inspector_id = u.id " +
            "WHERE ir.plan_id = #{planId} " +
            "ORDER BY ir.inspection_time DESC")
    List<InspectionRecord> findByPlanId(Long planId);

    @Select("SELECT ir.*, r.room_number, b.name as building_name, u.nickname as inspector_name " +
            "FROM inspection_records ir " +
            "LEFT JOIN rooms r ON ir.room_id = r.id " +
            "LEFT JOIN buildings b ON ir.building_id = b.id " +
            "LEFT JOIN users u ON ir.inspector_id = u.id " +
            "WHERE ir.room_id = #{roomId} " +
            "ORDER BY ir.inspection_time DESC")
    List<InspectionRecord> findByRoomId(Long roomId);

    @Select("SELECT ir.*, r.room_number, b.name as building_name, u.nickname as inspector_name " +
            "FROM inspection_records ir " +
            "LEFT JOIN rooms r ON ir.room_id = r.id " +
            "LEFT JOIN buildings b ON ir.building_id = b.id " +
            "LEFT JOIN users u ON ir.inspector_id = u.id " +
            "WHERE ir.rectification_status = #{status} " +
            "ORDER BY ir.inspection_time DESC")
    List<InspectionRecord> findByRectificationStatus(String status);

    @Select("SELECT ir.*, r.room_number, b.name as building_name, u.nickname as inspector_name " +
            "FROM inspection_records ir " +
            "LEFT JOIN rooms r ON ir.room_id = r.id " +
            "LEFT JOIN buildings b ON ir.building_id = b.id " +
            "LEFT JOIN users u ON ir.inspector_id = u.id " +
            "WHERE ir.result = #{result} " +
            "ORDER BY ir.inspection_time DESC")
    List<InspectionRecord> findByResult(String result);

    @Select("<script>" +
            "SELECT ir.*, r.room_number, b.name as building_name, u.nickname as inspector_name " +
            "FROM inspection_records ir " +
            "LEFT JOIN rooms r ON ir.room_id = r.id " +
            "LEFT JOIN buildings b ON ir.building_id = b.id " +
            "LEFT JOIN users u ON ir.inspector_id = u.id " +
            "WHERE 1=1 " +
            "<if test='planId != null'> AND ir.plan_id = #{planId}</if>" +
            "<if test='buildingId != null'> AND ir.building_id = #{buildingId}</if>" +
            "<if test='result != null and result != \"\"'> AND ir.result = #{result}</if>" +
            "<if test='rectificationStatus != null and rectificationStatus != \"\"'> AND ir.rectification_status = #{rectificationStatus}</if>" +
            "<if test='startDate != null'> AND ir.inspection_time &gt;= #{startDate}</if>" +
            "<if test='endDate != null'> AND ir.inspection_time &lt;= #{endDate}</if>" +
            "ORDER BY ir.inspection_time DESC" +
            "</script>")
    List<InspectionRecord> search(@Param("planId") Long planId,
                                  @Param("buildingId") Long buildingId,
                                  @Param("result") String result,
                                  @Param("rectificationStatus") String rectificationStatus,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate);

    @Insert("INSERT INTO inspection_records (plan_id, building_id, room_id, inspector_id, inspector_name, " +
            "inspection_time, overall_score, result, items_json, photos, remark, " +
            "need_rectification, rectification_status, rectification_deadline, " +
            "rectification_photos, rectification_time, create_time, update_time) " +
            "VALUES (#{planId}, #{buildingId}, #{roomId}, #{inspectorId}, #{inspectorName}, " +
            "#{inspectionTime}, #{overallScore}, #{result}, #{itemsJson}, #{photos}, #{remark}, " +
            "#{needRectification}, #{rectificationStatus}, #{rectificationDeadline}, " +
            "#{rectificationPhotos}, #{rectificationTime}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(InspectionRecord record);

    @Update("UPDATE inspection_records SET overall_score = #{overallScore}, result = #{result}, " +
            "items_json = #{itemsJson}, photos = #{photos}, remark = #{remark}, " +
            "need_rectification = #{needRectification}, " +
            "rectification_status = #{rectificationStatus}, " +
            "rectification_deadline = #{rectificationDeadline}, update_time = NOW() WHERE id = #{id}")
    int update(InspectionRecord record);

    @Update("UPDATE inspection_records SET rectification_status = #{rectificationStatus}, " +
            "rectification_time = #{rectificationTime}, " +
            "rectification_photos = #{rectificationPhotos}, " +
            "rectify_remark = #{rectifyRemark}, update_time = NOW() " +
            "WHERE id = #{id}")
    int updateRectify(InspectionRecord record);

    @Update("UPDATE inspection_records SET rectification_status = #{rectificationStatus}, " +
            "verified_by = #{verifiedBy}, verified_time = NOW(), update_time = NOW() " +
            "WHERE id = #{id}")
    int approveRectify(@Param("id") Long id, @Param("rectificationStatus") String rectificationStatus,
                       @Param("verifiedBy") String verifiedBy);

    @Delete("DELETE FROM inspection_records WHERE id = #{id}")
    int delete(Long id);
}
