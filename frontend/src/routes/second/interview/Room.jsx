import { useParams, useNavigate } from "react-router-dom";
import { rooms } from "./data.js";
import Chat from "../components/Chat";
import { useState } from "react";

export default function Room() {
  const { roomid } = useParams();
  const navigate = useNavigate();
  const roomIndex = rooms.findIndex((room) => room.id === roomid);
  const room = roomIndex !== -1 ? rooms[roomIndex] : null;
  const [messages, setMessages] = useState([]);

  const currentUser = { userId: "user1", name: "Alice" };

  if (!room) return <div>방을 찾을 수 없습니다.</div>;

  function navigateHandler() {
    // 참가자가 방을 나가면 제거
    const participantIndex = room.userList.findIndex(
      (participant) => participant.userId === currentUser.userId
    );
    if (participantIndex !== -1) {
      room.userList.splice(participantIndex, 1);
    }

    // 참가자가 아무도 없으면 방을 삭제
    if (room.userList.length === 0) {
      rooms.splice(roomIndex, 1);
    }

    navigate("/second/interview");
  }

  function startInterviewHandler() {
    navigate(`/second/interview/room/${roomid}/pt_ready`);
  }

  function sendMessage(newMessage) {
    const newChatMessage = {
      userId: currentUser.userId,
      name: currentUser.name,
      message: newMessage,
    };
    setMessages([...messages, newChatMessage]);
  }

  return (
    <div className="flex justify-center">
      <div
        className="w-full mt-16 overflow-hidden"
        style={{ minWidth: "1100px" }}
      >
        <div className="w-full h-[80vh] mx-auto mt-5 p-6 rounded-xl bg-white shadow-2xl">
          <div className="flex justify-between items-center mb-2">
            <div className="flex">
              <img
                className="h-[25px] pr-4 mt-1"
                src="/SSAFYRO.png"
                alt="SSAFYRO 로고"
              />
              <p className="font-extrabold text-2xl">SSAFYRO</p>
            </div>
            <div className="items-center">
              <h1 className="font-extrabold text-2xl">{room.title}</h1>
            </div>
            <button
              className="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600"
              onClick={navigateHandler}
            >
              나가기
            </button>
          </div>

          <div
            className="flex h-[95%] border rounded-xl mt-4"
            style={{
              backgroundColor: "rgba(249, 250, 255, 1)",
              borderColor: "rgba(249, 250, 255, 1)",
            }}
          >
            <div className="w-[70%] flex flex-col p-4">
              <div className="flex-grow rounded-lg p-1 flex items-center justify-between h-[50%]">
                {room.userList.map((participant, index) => (
                  <div
                    key={index}
                    className="w-[32%] h-[90%] bg-gray-200 rounded-lg flex flex-col items-center justify-center px-5"
                  >
                    <img
                      src="/main/users.png"
                      alt="User"
                      className="h-2/3 object-contain rounded-full"
                    />
                    <span className="text-sm font-bold mt-2">
                      {participant.name}
                    </span>
                  </div>
                ))}
                {Array(room.capacity - room.userList.length)
                  .fill()
                  .map((_, index) => (
                    <div
                      key={index + room.userList.length}
                      className="w-[32%] h-[90%] bg-gray-200 rounded-lg flex flex-col items-center justify-center px-5"
                    >
                      {/* 빈자리 */}
                    </div>
                  ))}
                {Array(3 - Math.max(room.capacity, room.userList.length))
                  .fill()
                  .map((_, index) => (
                    <div
                      key={index + room.capacity + room.userList.length}
                      className="w-[32%] h-[90%] bg-gray-200 rounded-lg flex flex-col items-center justify-center px-5"
                    >
                      {/* 최대 수용 인원을 초과한 자리 X 표시 */}
                      <span className="text-2xl text-gray-400">X</span>
                    </div>
                  ))}
              </div>

              {/* Chat 컴포넌트를 사용 */}
              <Chat
                currentUser={currentUser}
                messages={messages}
                sendMessage={sendMessage}
              />
            </div>
            <div className="w-[30%] flex flex-col justify-between">
              <div
                className="p-5 bg-white shadow-md rounded-xl ml-3 mr-5 mt-8"
                style={{ height: "60%" }}
              >
                <div className="flex items-center mb-4">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="2.0em"
                    height="2.0em"
                    viewBox="0 0 48 48"
                    className="text-blue-500"
                  >
                    <path
                      fill="none"
                      stroke="currentColor"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M18.005 43.5h12.188m-19.5-28.667C12.429 8.353 17.39 4.5 24.099 4.5s11.67 3.852 13.406 10.333s-1.502 13.125-7.312 16.48v7.312H18.005v-7.312c-7.65-3.654-9.049-10-7.312-16.48"
                    ></path>
                    <path
                      fill="none"
                      stroke="currentColor"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M14.28 18.296s-.786-8.599 9.888-9.982"
                    ></path>
                  </svg>
                  <h2 className="flex-grow text-2xl font-bold ml-2">
                    선배들의 Tip!
                  </h2>
                </div>
                <div className="flex items-start mt-8 px-2">
                  <div className="flex-shrink-0 w-6 h-6 bg-blue-500 text-white flex items-center justify-center rounded-full mr-2">
                    1
                  </div>
                  <p className="px-2 text-gray-700">
                    완벽하게 말할 필요 없어요! 자신이 생각한 바를 면접관이
                    이해할 수 있을 정도로만 전달할 수 있으면 되요!
                  </p>
                </div>
              </div>
              <div
                className="p-7 flex justify-center ml-3 mr-5 mt-8 mb-7"
                style={{ height: "25%" }}
              >
                <button
                  className="w-full font-extrabold bg-blue-500 text-white px-4 py-2 rounded-3xl hover:bg-blue-600"
                  onClick={startInterviewHandler}
                >
                  면접 시작
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
