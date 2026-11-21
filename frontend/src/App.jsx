import { Link, Route, Routes } from 'react-router-dom';
import ProductListPage from './pages/ProductListPage.jsx';
import ProductDetailPage from './pages/ProductDetailPage.jsx';

export default function App() {
  return (
    <div className="app">
      <header className="header">
        <div className="container header-inner">
          <Link to="/" className="logo">
            Chợ Online
          </Link>
          <nav className="nav">
            <Link to="/">Sản phẩm</Link>
          </nav>
        </div>
      </header>

      <main className="container main">
        <Routes>
          <Route path="/" element={<ProductListPage />} />
          <Route path="/products/:id" element={<ProductDetailPage />} />
          <Route path="*" element={<p className="state">Không tìm thấy trang.</p>} />
        </Routes>
      </main>

      <footer className="footer">
        <div className="container">Dự án thương mại điện tử cá nhân</div>
      </footer>
    </div>
  );
}
