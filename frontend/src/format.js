const vnd = new Intl.NumberFormat('vi-VN', {
  style: 'currency',
  currency: 'VND',
  maximumFractionDigits: 0,
});

export function formatPrice(value) {
  if (value === null || value === undefined) return 'Chưa có giá';
  return vnd.format(Number(value));
}

export function formatRating(value) {
  const n = Number(value);
  return Number.isFinite(n) ? n.toFixed(1) : '0.0';
}

// Tên hiển thị của một biến thể: ưu tiên variantName, rồi đến "màu, size", cuối cùng là mã SKU
export function skuLabel(sku) {
  if (sku.variantName) return sku.variantName;
  const parts = [sku.color, sku.size].filter(Boolean);
  return parts.length > 0 ? parts.join(', ') : sku.skuCode;
}
