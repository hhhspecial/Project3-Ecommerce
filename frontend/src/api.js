// Gọi backend. Backend luôn trả { success, message, data }, hàm này trả về phần data.
// Lỗi (4xx, 5xx) được ném ra dưới dạng Error có message lấy từ backend.

export async function apiGet(path, params = {}) {
  const query = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      query.append(key, value);
    }
  });
  const url = query.toString() ? `${path}?${query}` : path;

  const res = await fetch(url, { headers: { Accept: 'application/json' } });

  let body = null;
  try {
    body = await res.json();
  } catch {
    // phản hồi không phải JSON
  }

  if (!res.ok) {
    throw new Error((body && body.message) || `Lỗi ${res.status}`);
  }
  return body ? body.data : null;
}

export const getProducts = (params) => apiGet('/api/products', params);
export const getProduct = (id) => apiGet(`/api/products/${id}`);
export const getCategories = () => apiGet('/api/categories');
