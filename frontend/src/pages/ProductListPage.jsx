import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { getCategories, getProducts } from '../api.js';
import ProductCard from '../components/ProductCard.jsx';
import Pagination from '../components/Pagination.jsx';

const PAGE_SIZE = 12;

const SORT_OPTIONS = [
  { value: 'newest', label: 'Mới nhất' },
  { value: 'price_asc', label: 'Giá thấp đến cao' },
  { value: 'price_desc', label: 'Giá cao đến thấp' },
];

export default function ProductListPage() {
  // Bộ lọc nằm trên URL (?keyword=...&categoryId=...&sort=...&page=...) nên tải lại trang vẫn giữ nguyên
  const [searchParams, setSearchParams] = useSearchParams();
  const keyword = searchParams.get('keyword') || '';
  const categoryId = searchParams.get('categoryId') || '';
  const sort = searchParams.get('sort') || 'newest';
  const page = Math.max(1, Number(searchParams.get('page')) || 1);

  const [keywordInput, setKeywordInput] = useState(keyword);
  const [categories, setCategories] = useState([]);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Danh mục chỉ cần tải một lần
  useEffect(() => {
    getCategories()
      .then((data) => setCategories(Array.isArray(data) ? data : data?.content ?? []))
      .catch(() => setCategories([]));
  }, []);

  // Đồng bộ ô tìm kiếm khi URL đổi (ví dụ bấm nút Back)
  useEffect(() => {
    setKeywordInput(keyword);
  }, [keyword]);

  // Tải sản phẩm mỗi khi bộ lọc hoặc trang thay đổi
  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError('');

    getProducts({
      keyword,
      categoryId,
      sort,
      page: page - 1, // API tính trang từ 0
      size: PAGE_SIZE,
    })
      .then((data) => {
        if (!cancelled) setResult(data);
      })
      .catch((err) => {
        if (!cancelled) setError(err.message);
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [keyword, categoryId, sort, page]);

  // Đổi một vài tham số trên URL; đổi bộ lọc thì quay về trang 1
  function updateParams(changes, resetPage = true) {
    const next = new URLSearchParams(searchParams);
    Object.entries(changes).forEach(([key, value]) => {
      if (value === '' || value === null || value === undefined) next.delete(key);
      else next.set(key, value);
    });
    if (resetPage) next.delete('page');
    setSearchParams(next);
  }

  function handleSearch(event) {
    event.preventDefault();
    updateParams({ keyword: keywordInput.trim() });
  }

  const products = result?.content ?? [];

  return (
    <section>
      <form className="toolbar" onSubmit={handleSearch}>
        <input
          type="search"
          placeholder="Tìm sản phẩm..."
          value={keywordInput}
          onChange={(e) => setKeywordInput(e.target.value)}
        />
        <button type="submit" className="btn primary">
          Tìm
        </button>

        <select value={categoryId} onChange={(e) => updateParams({ categoryId: e.target.value })}>
          <option value="">Tất cả danh mục</option>
          {categories.map((c) => (
            <option key={c.id} value={c.id}>
              {c.name}
            </option>
          ))}
        </select>

        <select value={sort} onChange={(e) => updateParams({ sort: e.target.value })}>
          {SORT_OPTIONS.map((o) => (
            <option key={o.value} value={o.value}>
              {o.label}
            </option>
          ))}
        </select>
      </form>

      {loading && <p className="state">Đang tải...</p>}
      {error && <p className="state error">{error}</p>}

      {!loading && !error && products.length === 0 && (
        <p className="state">Không có sản phẩm phù hợp.</p>
      )}

      {!error && products.length > 0 && (
        <>
          <p className="result-count">{result.totalElements} sản phẩm</p>
          <div className="grid">
            {products.map((p) => (
              <ProductCard key={p.id} product={p} />
            ))}
          </div>
          <Pagination
            page={page}
            totalPages={result.totalPages}
            onChange={(p) => {
              updateParams({ page: p === 1 ? '' : p }, false);
              window.scrollTo({ top: 0, behavior: 'smooth' });
            }}
          />
        </>
      )}
    </section>
  );
}
