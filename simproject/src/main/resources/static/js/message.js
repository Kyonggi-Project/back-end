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

        stomp.subscribe("/topic/" + roomId, function () {
            loadChatMessages();
        });

        stomp.send("/app/enter/" + roomId, {}, JSON.stringify({roomId: roomId, sender: loginId}));
    });

    const sendMessage = (e) => {
        const msg = document.querySelector('input#msg');

        console.log(loginId + ":" + msg.value);
        stomp.send("/app/send/" + roomId, {}, JSON.stringify({roomId: roomId, content: msg.value, sender: loginId}));
        msg.value = '';
    };

    const sendBtn = document.querySelector('button#btnSend');
    sendBtn.addEventListener('click', sendMessage);


    // 채팅 메세지를 불러오기 위한 메서드
    const loadChatMessages = async () => {
        const url = `/api/v1/chat/messages/${roomId}`;

        const response = await axios.get(url);
        console.log(response.data);

        let htmlStr = '';
        for(let chat of response.data) {

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

        // 채팅 내역을 불러올 때 가장 최근 메세지가 보이도록 함
        const messageArea = document.querySelector('div#messages');
        messageArea.innerHTML = htmlStr;
        messageArea.scrollTop = messageArea.scrollHeight;
    };

});