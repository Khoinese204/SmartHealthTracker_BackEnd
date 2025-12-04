
package com.example.smarthealth.dto.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminSummaryResponse {
    private long totalUsers;
    private long activeUsers;
    private long totalPosts;
    private long totalGroups;
}
