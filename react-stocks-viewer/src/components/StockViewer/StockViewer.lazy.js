import React, { lazy, Suspense } from 'react';

const LazyStockViewer = lazy(() => import('./StockViewer'));

const StockViewer = props => (
  <Suspense fallback={null}>
    <LazyStockViewer {...props} />
  </Suspense>
);

export default StockViewer;
