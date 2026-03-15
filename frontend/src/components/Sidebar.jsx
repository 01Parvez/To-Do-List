import React from 'react'
import { NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import toast from 'react-hot-toast'

/**
 * Sidebar - navigation for authenticated users.
 * Shows different links based on role (admin vs user).
 */
export default function Sidebar() {
    const { user, isAdmin, logout } = useAuth()
    const navigate = useNavigate()

    const handleLogout = () => {
        logout()
        toast.success('Logged out successfully')
        navigate('/login')
    }

    const linkStyle = ({ isActive }) => ({
        display: 'flex',
        alignItems: 'center',
        gap: '0.75rem',
        padding: '0.65rem 1rem',
        borderRadius: '8px',
        fontSize: '0.9rem',
        fontWeight: 500,
        color: isActive ? '#fff' : 'var(--text-sec)',
        background: isActive ? 'var(--primary)' : 'transparent',
        boxShadow: isActive ? 'var(--glow)' : 'none',
        transition: 'all 0.2s',
        textDecoration: 'none',
        marginBottom: '2px',
    })

    return (
        <aside style={{
            width: 260,
            height: '100vh',
            background: 'var(--bg-secondary)',
            borderRight: '1px solid var(--border)',
            display: 'flex',
            flexDirection: 'column',
            padding: '1.5rem 1rem',
            position: 'fixed',
            top: 0, left: 0,
            zIndex: 100,
            overflow: 'auto',
        }}>
            {/* Logo */}
            <div style={{ marginBottom: '2rem', padding: '0 0.5rem' }}>
                <h2 style={{ fontSize: '1.3rem', fontWeight: 800, background: 'linear-gradient(135deg, var(--primary), var(--accent))', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
                    📚 LibraryMS
                </h2>
                <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.2rem' }}>
                    {isAdmin ? '👑 Administrator' : '👤 Member'}
                </p>
            </div>

            {/* Navigation Links */}
            <nav style={{ flex: 1 }}>
                {isAdmin ? (
                    <>
                        <p style={{ fontSize: '0.7rem', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.08em', marginBottom: '0.5rem', padding: '0 0.5rem' }}>Admin</p>
                        <NavLink to="/admin/dashboard" style={linkStyle}>🏠 Dashboard</NavLink>
                        <NavLink to="/admin/books" style={linkStyle}>📖 Manage Books</NavLink>
                        <NavLink to="/admin/members" style={linkStyle}>👥 Members</NavLink>
                        <NavLink to="/admin/borrows" style={linkStyle}>📋 All Borrows</NavLink>
                    </>
                ) : (
                    <>
                        <p style={{ fontSize: '0.7rem', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.08em', marginBottom: '0.5rem', padding: '0 0.5rem' }}>Menu</p>
                        <NavLink to="/dashboard" style={linkStyle}>🏠 Dashboard</NavLink>
                        <NavLink to="/books" style={linkStyle}>📖 Browse Books</NavLink>
                        <NavLink to="/my-borrows" style={linkStyle}>📋 My Borrows</NavLink>
                    </>
                )}
            </nav>

            {/* User Info + Logout */}
            <div style={{ borderTop: '1px solid var(--border)', paddingTop: '1rem', marginTop: '1rem' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', padding: '0.5rem', marginBottom: '0.75rem' }}>
                    <div style={{ width: 36, height: 36, borderRadius: '50%', background: 'var(--primary)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 700, fontSize: '0.95rem', flexShrink: 0 }}>
                        {user?.username?.[0]?.toUpperCase()}
                    </div>
                    <div style={{ overflow: 'hidden' }}>
                        <div style={{ fontWeight: 600, fontSize: '0.875rem', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{user?.username}</div>
                        <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{user?.email}</div>
                    </div>
                </div>
                <button onClick={handleLogout} className="btn btn-outline" style={{ width: '100%', justifyContent: 'center' }}>
                    🚪 Logout
                </button>
            </div>
        </aside>
    )
}
