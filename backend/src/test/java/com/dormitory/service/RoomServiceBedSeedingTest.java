package com.dormitory.service;

import com.dormitory.mapper.BedMapper;
import com.dormitory.mapper.BuildingMapper;
import com.dormitory.mapper.RoomMapper;
import com.dormitory.mapper.StudentMapper;
import com.dormitory.model.Bed;
import com.dormitory.model.Building;
import com.dormitory.model.Room;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceBedSeedingTest {

    @Mock RoomMapper roomMapper;
    @Mock BuildingMapper buildingMapper;
    @Mock BedMapper bedMapper;
    @Mock StudentMapper studentMapper;
    @InjectMocks RoomService roomService;

    @Test
    void createSeedsBedsForNewRoom() {
        when(buildingMapper.findById(1L)).thenReturn(new Building());
        when(roomMapper.findByBuildingAndNumber(anyLong(), anyString())).thenReturn(null);
        doAnswer(inv -> { ((Room) inv.getArgument(0)).setId(10L); return 1; })
                .when(roomMapper).insert(any(Room.class));

        Room room = new Room();
        room.setBuildingId(1L);
        room.setRoomNumber("101");
        room.setCapacity(4);
        room.setWindowBedsCount(2);
        room.setCorridorBedsCount(2);

        roomService.create(room);

        // 应为新房间生成 4 张床，且 roomId 指向新房间
        verify(bedMapper, times(4)).insert(any(Bed.class));
    }

    @Test
    void batchCreateSeedsBedsForEachCreatedRoom() {
        Building building = new Building();
        building.setFloors(1);
        building.setRoomsPerFloor(2);
        when(buildingMapper.findById(1L)).thenReturn(building);
        when(roomMapper.findByBuildingAndNumber(anyLong(), anyString())).thenReturn(null);
        doAnswer(inv -> { ((Room) inv.getArgument(0)).setId(99L); return 1; })
                .when(roomMapper).insert(any(Room.class));

        int created = roomService.batchCreate(1L);

        // 2 间房，每间默认容量 4 → 共 8 张床
        verify(bedMapper, times(created * 4)).insert(any(Bed.class));
    }
}
