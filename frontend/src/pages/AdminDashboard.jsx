import React, { useEffect, useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { dashboardService } from '../services/apiService'
import Sidebar from '../components/Sidebar'

/**
 * AdminDashboard - shows system-wide statistics for admin users.
 */
export default function AdminDashboard() {
    const { user } = useAuth()
    const [stats, setStats] = useState(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        dashboardService.getAdminStats()
            .then(setStats)
            .catch(console.error)
            .finally(() => setLoading(false))
    }, [])

    const statCards = stats ? [
        { label: 'Total Books', value: stats.totalBooks, icon: '📚', color: '#6366f1' },
        { label: 'Total Members', value: stats.totalMembers, icon: '👥', color: '#22d3ee' },
        { label: 'Borrowed Now', value: stats.totalBorrowed, icon: '📤', color: '#f59e0b' },
        { label: 'Overdue Books', value: stats.totalOverdue, icon: '⚠️', color: '#ef4444' },
        { label: 'Total Users', value: stats.totalUsers, icon: '👤', color: '#22c55e' },
    ] : []

    return (
        <div className="app-layout">
            <Sidebar />
            <main className="main-content fade-in">
                <div className="page-header">
                    <div>
                        <h1 className="page-title">Admin Dashboard</h1>
                        <p className="page-sub">Welcome back, <strong>{user?.username}</strong> 👋</p>
                    </div>
                </div>

                {loading ? (
                    <div className="spinner" />
                ) : (
                    <div className="stat-grid">
                        {statCards.map(card => (
                            <div className="stat-card" key={card.label} style={{ '--accent-color': card.color }}>
                                <span className="stat-label">{card.label}</span>
                                <span className="stat-value">{card.value}</span>
                                <span className="stat-icon">{card.icon}</span>
                            </div>
                        ))}
                    </div>
                )}

                {/* Quick Actions */}
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '1.5rem', marginTop: '1rem' }}>
                    <div className="card">
                        <h3 style={{ marginBottom: '1rem', fontSize: '1rem', fontWeight: 600 }}>⚡ Quick Actions</h3>
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.6rem' }}>
                            <a href="/admin/books" className="btn btn-primary">📖 Add New Book</a>
                            <a href="/admin/members" className="btn btn-outline">👥 Add Member</a>
                            <a href="/admin/borrows" className="btn btn-outline">📋 View All Borrows</a>
                        </div>
                    </div>
                    <div className="card">
                        <h3 style={{ marginBottom: '1rem', fontSize: '1rem', fontWeight: 600 }}>📊 System Summary</h3>
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.6rem', fontSize: '0.9rem', color: 'var(--text-sec)' }}>
                            <p>📌 Fine Rate: <strong style={{ color: 'var(--text-primary)' }}>₹10 per day</strong></p>
                            <p>📌 Borrow Duration: <strong style={{ color: 'var(--text-primary)' }}>14 days</strong></p>
                            <p>📌 API Docs: <a href="http://localhost:8080/swagger-ui.html" target="_blank" style={{ color: 'var(--primary)' }}>Swagger UI</a></p>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    )
}
