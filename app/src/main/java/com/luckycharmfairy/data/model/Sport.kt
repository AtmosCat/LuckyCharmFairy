package com.luckycharmfairy.data.model

data class Sport(
    var name: String = "",
    var teamNames: List<String>,
    val teams: List<Team>,
)

public val sports: List<Sport>
    get() = listOf(
        BaseBall, MenFootBall, MenBasketBall, MenVolleyBall, WomenBasketBall, WomenVolleyBall
    )

public val BaseBall = Sport(
    "야구",
    baseballTeamNames,
    baseballTeams
)

public val MenFootBall = Sport(
    "남자축구",
    menFootballTeamNames,
    menFootballTeams
)

public val MenBasketBall = Sport(
    "남자농구",
    menBasketballTeamNames,
    menBasketballTeams
)

public val MenVolleyBall = Sport(
    "남자배구",
    menVolleyballTeamNames,
    menVolleyballTeams
)

public val WomenBasketBall = Sport(
    "여자농구",
    womenBasketballTeamNames,
    womenBasketballTeams
)

public val WomenVolleyBall = Sport(
    "여자배구",
    womenVolleyballTeamNames,
    womenVolleyballTeams
)