import { useEffect } from 'react'
import { Link, Route, Routes, useLocation } from 'react-router-dom'
import Home from './pages/Home'
import Tickets from './pages/Tickets'
import Trains from './pages/Trains'
import Signup from './pages/Signup'
import { useStations } from './store/stations'

function NavLink({ to, children }: { to: string; children: React.ReactNode }) {
  const location = useLocation()
  const active = location.pathname === to
  return (
    <Link
      to={to}
      className={`flex items-center relative px-3.5 font-semibold text-sm no-underline
        ${active ? 'text-primary' : 'text-ink'}
        after:absolute after:bottom-3 after:left-1/2 after:-translate-x-1/2 after:h-0.5 after:bg-primary after:transition-all after:duration-150
        ${active ? 'after:w-[60%]' : 'after:w-0'}
        hover:text-primary`}
    >
      {children}
    </Link>
  )
}

export default function App() {
  const fetchStations = useStations(s => s.fetch)

  useEffect(() => { fetchStations() }, [])

  return (
    <div className="min-h-screen bg-page">
      <header className="flex items-center h-14 px-6 bg-card border-b border-stroke gap-6">
        <Link to="/" className="text-base font-bold whitespace-nowrap no-underline text-primary hover:text-primary-hi">
          中国铁路客户服务中心
        </Link>
        <nav className="flex items-stretch gap-0 font-semibold h-full">
          <NavLink to="/tickets">车票</NavLink>
          <NavLink to="/trains">时刻表</NavLink>
        </nav>
        <div className="flex gap-2 ml-auto">
          <Link
            to="/signin"
            className="px-4 py-2 border text-sm font-semibold no-underline text-primary border-primary transition-all hover:bg-primary hover:text-white"
          >
            登 录
          </Link>
          <Link
            to="/signup"
            className="px-4 py-2 border text-sm font-semibold no-underline bg-primary text-white border-primary transition-all hover:bg-white hover:text-primary"
          >
            注 册
          </Link>
        </div>
      </header>

      <main className="max-w-5xl mx-auto px-4 pt-6 pb-12">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/tickets" element={<Tickets />} />
          <Route path="/trains" element={<Trains />} />
          <Route path="/trains/:code" element={<Trains />} />
          <Route path="/signup" element={<Signup />} />
        </Routes>
      </main>
    </div>
  )
}
