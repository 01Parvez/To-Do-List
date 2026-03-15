import React, { useState, useEffect, useCallback } from 'react'
import { bookService, borrowService } from '../services/apiService'
import Sidebar from '../components/Sidebar'
import { useAuth } from '../context/AuthContext'
import toast from 'react-hot-toast'

/**
 * BooksPage - displays book catalog with search, pagination, and borrow action.
 * Admin: sees Add/Edit/Delete buttons.
 * User: sees Borrow button.
 */
export default function BooksPage() {
    const { isAdmin } = useAuth()
    const [books, setBooks] = useState([])
    const [page, setPage] = useState({ current: 0, total: 0, size: 10 })
    const [keyword, setKeyword] = useState('')
    const [searchInput, setSearchInput] = useState('')
    const [loading, setLoading] = useState(true)
    const [showModal, setShowModal] = useState(false)
    const [editBook, setEditBook] = useState(null)
    const [form, setForm] = useState({ title: '', author: '', isbn: '', category: '', publisher: '', publicationYear: '', totalCopies: 1 })
    const [formLoading, setFormLoading] = useState(false)

    const fetchBooks = useCallback(async (p = 0) => {
        setLoading(true)
        try {
            const data = keyword
                ? await bookService.search(keyword, p, page.size)
                : await bookService.getAll(p, page.size)
            setBooks(data.content)
            setPage(prev => ({ ...prev, current: data.number, total: data.totalPages }))
        } catch { toast.error('Failed to load books') }
        finally { setLoading(false) }
    }, [keyword, page.size])

    useEffect(() => { fetchBooks(0) }, [keyword])

    const openAdd = () => {
        setEditBook(null)
        setForm({ title: '', author: '', isbn: '', category: '', publisher: '', publicationYear: '', totalCopies: 1 })
        setShowModal(true)
    }

    const openEdit = (book) => {
        setEditBook(book)
        setForm({ title: book.title, author: book.author, isbn: book.isbn || '', category: book.category || '', publisher: book.publisher || '', publicationYear: book.publicationYear || '', totalCopies: book.totalCopies })
        setShowModal(true)
    }

    const handleDelete = async (id) => {
        if (!window.confirm('Delete this book?')) return
        try { await bookService.delete(id); toast.success('Book deleted'); fetchBooks(page.current) }
        catch (e) { toast.error(e.response?.data?.message || 'Delete failed') }
    }

    const handleSubmit = async (e) => {
        e.preventDefault(); setFormLoading(true)
        try {
            if (editBook) { await bookService.update(editBook.id, form); toast.success('Book updated') }
            else { await bookService.create(form); toast.success('Book added') }
            setShowModal(false); fetchBooks(page.current)
        } catch (e) { toast.error(e.response?.data?.message || 'Save failed') }
        finally { setFormLoading(false) }
    }

    const handleBorrow = async (bookId) => {
        try { await borrowService.borrowBook(bookId); toast.success('Book borrowed! Due in 14 days.'); fetchBooks(page.current) }
        catch (e) { toast.error(e.response?.data?.message || 'Could not borrow') }
    }

    return (
        <div className="app-layout">
            <Sidebar />
            <main className="main-content fade-in">
                <div className="page-header">
                    <div>
                        <h1 className="page-title">{isAdmin ? 'Manage Books' : 'Book Catalog'}</h1>
                        <p className="page-sub">{books.length} books found</p>
                    </div>
                    <div style={{ display: 'flex', gap: '1rem', alignItems: 'center', flexWrap: 'wrap' }}>
                        <div className="search-bar">
                            <span>🔍</span>
                            <input placeholder="Search by title, author, category..."
                                value={searchInput}
                                onChange={e => setSearchInput(e.target.value)}
                                onKeyDown={e => e.key === 'Enter' && setKeyword(searchInput)} />
                            {searchInput && <button onClick={() => { setSearchInput(''); setKeyword('') }} style={{ background: 'none', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}>✕</button>}
                        </div>
                        {isAdmin && <button className="btn btn-primary" onClick={openAdd}>+ Add Book</button>}
                    </div>
                </div>

                {loading ? <div className="spinner" /> : books.length === 0 ? (
                    <div className="empty-state"><h3>No books found</h3><p>Try a different search term</p></div>
                ) : (
                    <>
                        <div className="table-container">
                            <table>
                                <thead>
                                    <tr><th>Title / Author</th><th>Category</th><th>ISBN</th><th>Copies</th><th>Available</th><th>Actions</th></tr>
                                </thead>
                                <tbody>
                                    {books.map(book => (
                                        <tr key={book.id}>
                                            <td>
                                                <strong>{book.title}</strong><br />
                                                <small style={{ color: 'var(--text-muted)' }}>{book.author}</small>
                                            </td>
                                            <td><span className="badge badge-primary">{book.category || '—'}</span></td>
                                            <td style={{ fontSize: '0.8rem', color: 'var(--text-sec)' }}>{book.isbn || '—'}</td>
                                            <td>{book.totalCopies}</td>
                                            <td>
                                                <span className="avail-dot" style={{ background: book.available ? 'var(--success)' : 'var(--danger)', width: 8, height: 8, borderRadius: '50%', display: 'inline-block', marginRight: 6 }} />
                                                {book.availableCopies}
                                            </td>
                                            <td>
                                                <div style={{ display: 'flex', gap: '0.4rem' }}>
                                                    {isAdmin ? (
                                                        <>
                                                            <button className="btn btn-outline btn-sm" onClick={() => openEdit(book)}>✏️ Edit</button>
                                                            <button className="btn btn-danger btn-sm" onClick={() => handleDelete(book.id)}>🗑️</button>
                                                        </>
                                                    ) : (
                                                        <button className="btn btn-success btn-sm" onClick={() => handleBorrow(book.id)} disabled={!book.available}>
                                                            {book.available ? '📥 Borrow' : 'Unavailable'}
                                                        </button>
                                                    )}
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>

                        {/* Pagination */}
                        {page.total > 1 && (
                            <div className="pagination">
                                <button className="pagination-btn" disabled={page.current === 0} onClick={() => fetchBooks(page.current - 1)}>‹ Prev</button>
                                {[...Array(page.total)].map((_, i) => (
                                    <button key={i} className={`pagination-btn ${i === page.current ? 'active' : ''}`} onClick={() => fetchBooks(i)}>{i + 1}</button>
                                ))}
                                <button className="pagination-btn" disabled={page.current >= page.total - 1} onClick={() => fetchBooks(page.current + 1)}>Next ›</button>
                            </div>
                        )}
                    </>
                )}

                {/* Add/Edit Modal */}
                {showModal && (
                    <div className="modal-overlay" onClick={() => setShowModal(false)}>
                        <div className="modal" onClick={e => e.stopPropagation()}>
                            <div className="modal-header">
                                <span className="modal-title">{editBook ? 'Edit Book' : 'Add New Book'}</span>
                                <button className="modal-close" onClick={() => setShowModal(false)}>✕</button>
                            </div>
                            <form onSubmit={handleSubmit}>
                                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.8rem' }}>
                                    <div className="form-group" style={{ gridColumn: '1 / -1' }}>
                                        <label>Title *</label>
                                        <input className="form-control" value={form.title} onChange={e => setForm(p => ({ ...p, title: e.target.value }))} required />
                                    </div>
                                    <div className="form-group"><label>Author *</label><input className="form-control" value={form.author} onChange={e => setForm(p => ({ ...p, author: e.target.value }))} required /></div>
                                    <div className="form-group"><label>ISBN</label><input className="form-control" value={form.isbn} onChange={e => setForm(p => ({ ...p, isbn: e.target.value }))} /></div>
                                    <div className="form-group"><label>Category</label><input className="form-control" value={form.category} onChange={e => setForm(p => ({ ...p, category: e.target.value }))} /></div>
                                    <div className="form-group"><label>Publisher</label><input className="form-control" value={form.publisher} onChange={e => setForm(p => ({ ...p, publisher: e.target.value }))} /></div>
                                    <div className="form-group"><label>Year</label><input type="number" className="form-control" value={form.publicationYear} onChange={e => setForm(p => ({ ...p, publicationYear: e.target.value }))} /></div>
                                    <div className="form-group"><label>Total Copies *</label><input type="number" min="1" className="form-control" value={form.totalCopies} onChange={e => setForm(p => ({ ...p, totalCopies: parseInt(e.target.value) }))} required /></div>
                                </div>
                                <div className="modal-footer">
                                    <button type="button" className="btn btn-outline" onClick={() => setShowModal(false)}>Cancel</button>
                                    <button type="submit" className="btn btn-primary" disabled={formLoading}>{formLoading ? 'Saving...' : 'Save Book'}</button>
                                </div>
                            </form>
                        </div>
                    </div>
                )}
            </main>
        </div>
    )
}
