package com.municipality.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberStatistics {
    private Integer memberId;
    private String memberName;
    private String username;
    private String email;
    private Long totalLoans;
    private Long activeLoans;
    private Long overdueLoans;
    private LocalDate joinDate;
    private LocalDateTime lastActivityDate;
    
    // Constructor for most active members
    public MemberStatistics(Integer memberId, String memberName, String username, Long totalLoans) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.username = username;
        this.totalLoans = totalLoans;
        this.activeLoans = 0L;
        this.overdueLoans = 0L;
    }
}