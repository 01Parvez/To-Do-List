import React, { useState, useEffect } from 'react'
import { memberService } from '../services/apiService'
import Sidebar from '../components/Sidebar'
import toast from 'react-hot-toast'

/**
 * MembersPage - Admin: manage library members (CRUD).
 */
export default function MembersPage() {
    const [members, setMembers] = useState([])
    const [page, setPage] = useState({ current: 0, total: 0, size: 10 })
    const [loading, setLoading] = useState(true)
    const [showModal, setShowModal] = useState(false)
    const [editMember, setEditMember] = useState(null)
    const [form, setForm] = useState({ name: '', email: '', phone: '', membershipDate: '' })
    const [formLoading, setFormLoading] = useState(false)

    const fetchMembers = async (p = 0) => {
        setLoading(true)
        try {
            const data = await memberService.getAll(p, page.size)
            setMembers(data.content)
            setPage(prev => ({ ...prev, current: data.number, total: data.totalPages }))
        } catch { toast.error('Failed to load members') }
        finally { setLoading(false) }
    }

    useEffect(() => { fetchMembers() }, [])

    const openAdd = () => { setEditMember(null); setForm({ name: '', email: '', phone: '', membershipDate: '' }); setShowModal(true) }
    const openEdit = (m) => { setEditMember(m); setForm({ name: m.name, email: m.email, phone: m.phone || '', membershipDate: m.membershipDate || '' }); setShowModal(true) }

    const handleDelete = async (id) => {
        if (!window.confirm('Delete this member?')) return
        try { await memberService.delete(id); toast.success('Member deleted'); fetchMembers(page.current) }
        catch (e) { toast.error(e.response?.data?.message || 'Delete failed') }
    }

    const handleToggle = async (id) => {
        try { await memberService.toggleStatus(id); fetchMembers(page.current); toast.success('Status updated') }
        catch { toast.error('Toggle failed') }
    }

    const handleSubmit = async (e) => {
        e.preventDefault(); setFormLoading(true)
        try {
            if (editMember) { await memberService.update(editMember.id, form); toast.success('Member updated') }
            else { await memberService.create(form); toast.success('Member created') }
            setShowModal(false); fetchMembers(page.current)
        } catch (e) { toast.error(e.response?.data?.message || 'Save failed') }
        finally { setFormLoading(false) }
    }

    return (
        <div className="app-layout">
            <Sidebar />
            <main className="main-content fade-in">
                <div className="page-header">
                    <div><h1 className="page-title">Members</h1><p className="page-sub">Manage library members</p></div>
                    <button className="btn btn-primary" onClick={openAdd}>+ Add Member</button>
                </div>

                {loading ? <div className="spinner" /> : members.length === 0 ? (
                    <div className="empty-state"><h3>No members yet</h3><p>Add your first member!</p></div>
                ) : (
                    <>
                        <div className="table-container">
                            <table>
                                <thead><tr><th>Name</th><th>Email</th><th>Phone</th><th>Membership Date</th><th>Status</th><th>Actions</th></tr></thead>
                                <tbody>
                                    {members.map(m => (
                                        <tr key={m.id}>
                                            <td><strong>{m.name}</strong>{m.username && <><br /><small style={{ color: 'var(--text-muted)' }}>@{m.username}</small></>}</td>
                                            <td>{m.email}</td>
                                            <td>{m.phone || '—'}</td>
                                            <td>{m.membershipDate || '—'}</td>
                                            <td>
                                                <span className={`badge ${m.status === 'ACTIVE' ? 'badge-success' : 'badge-muted'}`}>{m.status}</span>
                                            </td>
                                            <td>
                                                <div style={{ display: 'flex', gap: '0.4rem' }}>
                                                    <button className="btn btn-outline btn-sm" onClick={() => openEdit(m)}>✏️</button>
                                                    <button className="btn btn-outline btn-sm" onClick={() => handleToggle(m.id)}>🔄</button>
                                                    <button className="btn btn-danger btn-sm" onClick={() => handleDelete(m.id)}>🗑️</button>
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                        {page.total > 1 && (
                            <div className="pagination">
                                <button className="pagination-btn" disabled={page.current === 0} onClick={() => fetchMembers(page.current - 1)}>‹</button>
                                <span style={{ color: 'var(--text-sec)', fontSize: '0.85rem' }}>Page {page.current + 1} of {page.total}</span>
                                <button className="pagination-btn" disabled={page.current >= page.total - 1} onClick={() => fetchMembers(page.current + 1)}>›</button>
                            </div>
                        )}
                    </>
                )}

                {showModal && (
                    <div className="modal-overlay" onClick={() => setShowModal(false)}>
                        <div className="modal" onClick={e => e.stopPropagation()}>
                            <div className="modal-header">
                                <span className="modal-title">{editMember ? 'Edit Member' : 'Add Member'}</span>
                                <button className="modal-close" onClick={() => setShowModal(false)}>✕</button>
                            </div>
                            <form onSubmit={handleSubmit}>
                                <div className="form-group"><label>Full Name *</label><input className="form-control" value={form.name} onChange={e => setForm(p => ({ ...p, name: e.target.value }))} required /></div>
                                <div className="form-group"><label>Email *</label><input type="email" className="form-control" value={form.email} onChange={e => setForm(p => ({ ...p, email: e.target.value }))} required /></div>
                                <div className="form-group"><label>Phone</label><input className="form-control" value={form.phone} onChange={e => setForm(p => ({ ...p, phone: e.target.value }))} placeholder="+91XXXXXXXXXX" /></div>
                                <div className="form-group"><label>Membership Date</label><input type="date" className="form-control" value={form.membershipDate} onChange={e => setForm(p => ({ ...p, membershipDate: e.target.value }))} /></div>
                                <div className="modal-footer">
                                    <button type="button" className="btn btn-outline" onClick={() => setShowModal(false)}>Cancel</button>
                                    <button type="submit" className="btn btn-primary" disabled={formLoading}>{formLoading ? 'Saving...' : 'Save'}</button>
                                </div>
                            </form>
                        </div>
                    </div>
                )}
            </main>
        </div>
    )
}
