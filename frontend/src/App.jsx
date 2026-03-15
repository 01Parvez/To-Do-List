import React from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import ProtectedRoute from './components/ProtectedRoute'

// Pages
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import AdminDashboard from './pages/AdminDashboard'
import UserDashboard from './pages/UserDashboard'
import BooksPage from './pages/BooksPage'
import MembersPage from './pages/MembersPage'
import BorrowHistoryPage from './pages/BorrowHistoryPage'

/**
 * App.jsx - Root component.
 * Sets up React Router with protected routes.
 *
 * Route Structure:
 *  /                     → redirect to /login
 *  /login                → Public: Login page
 *  /register             → Public: Register page
 *  /dashboard            → Auth: User dashboard
 *  /books                → Auth: Book catalog (user borrow view)
 *  /my-borrows           → Auth: User borrow history
 *  /admin/dashboard      → Admin only
 *  /admin/books          → Admin only: Book management
 *  /admin/members        → Admin only
 *  /admin/borrows        → Admin only
 */
export default function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <Routes>
                    {/* Redirect root to login */}
                    <Route path="/" element={<Navigate to="/login" replace />} />

                    {/* Public routes */}
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<RegisterPage />} />

                    {/* User routes */}
                    <Route path="/dashboard" element={
                        <ProtectedRoute><UserDashboard /></ProtectedRoute>
                    } />
                    <Route path="/books" element={
                        <ProtectedRoute><BooksPage /></ProtectedRoute>
                    } />
                    <Route path="/my-borrows" element={
                        <ProtectedRoute><BorrowHistoryPage /></ProtectedRoute>
                    } />

                    {/* Admin routes */}
                    <Route path="/admin/dashboard" element={
                        <ProtectedRoute requireAdmin><AdminDashboard /></ProtectedRoute>
                    } />
                    <Route path="/admin/books" element={
                        <ProtectedRoute requireAdmin><BooksPage /></ProtectedRoute>
                    } />
                    <Route path="/admin/members" element={
                        <ProtectedRoute requireAdmin><MembersPage /></ProtectedRoute>
                    } />
                    <Route path="/admin/borrows" element={
                        <ProtectedRoute requireAdmin><BorrowHistoryPage /></ProtectedRoute>
                    } />

                    {/* 404 */}
                    <Route path="*" element={
                        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '100vh', flexDirection: 'column', gap: '1rem', color: 'var(--text-sec)' }}>
                            <h1 style={{ fontSize: '4rem', color: 'var(--primary)' }}>404</h1>
                            <p>Page not found</p>
                            <a href="/login" className="btn btn-primary">Go Home</a>
                        </div>
                    } />
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    )
}
