import { NavLink } from 'react-router-dom'
import { useEffect, useRef } from 'react'

const itemsMenu = [
  { path: '/', label: 'Dashboard' },
  { path: '/catalogo', label: 'Catalogo' },
]

export default function Sidebar({ sidebarOpen, isMobile, closeSidebar }) {
  const sidebarRef = useRef(null)

  useEffect(() => {
    const manejarClicFuera = (event) => {
      if (isMobile && sidebarOpen && sidebarRef.current && !sidebarRef.current.contains(event.target)) {
        closeSidebar()
      }
    }

    document.addEventListener('mousedown', manejarClicFuera)
    return () => document.removeEventListener('mousedown', manejarClicFuera)
  }, [isMobile, sidebarOpen, closeSidebar])

  return (
    <>
      {isMobile && sidebarOpen && (
        <div 
          className="fixed inset-0 bg-[#0a0a0a]/40 z-20 transition-opacity duration-100"
          onClick={closeSidebar}
        />
      )}

      <aside 
        ref={sidebarRef}
        className={`
          fixed left-0 top-16 h-[calc(100vh-4rem)] bg-[#1b2838] border-r border-[#2a475e]
          transition-transform duration-200 ease-in-out z-30
          will-change-transform
          flex flex-col
          ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'}
          ${isMobile ? 'w-72 shadow-2xl' : 'w-64'}
          ${!isMobile && !sidebarOpen ? 'w-0 overflow-hidden' : ''}
        `}
        style={{
          ...(!isMobile && !sidebarOpen ? { width: 0, minWidth: 0 } : {})
        }}
      >
        <nav className="flex-1 overflow-y-auto py-4">
          <ul className="space-y-1 px-2">
            {itemsMenu.map((item) => (
              <li key={item.path}>
                <NavLink
                  to={item.path}
                  onClick={() => isMobile && closeSidebar()}
                  className={({ isActive }) => `
                    flex items-center px-3 py-2.5 transition-colors duration-150 group relative rounded
                    ${isActive 
                      ? 'bg-[#2a475e] text-[#66c0f4] border border-[#66c0f4]/20' 
                      : 'text-[#8f9aa7] hover:bg-[#2a475e]/30 hover:text-white'
                    }
                    ${!sidebarOpen && 'justify-center'}
                  `}
                >
                  <span className={`text-sm font-medium truncate ${!sidebarOpen ? 'hidden' : ''}`}>
                    {item.label}
                  </span>
                  
                  {!isMobile && !sidebarOpen && (
                    <div className="absolute left-full ml-3 px-2.5 py-1.5 bg-[#1b2838] border border-[#2a475e] text-white text-xs font-light tracking-wider opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-200 pointer-events-none whitespace-nowrap z-50 shadow-2xl">
                      {item.label}
                    </div>
                  )}
                </NavLink>
              </li>
            ))}
          </ul>
        </nav>

        {sidebarOpen && (
          <div className="flex-shrink-0 p-4 border-t border-[#2a475e] bg-[#1b2838]">
            <div className="px-3 py-3 bg-[#2a475e]/20 border border-[#66c0f4]/10 rounded">
              <p className="text-xs font-medium text-[#8f9aa7] uppercase tracking-wider">
                Versión 1.0
              </p>
              <p className="text-xs text-[#2a475e] font-light mt-0.5">
                Última actualización
              </p>
            </div>
          </div>
        )}
      </aside>
    </>
  )
}