import React, { createContext, useContext, useState, useEffect } from 'react'

/**
 * AuthContext - manages authentication state across the app.
 *
 * Provides:
 *  - user: current user object { id, username, email, role, token }
 *  - login(userData): store user and JWT in localStorage
 *  - logout(): clear auth state
 *  - isAdmin: true if user has ROLE_ADMIN
 *  - isAuthenticated: true if logged in
 *
 * localStorage is used for JWT persistence across page refreshes.
 */

const AuthContext = createContext(null)

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(() => {
        // Restore user from localStorage on page refresh
        try {
            const saved = localStorage.getItem('libraryUser')
            return saved ? JSON.parse(saved) : null
        } catch {
            return null
        }
    })

    const [loading, setLoading] = useState(false)

    /**
     * Store user after successful login.
     * @param {Object} userData - { token, userId, username, email, role }
     */
    const login = (userData) => {
        setUser(userData)
        localStorage.setItem('libraryUser', JSON.stringify(userData))
        // Store JWT token separately for easy access in api.js
        localStorage.setItem('libraryToken', userData.token)
    }

    /** Clear all auth state */
    const logout = () => {
        setUser(null)
        localStorage.removeItem('libraryUser')
        localStorage.removeItem('libraryToken')
    }

    const isAuthenticated = !!user
    const isAdmin = user?.role === 'ROLE_ADMIN'

    return (
        <AuthContext.Provider value={{
            user,
            login,
            logout,
            isAuthenticated,
            isAdmin,
            loading,
            setLoading
        }}>
            {children}
        </AuthContext.Provider>
    )
}

/** Custom hook to use auth context */
export const useAuth = () => {
    const ctx = useContext(AuthContext)
    if (!ctx) throw new Error('useAuth must be used within AuthProvider')
    return ctx
}
