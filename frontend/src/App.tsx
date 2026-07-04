import { useEffect } from 'react'
import { Link, Route, Routes, useLocation, useNavigate } from 'react-router-dom'
import Home from './pages/Home'
import Tickets from './pages/Tickets'
import Trains from './pages/Trains'
import Signin from './pages/Signin'
import Signup from './pages/Signup'
import NotFound from './pages/NotFound'
import { useStations } from './store/stations'
import { useAuth } from './store/auth'
import { Button } from '@/components/ui/button'

function NavLink({ to, children }: { to: string; children: React.ReactNode }) {
  const location = useLocation()
  const active = location.pathname === to
  return (
    <Link
      to={to}
      className={`flex items-center relative px-3.5 font-semibold text-sm no-underline
        ${active ? 'text-foreground' : 'text-muted-foreground'}
        after:absolute after:bottom-3 after:left-1/2 after:-translate-x-1/2 after:h-0.5 after:bg-foreground after:transition-all after:duration-150
        ${active ? 'after:w-[60%]' : 'after:w-0'}
        hover:text-foreground`}
    >
      {children}
    </Link>
  )
}

export default function App() {
  const fetchStations = useStations(s => s.fetch)
  const checkSession = useAuth(s => s.check)
  const username = useAuth(s => s.username)
  const signout = useAuth(s => s.signout)
  const navigate = useNavigate()

  useEffect(() => { fetchStations(); checkSession() }, [])

  async function handleSignout() {
    await signout()
    navigate('/')
  }

  return (
    <div className="min-h-screen bg-background">
      <header className="flex items-center h-14 px-6 bg-card border-b border-border gap-6">
        <Link to="/" className="text-base font-bold whitespace-nowrap no-underline text-primary hover:text-primary/80">
          中国铁路客户服务中心
        </Link>
        <nav className="flex items-stretch gap-0 font-semibold h-full">
          <NavLink to="/tickets">车票</NavLink>
          <NavLink to="/trains">时刻表</NavLink>
        </nav>
        <div className="flex gap-2 ml-auto items-center">
          {username ? (
            <>
              <span className="text-sm font-semibold text-foreground">{username}</span>
              <Button type="button" onClick={handleSignout}>
                登出
              </Button>
            </>
          ) : (
            <>
              <Button variant="outline" asChild>
                <Link to="/signin">登录</Link>
              </Button>
              <Button asChild>
                <Link to="/signup">注册</Link>
              </Button>
            </>
          )}
        </div>
      </header>

      <main className="max-w-5xl mx-auto px-4 pt-6 pb-12">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/tickets" element={<Tickets />} />
          <Route path="/trains" element={<Trains />} />
          <Route path="/signin" element={<Signin />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="*" element={<NotFound />} />
        </Routes>
      </main>
    </div>
  )
}
