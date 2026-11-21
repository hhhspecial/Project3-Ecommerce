// page: số trang hiện tại (bắt đầu từ 1), totalPages: tổng số trang
export default function Pagination({ page, totalPages, onChange }) {
  if (totalPages <= 1) return null;

  // Hiện tối đa 5 số trang quanh trang hiện tại
  const start = Math.max(1, Math.min(page - 2, totalPages - 4));
  const end = Math.min(totalPages, start + 4);
  const pages = [];
  for (let p = start; p <= end; p += 1) pages.push(p);

  return (
    <nav className="pagination" aria-label="Phân trang">
      <button type="button" disabled={page <= 1} onClick={() => onChange(page - 1)}>
        ‹ Trước
      </button>
      {pages.map((p) => (
        <button
          key={p}
          type="button"
          className={p === page ? 'active' : ''}
          onClick={() => onChange(p)}
        >
          {p}
        </button>
      ))}
      <button type="button" disabled={page >= totalPages} onClick={() => onChange(page + 1)}>
        Sau ›
      </button>
    </nav>
  );
}
