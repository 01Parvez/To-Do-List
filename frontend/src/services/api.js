import axios from 'axios'

/**
 * Axios instance configured for the Library API.
 *
 * Base URL: /api (proxied to http://localhost:8080 by Vite)
 *
 * Interceptors:
 * 1. Request interceptor: automatically adds JWT Bearer token to every request
 * 2. Response interceptor: handles 401 (redirect to login) and generic errors
 */
const api = axios.create({
    baseURL: '/api',
    headers: { 'Content-Type': 'application/json' },
    timeout: 10000, // 10 seconds
})

// ── Request Interceptor ──
// Automatically attach JWT token from localStorage to every request.
// The backend's JwtAuthenticationFilter reads this "Authorization" header.
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('libraryToken')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    (error) => Promise.reject(error)
)

// ── Response Interceptor ──
// Handle global errors:
// - 401: token expired or invalid → redirect to login
// - Others: extract readable error message
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            // Token expired - clear auth and redirect
            localStorage.removeItem('libraryToken')
            localStorage.removeItem('libraryUser')
            window.location.href = '/login'
        }
        return Promise.reject(error)
    }
)

export default api
