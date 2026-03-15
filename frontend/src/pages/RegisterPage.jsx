import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { authService } from '../services/apiService'
import toast from 'react-hot-toast'

/**
 * RegisterPage - new user registration.
 * Auto-logs in after successful registration.
 */
export default function RegisterPage() {
    const [form, setForm] = useState({ username: '', email: '', password: '', confirmPassword: '' })
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
        if (form.password !== form.confirmPassword) {
            return setError('Passwords do not match')
        }
        setLoading(true)
        try {
            const data = await authService.register({
                username: form.username,
                email: form.email,
                password: form.password
            })
            login(data)
            toast.success('Account created! Welcome!')
            navigate('/dashboard')
        } catch (err) {
            const msg = err.response?.data?.message || err.response?.data?.errors
            setError(typeof msg === 'object' ? Object.values(msg).join(', ') : (msg || 'Registration failed'))
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="auth-page">
            <div className="auth-card fade-in">
                <div className="auth-logo">
                    <h1>📚 LibraryMS</h1>
                    <p>Create your account</p>
                </div>

                {error && <div className="error-msg">{error}</div>}

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="username">Username</label>
                        <input id="username" name="username" className="form-control"
                            placeholder="Choose a username" value={form.username}
                            onChange={handleChange} required minLength={3} />
                    </div>
                    <div className="form-group">
                        <label htmlFor="email">Email</label>
                        <input id="email" name="email" type="email" className="form-control"
                            placeholder="your@email.com" value={form.email}
                            onChange={handleChange} required />
                    </div>
                    <div className="form-group">
                        <label htmlFor="password">Password</label>
                        <input id="password" name="password" type="password" className="form-control"
                            placeholder="Min 6 characters" value={form.password}
                            onChange={handleChange} required minLength={6} />
                    </div>
                    <div className="form-group">
                        <label htmlFor="confirmPassword">Confirm Password</label>
                        <input id="confirmPassword" name="confirmPassword" type="password" className="form-control"
                            placeholder="Repeat password" value={form.confirmPassword}
                            onChange={handleChange} required />
                    </div>

                    <button type="submit" className="btn btn-primary btn-lg" style={{ width: '100%' }} disabled={loading}>
                        {loading ? '⏳ Creating account...' : '✅ Create Account'}
                    </button>
                </form>

                <div className="auth-footer">
                    Already have an account? <Link to="/login">Sign in</Link>
                </div>
            </div>
        </div>
    )
}
