package com.luckycharmfairy.data.model

data class Team(
    var sport: String = "",
    var name: String = "",
    var shortname: String = "",
    var teamcolor: String = "",
    var home: String = "",
    var fans: Int = 0,
    var youtube: String = "",
    var instagram: String = "",
    var facebook: String = "",
    var homepage: String = "",
    var players: MutableList<String> = mutableListOf()
)

public val baseballTeams = listOf(
    "한화 이글스",
    "LG 트윈스",
    "삼성 라이온즈",
    "기아 타이거즈",
    "KT 위즈",
    "키움 히어로즈",
    "롯데 자이언츠",
    "두산 베어스",
    "SSG 랜더스",
    "NC 다이노스",
    "직접 입력"
    )

public val footballTeams = listOf(
    "강원 FC",
    "광주 FC",
    "김천 상무 FC",
    "대구 FC",
    "대전 하나 시티즌",
    "FC 서울",
    "수원 FC",
    "울산HD FC",
    "인천 유나이티드 FC",
    "전북 현대 모터스",
    "제주 유나이티드 FC",
    "포항 스틸러스",
    "직접 입력"
)

public val basketballTeams = listOf(
    "고양 소노",
    "대구 한국가스공사",
    "서울 SK",
    "창원 LG",
    "울산 현대 모비스",
    "부산 KCC",
    "수원 KT",
    "안양 정관장",
    "원주 DB",
    "서울 삼성",
    "직접 입력"
)

public val menVolleyballTeams = listOf(
    "인천 대한항공 점보스",
    "안산 OK저축은행 읏맨",
    "서울 우리카드 우리WON",
    "천안 현대캐피탈 스카이워커스",
    "수원 한국전력 빅스톰",
    "대전 삼성화재 블루팡스",
    "의정부 KB손해보험 스타즈",
    "직접 입력"
)

public val womenVolleyballTeams = listOf(
    "수원 현대건설 힐스테이트",
    "인천 흥국생명 핑크스파이더스",
    "대전 정관장 레드스파크스",
    "GS칼텍스 서울 KIXX",
    "화성 IBK기업은행 알토스",
    "김천 한국도로공사 하이패스",
    "광주 페퍼저축은행 AI 페퍼스",
    "직접 입력"
)