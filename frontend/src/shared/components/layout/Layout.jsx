import { Outlet } from 'react-router-dom'
import Header from './Header'
import Sidebar from './Sidebar'
import { useState, useEffect } from 'react'

export default function Layout() {
  const [sidebarOpen, setSidebarOpen] = useState(true)
  const [esMovil, setEsMovil] = useState(false)

  useEffect(() => {
    const manejarRedimension = () => {
      const mobile = window.innerWidth < 768
      setEsMovil(mobile)
      if (mobile) {
        setSidebarOpen(false)
      } else {
        setSidebarOpen(true)
      }
    }

    manejarRedimension()
    window.addEventListener('resize', manejarRedimension)
    return () => window.removeEventListener('resize', manejarRedimension)
  }, [])

  const alternarSidebar = () => {
    setSidebarOpen(!sidebarOpen)
  }

  const cerrarSidebar = () => {
    setSidebarOpen(false)
  }

  const desplazamiento = sidebarOpen && !esMovil ? '16rem' : '0rem'
  const ancho = sidebarOpen && !esMovil ? 'calc(100% - 16rem)' : '100%'

  return (
    <div className="min-h-screen bg-[#1b2838] flex flex-col">
      <Header sidebarOpen={sidebarOpen} toggleSidebar={alternarSidebar} />
      
      <div className="flex flex-1 relative overflow-hidden">
        {esMovil && sidebarOpen && (
          <div 
            className="fixed inset-0 bg-[#0a0a0a]/40 z-20 transition-opacity duration-200"
            onClick={cerrarSidebar}
          />
        )}
        
        <Sidebar 
          sidebarOpen={sidebarOpen} 
          isMobile={esMovil}
          closeSidebar={cerrarSidebar}
        />
        
        <div 
          className="flex-1 transition-transform duration-150 ease-out will-change-transform overflow-hidden"
          style={{
            transform: `translate3d(${desplazamiento}, 0, 0)`,
            width: ancho,
            maxWidth: ancho
          }}
        >
          <main className="w-full max-w-full min-h-[calc(100vh-4rem)] overflow-x-hidden">
            <div className="p-4 sm:p-6 mt-16 max-w-full">
              <Outlet />
            </div>
          </main>
        </div>
      </div>
    </div>
  )
}