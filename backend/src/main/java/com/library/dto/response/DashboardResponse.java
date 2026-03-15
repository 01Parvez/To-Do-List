package com.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DashboardResponse DTO - statistics for admin and user dashboards.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    // ── Admin stats ──
    /** Total books in the catalog */
    private long totalBooks;

    /** Total registered members */
    private long totalMembers;

    /** Books currently borrowed */
    private long totalBorrowed;

    /** Books past due date */
    private long totalOverdue;

    /** Total registered user accounts */
    private long totalUsers;

    // ── User-specific stats ──
    /** Number of books currently borrowed by the logged-in user */
    private long myActiveBorrows;

    /** Number of overdue books for the logged-in user */
    private long myOverdueBorrows;

    /** Total fine amount owed by the logged-in user */
    private double myTotalFine;
}
