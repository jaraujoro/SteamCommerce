const Footer = () => {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="bg-white border-t border-gray-200 mt-auto">
      <div className="max-w-7xl mx-auto px-4 py-6">
        <div className="flex flex-col md:flex-row justify-between items-center gap-4">
          {/* Información de contacto */}
          <div className="flex flex-col md:flex-row items-center gap-4 md:gap-6 text-sm">
            <div>
              <span className="text-gray-500 font-light">MIS REDES:</span>
              <a
                href="https://linktr.ee/darkdota"
                target="_blank"
                rel="noopener noreferrer"
                className="ml-1.5 text-gray-700 hover:text-gray-900 transition-colors font-medium"
              >
                linktr.ee/darkdota
              </a>
            </div>
            
            <span className="hidden md:inline text-gray-300">|</span>
            
            <div>
              <span className="text-gray-500 font-light">WHATSAPP:</span>
              <a
                href="https://wa.me/51918560935"
                target="_blank"
                rel="noopener noreferrer"
                className="ml-1.5 text-gray-700 hover:text-gray-900 transition-colors font-medium"
              >
                +51 918 560 935
              </a>
            </div>
          </div>

          {/* Derechos de autor */}
          <div className="text-xs text-gray-400 font-light">
            © {currentYear} Todos los derechos reservados
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;