import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Dev server chạy ở http://localhost:5173.
// Mọi request /api và /uploads được chuyển tiếp sang backend (8080),
// nên trình duyệt coi là cùng một nguồn và không cần cấu hình CORS.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8080',
      '/uploads': 'http://localhost:8080',
    },
  },
});
