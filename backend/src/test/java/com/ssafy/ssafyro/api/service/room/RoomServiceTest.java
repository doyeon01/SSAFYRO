package com.ssafy.ssafyro.api.service.room;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.ssafy.ssafyro.IntegrationTestSupport;
import com.ssafy.ssafyro.api.service.room.request.RoomCreateServiceRequest;
import com.ssafy.ssafyro.api.service.room.request.RoomEnterServiceRequest;
import com.ssafy.ssafyro.api.service.room.request.RoomExitServiceRequest;
import com.ssafy.ssafyro.api.service.room.request.RoomListServiceRequest;
import com.ssafy.ssafyro.api.service.room.response.RoomCreateResponse;
import com.ssafy.ssafyro.api.service.room.response.RoomDetailResponse;
import com.ssafy.ssafyro.api.service.room.response.RoomListResponse;
import com.ssafy.ssafyro.domain.room.RoomType;
import com.ssafy.ssafyro.domain.room.redis.RoomRedis;
import com.ssafy.ssafyro.domain.room.redis.RoomRedisRepository;
import com.ssafy.ssafyro.domain.room.redis.RoomStatus;
import com.ssafy.ssafyro.error.room.RoomNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class RoomServiceTest extends IntegrationTestSupport {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRedisRepository roomRedisRepository;

    @Autowired
    private RedisTemplate<String, RoomRedis> redisTemplate;

    @AfterEach
    void tearDown() {
        redisTemplate.delete(redisTemplate.keys("room:*"));
    }

    @DisplayName("방을 생성한다.")
    @Test
    void createRoomTest() {
        // given
        RoomCreateServiceRequest roomCreateServiceRequest = new RoomCreateServiceRequest(1L, "test", "test",
                "INTERVIEW", 3);

        // when
        RoomCreateResponse roomCreateResponse = roomService.createRoom(roomCreateServiceRequest);
        RoomRedis savedRoom = roomRedisRepository.findById(roomCreateResponse.roomId()).orElse(null);

        // then
        assertThat(roomCreateResponse.roomId()).isNotNull();
        assertThat(savedRoom).isNotNull();
        assertThat(savedRoom.getTitle()).isEqualTo("test");
        assertThat(savedRoom.getType()).isEqualTo(RoomType.INTERVIEW);
        assertThat(savedRoom.getCapacity()).isEqualTo(3);
    }

    @DisplayName("방을 조회한다.")
    @Test
    void getRoomByIdTest() {
        // given
        RoomRedis room = RoomRedis.builder()
                .title("test")
                .description("test description")
                .type(RoomType.INTERVIEW)
                .capacity(3)
                .build();

        String savedRoomId = roomRedisRepository.save(room);

        // when
        RoomDetailResponse roomDetailResponse = roomService.getRoomById(savedRoomId);

        // then
        assertThat(roomDetailResponse).isNotNull();
        assertThat(roomDetailResponse.title()).isEqualTo(room.getTitle());
        assertThat(roomDetailResponse.type()).isEqualTo(room.getType());
        assertThat(roomDetailResponse.capacity()).isEqualTo(room.getCapacity());
    }

    @DisplayName("방 목록을 조회한다.")
    @Test
    void getRoomListTest() {
        // given
        String roomType = RoomType.INTERVIEW.name();
        int capacity = 3;
        String status = RoomStatus.WAIT.name();
        int page = 1;
        int size = 10;

        RoomListServiceRequest request = new RoomListServiceRequest(roomType, capacity, status, page, size);

        roomRedisRepository.save(createRoom("Room 1", RoomType.INTERVIEW, 3));
        roomRedisRepository.save(createRoom("Room 2", RoomType.INTERVIEW, 3));

        // when
        RoomListResponse response = roomService.getRoomList(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.rooms()).hasSize(2);
        assertThat(response.rooms()).extracting("title", "type", "capacity")
                .containsExactlyInAnyOrder(
                        tuple("Room 1", RoomType.INTERVIEW, 3),
                        tuple("Room 2", RoomType.INTERVIEW, 3));
    }

    @DisplayName("type=null, capacity=null, status=null, page=1, size=10인 방 목록을 조회한다.")
    @Test
    void getRoomListWhenParamsNullTest() {
        //given
        for (int i = 0; i < 10; i++) {
            roomRedisRepository.save(createRoom("Room " + i, RoomType.INTERVIEW, 3));
        }
        RoomListServiceRequest request = new RoomListServiceRequest(null, null, null, 1, 10);
        //when
        RoomListResponse response = roomService.getRoomList(request);

        //then
        assertThat(response).isNotNull();
        assertThat(response.rooms()).hasSize(10);
        assertThat(response.rooms()).extracting("title", "type", "capacity")
                .containsExactlyInAnyOrder(
                        tuple("Room 0", RoomType.INTERVIEW, 3),
                        tuple("Room 1", RoomType.INTERVIEW, 3),
                        tuple("Room 2", RoomType.INTERVIEW, 3),
                        tuple("Room 3", RoomType.INTERVIEW, 3),
                        tuple("Room 4", RoomType.INTERVIEW, 3),
                        tuple("Room 5", RoomType.INTERVIEW, 3),
                        tuple("Room 6", RoomType.INTERVIEW, 3),
                        tuple("Room 7", RoomType.INTERVIEW, 3),
                        tuple("Room 8", RoomType.INTERVIEW, 3),
                        tuple("Room 9", RoomType.INTERVIEW, 3));
    }

    @DisplayName("빈 방 목록을 조회한다.")
    @Test
    void getEmptyRoomListTest() {
        // given
        String roomType = RoomType.INTERVIEW.name();
        int capacity = 3;
        String status = RoomStatus.WAIT.name();
        int page = 1;
        int size = 10;

        RoomListServiceRequest request = new RoomListServiceRequest(roomType, capacity, status, page, size);

        // when
        RoomListResponse response = roomService.getRoomList(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.rooms()).isEmpty();
    }

    private RoomRedis createRoom(String title, RoomType type, int capacity) {
        return RoomRedis.builder()
                .title(title)
                .description("Test Room Description")
                .type(type)
                .capacity(capacity).build();
    }

    @Test
    @DisplayName("존재하는 방에 사용자가 입장할 수 있다.")
    void enterExistingRoom() {
        // given
        RoomRedis testRoom = createRoom("Test Room", RoomType.INTERVIEW, 3);
        testRoom.addParticipant(123L);
        roomRedisRepository.save(testRoom);
        String roomId = testRoom.getId();
        Long userId = 1L;
        RoomEnterServiceRequest request = new RoomEnterServiceRequest(userId, roomId);

        // when
        roomService.enterRoom(request);
        RoomRedis room = roomRedisRepository.findById(roomId).orElse(null);

        // then
        assertThat(room).isNotNull();
        assertThat(room.getUserList()).contains(userId);
    }

    @Test
    @DisplayName("존재하지 않는 방에 입장 시도 시 예외가 발생한다.")
    void enterNonExistingRoom() {
        // given
        RoomRedis testRoom = createRoom("Test Room", RoomType.INTERVIEW, 3);
        testRoom.addParticipant(123L);
        roomRedisRepository.save(testRoom);
        String nonExistingRoomId = "non-existing-id";
        Long userId = 1L;
        RoomEnterServiceRequest request = new RoomEnterServiceRequest(userId, nonExistingRoomId);

        // when & then
        assertThatThrownBy(() -> roomService.enterRoom(request))
                .isInstanceOf(RoomNotFoundException.class)
                .hasMessage("Room not found");
    }

    @Test
    @DisplayName("사용자가 방에 입장한 후 해당 방의 사용자 목록이 업데이트된다.")
    void updateUserListAfterEnteringRoom() {
        // given
        RoomRedis testRoom = createRoom("Test Room", RoomType.INTERVIEW, 3);
        testRoom.addParticipant(123L);
        roomRedisRepository.save(testRoom);

        String roomId = testRoom.getId();
        Long userId = 1L;
        RoomEnterServiceRequest request = new RoomEnterServiceRequest(userId, roomId);

        // when
        roomService.enterRoom(request);

        // then
        RoomRedis room = roomRedisRepository.findById(roomId).orElse(null);
        assertThat(room).isNotNull();
        assertThat(room.getUserList()).contains(userId);
        assertThat(room.getUserList().size()).isEqualTo(2);
    }

    @DisplayName("존재하지 않는 방에서 나가려고 시도하면 예외가 발생한다.")
    @Test
    void exitNonExistingRoomTest() {
        // given
        String nonExistingRoomId = "non-existing-id";
        Long userId = 1L;
        RoomExitServiceRequest request = new RoomExitServiceRequest(nonExistingRoomId, userId);

        // when & then
        assertThatThrownBy(() -> roomService.exitRoom(request))
                .isInstanceOf(RoomNotFoundException.class)
                .hasMessage("Room not found");
    }

    @DisplayName("사용자가 방에서 나간 후 해당 방의 사용자 목록이 업데이트된다.")
    @Test
    void updateUserListAfterExitingRoom() {
        // given
        RoomRedis testRoom = createRoom("Test Room", RoomType.INTERVIEW, 3);
        testRoom.addParticipant(123L);
        testRoom.addParticipant(1L);
        roomRedisRepository.save(testRoom);

        String roomId = testRoom.getId();
        Long userId = 1L;
        RoomExitServiceRequest request = new RoomExitServiceRequest(roomId, userId);

        // when
        roomService.exitRoom(request);

        // then
        RoomRedis room = roomRedisRepository.findById(roomId).orElse(null);
        assertThat(room).isNotNull();
        assertThat(room.getUserList()).doesNotContain(userId);
        assertThat(room.getUserList().size()).isEqualTo(1);
    }

}
