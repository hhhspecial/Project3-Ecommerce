import { useState } from 'react';

// Ảnh trống hoặc tải lỗi (ví dụ link giả trong dữ liệu seed) thì hiện khung xám thay vì icon vỡ
const PLACEHOLDER =
  'data:image/svg+xml;utf8,' +
  encodeURIComponent(
    '<svg xmlns="http://www.w3.org/2000/svg" width="400" height="400" viewBox="0 0 400 400">' +
      '<rect width="400" height="400" fill="#eef0f3"/>' +
      '<text x="200" y="208" text-anchor="middle" font-family="sans-serif" font-size="22" fill="#9aa1ac">Chưa có ảnh</text>' +
      '</svg>'
  );

export default function SafeImage({ src, alt, className }) {
  const [failed, setFailed] = useState(false);
  const finalSrc = !src || failed ? PLACEHOLDER : src;

  return (
    <img
      className={className}
      src={finalSrc}
      alt={alt || ''}
      loading="lazy"
      onError={() => setFailed(true)}
    />
  );
}
