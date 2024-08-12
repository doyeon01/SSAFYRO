package com.ssafy.ssafyro.domain.report;

import com.ssafy.ssafyro.api.service.report.dto.ReportScoreAverageDto;
import com.ssafy.ssafyro.domain.room.RoomType;
import com.ssafy.ssafyro.domain.user.User;
import java.util.List;

public interface ReportQueryRepository {

    List<Report> findReports();

    int countReportsType(RoomType type, User user);

    ReportScoreAverageDto findTotalAvgBy(RoomType type, User user);
}
