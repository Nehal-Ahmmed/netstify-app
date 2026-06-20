package com.nhbhuiyan.nestify.projectplans.domain.model

data class ProjectPlanModel(
    var Id: Int = 0,
    var ImagePath: Int,
    var Title: String = "",
    var Description: String = "",
    var Ideas: Int = 0,
    var Completed: Int = 0,
    var WorkingWith: String = ""
)
