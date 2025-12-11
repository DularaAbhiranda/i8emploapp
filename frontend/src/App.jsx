import { useState, useEffect } from 'react'

function App() {
  const [personnel, setPersonnel] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [stats, setStats] = useState({ total: 0, active: 0, logs: 0 })
  const [activeTab, setActiveTab] = useState('personnel')

  // Form State
  const [formData, setFormData] = useState({ name: '', email: '', role: '', department: '' })

  const API_BASE = '/api' // Proxy handles redirection to localhost:8081

  useEffect(() => {
    fetchData()
    const interval = setInterval(fetchData, 10000)
    return () => clearInterval(interval)
  }, [])

  const fetchData = async () => {
    try {
      // Fetch Personnel
      const personnelRes = await fetch(`${API_BASE}/personnel`)
      if (!personnelRes.ok) throw new Error('API Unreachable')
      const personnelData = await personnelRes.json()

      // Fetch Audit Logs for stats (simulated count if large)
      const logsRes = await fetch(`${API_BASE}/audit-logs`)
      const logsData = await logsRes.json()

      setPersonnel(personnelData)
      setStats({
        total: personnelData.length,
        active: personnelData.filter(p => p.status === 'ACTIVE').length,
        logs: logsData.length
      })
      setError(null)
    } catch (err) {
      console.error(err)
      setError('Backend System Unreachable - Please start the Spring Boot App')
    } finally {
      setLoading(false)
    }
  }

  const handleCreate = async (e) => {
    e.preventDefault()
    try {
      const res = await fetch(`${API_BASE}/personnel`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
      })
      if (res.ok) {
        setFormData({ name: '', email: '', role: '', department: '' })
        fetchData()
        alert('Personnel Created!')
      }
    } catch (err) {
      alert('Failed to create personnel')
    }
  }

  const handleDelete = async (id) => {
    if (!confirm('Delete this record?')) return
    try {
      await fetch(`${API_BASE}/personnel/${id}`, { method: 'DELETE' })
      fetchData()
    } catch (err) {
      alert('Delete failed')
    }
  }

  return (
    <div className="app-container">
      <header className="header animate-fade-in">
        <div>
          <h1>INNOV8 Observability Lab</h1>
          <p style={{ color: 'var(--text-muted)' }}>Real-time Monitoring & Audit Trails</p>
        </div>
        <div className={`status-badge ${error ? 'status-inactive' : 'status-active'}`}>
          {error ? 'System Offline' : 'System Online'}
        </div>
      </header>

      {/* Stats Grid */}
      <div className="grid-cols-3 animate-fade-in" style={{ animationDelay: '0.1s' }}>
        <StatCard title="Total Personnel" value={loading ? '...' : stats.total} icon="👥" />
        <StatCard title="Active Now" value={loading ? '...' : stats.active} icon="🟢" />
        <StatCard title="Total Audit Logs" value={loading ? '...' : stats.logs} icon="📜" />
      </div>

      {/* Main Content */}
      <div className="glass-panel animate-fade-in" style={{ padding: '2rem', animationDelay: '0.2s' }}>
        <div style={{ display: 'flex', gap: '1rem', marginBottom: '2rem', borderBottom: '1px solid rgba(255,255,255,0.1)', paddingBottom: '1rem' }}>
          <TabButton active={activeTab === 'personnel'} onClick={() => setActiveTab('personnel')} label="Personnel Management" />
          <TabButton active={activeTab === 'create'} onClick={() => setActiveTab('create')} label="Add New" />
        </div>

        {error && (
          <div style={{ padding: '1rem', background: 'rgba(239, 68, 68, 0.1)', borderRadius: '8px', color: '#fca5a5', marginBottom: '1rem' }}>
            ⚠️ {error} - Ensure Backend is running on port 8081
          </div>
        )}

        {activeTab === 'personnel' && (
          <div style={{ overflowX: 'auto' }}>
            <table className="data-table">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Role</th>
                  <th>Department</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {personnel.map(p => (
                  <tr key={p.id}>
                    <td>
                      <div style={{ fontWeight: '500' }}>{p.name}</div>
                      <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{p.email}</div>
                    </td>
                    <td>{p.role}</td>
                    <td>{p.department}</td>
                    <td>
                      <span className={`status-badge status-${p.status ? p.status.toLowerCase() : 'unknown'}`}>
                        {p.status}
                      </span>
                    </td>
                    <td>
                      <button className="btn btn-danger" style={{ padding: '0.5rem' }} onClick={() => handleDelete(p.id)}>
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            {personnel.length === 0 && <p style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-muted)' }}>No personnel found.</p>}
          </div>
        )}

        {activeTab === 'create' && (
          <form onSubmit={handleCreate} style={{ maxWidth: '500px' }}>
            <h3 style={{ marginBottom: '1.5rem' }}>Add New Personnel</h3>
            <input
              className="input-field" placeholder="Full Name"
              value={formData.name} onChange={e => setFormData({ ...formData, name: e.target.value })} required
            />
            <input
              className="input-field" placeholder="Email Address" type="email"
              value={formData.email} onChange={e => setFormData({ ...formData, email: e.target.value })} required
            />
            <input
              className="input-field" placeholder="Role (e.g. Engineer)"
              value={formData.role} onChange={e => setFormData({ ...formData, role: e.target.value })} required
            />
            <input
              className="input-field" placeholder="Department (e.g. Engineering)"
              value={formData.department} onChange={e => setFormData({ ...formData, department: e.target.value })} required
            />
            <button type="submit" className="btn btn-primary" style={{ width: '100%', justifyContent: 'center' }}>
              Create Personnel
            </button>
          </form>
        )}
      </div>
    </div>
  )
}

// Components
const StatCard = ({ title, value, icon }) => (
  <div className="glass-panel" style={{ padding: '1.5rem', display: 'flex', alignItems: 'center', gap: '1rem' }}>
    <div style={{ fontSize: '2.5rem', background: 'rgba(255,255,255,0.05)', padding: '0.5rem', borderRadius: '12px' }}>{icon}</div>
    <div>
      <div style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>{title}</div>
      <div style={{ fontSize: '1.75rem', fontWeight: '700' }}>{value}</div>
    </div>
  </div>
)

const TabButton = ({ active, onClick, label }) => (
  <button
    onClick={onClick}
    style={{
      background: 'none',
      border: 'none',
      color: active ? 'white' : 'var(--text-muted)',
      fontWeight: active ? '600' : '400',
      cursor: 'pointer',
      padding: '0.5rem 1rem',
      borderBottom: active ? '2px solid var(--color-primary)' : '2px solid transparent',
      transition: 'all 0.2s'
    }}
  >
    {label}
  </button>
)

export default App
