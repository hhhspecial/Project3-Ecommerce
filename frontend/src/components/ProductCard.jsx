import { Link } from 'react-router-dom';
import SafeImage from './SafeImage.jsx';
import { formatPrice, formatRating } from '../format.js';

export default function ProductCard({ product }) {
  return (
    <Link to={`/products/${product.id}`} className="card">
      <div className="card-thumb">
        <SafeImage src={product.coverUrl} alt={product.name} />
      </div>
      <div className="card-body">
        <h3 className="card-title">{product.name}</h3>
        <div className="card-shop">{product.shopName}</div>
        <div className="card-price">{formatPrice(product.minPrice)}</div>
        <div className="card-meta">
          <span>★ {formatRating(product.avgRating)}</span>
          <span>({product.reviewCount ?? 0} đánh giá)</span>
        </div>
      </div>
    </Link>
  );
}
