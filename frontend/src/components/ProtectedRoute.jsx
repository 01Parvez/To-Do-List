import React from 'react'
import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

/**
 * ProtectedRoute - wraps routes that require authentication (or admin role).
 *
 * Usage:
 *  <ProtectedRoute>
 *    <SomePage />
 *  </ProtectedRoute>
 *
 *  <ProtectedRoute requireAdmin>
 *    <AdminOnlyPage />
 *  </ProtectedRoute>
 *
 * If not authenticated → redirect to /login
 * If not admin (when requireAdmin) → redirect to /dashboard
 */
export default function ProtectedRoute({ children, requireAdmin = false }) {
    const { isAuthenticated, isAdmin } = useAuth()

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />
    }

    if (requireAdmin && !isAdmin) {
        return <Navigate to="/dashboard" replace />
    }

    return children
}
