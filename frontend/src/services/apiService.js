import api from './api'

/** Auth API calls */
export const authService = {
    /** POST /api/auth/register → { token, userId, username, email, role } */
    register: (data) => api.post('/auth/register', data).then(r => r.data),
    /** POST /api/auth/login → { token, userId, username, email, role } */
    login: (data) => api.post('/auth/login', data).then(r => r.data),
}

/** Book API calls */
export const bookService = {
    getAll: (page = 0, size = 10, sortBy = 'title') =>
        api.get(`/books?page=${page}&size=${size}&sortBy=${sortBy}`).then(r => r.data),
    search: (keyword, page = 0, size = 10) =>
        api.get(`/books/search?keyword=${keyword}&page=${page}&size=${size}`).then(r => r.data),
    getById: (id) => api.get(`/books/${id}`).then(r => r.data),
    create: (data) => api.post('/books', data).then(r => r.data),
    update: (id, data) => api.put(`/books/${id}`, data).then(r => r.data),
    delete: (id) => api.delete(`/books/${id}`),
}

/** Member API calls */
export const memberService = {
    getAll: (page = 0, size = 10) =>
        api.get(`/members?page=${page}&size=${size}`).then(r => r.data),
    getById: (id) => api.get(`/members/${id}`).then(r => r.data),
    create: (data) => api.post('/members', data).then(r => r.data),
    update: (id, data) => api.put(`/members/${id}`, data).then(r => r.data),
    delete: (id) => api.delete(`/members/${id}`),
    toggleStatus: (id) => api.patch(`/members/${id}/toggle-status`).then(r => r.data),
}

/** Borrow API calls */
export const borrowService = {
    borrowBook: (bookId) => api.post('/borrow', { bookId }).then(r => r.data),
    returnBook: (borrowId) => api.post(`/borrow/return/${borrowId}`).then(r => r.data),
    getMyActiveBorrows: () => api.get('/borrow/my-borrows').then(r => r.data),
    getUserHistory: (userId, page = 0, size = 10) =>
        api.get(`/borrow/user/${userId}?page=${page}&size=${size}`).then(r => r.data),
    getOverdue: () => api.get('/borrow/overdue').then(r => r.data),
}

/** Dashboard API calls */
export const dashboardService = {
    getAdminStats: () => api.get('/dashboard/admin').then(r => r.data),
    getUserStats: () => api.get('/dashboard/user').then(r => r.data),
}
