import { Route, Routes } from 'react-router-dom';
import Layout from '../../shared/components/layout/Layout';
import DashboardPage from '../../modules/dashboard/pages/DashboardPage';
import CatalogoPage from '../../modules/catalogo/pages/CatalogoPage';
import PerfilPage from '../../modules/perfil/pages/PerfilPage';

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        <Route index element={<DashboardPage />} />
        <Route path="catalogo" element={<CatalogoPage />} />
        <Route path="perfil" element={<PerfilPage />} />
      </Route>
    </Routes>
  );
};

export default AppRoutes;
