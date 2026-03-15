import React, { useState, useEffect } from 'react'
import { borrowService } from '../services/apiService'
import { useAuth } from '../context/AuthContext'
import Sidebar from '../components/Sidebar'
import toast from 'react-hot-toast'

/**
 * BorrowHistoryPage - shows borrow history with return action.
 * Admin: can see all borrows, return on behalf.
 * User: sees their own history.
 */
export default function BorrowHistoryPage() {
    const { user, isAdmin } = useAuth()
    const [borrows, setBorrows] = useState([])
    const [overdue, setOverdue] = useState([])
    const [page, setPage] = useState({ current: 0, total: 0, size: 10 })
    const [loading, setLoading] = useState(true)
    const [returning, setReturning] = useState(null)

    const fetchBorrows = async (p = 0) => {
        setLoading(true)
        try {
            if (isAdmin) {
                const [data, ov] = await Promise.all([
                    borrowService.getUserHistory(user.userId, p, page.size),
                    borrowService.getOverdue()
                ])
                setBorrows(data.content)
                setOverdue(ov)
                setPage(prev => ({ ...prev, current: data.number, total: data.totalPages }))
            } else {
                const data = await borrowService.getUserHistory(user.userId, p, page.size)
                setBorrows(data.content)
                setPage(prev => ({ ...prev, current: data.number, total: data.totalPages }))
            }
        } catch { toast.error('Failed to load borrows') }
        finally { setLoading(false) }
    }

    useEffect(() => { fetchBorrows() }, [])

    const handleReturn = async (borrowId) => {
        setReturning(borrowId)
        try {
            const result = await borrowService.returnBook(borrowId)
            toast.success(`Returned! ${result.fineAmount > 0 ? `Fine: ₹${result.fineAmount}` : 'No fine.'}`)
            fetchBorrows(page.current)
        } catch (e) { toast.error(e.response?.data?.message || 'Return failed') }
        finally { setReturning(null) }
    }

    const statusBadge = (b) => {
        if (b.status === 'RETURNED') return <span className="badge badge-success">Returned</span>
        if (b.overdue || b.status === 'OVERDUE') return <span className="badge badge-danger">Overdue</span>
        return <span className="badge badge-primary">Active</span>
    }

    return (
        <div className="app-layout">
            <Sidebar />
            <main className="main-content fade-in">
                <div className="page-header">
                    <div>
                        <h1 className="page-title">{isAdmin ? 'All Borrows' : 'My Borrow History'}</h1>
                        <p className="page-sub">{borrows.length} records</p>
                    </div>
                </div>

                {/* Overdue alert (admin only) */}
                {isAdmin && overdue.length > 0 && (
                    <div style={{ background: 'rgba(239,68,68,0.1)', border: '1px solid rgba(239,68,68,0.3)', borderRadius: 'var(--radius)', padding: '1rem 1.5rem', marginBottom: '1.5rem', color: '#fca5a5' }}>
                        ⚠️ <strong>{overdue.length} overdue books</strong> need attention
                    </div>
                )}

                {loading ? <div className="spinner" /> : borrows.length === 0 ? (
                    <div className="empty-state"><h3>No borrow records</h3><p>{isAdmin ? 'No borrows yet.' : "You haven't borrowed any books yet."}</p></div>
                ) : (
                    <>
                        <div className="table-container">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Book</th>
                                        {isAdmin && <th>User</th>}
                                        <th>Borrowed</th>
                                        <th>Due Date</th>
                                        <th>Returned</th>
                                        <th>Status</th>
                                        <th>Fine</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {borrows.map(b => (
                                        <tr key={b.id} style={{ background: b.overdue && !b.returnDate ? 'rgba(239,68,68,0.04)' : 'transparent' }}>
                                            <td><strong>{b.bookTitle}</strong><br /><small style={{ color: 'var(--text-muted)' }}>{b.bookAuthor}</small></td>
                                            {isAdmin && <td>{b.username}</td>}
                                            <td>{b.borrowDate}</td>
                                            <td style={{ color: b.overdue && !b.returnDate ? 'var(--danger)' : 'inherit' }}>{b.dueDate}</td>
                                            <td>{b.returnDate || '—'}</td>
                                            <td>{statusBadge(b)}</td>
                                            <td style={{ color: b.fineAmount > 0 ? 'var(--warning)' : 'var(--text-sec)' }}>
                                                ₹{b.fineAmount || '0.00'}
                                            </td>
                                            <td>
                                                {b.status !== 'RETURNED' && (
                                                    <button className="btn btn-success btn-sm" onClick={() => handleReturn(b.id)} disabled={returning === b.id}>
                                                        {returning === b.id ? '...' : '📥 Return'}
                                                    </button>
                                                )}
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                        {page.total > 1 && (
                            <div className="pagination">
                                <button className="pagination-btn" disabled={page.current === 0} onClick={() => fetchBorrows(page.current - 1)}>‹ Prev</button>
                                <span style={{ color: 'var(--text-sec)', fontSize: '0.85rem' }}>Page {page.current + 1} of {page.total}</span>
                                <button className="pagination-btn" disabled={page.current >= page.total - 1} onClick={() => fetchBorrows(page.current + 1)}>Next ›</button>
                            </div>
                        )}
                    </>
                )}
            </main>
        </div>
    )
}
