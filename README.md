# NaviWavi
모바일 컴퓨팅 Term Project <br>
https://docs.google.com/document/d/1RdzLOr3-v5hGspap78rsYbzx5tay5mwTpSBBHw4yG58/edit

## 🤞 Abstract
운전 중에 감정이 불안정하면 사고로 이어질 가능성이 높다.
<br>
우리는 이를 방지하고자 감정 변화 유도 네비게이션을 제안한다.
<br>
스마트폰으로 감정 측정 및 제어 모듈을 구현하고 그 유효성을 판별하고자 한다.
<br>
운전자의 얼굴을 인식해 현재 감정 상태를 파악하고, Neutral한 상태로 유도하기 위해 맞춤형 음악을 재생시켜준다.
<br>
목적지까지 주행을 완료 후, 설문을 통해 운전자의 감정 상태의 변화에 대해 물어본다.


## 📜 Implementation Order

- __Tmap API를 기반으로 내비게이션 구현__
    - 사용자에게 목적지를 입력받기
    - 사용자가 입력한 목적지에 따라 가고자 하는 목적지 3가지 옵션을 제시
	- 현 위치 ~ 목적지 까지의 경로 안내 구현
	- 현 위치에서 일정 거리를 전진할 때 마다 경로 갱신
	- 목적지에 다다르면 경로 안내 종료

- __Naver clova face recognition API를 기반으로 운전자 감정을 분석__
  - 스마트폰의 rear camera를 활용하여 운전자의 얼굴 촬영
  - 10초에 한 컷씩 촬영 (가정)
  - anger, disgust, fear, laugh, neutral, sad, surprise, smile, talking으로 분석
  - confidence 도 메트릭으로 고려 중임
  - (Naver clova face recognition API) https://apidocs.ncloud.com/ko/ai-naver/clova_face_recognition/face/

- __사용자의 얼굴을 통해 분석한 감정을 토대로 음악 재생__
	-  Neutral한 감정 상태로 변화를 이끌어 내고자 감정 별로 다른 음악을 재생시킴
	- 어떤 음악을 틀지는 선행 연구를 참고해야할 듯 (몇 bpm의 음악이 가장 좋은지 등…)

- __목적지 도착 후 운전자에게 설문조사 실시__
	- 운전을 하면서 감정의 변화를 느꼈는지
	- 현재 감정상태가 Neutral 한지 검증
