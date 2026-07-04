import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import axios from 'axios'
import { useAuth } from '../store/auth'
import { Button } from '@/components/ui/button'

export default function Signin() {
  const navigate = useNavigate()
  const signin = useAuth(s => s.signin)
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError('')
    setSubmitting(true)
    try {
      await signin(username, password)
      navigate('/tickets')
    } catch (err) {
      if (axios.isAxiosError(err) && err.response) {
        setError(err.response.data.message || '登录失败')
      } else {
        setError('网络错误，请稍后重试')
      }
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="max-w-sm mx-auto">
      <section className="bg-card border border-stroke px-8 py-8">
        <h1 className="text-xl font-bold text-ink mb-6 text-center">登录</h1>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <div>
            <label className="block text-xs font-semibold text-muted mb-1">用户名</label>
            <input
              type="text"
              value={username}
              onChange={e => setUsername(e.target.value)}
              className="w-full h-9 px-2.5 text-sm border border-stroke bg-card text-ink outline-none focus:border-primary focus:ring-2 focus:ring-primary/20"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-muted mb-1">密码</label>
            <input
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              className="w-full h-9 px-2.5 text-sm border border-stroke bg-card text-ink outline-none focus:border-primary focus:ring-2 focus:ring-primary/20"
            />
          </div>

          {error && (
            <p className="text-sm text-accent">{error}</p>
          )}

          <Button type="submit" disabled={submitting}>
            {submitting ? '登录中...' : '登录'}
          </Button>
        </form>

        <p className="text-sm text-muted mt-4 text-center">
          没有账号？<Link to="/signup" className="text-primary hover:text-primary-hi">去注册</Link>
        </p>
      </section>
    </div>
  )
}
