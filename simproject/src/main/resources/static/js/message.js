// SockJS 연동을 위한 js파일
document.addEventListener("DOMContentLoaded", function () {
    const roomId = document.querySelector('input#roomId').value;;
    const loginId = document.querySelector('input#loginId').value;

    console.log(roomId + ", " + loginId);

    const sockJs = new SockJS("/ws/stomp");
    // SockJS를 내부에 들고 있는 stomp를 내어줌
    const stomp = Stomp.over(sockJs);

    // connection이 맺어지면 실행
    stomp.connect({}, function () {
        console.log("STOMP Connection");

        loadChatMessages().then(function() {
            stomp.subscribe("/topic/" + roomId, function (chat) {
                showChatMessages(JSON.parse(chat.body));
            });

            stomp.send("/app/enter/" + roomId, {}, JSON.stringify({roomId: roomId, sender: loginId, status: "ENTER"}));
        });
    });

    // 메시지를 작성하고 전송하기 위한 메소드
    const sendMessage = (e) => {
        const msg = document.querySelector('input#msg');

        console.log(loginId + ":" + msg.value);
        stomp.send("/app/send/" + roomId, {}, JSON.stringify({roomId: roomId, content: msg.value, sender: loginId,
        status: "TALK"}));
        msg.value = '';
    };

    // btnSend 버튼에 이벤트 추가
    const sendBtn = document.querySelector('button#btnSend');
    sendBtn.addEventListener('click', sendMessage);

    // 채팅방을 완전히 퇴장할 시 퇴장 메시지 전송
    const leaveChatRoom = (e) => {
        console.log("Leave : " + loginId);
        stomp.send("/app/leave/" + roomId, {}, JSON.stringify({roomId: roomId, sender: loginId,
            status: "LEAVE"}));
        location.replace('/chat/room/list');
    };

    // btnLeave 버튼에 이벤트 추가
    const leaveBtn = document.querySelector('button#btnLeave');
    leaveBtn.addEventListener('click', leaveChatRoom);



    // 채팅 메세지를 불러오기 위한 메서드
    const loadChatMessages = async () => {
        const url = `/api/v1/chat/messages/${roomId}/${loginId}`;

        const response = await axios.get(url);
        const data = response.data;

        let htmlStr = '';
        const list = data['list'];
        let count = 0 - data['count'];
        for(let chat of list) {
            if(count >= 1){
                if(chat.sender == loginId) {
                    htmlStr += `
					<div class="d-flex align-items-center alert alert-warning border border-dark">
					  <div class="flex-grow-1 ms-3">
					    <h5>${chat.sender}</h5>
					    <p>${chat.content}</p>
					  </div>
					</div>
				`;
                } else {
                    htmlStr += `
					<div class="d-flex align-items-center alert alert-primary border border-dark">
					  <div class="flex-grow-1 ms-3">
					    <h5>${chat.sender}</h5>
					    <p>${chat.content}</p>
					  </div>
					</div>
				`;
                }
            }

            else if(((chat.sender == loginId) && (chat.status == "ENTER"))){
                count++;
                if(count >= 1){
                    if(chat.sender == loginId) {
                        htmlStr += `
                        <div class="d-flex align-items-center alert alert-warning border border-dark">
                          <div class="flex-grow-1 ms-3">
                            <h5>${chat.sender}</h5>
                            <p>${chat.content}</p>
                          </div>
                        </div>
                    `;
                    } else {
                        htmlStr += `
                        <div class="d-flex align-items-center alert alert-primary border border-dark">
                          <div class="flex-grow-1 ms-3">
                            <h5>${chat.sender}</h5>
                            <p>${chat.content}</p>
                          </div>
                        </div>
                    `;
                    }
                }
            }
        }

        // 채팅 내역을 불러올 때 가장 최근 메세지가 보이도록 함
        const messageArea = document.querySelector('div#messages');
        messageArea.innerHTML = htmlStr;
        messageArea.scrollTop = messageArea.scrollHeight;
    };

    const showChatMessages = (chat) => {
        let htmlStr = '';
        const messageArea = document.querySelector('div#messages');
        let existingHTML = messageArea.innerHTML;
        if(chat.content != null){
            htmlStr += `
                        <div class="d-flex align-items-center alert alert-primary border border-dark">
                          <div class="flex-grow-1 ms-3">
                            <h5>${chat.sender}</h5>
                            <p>${chat.content}</p>
                          </div>
                        </div>
                    `;
            existingHTML += htmlStr;
            messageArea.innerHTML = existingHTML;
        }
    };
});