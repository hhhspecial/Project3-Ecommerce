import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { getProduct } from '../api.js';
import SafeImage from '../components/SafeImage.jsx';
import { formatPrice, formatRating, skuLabel } from '../format.js';

// Backend trả ảnh với trường imageUrl; dự phòng thêm url nếu bạn đặt tên khác
const imageUrl = (img) => img.imageUrl ?? img.url ?? null;

export default function ProductDetailPage() {
  const { id } = useParams();

  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedSkuId, setSelectedSkuId] = useState(null);
  const [activeImage, setActiveImage] = useState(null);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError('');
    setProduct(null);

    getProduct(id)
      .then((data) => {
        if (cancelled) return;
        setProduct(data);

        // Mặc định chọn biến thể rẻ nhất (backend đã sắp theo giá tăng dần)
        const skus = data.skus ?? [];
        setSelectedSkuId(skus.length > 0 ? skus[0].id : null);

        // Ảnh đang xem: ảnh đại diện, nếu không có thì ảnh đầu tiên trong thư viện
        const images = data.images ?? [];
        setActiveImage(data.coverUrl || (images[0] && imageUrl(images[0])) || null);
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
  }, [id]);

  if (loading) return <p className="state">Đang tải...</p>;
  if (error) {
    return (
      <div>
        <p className="state error">{error}</p>
        <Link to="/" className="back">
          ← Về danh sách sản phẩm
        </Link>
      </div>
    );
  }
  if (!product) return null;

  const skus = product.skus ?? [];
  const images = product.images ?? [];
  const selectedSku = skus.find((s) => s.id === selectedSkuId) || null;

  // Danh sách ảnh nhỏ: ảnh đại diện (nếu chưa có trong thư viện) + ảnh thư viện
  const thumbUrls = [];
  if (product.coverUrl) thumbUrls.push(product.coverUrl);
  images.forEach((img) => {
    const url = imageUrl(img);
    if (url && !thumbUrls.includes(url)) thumbUrls.push(url);
  });

  const priceText = selectedSku
    ? formatPrice(selectedSku.price)
    : formatPrice(skus.length > 0 ? skus[0].price : null);

  const outOfStock = selectedSku ? selectedSku.stockQuantity <= 0 : true;

  return (
    <section>
      <Link to="/" className="back">
        ← Về danh sách sản phẩm
      </Link>

      <div className="detail">
        <div className="gallery">
          <div className="gallery-main">
            <SafeImage src={activeImage} alt={product.name} />
          </div>
          {thumbUrls.length > 1 && (
            <div className="gallery-thumbs">
              {thumbUrls.map((url) => (
                <button
                  key={url}
                  type="button"
                  className={url === activeImage ? 'thumb active' : 'thumb'}
                  onClick={() => setActiveImage(url)}
                >
                  <SafeImage src={url} alt="" />
                </button>
              ))}
            </div>
          )}
        </div>

        <div className="info">
          <h1>{product.name}</h1>

          <div className="info-meta">
            <span>★ {formatRating(product.avgRating)}</span>
            <span>{product.reviewCount ?? 0} đánh giá</span>
            {product.categoryName && <span>Danh mục: {product.categoryName}</span>}
          </div>
          {product.shopName && <div className="info-shop">Cửa hàng: {product.shopName}</div>}

          <div className="price">{priceText}</div>

          {skus.length > 0 ? (
            <div className="variants">
              <div className="variants-title">Phân loại</div>
              <div className="variants-list">
                {skus.map((sku) => (
                  <button
                    key={sku.id}
                    type="button"
                    className={sku.id === selectedSkuId ? 'variant active' : 'variant'}
                    disabled={sku.stockQuantity <= 0}
                    onClick={() => setSelectedSkuId(sku.id)}
                  >
                    {skuLabel(sku)}
                    {sku.stockQuantity <= 0 && ' (hết hàng)'}
                  </button>
                ))}
              </div>
              {selectedSku && (
                <div className="stock">
                  {selectedSku.stockQuantity > 0
                    ? `Còn ${selectedSku.stockQuantity} sản phẩm`
                    : 'Hết hàng'}
                </div>
              )}
            </div>
          ) : (
            <p className="state">Sản phẩm này chưa có phân loại để bán.</p>
          )}

          <button
            type="button"
            className="btn primary big"
            disabled
            title="Giỏ hàng sẽ được làm ở tuần 6"
          >
            {outOfStock ? 'Hết hàng' : 'Thêm vào giỏ (sắp có)'}
          </button>

          {product.summary && <p className="summary">{product.summary}</p>}
        </div>
      </div>

      {product.description && (
        <div className="description">
          <h2>Mô tả sản phẩm</h2>
          <p>{product.description}</p>
        </div>
      )}
    </section>
  );
}
