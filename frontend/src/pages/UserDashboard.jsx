import React, { useEffect, useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { dashboardService, borrowService } from '../services/apiService'
import Sidebar from '../components/Sidebar'

/**
 * UserDashboard - personal stats for logged-in users.
 */
export default function UserDashboard() {
    const { user } = useAuth()
    const [stats, setStats] = useState(null)
    const [borrows, setBorrows] = useState([])
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        Promise.all([
            dashboardService.getUserStats(),
            borrowService.getMyActiveBorrows()
        ]).then(([s, b]) => {
            setStats(s)
            setBorrows(b)
        }).catch(console.error)
            .finally(() => setLoading(false))
    }, [])

    const statCards = stats ? [
        { label: 'Active Borrows', value: stats.myActiveBorrows, icon: '📤', color: '#6366f1' },
        { label: 'Overdue Books', value: stats.myOverdueBorrows, icon: '⚠️', color: '#ef4444' },
        { label: 'Total Fine Owed', value: `₹${stats.myTotalFine?.toFixed(2) || '0.00'}`, icon: '💰', color: '#f59e0b' },
    ] : []

    return (
        <div className="app-layout">
            <Sidebar />
            <main className="main-content fade-in">
                <div className="page-header">
                    <div>
                        <h1 className="page-title">My Dashboard</h1>
                        <p className="page-sub">Hello, <strong>{user?.username}</strong> 👋</p>
                    </div>
                    <a href="/books" className="btn btn-primary">📖 Browse Books</a>
                </div>

                {loading ? <div className="spinner" /> : (
                    <>
                        <div className="stat-grid">
                            {statCards.map(card => (
                                <div className="stat-card" key={card.label} style={{ '--accent-color': card.color }}>
                                    <span className="stat-label">{card.label}</span>
                                    <span className="stat-value">{card.value}</span>
                                    <span className="stat-icon">{card.icon}</span>
                                </div>
                            ))}
                        </div>

                        {/* Currently borrowed books */}
                        <div className="card" style={{ marginTop: '1.5rem' }}>
                            <h3 style={{ marginBottom: '1.2rem', fontWeight: 700 }}>📚 Currently Borrowed</h3>
                            {borrows.length === 0 ? (
                                <div className="empty-state">
                                    <h3>No active borrows</h3>
                                    <p>Go browse the book catalog and borrow something!</p>
                                    <a href="/books" className="btn btn-primary" style={{ marginTop: '1rem', display: 'inline-flex' }}>Browse Books</a>
                                </div>
                            ) : (
                                <div className="table-container">
                                    <table>
                                        <thead>
                                            <tr><th>Book</th><th>Borrow Date</th><th>Due Date</th><th>Status</th><th>Fine</th></tr>
                                        </thead>
                                        <tbody>
                                            {borrows.map(b => (
                                                <tr key={b.id}>
                                                    <td><strong>{b.bookTitle}</strong><br /><small style={{ color: 'var(--text-muted)' }}>{b.bookAuthor}</small></td>
                                                    <td>{b.borrowDate}</td>
                                                    <td style={{ color: b.overdue ? 'var(--danger)' : 'inherit' }}>{b.dueDate}</td>
                                                    <td>
                                                        <span className={`badge ${b.overdue ? 'badge-danger' : 'badge-primary'}`}>
                                                            {b.overdue ? `Overdue (${b.daysOverdue}d)` : 'Active'}
                                                        </span>
                                                    </td>
                                                    <td>₹{b.fineAmount || '0.00'}</td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            )}
                        </div>
                    </>
                )}
            </main>
        </div>
    )
}
