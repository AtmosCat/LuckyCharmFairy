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

public val baseballTeamNames = listOf(
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

public val allTeams = baseballTeams + menFootballTeams + menBasketballTeams + menVolleyballTeams + womenVolleyballTeams + womenBasketballTeams

public val baseballTeams: List<Team>
    get() = listOf(
        baseball_HanwhaEagles,
        baseball_SamsungLions,
        baseball_KiwoonHeroes,
        baseball_LgTwins,
        baseball_KiaTigers,
        baseball_DusanBears,
        baseball_LotteGiants,
        baseball_NcDinos,
        baseball_SsgLanders,
        baseball_KtWiz
    )

public val baseball_HanwhaEagles = Team(
    sport = "야구",
    name = "한화 이글스",
    shortname = "한화",
    teamcolor = "#FF6600",
    home = "대전베이스볼드림파크",
    fans = 0,
    youtube = "https://www.youtube.com/channel/UCdq4Ji3772xudYRUatdzRrg",
    instagram = "https://www.instagram.com/hanwhaeagles_soori/",
    facebook = "https://www.facebook.com/hanwhaeagles.news/?locale=ko_KR",
    homepage = "https://www.hanwhaeagles.co.kr/index.do",
    players = mutableListOf()
)

public val baseball_LgTwins = Team(
    sport = "야구",
    name = "LG 트윈스",
    shortname = "LG",
    teamcolor = "#C30452",
    home = "잠실종합운동장",
    fans = 0,
    youtube = "https://www.youtube.com/c/lgtwinstv",
    instagram = "https://www.instagram.com/lgtwinsbaseballclub/",
    facebook = "https://www.facebook.com/LGTWINSSEOUL/?locale=ko_KR",
    homepage = "https://www.lgtwins.com/service/html.ncd?view=/pc_twins/twins_main/twins_main",
    players = mutableListOf()
)

public val baseball_SamsungLions = Team(
    sport = "야구",
    name = "삼성 라이온즈",
    shortname = "삼성",
    teamcolor = "#074CA1",
    home = "대구삼성라이온즈파크",
    fans = 0,
    youtube = "https://www.youtube.com/@lionstv1982",
    instagram = "https://www.instagram.com/samsunglions_baseballclub/?hl=ko",
    facebook = "https://www.facebook.com/snssamsunglions/?locale=ko_KR",
    homepage = "https://www.samsunglions.com/",
    players = mutableListOf()
)


public val baseball_KiaTigers = Team(
    sport = "야구",
    name = "기아 타이거즈",
    shortname = "기아",
    teamcolor = "#EA0029",
    home = "광주기아챔피언스필드",
    fans = 0,
    youtube = "https://www.youtube.com/channel/UCKp8knO8a6tSI1oaLjfd9XA",
    instagram = "https://www.instagram.com/always_kia_tigers/",
    facebook = "https://www.facebook.com/Victory.KiaTigers/?locale=ko_KR",
    homepage = "https://tigers.co.kr/",
    players = mutableListOf()
)

public val baseball_KtWiz = Team(
    sport = "야구",
    name = "KT 위즈",
    shortname = "KT",
    teamcolor = "#000000",
    home = "수원케이티위즈파크",
    fans = 0,
    youtube = "https://www.youtube.com/c/ktwiztv",
    instagram = "https://www.instagram.com/ktwiz.pr/p/BDF1QJ9pWWV/",
    facebook = "https://www.facebook.com/ktwiz/?locale=ko_KR",
    homepage = "https://www.ktwiz.co.kr/",
    players = mutableListOf()
)

public val baseball_KiwoonHeroes = Team(
    sport = "야구",
    name = "키움 히어로즈",
    shortname = "키움",
    teamcolor = "#570514",
    home = "고척스카이돔",
    fans = 0,
    youtube = "https://www.youtube.com/@heroesbaseballclub",
    instagram = "https://www.instagram.com/heroesbaseballclub/p/DBIqMWuP_5K/",
    facebook = "https://www.facebook.com/kiwoomheroesbaseballclub/?locale=ko_KR",
    homepage = "https://heroesbaseball.co.kr/index.do",
    players = mutableListOf()
)

public val baseball_LotteGiants = Team(
    sport = "야구",
    name = "롯데 자이언츠",
    shortname = "롯데",
    teamcolor = "#041E42",
    home = "부산사직야구장",
    fans = 0,
    youtube = "https://www.youtube.com/channel/UCAZQZdSY5_YrziMPqXi-Zfw",
    instagram = "https://www.instagram.com/busanlottegiants/",
    facebook = "https://www.facebook.com/lottegiantsbusan/?locale=ko_KR",
    homepage = "https://www.giantsclub.com/html/index.asp?pcode=813",
    players = mutableListOf()
)

public val baseball_DusanBears = Team(
    sport = "야구",
    name = "두산 베어스",
    shortname = "두산",
    teamcolor = "#131230",
    home = "잠실종합운동장",
    fans = 0,
    youtube = "https://www.youtube.com/channel/UCsebzRfMhwYfjeBIxNX1brg",
    instagram = "https://www.instagram.com/doosanbears.1982/",
    facebook = "https://www.facebook.com/1982doosanbears/?locale=ko_KR",
    homepage = "https://www.doosanbears.com/",
    players = mutableListOf()
)

public val baseball_SsgLanders = Team(
    sport = "야구",
    name = "SSG 랜더스",
    shortname = "SSG",
    teamcolor = "#CE0E2D",
    home = "인천SSG랜더스필드",
    fans = 0,
    youtube = "https://www.youtube.com/channel/UCt8iRtgjVqm5rJHNl1TUojg",
    instagram = "https://www.instagram.com/ssglanders.incheon/?hl=ko",
    facebook = "https://www.facebook.com/SSGlanders/?locale=ko_KR",
    homepage = "https://www.ssglanders.com/main",
    players = mutableListOf()
)

public val baseball_NcDinos = Team(
    sport = "야구",
    name = "NC 다이노스",
    shortname = "NC",
    teamcolor = "#315288",
    home = "창원NC파크",
    fans = 0,
    youtube = "https://www.youtube.com/@ncdinos",
    instagram = "https://www.instagram.com/ncdinos2011/",
    facebook = "https://www.facebook.com/ncdinos/?locale=ko_KR",
    homepage = "https://www.ncdinos.com/",
    players = mutableListOf()
)

public val menFootballTeamNames = listOf(
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
    "경남 FC",
    "김포 FC",
    "부산 아이파크",
    "부천 FC 1995",
    "서울 이랜드 FC",
    "성남 FC",
    "수원 삼성 블루윙즈",
    "충남 아산 FC",
    "안산 그리너스 FC",
    "FC 안양",
    "전남 드래곤즈",
    "천안 시티 FC",
    "충북 청주 FC",
    "직접 입력"
)

public val menFootballTeams: List<Team>
    get() = listOf(
        menFootball_DaejeonHanaCitizen,
        menFootball_GimcheonSangmuFc,
        menFootball_KangwonFc,
        menFootball_JeonbukHyundaiMotors,
        menFootball_DaeguFc,
        menFootball_FcSeoul,
        menFootball_GwangjuFc,
        menFootball_IncheonUnitedFc,
        menFootball_JejuUnitedFc,
        menFootball_PohangSteelers,
        menFootball_SuwonFc,
        menFootball_UlsanHDFc, // 이상 K리그1
        menFootball_GyeongnamFc,
        menFootball_GimpoFc,
        menFootball_BusanIPark,
        menFootball_BucheonFc1995,
        menFootball_SeoulElandFc,
        menFootball_SeongnamFc,
        menFootball_SuwonSamsungBluewings,
        menFootball_ChungnamAsanFc,
        menFootball_AnsanGreenersFc,
        menFootball_FcAnyang,
        menFootball_JeonnamDragons,
        menFootball_CheonanCityFc,
        menFootball_ChungbukCheongjuFc,
    )

public val menFootball_GwangjuFc = Team(
    sport = "남자축구",
    name = "광주 FC",
    shortname = "광주",
    teamcolor = "#FFD24F",
    home = "광주",
    fans = 0,
    youtube = "https://www.youtube.com/@Gwangju_FC",
    instagram = "https://www.instagram.com/gwangju_fc/",
    facebook = "https://www.facebook.com/gjfc2010?locale=fr_CA",
    homepage = "https://www.gwangjufc.com/",
    players = mutableListOf()
)

public val menFootball_KangwonFc = Team(
    sport = "남자축구",
    name = "강원 FC",
    shortname = "강원",
    teamcolor = "#DD5828",
    home = "강원",
    fans = 0,
    youtube = "https://www.youtube.com/gangwonfc",
    instagram = "https://www.instagram.com/gangwon_fc/",
    facebook = "https://www.facebook.com/gangwonfc/?locale=ko_KR",
    homepage = "https://www.gangwon-fc.com/",
    players = mutableListOf()
)

public val menFootball_GimcheonSangmuFc = Team(
    sport = "남자축구",
    name = "김천 상무 FC",
    shortname = "김천상무",
    teamcolor = "#B91D22",
    home = "김천",
    fans = 0,
    youtube = "https://www.youtube.com/channel/UCSZ-CvpbBm6JnZnWYmiNrlQ",
    instagram = "https://www.instagram.com/gimcheonfc/",
    facebook = "https://www.facebook.com/gimcheonsangmu/",
    homepage = "https://www.kleague.com/club/club.do?teamId=K35",
    players = mutableListOf()
)

public val menFootball_DaeguFc = Team(
    sport = "남자축구",
    name = "대구 FC",
    shortname = "대구",
    teamcolor = "#0057B8",
    home = "대구",
    fans = 0,
    youtube = "https://www.youtube.com/user/TheDaeguFC",
    instagram = "https://www.instagram.com/daegufc.co.kr/",
    facebook = "https://www.facebook.com/daegufc2002/?locale=ko_KR",
    homepage = "https://www.daegufc.co.kr/main/",
    players = mutableListOf()
)
public val menFootball_DaejeonHanaCitizen = Team(
    sport = "남자축구",
    name = "대전 하나 시티즌",
    shortname = "대전",
    teamcolor = "#9F2943",
    home = "대전",
    fans = 0,
    youtube = "https://www.youtube.com/@daejeonhanacitizen",
    instagram = "https://www.instagram.com/daejeon_hana/",
    facebook = "https://www.facebook.com/dhcfc.kr/videos/?_rdr",
    homepage = "https://www.dhcfc.kr/",
    players = mutableListOf()
)
public val menFootball_FcSeoul = Team(
    sport = "남자축구",
    name = "FC 서울",
    shortname = "FC 서울",
    teamcolor = "#b5191a",
    home = "서울",
    fans = 0,
    youtube = "https://www.youtube.com/user/fcseoul",
    instagram = "https://www.instagram.com/fcseoul/",
    facebook = "https://www.facebook.com/fcseoul/?locale=ko_KR",
    homepage = "https://www.fcseoul.com/;jsessionid=14FDB8A99F1F27F3F075059225E41E15",
    players = mutableListOf()
)
public val menFootball_SuwonFc = Team(
    sport = "남자축구",
    name = "수원 FC",
    shortname = "수원FC",
    teamcolor = "#00396f",
    home = "수원",
    fans = 0,
    youtube = "https://www.youtube.com/channel/UCHPiDeQQyVcYe-nhyUanSWg",
    instagram = "https://www.instagram.com/suwonfc/",
    facebook = "https://www.facebook.com/suwonfc2003/?locale=ko_KR",
    homepage = "https://www.suwonfc.com/",
    players = mutableListOf()
)
public val menFootball_UlsanHDFc = Team(
    sport = "남자축구",
    name = "울산HD FC",
    shortname = "울산",
    teamcolor = "#013F8A",
    home = "울산",
    fans = 0,
    youtube = "https://www.youtube.com/channel/UCZ8lUWJ0_vZZf-SiNedia7Q",
    instagram = "https://www.instagram.com/uhdfc_1983/",
    facebook = "https://www.facebook.com/ulsanfc/?locale=ko_KR",
    homepage = "https://www.uhdfc.com/",
    players = mutableListOf()
)
public val menFootball_IncheonUnitedFc = Team(
    sport = "남자축구",
    name = "인천 유나이티드 FC",
    shortname = "인천",
    teamcolor = "#004F9E",
    home = "인천",
    fans = 0,
    youtube = "https://www.youtube.com/user/TheINCHEONUNITED",
    instagram = "https://www.instagram.com/incheonutd/",
    facebook = "https://www.facebook.com/incheonutdfc/",
    homepage = "https://www.incheonutd.com/main/index.php",
    players = mutableListOf()
)
public val menFootball_JeonbukHyundaiMotors = Team(
    sport = "남자축구",
    name = "전북 현대 모터스",
    shortname = "전북",
    teamcolor = "#034F36",
    home = "전북",
    fans = 0,
    youtube = "https://www.youtube.com/user/JEONBUKHYUNDAI",
    instagram = "https://www.instagram.com/jeonbuk1994/",
    facebook = "https://www.facebook.com/jeonbuk1994/?locale=ko_KR",
    homepage = "https://hyundai-motorsfc.com/",
    players = mutableListOf()
)
public val menFootball_JejuUnitedFc = Team(
    sport = "남자축구",
    name = "제주 유나이티드 FC",
    shortname = "제주",
    teamcolor = "#F58024",
    home = "제주",
    fans = 0,
    youtube = "https://www.youtube.com/channel/UCQfQeoiJTN2EVqde4_0PlUA",
    instagram = "https://www.instagram.com/jejuunitedfc/",
    facebook = "https://www.facebook.com/JejuUnitedFootballClub/?locale=ko_KR",
    homepage = "https://www.jeju-utd.com/",
    players = mutableListOf()
)
public val menFootball_PohangSteelers = Team(
    sport = "남자축구",
    name = "포항 스틸러스",
    shortname = "포항",
    teamcolor = "#FB342E",
    home = "포항",
    fans = 0,
    youtube = "https://www.youtube.com/channel/UCOZQIw1I6ixjQZ_va_eCn7w",
    instagram = "https://www.instagram.com/fc.pohangsteelers/",
    facebook = "https://www.facebook.com/fcpohangsteelers/?locale=ko_KR",
    homepage = "https://www.steelers.co.kr/",
    players = mutableListOf()
)

public val menFootball_GyeongnamFc = Team(
    sport = "남자축구",
    name = "경남 FC",
    shortname = "경남",
    teamcolor = "#c02717",
    home = "경남",
    fans = 0,
    youtube = "",
    instagram = "",
    facebook = "",
    homepage = "",
    players = mutableListOf()
)

public val menFootball_GimpoFc = Team(
    sport = "남자축구",
    name = "김포 FC",
    shortname = "김포",
    teamcolor = "#06443f",
    home = "김포",
    fans = 0,
    youtube = "",
    instagram = "",
    facebook = "",
    homepage = "",
    players = mutableListOf()
)
public val menFootball_BusanIPark = Team(
    sport = "남자축구",
    name = "부산 아이파크",
    shortname = "부산",
    teamcolor = "#ee3123",
    home = "부산",
    fans = 0,
    youtube = "",
    instagram = "",
    facebook = "",
    homepage = "",
    players = mutableListOf()
)
public val menFootball_BucheonFc1995 = Team(
    sport = "남자축구",
    name = "부천 FC 1995",
    shortname = "부천",
    teamcolor = "#ac2424",
    home = "부천",
    fans = 0,
    youtube = "",
    instagram = "",
    facebook = "",
    homepage = "",
    players = mutableListOf()
)
public val menFootball_SeoulElandFc = Team(
    sport = "남자축구",
    name = "서울 이랜드 FC",
    shortname = "서울 이랜드",
    teamcolor = "#000130",
    home = "서울",
    fans = 0,
    youtube = "",
    instagram = "",
    facebook = "",
    homepage = "",
    players = mutableListOf()
)
public val menFootball_SeongnamFc = Team(
    sport = "남자축구",
    name = "성남 FC",
    shortname = "성남",
    teamcolor = "#1e1c1e",
    home = "성남",
    fans = 0,
    youtube = "",
    instagram = "",
    facebook = "",
    homepage = "",
    players = mutableListOf()
)
public val menFootball_SuwonSamsungBluewings = Team(
    sport = "남자축구",
    name = "수원 삼성 블루윙즈",
    shortname = "수원 삼성",
    teamcolor = "#0058a7",
    home = "수원",
    fans = 0,
    youtube = "",
    instagram = "",
    facebook = "",
    homepage = "",
    players = mutableListOf()
)
public val menFootball_ChungnamAsanFc = Team(
    sport = "남자축구",
    name = "충남 아산 FC",
    shortname = "아산",
    teamcolor = "#244694",
    home = "아산",
    fans = 0,
    youtube = "",
    instagram = "",
    facebook = "",
    homepage = "",
    players = mutableListOf()
)
public val menFootball_AnsanGreenersFc = Team(
    sport = "남자축구",
    name = "안산 그리너스 FC",
    shortname = "안산",
    teamcolor = "#00a083",
    home = "안산",
    fans = 0,
    youtube = "",
    instagram = "",
    facebook = "",
    homepage = "",
    players = mutableListOf()
)
public val menFootball_FcAnyang = Team(
    sport = "남자축구",
    name = "FC 안양",
    shortname = "안양",
    teamcolor = "#4b237a",
    home = "안양",
    fans = 0,
    youtube = "",
    instagram = "",
    facebook = "",
    homepage = "",
    players = mutableListOf()
)
public val menFootball_JeonnamDragons = Team(
    sport = "남자축구",
    name = "전남 드래곤즈",
    shortname = "전남",
    teamcolor = "#ffde00",
    home = "전남",
    fans = 0,
    youtube = "",
    instagram = "",
    facebook = "",
    homepage = "",
    players = mutableListOf()
)
public val menFootball_CheonanCityFc = Team(
    sport = "남자축구",
    name = "천안 시티 FC",
    shortname = "천안",
    teamcolor = "#5fb4e5",
    home = "천안",
    fans = 0,
    youtube = "",
    instagram = "",
    facebook = "",
    homepage = "",
    players = mutableListOf()
)
public val menFootball_ChungbukCheongjuFc = Team(
    sport = "남자축구",
    name = "충북 청주 FC",
    shortname = "청주",
    teamcolor = "#1c235a",
    home = "청주",
    fans = 0,
    youtube = "",
    instagram = "",
    facebook = "",
    homepage = "",
    players = mutableListOf()
)

public val menBasketballTeamNames = listOf(
    "고양 소노 스카이거너스",
    "대구 한국가스공사 페가수스",
    "서울 SK 나이츠",
    "창원 LG 세이커스",
    "울산 현대모비스 피버스",
    "부산 KCC 이지스",
    "수원 KT 소닉붐",
    "안양 정관장 레드부스터스",
    "원주 DB 프로미",
    "서울 삼성 썬더스",
    "직접 입력"
)

public val menBasketballTeams: List<Team>
    get() = listOf(
        menBasketball_GoyangSonoSkygunners,
        menBasketball_DaeguKogasPegasus,
        menBasketball_BusanKccEgis,
        menBasketball_SeoulSkKnights,
        menBasketball_ChangwonLgSakers,
        menBasketball_AnyangKgcRedboosters,
        menBasketball_SeoulSamsungThunders,
        menBasketball_SuwonKtSonicboom,
        menBasketball_WonjuDbPromy,
        menBasketball_UlsanHyundaiMobisPheobus
    )

public val menBasketball_GoyangSonoSkygunners = Team(
    sport = "남자농구",
    name = "고양 소노 스카이거너스",
    shortname = "고양 소노",
    teamcolor = "#72a3cc",
    home = "고양",
    fans = 0,
    youtube = "https://www.youtube.com/@sonobasketball",
    instagram = "https://www.instagram.com/sonoskygunners_official/",
    facebook = "https://www.facebook.com/@goyangcity/",
    homepage = "https://skygunnersshop.co.kr/",
    players = mutableListOf()
)

public val menBasketball_DaeguKogasPegasus = Team(
    sport = "남자농구",
    name = "대구 한국가스공사 페가수스",
    shortname = "한국가스공사",
    teamcolor = "#3d4498",
    home = "대구",
    fans = 0,
    youtube = "https://www.youtube.com/@kogaspegasus",
    instagram = "https://www.instagram.com/kogasbasketball/",
    facebook = "https://www.facebook.com/kogas.kr/?locale=ko_KR",
    homepage = "https://www.kogas.or.kr/site/kids/1030300000000",
    players = mutableListOf()
)

public val menBasketball_SeoulSkKnights = Team(
    sport = "남자농구",
    name = "서울 SK 나이츠",
    shortname = "SK 나이츠",
    teamcolor = "#e1002a",
    home = "서울",
    fans = 0,
    youtube = "https://www.youtube.com/user/skknightsteam",
    instagram = "https://www.instagram.com/sk_knights_official/",
    facebook = "https://www.facebook.com/skknights/?locale=ko_KR",
    homepage = "https://www.sksports.net/knights/main.asp",
    players = mutableListOf()
)

public val menBasketball_ChangwonLgSakers = Team(
    sport = "남자농구",
    name = "창원 LG 세이커스",
    shortname = "LG 세이커스",
    teamcolor = "#a50034",
    home = "창원",
    fans = 0,
    youtube = "https://www.youtube.com/@LGsakers",
    instagram = "https://www.instagram.com/lgsakers/",
    facebook = "https://www.facebook.com/lgsakerssns/?locale=ko_KR",
    homepage = "https://sakershop.co.kr/",
    players = mutableListOf()
)

public val menBasketball_UlsanHyundaiMobisPheobus = Team(
    sport = "남자농구",
    name = "울산 현대모비스 피버스",
    shortname = "현대모비스",
    teamcolor = "#da291c",
    home = "울산",
    fans = 0,
    youtube = "https://www.youtube.com/@PhoebusTV/community",
    instagram = "https://www.instagram.com/hyundaimobis_phoebus/",
    facebook = "https://www.facebook.com/hyundaimobisphoebus/?locale=ko_KR",
    homepage = "https://phoebus.kbl.or.kr/",
    players = mutableListOf()
)

public val menBasketball_BusanKccEgis = Team(
    sport = "남자농구",
    name = "부산 KCC 이지스",
    shortname = "KCC",
    teamcolor = "#0f236a",
    home = "부산",
    fans = 0,
    youtube = "https://www.youtube.com/@kccegis",
    instagram = "https://www.instagram.com/kcc_egis/",
    facebook = "https://www.facebook.com/kccegissns/?locale=ko_KR",
    homepage = "https://www.kccegis.com/",
    players = mutableListOf()
)

public val menBasketball_SuwonKtSonicboom = Team(
    sport = "남자농구",
    name = "수원 KT 소닉붐",
    shortname = "KT 소닉붐",
    teamcolor = "#ec1c24",
    home = "수원",
    fans = 0,
    youtube = "https://www.youtube.com/channel/UC_nAQFbS0JylS__d1r8NS8Q/about",
    instagram = "https://www.instagram.com/kt_sonicboom_official/",
    facebook = "https://www.facebook.com/ktsonicboom/?locale=ko_KR",
    homepage = "https://m.corp.kt.com/html/promote/sports/basketball.html",
    players = mutableListOf()
)

public val menBasketball_AnyangKgcRedboosters = Team(
    sport = "남자농구",
    name = "안양 정관장 레드부스터스",
    shortname = "정관장",
    teamcolor = "#cf1d24",
    home = "안양",
    fans = 0,
    youtube = "https://www.youtube.com/channel/UCbWGo9Q8CoBVqIk-Z99YZfA",
    instagram = "https://www.instagram.com/red_boosters/",
    facebook = "https://www.facebook.com/kgcsns?locale=hr_HR",
    homepage = "https://m.kgcsports.com/basketball/main.php",
    players = mutableListOf()
)

public val menBasketball_WonjuDbPromy = Team(
    sport = "남자농구",
    name = "원주 DB 프로미",
    shortname = "DB 프로미",
    teamcolor = "#00703c",
    home = "원주",
    fans = 0,
    youtube = "https://www.youtube.com/c/dbpromy",
    instagram = "https://www.instagram.com/dbpromy_official/",
    facebook = "https://www.facebook.com/dbpromybasketball/?locale=ko_KR",
    homepage = "https://promy.kbl.or.kr/",
    players = mutableListOf()
)

public val menBasketball_SeoulSamsungThunders = Team(
    sport = "남자농구",
    name = "서울 삼성 썬더스",
    shortname = "삼성 썬더스",
    teamcolor = "#074ca1",
    home = "서울",
    fans = 0,
    youtube = "https://www.youtube.com/channel/UCj5kPhVr4HZ0Q9vbqbLIYHw/about",
    instagram = "https://www.instagram.com/thundersgram/",
    facebook = "https://www.facebook.com/gothunders/?locale=ko_KR",
    homepage = "https://thunders.kbl.or.kr/",
    players = mutableListOf()
)


public val menVolleyballTeamNames = listOf(
    "인천 대한항공 점보스",
    "안산 OK저축은행 읏맨",
    "서울 우리카드 우리WON",
    "천안 현대캐피탈 스카이워커스",
    "수원 한국전력 빅스톰",
    "대전 삼성화재 블루팡스",
    "의정부 KB손해보험 스타즈",
    "직접 입력"
)

public val menVolleyballTeams: List<Team>
    get() = listOf(
        menVolleyball_IncheonKalJumbos,
        menVolleyball_AnsanOkbankOkman,
        menVolleyball_SuwonKepcoBigstorm,
        menVolleyball_SeoulWooricardWooriwon,
        menVolleyball_CheonanHyundaicapitalSkywalkers,
        menVolleyball_DaejeonSamsungInsurancesBluefangs,
        menVolleyball_UijeonguKbStars
    )

public val menVolleyball_IncheonKalJumbos = Team(
    sport = "남자배구",
    name = "인천 대한항공 점보스",
    shortname = "대한항공",
    teamcolor = "#2e95d0",
    home = "인천",
    fans = 0,
    youtube = "https://www.youtube.com/channel/UC_uQqdSxfNwxrYtNRGdtMXQ/about",
    instagram = "https://www.instagram.com/kal_jbos/channel/",
    facebook = "https://www.facebook.com/KoreanAir.Jumbos/?locale=ko_KR",
    homepage = "https://kovo.co.kr/jumbos",
    players = mutableListOf()
)

public val menVolleyball_AnsanOkbankOkman = Team(
    sport = "남자배구",
    name = "안산 OK저축은행 읏맨",
    shortname = "OK저축은행",
    teamcolor = "#f55000",
    home = "안산",
    fans = 0,
    youtube = "https://www.youtube.com/@okman_volleyballclub",
    instagram = "https://www.instagram.com/okman_volleyballclub/",
    facebook = "https://www.facebook.com/AnsanOKmanvolleyball/?_rdr",
    homepage = "https://www.okfinancialsports.com/volleyball/main/main.asp",
    players = mutableListOf()
)
public val menVolleyball_SeoulWooricardWooriwon = Team(
    sport = "남자배구",
    name = "서울 우리카드 우리WON",
    shortname = "우리카드",
    teamcolor = "#010048",
    home = "서울",
    fans = 0,
    youtube = "https://www.youtube.com/channel/UC75O0ztn068QE_F2QjCKXUw",
    instagram = "https://www.instagram.com/wooricard_volleyball_club/",
    facebook = "https://www.facebook.com/wooricardwon/",
    homepage = "https://m.wooricard.com/dcmw/yh1/cid/cid04/wbvbteam/M1CID201S33.do",
    players = mutableListOf()
)
public val menVolleyball_CheonanHyundaicapitalSkywalkers = Team(
    sport = "남자배구",
    name = "천안 현대캐피탈 스카이워커스",
    shortname = "현대캐피탈",
    teamcolor = "#0078c0",
    home = "천안",
    fans = 0,
    youtube = "https://www.youtube.com/@skywalkers_vbc",
    instagram = "https://www.instagram.com/skywalkers_vbc/",
    facebook = "https://www.facebook.com/HyundaiCapital.SkyWalkers/?locale=ko_KR",
    homepage = "https://www.skywalkers.co.kr/",
    players = mutableListOf()
)
public val menVolleyball_SuwonKepcoBigstorm = Team(
    sport = "남자배구",
    name = "수원 한국전력 빅스톰",
    shortname = "한국전력",
    teamcolor = "#e60012",
    home = "수원",
    fans = 0,
    youtube = "https://www.youtube.com/channel/UCAtifcl-84UeTYd-2MRWKkA",
    instagram = "https://www.instagram.com/vixtorm_vbc/",
    facebook = "https://www.facebook.com/kepcovolleyball/?locale=ko_KR",
    homepage = "https://home.kepco.co.kr/kepco/SP/A/main/volley.do?menuCd=FN1104",
    players = mutableListOf()
)
public val menVolleyball_DaejeonSamsungInsurancesBluefangs = Team(
    sport = "남자배구",
    name = "대전 삼성화재 블루팡스",
    shortname = "삼성화재",
    teamcolor = "#074ca1",
    home = "대전",
    fans = 0,
    youtube = "https://www.youtube.com/@bluefangsvc",
    instagram = "https://www.instagram.com/bluefangsvc/",
    facebook = "https://www.facebook.com/bluefangs/?locale=ko_KR",
    homepage = "https://kovo.co.kr/bluefangs",
    players = mutableListOf()
)
public val menVolleyball_UijeonguKbStars = Team(
    sport = "남자배구",
    name = "의정부 KB손해보험 스타즈",
    shortname = "KB손해보험",
    teamcolor = "#ffbc00",
    home = "의정부",
    fans = 0,
    youtube = "https://www.youtube.com/channel/UC_NHmC0gcNExMXcbKUl0l4w",
    instagram = "https://www.instagram.com/kbstarsvc/",
    facebook = "https://www.facebook.com/kbstarsvc/",
    homepage = "https://www.kbstarsvc.co.kr/main/main.asp",
    players = mutableListOf()
)

public val womenVolleyballTeamNames = listOf(
    "수원 현대건설 힐스테이트",
    "인천 흥국생명 핑크스파이더스",
    "대전 정관장 레드스파크스",
    "GS칼텍스 서울 KIXX",
    "화성 IBK기업은행 알토스",
    "김천 한국도로공사 하이패스",
    "광주 페퍼저축은행 AI 페퍼스",
    "직접 입력"
)

public val womenVolleyballTeams: List<Team>
    get() = listOf(
        womenVolleyball_SuwonHyundaiconstructionHillstate,
        womenVolleyball_DaejeonKgcRedsparks,
        womenVolleyball_GscaltexSeoulKixx,
        womenVolleyball_HwaseongIbkAltos,
        womenVolleyball_IncheonHeungkukinsurancesPinkspiders,
        womenVolleyball_GimcheonExHipass,
        womenVolleyball_GwangjuPepperbanAipeppers
    )

public val womenVolleyball_SuwonHyundaiconstructionHillstate = Team(
    sport = "여자배구",
    name = "수원 현대건설 힐스테이트",
    shortname = "현대건설",
    teamcolor = "#002d74",
    home = "수원",
    fans = 0,
    youtube = "https://www.youtube.com/@HDECVolleyballTeam",
    instagram = "https://www.instagram.com/hdecvolleyballteam/",
    facebook = "https://www.facebook.com/hdecvolleyball/?locale=ko_KR",
    homepage = "https://hillstate.hdec.kr/",
    players = mutableListOf()
)

public val womenVolleyball_IncheonHeungkukinsurancesPinkspiders = Team(
    sport = "여자배구",
    name = "인천 흥국생명 핑크스파이더스",
    shortname = "흥국생명",
    teamcolor = "#e5007f",
    home = "인천",
    fans = 0,
    youtube = "https://www.youtube.com/@hkpinkspiders",
    instagram = "https://www.instagram.com/hkpinkspiders/",
    facebook = "https://www.facebook.com/story.php/?story_fbid=1592095604182838&id=178598262199253",
    homepage = "https://www.pinkspiders.co.kr/",
    players = mutableListOf()
)
public val womenVolleyball_DaejeonKgcRedsparks = Team(
    sport = "여자배구",
    name = "대전 정관장 레드스파크스",
    shortname = "정관장",
    teamcolor = "#d70029",
    home = "대전",
    fans = 0,
    youtube = "https://www.youtube.com/@Red_Sparks",
    instagram = "https://www.instagram.com/red__sparks/",
    facebook = "https://www.facebook.com/jkjredsparks/?locale=ko_KR",
    homepage = "https://www.kgcsports.com/volleyball/main/main.php",
    players = mutableListOf()
)
public val womenVolleyball_GscaltexSeoulKixx = Team(
    sport = "여자배구",
    name = "GS칼텍스 서울 KIXX",
    shortname = "GS칼텍스",
    teamcolor = "#00718f",
    home = "서울",
    fans = 0,
    youtube = "https://www.youtube.com/@gscaltexkixx?app=desktop",
    instagram = "https://www.instagram.com/gscaltexkixx/",
    facebook = "https://www.facebook.com/gsvolleyball/",
    homepage = "https://www.gsvolleyball.com/",
    players = mutableListOf()
)
public val womenVolleyball_HwaseongIbkAltos = Team(
    sport = "여자배구",
    name = "화성 IBK기업은행 알토스",
    shortname = "IBK기업은행",
    teamcolor = "#004b9c",
    home = "http://sports.ibk.co.kr/m/volleyball/main/",
    fans = 0,
    youtube = "https://www.youtube.com/channel/UCfkPAZMoJYLrBfy8dftzCvg/featured",
    instagram = "https://www.instagram.com/ibk__altos/",
    facebook = "https://www.facebook.com/ibkaltos/?locale=ko_KR",
    homepage = "http://sports.ibk.co.kr/m/volleyball/main/",
    players = mutableListOf()
)
public val womenVolleyball_GimcheonExHipass = Team(
    sport = "여자배구",
    name = "김천 한국도로공사 하이패스",
    shortname = "한국도로공사",
    teamcolor = "#102a85",
    home = "김천",
    fans = 0,
    youtube = "https://www.youtube.com/@exvolleyball",
    instagram = "https://www.instagram.com/hipassvolleyclub/?hl=ko",
    facebook = "https://www.facebook.com/hipassvolleyclub/?locale=ko_KR",
    homepage = "https://www.exsportsclub.com/",
    players = mutableListOf()
)
public val womenVolleyball_GwangjuPepperbanAipeppers = Team(
    sport = "여자배구",
    name = "광주 페퍼저축은행 AI 페퍼스",
    shortname = "페퍼저축은행",
    teamcolor = "#df0124",
    home = "광주",
    fans = 0,
    youtube = "https://www.youtube.com/@aipeppers",
    instagram = "https://www.instagram.com/aipeppers/",
    facebook = "",
    homepage = "http://www.aipeppers.kr/",
    players = mutableListOf()
)

public val womenBasketballTeamNames = listOf(
    "용인 삼성생명 블루밍스",
    "인천 신한은행 에스버드",
    "아산 우리은행 우리WON",
    "부천 하나은행",
    "부산 BNK 썸",
    "청주 KB 스타즈",
    "직접 입력"
)

public val womenBasketballTeams: List<Team>
    get() = listOf(
        womenBasketball_YonginSamsungLifeInsuranceBlueminx,
        womenBasketball_IncheonShinhanBankSBird,
        womenBasketball_AsanWooribankWooriWon,
        womenBasketball_BucheonHanaBank,
        womenBasketball_BusanBNKSum,
        womenBasketball_ChungjuKbStars
    )

public val womenBasketball_YonginSamsungLifeInsuranceBlueminx = Team(
    sport = "여자농구",
    name = "용인 삼성생명 블루밍스",
    shortname = "삼성생명",
    teamcolor = "#074ca1",
    home = "용인",
    fans = 0,
    youtube = "https://www.youtube.com/channel/UCrz0wWdmp-ZhRIVcbuJxETA",
    instagram = "https://www.instagram.com/goblueminx/",
    facebook = "https://www.facebook.com/goblueminx/?locale=ko_KR",
    homepage = "http://www.samsungblueminx.com/",
    players = mutableListOf()
)

public val womenBasketball_IncheonShinhanBankSBird = Team(
    sport = "여자농구",
    name = "인천 신한은행 에스버드",
    shortname = "신한은행",
    teamcolor = "#112369",
    home = "인천",
    fans = 0,
    youtube = "",
    instagram = "",
    facebook = "",
    homepage = "",
    players = mutableListOf()
)

public val womenBasketball_AsanWooribankWooriWon = Team(
    sport = "여자농구",
    name = "아산 우리은행 우리WON",
    shortname = "우리은행",
    teamcolor = "#0067ac",
    home = "아산",
    fans = 0,
    youtube = "",
    instagram = "",
    facebook = "",
    homepage = "",
    players = mutableListOf()
)
public val womenBasketball_BucheonHanaBank = Team(
    sport = "여자농구",
    name = "부천 하나은행",
    shortname = "하나은행",
    teamcolor = "#008f73",
    home = "부천",
    fans = 0,
    youtube = "",
    instagram = "",
    facebook = "",
    homepage = "",
    players = mutableListOf()
)
public val womenBasketball_BusanBNKSum = Team(
    sport = "여자농구",
    name = "부산 BNK 썸",
    shortname = "BNK 썸",
    teamcolor = "#d62328",
    home = "부산",
    fans = 0,
    youtube = "",
    instagram = "",
    facebook = "",
    homepage = "",
    players = mutableListOf()
)
public val womenBasketball_ChungjuKbStars = Team(
    sport = "여자농구",
    name = "청주 KB 스타즈",
    shortname = "KB 스타즈",
    teamcolor = "#ffbc00",
    home = "청주",
    fans = 0,
    youtube = "",
    instagram = "",
    facebook = "",
    homepage = "",
    players = mutableListOf()
)
