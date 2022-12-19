# Board_Game_Server
### 학교 과제 보드네트워크게임의 서버입니다.
_____________________________________

master Branch의 경우 서버와 클라이언트의 통신을 스트림 기반으로 구현했고, buff_chan의 경우 버퍼/채널 기반으로 구현했습니다.

서버의 기능은 다음과 같습니다.
1. 클라이언트가 서버에 접근을 시도하면 클라이언트의 이름을 서버에 등록합니다.
2. 2개의 클라이언트가 등록되면 클라이언트에서 주사위 던진 결과값을 받아 500칸의 보드게임을 진행합니다.
3. 진행되는 결과는 해당 클라이언트와 상대 클라이언트에 전달합니다.
4. 각 Jump, Back, Skip 이벤트는 보드가 만들어 질때 랜덤으로 생성됩니다.
5. 보드에는 Jump, Back, Skip 이벤트가 존재하고, 보드 이외에 Catch 이벤트가 존재합니다.
- Jump는 던진 주사위의 결과값 만큼 앞으로 이동합니다.
- Back은 던진 주사위의 결과값 만큼 뒤로 이동합니다.
- Skip은 다음턴에 주사위를 던져도 이동할 수 없고, 다음 턴에 이동이 가능합니다.
- Catch 이벤트는 한 말이 다른 말과 같은 칸으로 이동하면 발생되며, 상대방의 말을 처음으로 보내는 역할을 합니다.
