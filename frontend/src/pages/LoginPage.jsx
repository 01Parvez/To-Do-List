import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { authService } from '../services/apiService'
import toast from 'react-hot-toast'

/**
 * LoginPage - handles user authentication.
 * On success: stores JWT in AuthContext and redirects to dashboard.
 */
export default function LoginPage() {
    const [form, setForm] = useState({ username: '', password: '' })
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)
    const { login } = useAuth()
    const navigate = useNavigate()

    const handleChange = (e) => {
        setForm(prev => ({ ...prev, [e.target.name]: e.target.value }))
        setError('')
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        setLoading(true)
        setError('')
        try {
            const data = await authService.login(form)
            login(data) // store in AuthContext + localStorage
            toast.success(`Welcome back, ${data.username}!`)
            // Redirect based on role
            navigate(data.role === 'ROLE_ADMIN' ? '/admin/dashboard' : '/dashboard')
        } catch (err) {
            setError(err.response?.data?.message || 'Invalid username or password')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="auth-page">
            <div className="auth-card fade-in">
                <div className="auth-logo">
                    <h1>📚 LibraryMS</h1>
                    <p>Library Management System</p>
                </div>

                <h2 style={{ fontSize: '1.2rem', fontWeight: 700, marginBottom: '1.5rem' }}>
                    Sign in to your account
                </h2>

                {error && <div className="error-msg">{error}</div>}

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="username">Username</label>
                        <input id="username" name="username" className="form-control"
                            placeholder="Enter your username"
                            value={form.username} onChange={handleChange} required autoFocus />
                    </div>
                    <div className="form-group">
                        <label htmlFor="password">Password</label>
                        <input id="password" name="password" type="password" className="form-control"
                            placeholder="Enter your password"
                            value={form.password} onChange={handleChange} required />
                    </div>

                    <button type="submit" className="btn btn-primary btn-lg" style={{ width: '100%', marginTop: '0.5rem' }} disabled={loading}>
                        {loading ? '⏳ Signing in...' : '🔐 Sign In'}
                    </button>
                </form>

                <div className="auth-footer">
                    <p>Don't have an account? <Link to="/register">Register here</Link></p>
                    <p style={{ marginTop: '0.8rem', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                        Demo: <strong>admin</strong> / admin123 &nbsp;|&nbsp; <strong>user</strong> / user123
                    </p>
                </div>
            </div>
        </div>
    )
}
