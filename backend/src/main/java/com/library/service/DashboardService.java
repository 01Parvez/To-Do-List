package com.library.service;

import com.library.dto.response.BorrowResponse;
import com.library.dto.response.DashboardResponse;
import com.library.entity.Borrow;
import com.library.entity.User;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DashboardService - aggregates statistics for admin and user dashboards.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BorrowRepository borrowRepository;
    private final UserRepository userRepository;
    private final BorrowService borrowService;

    /**
     * Admin dashboard stats:
     * - Total books, members, users
     * - Currently borrowed, overdue counts
     */
    public DashboardResponse getAdminDashboard() {
        LocalDate today = LocalDate.now();

        return DashboardResponse.builder()
                .totalBooks(bookRepository.count())
                .totalMembers(memberRepository.count())
                .totalUsers(userRepository.count())
                .totalBorrowed(borrowRepository.countByStatus(Borrow.BorrowStatus.BORROWED))
                .totalOverdue(borrowRepository.countOverdue(today))
                .build();
    }

    /**
     * User dashboard stats:
     * - Personal active borrows
     * - Personal overdue borrows
     * - Total fine amount owed
     */
    public DashboardResponse getUserDashboard(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        LocalDate today = LocalDate.now();

        // Get all active borrows for this user
        List<Borrow> activeBorrows = borrowRepository
                .findByUserIdAndStatus(user.getId(), Borrow.BorrowStatus.BORROWED);

        // Count how many are overdue
        long overdueCount = activeBorrows.stream()
                .filter(b -> b.getDueDate().isBefore(today))
                .count();

        // Sum all fine amounts (including unpaid fines from returned books)
        List<BorrowResponse> history = borrowRepository
                .findByUserId(user.getId(), org.springframework.data.domain.Pageable.unpaged())
                .stream()
                .map(borrowService::mapToResponse)
                .toList();

        double totalFine = history.stream()
                .mapToDouble(b -> b.getFineAmount().doubleValue())
                .sum();

        return DashboardResponse.builder()
                .myActiveBorrows(activeBorrows.size())
                .myOverdueBorrows(overdueCount)
                .myTotalFine(totalFine)
                .build();
    }
}
