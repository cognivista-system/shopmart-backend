# ShopMart API — Full Reference

**121 endpoints across 18 modules.** Base path: `/api` · Auth: `Authorization: Bearer <accessToken>` · Docs: `/api/swagger-ui.html`

## Conventions

Every response is wrapped in a standard envelope:

```json
{
  "success": true,
  "message": "OK",
  "data": {}
}
```

Errors:

```json
{
  "success": false,
  "message": "Resource not found",
  "data": null
}
```

Paged list responses (`data` field):

```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0,
  "last": true
}
```

Access levels: 🌐 public · 🔒 authenticated · 👑 admin · 🏪 vendor

## All endpoints

| # | Method | Path | Access | Description |
|---|--------|------|--------|-------------|
| 1 | PUT | `/api/auth/change-password` | 🔒 auth | change password |
| 2 | POST | `/api/auth/forgot-password` | 🌐 public | forgot password |
| 3 | POST | `/api/auth/login` | 🌐 public | login |
| 4 | POST | `/api/auth/logout` | 🔒 auth | logout |
| 5 | GET | `/api/auth/me` | 🔒 auth | get profile |
| 6 | PUT | `/api/auth/me` | 🔒 auth | update profile |
| 7 | POST | `/api/auth/refresh-token` | 🌐 public | refresh |
| 8 | POST | `/api/auth/register` | 🌐 public | register |
| 9 | POST | `/api/auth/resend-otp` | 🌐 public | resend otp |
| 10 | POST | `/api/auth/reset-password` | 🌐 public | reset password |
| 11 | POST | `/api/auth/verify-otp` | 🌐 public | verify otp |
| 12 | GET | `/api/categories` | 🔒 auth | get all |
| 13 | POST | `/api/categories` | 🔒 auth | create |
| 14 | DELETE | `/api/categories/{id}` | 🔒 auth | delete |
| 15 | GET | `/api/categories/{id}` | 🌐 public | get by id |
| 16 | PUT | `/api/categories/{id}` | 🔒 auth | update |
| 17 | GET | `/api/brands` | 🔒 auth | get all |
| 18 | POST | `/api/brands` | 🔒 auth | create |
| 19 | DELETE | `/api/brands/{id}` | 🔒 auth | delete |
| 20 | GET | `/api/brands/{id}` | 🌐 public | get by id |
| 21 | PUT | `/api/brands/{id}` | 🔒 auth | update |
| 22 | GET | `/api/products` | 🔒 auth | search |
| 23 | POST | `/api/products` | 🔒 auth | create |
| 24 | GET | `/api/products/featured` | 🌐 public | featured |
| 25 | GET | `/api/products/slug/{slug}` | 🌐 public | get by slug |
| 26 | DELETE | `/api/products/{id}` | 🔒 auth | delete |
| 27 | GET | `/api/products/{id}` | 🌐 public | get by id |
| 28 | PUT | `/api/products/{id}` | 🔒 auth | update |
| 29 | PATCH | `/api/products/{id}/stock` | 🔒 auth | update stock |
| 30 | DELETE | `/api/cart` | 🔒 auth | clear |
| 31 | GET | `/api/cart` | 🔒 auth | get cart |
| 32 | POST | `/api/cart/items` | 🔒 auth | add |
| 33 | DELETE | `/api/cart/items/{itemId}` | 🔒 auth | remove |
| 34 | PUT | `/api/cart/items/{itemId}` | 🔒 auth | update |
| 35 | DELETE | `/api/wishlist` | 🔒 auth | clear |
| 36 | GET | `/api/wishlist` | 🔒 auth | get |
| 37 | DELETE | `/api/wishlist/{productId}` | 🔒 auth | remove |
| 38 | POST | `/api/wishlist/{productId}` | 🔒 auth | add |
| 39 | POST | `/api/wishlist/{productId}/move-to-cart` | 🔒 auth | move to cart |
| 40 | GET | `/api/orders` | 🔒 auth | get orders |
| 41 | POST | `/api/orders` | 🔒 auth | create |
| 42 | GET | `/api/orders/{id}` | 🔒 auth | get details |
| 43 | POST | `/api/orders/{id}/cancel` | 🔒 auth | cancel |
| 44 | PATCH | `/api/orders/{id}/status` | 🔒 auth | update status |
| 45 | GET | `/api/orders/{id}/track` | 🔒 auth | track |
| 46 | POST | `/api/payments/initiate` | 🔒 auth | initiate |
| 47 | GET | `/api/payments/order/{orderId}` | 🔒 auth | history |
| 48 | POST | `/api/payments/verify` | 🔒 auth | verify |
| 49 | GET | `/api/coupons` | 🔒 auth | get all |
| 50 | POST | `/api/coupons` | 🔒 auth | create |
| 51 | POST | `/api/coupons/validate` | 🔒 auth | validate |
| 52 | DELETE | `/api/coupons/{id}` | 🔒 auth | delete |
| 53 | GET | `/api/coupons/{id}` | 🔒 auth | get by id |
| 54 | PUT | `/api/coupons/{id}` | 🔒 auth | update |
| 55 | GET | `/api/products/{productId}/reviews` | 🌐 public | get product reviews |
| 56 | POST | `/api/products/{productId}/reviews` | 🔒 auth | add review |
| 57 | GET | `/api/products/{productId}/reviews/summary` | 🌐 public | get summary |
| 58 | DELETE | `/api/reviews/{id}` | 🔒 auth | delete review |
| 59 | PUT | `/api/reviews/{id}` | 🔒 auth | update review |
| 60 | PATCH | `/api/reviews/{id}/moderate` | 🔒 auth | moderate |
| 61 | GET | `/api/blogs` | 🌐 public | list published |
| 62 | POST | `/api/blogs` | 🔒 auth | create |
| 63 | GET | `/api/blogs/admin/all` | 🔒 auth | list all |
| 64 | GET | `/api/blogs/admin/{id}` | 🔒 auth | get by id |
| 65 | DELETE | `/api/blogs/{id}` | 🔒 auth | delete |
| 66 | PUT | `/api/blogs/{id}` | 🔒 auth | update |
| 67 | GET | `/api/blogs/{slug}` | 🌐 public | get by slug |
| 68 | GET | `/api/notifications` | 🔒 auth | list |
| 69 | PATCH | `/api/notifications/read-all` | 🔒 auth | mark all read |
| 70 | POST | `/api/notifications/send` | 🔒 auth | send |
| 71 | GET | `/api/notifications/unread-count` | 🔒 auth | unread count |
| 72 | DELETE | `/api/notifications/{id}` | 🔒 auth | delete |
| 73 | PATCH | `/api/notifications/{id}/read` | 🔒 auth | mark read |
| 74 | GET | `/api/admin/analytics/dashboard` | 👑 admin | dashboard |
| 75 | GET | `/api/admin/analytics/low-stock` | 👑 admin | low stock |
| 76 | GET | `/api/admin/analytics/order-status` | 👑 admin | order status |
| 77 | GET | `/api/admin/analytics/sales` | 👑 admin | sales |
| 78 | GET | `/api/admin/analytics/top-products` | 👑 admin | top products |
| 79 | GET | `/api/search` | 🌐 public | search |
| 80 | DELETE | `/api/search/history` | 🔒 auth | clear history |
| 81 | GET | `/api/search/history` | 🔒 auth | history |
| 82 | GET | `/api/search/suggest` | 🌐 public | suggest |
| 83 | GET | `/api/search/trending` | 🌐 public | trending |
| 84 | GET | `/api/products/{id}/frequently-bought-together` | 🌐 public | frequently bought together |
| 85 | GET | `/api/products/{id}/similar` | 🌐 public | similar |
| 86 | POST | `/api/products/{id}/view` | 🌐 public | record view |
| 87 | GET | `/api/recommendations/for-you` | 🔒 auth | for you |
| 88 | GET | `/api/recommendations/recently-viewed` | 🔒 auth | recently viewed |
| 89 | GET | `/api/recommendations/trending` | 🌐 public | trending |
| 90 | GET | `/api/vendors` | 🔒 auth | list |
| 91 | GET | `/api/vendors/me` | 🔒 auth | me |
| 92 | PUT | `/api/vendors/me` | 🔒 auth | update profile |
| 93 | GET | `/api/vendors/me/earnings` | 🔒 auth | my earnings |
| 94 | GET | `/api/vendors/me/orders` | 🔒 auth | my orders |
| 95 | GET | `/api/vendors/me/payouts` | 🔒 auth | my payouts |
| 96 | GET | `/api/vendors/me/products` | 🔒 auth | my products |
| 97 | PATCH | `/api/vendors/payouts/{payoutId}/paid` | 🔒 auth | mark payout paid |
| 98 | POST | `/api/vendors/register` | 🔒 auth | register |
| 99 | GET | `/api/vendors/store/{slug}` | 🌐 public | get by slug |
| 100 | GET | `/api/vendors/{id}` | 🔒 auth | get by id |
| 101 | GET | `/api/vendors/{id}/payouts` | 🔒 auth | list payouts |
| 102 | POST | `/api/vendors/{id}/payouts` | 🔒 auth | create payout |
| 103 | PATCH | `/api/vendors/{id}/status` | 🔒 auth | update status |
| 104 | GET | `/api/warehouses` | 👑 admin | get all |
| 105 | POST | `/api/warehouses` | 👑 admin | create |
| 106 | GET | `/api/warehouses/inventory/product/{productId}` | 👑 admin | get product stock |
| 107 | GET | `/api/warehouses/transfers` | 👑 admin | list transfers |
| 108 | POST | `/api/warehouses/transfers` | 👑 admin | transfer |
| 109 | DELETE | `/api/warehouses/{id}` | 👑 admin | delete |
| 110 | GET | `/api/warehouses/{id}` | 👑 admin | get by id |
| 111 | PUT | `/api/warehouses/{id}` | 👑 admin | update |
| 112 | GET | `/api/warehouses/{id}/inventory` | 👑 admin | get inventory |
| 113 | PUT | `/api/warehouses/{id}/inventory` | 👑 admin | update inventory |
| 114 | GET | `/api/warehouses/{id}/low-stock` | 👑 admin | low stock |
| 115 | GET | `/api/admin/reports/inventory` | 👑 admin | inventory |
| 116 | GET | `/api/admin/reports/orders` | 👑 admin | orders |
| 117 | GET | `/api/admin/reports/revenue-by-brand` | 👑 admin | revenue by brand |
| 118 | GET | `/api/admin/reports/revenue-by-category` | 👑 admin | revenue by category |
| 119 | GET | `/api/admin/reports/revenue-by-vendor` | 👑 admin | revenue by vendor |
| 120 | GET | `/api/admin/reports/sales` | 👑 admin | sales |
| 121 | GET | `/api/admin/reports/top-customers` | 👑 admin | top customers |

**Total: 121 endpoints**

## Request / response JSON by module

### Authentication

| Method | Path | Access |
|--------|------|--------|
| PUT | `/api/auth/change-password` | 🔒 auth |
| POST | `/api/auth/forgot-password` | 🌐 public |
| POST | `/api/auth/login` | 🌐 public |
| POST | `/api/auth/logout` | 🔒 auth |
| GET | `/api/auth/me` | 🔒 auth |
| PUT | `/api/auth/me` | 🔒 auth |
| POST | `/api/auth/refresh-token` | 🌐 public |
| POST | `/api/auth/register` | 🌐 public |
| POST | `/api/auth/resend-otp` | 🌐 public |
| POST | `/api/auth/reset-password` | 🌐 public |
| POST | `/api/auth/verify-otp` | 🌐 public |

**`ChangePasswordRequest`** (request body)

```json
{
  "currentPassword": "Secret@123",
  "newPassword": "Secret@123"
}
```

**`EmailRequest`** (request body)

```json
{
  "email": "user@example.com"
}
```

**`LoginRequest`** (request body)

```json
{
  "email": "user@example.com",
  "password": "Secret@123"
}
```

**`OtpRequest`** (request body)

```json
{
  "email": "user@example.com",
  "code": "SAVE10"
}
```

**`RefreshTokenRequest`** (request body)

```json
{
  "refreshToken": "string"
}
```

**`RegisterRequest`** (request body)

```json
{
  "name": "Sample Name",
  "email": "user@example.com",
  "phone": "+91 90000 00000",
  "8": null,
  "password": null
}
```

**`ResetPasswordRequest`** (request body)

```json
{
  "email": "user@example.com",
  "code": "SAVE10",
  "newPassword": "Secret@123"
}
```

**`UpdateProfileRequest`** (request body)

```json
{
  "name": "Sample Name",
  "phone": "+91 90000 00000",
  "avatarUrl": "https://cdn.example.com/img.jpg"
}
```

**`AuthResponse`**

```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "tokenType": "PERCENTAGE",
  "user": {
    "id": 1,
    "name": "Sample Name",
    "email": "user@example.com",
    "phone": "+91 90000 00000",
    "avatarUrl": "https://cdn.example.com/img.jpg",
    "emailVerified": false,
    "roles": [
      "string"
    ]
  }
}
```

**`UserResponse`**

```json
{
  "id": 1,
  "name": "Sample Name",
  "email": "user@example.com",
  "phone": "+91 90000 00000",
  "avatarUrl": "https://cdn.example.com/img.jpg",
  "emailVerified": false,
  "roles": [
    "string"
  ]
}
```

### Categories

| Method | Path | Access |
|--------|------|--------|
| GET | `/api/categories` | 🔒 auth |
| POST | `/api/categories` | 🔒 auth |
| DELETE | `/api/categories/{id}` | 🔒 auth |
| GET | `/api/categories/{id}` | 🌐 public |
| PUT | `/api/categories/{id}` | 🔒 auth |

**`CategoryRequest`** (request body)

```json
{
  "name": "Sample Name",
  "description": "Lorem ipsum...",
  "bannerUrl": "https://cdn.example.com/img.jpg",
  "metaTitle": "Sample title",
  "metaDescription": "Lorem ipsum...",
  "active": true
}
```

**`CategoryResponse`**

```json
{
  "id": 1,
  "name": "Sample Name",
  "slug": "sample-slug",
  "description": "Lorem ipsum...",
  "bannerUrl": "https://cdn.example.com/img.jpg",
  "metaTitle": "Sample title",
  "metaDescription": "Lorem ipsum...",
  "active": true
}
```

### Brands

| Method | Path | Access |
|--------|------|--------|
| GET | `/api/brands` | 🔒 auth |
| POST | `/api/brands` | 🔒 auth |
| DELETE | `/api/brands/{id}` | 🔒 auth |
| GET | `/api/brands/{id}` | 🌐 public |
| PUT | `/api/brands/{id}` | 🔒 auth |

**`BrandRequest`** (request body)

```json
{
  "name": "Sample Name",
  "description": "Lorem ipsum...",
  "logoUrl": "https://cdn.example.com/img.jpg",
  "bannerUrl": "https://cdn.example.com/img.jpg",
  "metaTitle": "Sample title",
  "metaDescription": "Lorem ipsum...",
  "active": true
}
```

**`BrandResponse`**

```json
{
  "id": 1,
  "name": "Sample Name",
  "slug": "sample-slug",
  "description": "Lorem ipsum...",
  "logoUrl": "https://cdn.example.com/img.jpg",
  "bannerUrl": "https://cdn.example.com/img.jpg",
  "metaTitle": "Sample title",
  "metaDescription": "Lorem ipsum...",
  "active": true
}
```

### Products

| Method | Path | Access |
|--------|------|--------|
| GET | `/api/products` | 🔒 auth |
| POST | `/api/products` | 🔒 auth |
| GET | `/api/products/featured` | 🌐 public |
| GET | `/api/products/slug/{slug}` | 🌐 public |
| DELETE | `/api/products/{id}` | 🔒 auth |
| GET | `/api/products/{id}` | 🌐 public |
| PUT | `/api/products/{id}` | 🔒 auth |
| PATCH | `/api/products/{id}/stock` | 🔒 auth |

**`ProductRequest`** (request body)

```json
{
  "name": "Sample Name",
  "description": "Lorem ipsum...",
  "sku": "string",
  "price": 999.0,
  "salePrice": 999.0,
  "stock": 100,
  "categoryId": 1,
  "brandId": 1,
  "status": "ACTIVE",
  "featured": false,
  "metaTitle": "Sample title",
  "metaDescription": "Lorem ipsum...",
  "images": [
    {
      "id": 1,
      "url": "https://cdn.example.com/img.jpg",
      "alt": "string",
      "position": 1
    }
  ],
  "variants": [
    {
      "id": 1,
      "sku": "string",
      "size": "string",
      "color": "string",
      "price": 999.0,
      "stock": 100
    }
  ]
}
```

**`ProductResponse`**

```json
{
  "id": 1,
  "name": "Sample Name",
  "slug": "sample-slug",
  "description": "Lorem ipsum...",
  "sku": "string",
  "price": 999.0,
  "salePrice": 999.0,
  "stock": 100,
  "categoryId": 1,
  "categoryName": "Sample Name",
  "brandId": 1,
  "brandName": "Sample Name",
  "status": "ACTIVE",
  "featured": false,
  "ratingAverage": 4.5,
  "ratingCount": 5,
  "metaTitle": "Sample title",
  "metaDescription": "Lorem ipsum...",
  "images": [
    {
      "id": 1,
      "url": "https://cdn.example.com/img.jpg",
      "alt": "string",
      "position": 1
    }
  ],
  "variants": [
    {
      "id": 1,
      "sku": "string",
      "size": "string",
      "color": "string",
      "price": 999.0,
      "stock": 100
    }
  ]
}
```

**`ProductSummary`**

```json
{
  "id": 1,
  "name": "Sample Name",
  "slug": "sample-slug",
  "price": 999.0,
  "salePrice": 999.0,
  "thumbnail": "https://cdn.example.com/img.jpg",
  "brandName": "Sample Name",
  "ratingAverage": 4.5,
  "featured": false
}
```

### Cart

| Method | Path | Access |
|--------|------|--------|
| DELETE | `/api/cart` | 🔒 auth |
| GET | `/api/cart` | 🔒 auth |
| POST | `/api/cart/items` | 🔒 auth |
| DELETE | `/api/cart/items/{itemId}` | 🔒 auth |
| PUT | `/api/cart/items/{itemId}` | 🔒 auth |

**`AddToCartRequest`** (request body)

```json
{
  "productId": 1,
  "variantId": 1,
  "quantity": 2
}
```

**`UpdateCartItemRequest`** (request body)

```json
{
  "quantity": 2
}
```

**`CartItemResponse`**

```json
{
  "id": 1,
  "productId": 1,
  "variantId": 1,
  "name": "Sample Name",
  "thumbnail": "https://cdn.example.com/img.jpg",
  "quantity": 2,
  "unitPrice": 999.0,
  "lineTotal": 999.0
}
```

**`CartResponse`**

```json
{
  "id": 1,
  "items": [
    {
      "id": 1,
      "productId": 1,
      "variantId": 1,
      "name": "Sample Name",
      "thumbnail": "https://cdn.example.com/img.jpg",
      "quantity": 2,
      "unitPrice": 999.0,
      "lineTotal": 999.0
    }
  ],
  "totalItems": 1,
  "subtotal": 999.0
}
```

### Wishlist

| Method | Path | Access |
|--------|------|--------|
| DELETE | `/api/wishlist` | 🔒 auth |
| GET | `/api/wishlist` | 🔒 auth |
| DELETE | `/api/wishlist/{productId}` | 🔒 auth |
| POST | `/api/wishlist/{productId}` | 🔒 auth |
| POST | `/api/wishlist/{productId}/move-to-cart` | 🔒 auth |

**`WishlistItemResponse`**

```json
{
  "id": 1,
  "productId": 1,
  "name": "Sample Name",
  "slug": "sample-slug",
  "thumbnail": "https://cdn.example.com/img.jpg",
  "price": 999.0,
  "salePrice": 999.0,
  "inStock": false
}
```

### Orders

| Method | Path | Access |
|--------|------|--------|
| GET | `/api/orders` | 🔒 auth |
| POST | `/api/orders` | 🔒 auth |
| GET | `/api/orders/{id}` | 🔒 auth |
| POST | `/api/orders/{id}/cancel` | 🔒 auth |
| PATCH | `/api/orders/{id}/status` | 🔒 auth |
| GET | `/api/orders/{id}/track` | 🔒 auth |

**`CreateOrderRequest`** (request body)

```json
{
  "shippingAddress": {
    "name": "Sample Name",
    "phone": "+91 90000 00000",
    "line1": "string",
    "line2": "string",
    "city": "string",
    "state": "string",
    "postalCode": "SAVE10",
    "country": "string"
  },
  "paymentMethod": "string",
  "COD": null,
  "couponCode": null
}
```

**`OrderItemResponse`**

```json
{
  "productId": 1,
  "productName": "Sample Name",
  "thumbnail": "https://cdn.example.com/img.jpg",
  "quantity": 2,
  "unitPrice": 999.0,
  "lineTotal": 999.0
}
```

**`OrderResponse`**

```json
{
  "id": 1,
  "orderNumber": "string",
  "status": "ACTIVE",
  "paymentStatus": "ACTIVE",
  "paymentMethod": "string",
  "subtotal": 999.0,
  "shippingFee": 999.0,
  "discount": 999.0,
  "total": 999.0,
  "shippingAddress": {
    "name": "Sample Name",
    "phone": "+91 90000 00000",
    "line1": "string",
    "line2": "string",
    "city": "string",
    "state": "string",
    "postalCode": "SAVE10",
    "country": "string"
  },
  "items": [
    {
      "productId": 1,
      "productName": "Sample Name",
      "thumbnail": "https://cdn.example.com/img.jpg",
      "quantity": 2,
      "unitPrice": 999.0,
      "lineTotal": 999.0
    }
  ],
  "placedAt": "2026-06-10T12:00:00Z"
}
```

**`OrderSummary`**

```json
{
  "id": 1,
  "orderNumber": "string",
  "status": "ACTIVE",
  "total": 999.0,
  "itemCount": 1,
  "placedAt": "2026-06-10T12:00:00Z"
}
```

**`TrackingResponse`**

```json
{
  "orderNumber": "string",
  "currentStatus": "ACTIVE",
  "timeline": [
    null
  ]
}
```

### Payments

| Method | Path | Access |
|--------|------|--------|
| POST | `/api/payments/initiate` | 🔒 auth |
| GET | `/api/payments/order/{orderId}` | 🔒 auth |
| POST | `/api/payments/verify` | 🔒 auth |

**`CreatePaymentRequest`** (request body)

```json
{
  "orderId": 1
}
```

**`VerifyPaymentRequest`** (request body)

```json
{
  "orderId": 1,
  "gatewayRef": "string",
  "transactionId": "string",
  "signature": "string"
}
```

**`PaymentIntentResponse`**

```json
{
  "paymentId": 1,
  "orderId": 1,
  "provider": "string",
  "gatewayRef": "string",
  "amount": 999.0,
  "currency": "string",
  "status": "ACTIVE",
  "requiresGatewayCheckout": false
}
```

**`PaymentResponse`**

```json
{
  "id": 1,
  "orderId": 1,
  "method": "string",
  "provider": "string",
  "amount": 999.0,
  "status": "ACTIVE",
  "gatewayRef": "string",
  "transactionId": "string",
  "createdAt": "2026-06-10T12:00:00Z"
}
```

### Coupons

| Method | Path | Access |
|--------|------|--------|
| GET | `/api/coupons` | 🔒 auth |
| POST | `/api/coupons` | 🔒 auth |
| POST | `/api/coupons/validate` | 🔒 auth |
| DELETE | `/api/coupons/{id}` | 🔒 auth |
| GET | `/api/coupons/{id}` | 🔒 auth |
| PUT | `/api/coupons/{id}` | 🔒 auth |

**`CouponRequest`** (request body)

```json
{
  "code": "SAVE10",
  "description": "Lorem ipsum...",
  "discountType": "PERCENTAGE",
  "discountValue": null,
  "minOrderAmount": 999.0,
  "maxDiscountAmount": 999.0,
  "usageLimit": 10,
  "perUserLimit": 10,
  "startsAt": "2026-06-10T12:00:00Z",
  "expiresAt": "2026-06-10T12:00:00Z",
  "active": true
}
```

**`ValidateCouponRequest`** (request body)

```json
{
  "code": "SAVE10",
  "orderAmount": 999.0
}
```

**`CouponApplication`**

```json
{
  "couponId": 1,
  "code": "SAVE10",
  "discount": 999.0
}
```

**`CouponResponse`**

```json
{
  "id": 1,
  "code": "SAVE10",
  "description": "Lorem ipsum...",
  "discountType": "PERCENTAGE",
  "discountValue": 999.0,
  "minOrderAmount": 999.0,
  "maxDiscountAmount": 999.0,
  "usageLimit": 10,
  "usedCount": 1,
  "perUserLimit": 10,
  "startsAt": "2026-06-10T12:00:00Z",
  "expiresAt": "2026-06-10T12:00:00Z",
  "active": true
}
```

### Reviews

| Method | Path | Access |
|--------|------|--------|
| GET | `/api/products/{productId}/reviews` | 🌐 public |
| POST | `/api/products/{productId}/reviews` | 🔒 auth |
| GET | `/api/products/{productId}/reviews/summary` | 🌐 public |
| DELETE | `/api/reviews/{id}` | 🔒 auth |
| PUT | `/api/reviews/{id}` | 🔒 auth |
| PATCH | `/api/reviews/{id}/moderate` | 🔒 auth |

**`ReviewRequest`** (request body)

```json
{
  "rating": 5,
  "title": "Sample title",
  "comment": "Great product"
}
```

**`ReviewResponse`**

```json
{
  "id": 1,
  "productId": 1,
  "userName": "Sample Name",
  "rating": 5,
  "title": "Sample title",
  "comment": "Great product",
  "verifiedPurchase": false,
  "status": "ACTIVE",
  "createdAt": "2026-06-10T12:00:00Z"
}
```

**`ReviewSummary`**

```json
{
  "productId": 1,
  "averageRating": null,
  "totalReviews": 1
}
```

### Blog

| Method | Path | Access |
|--------|------|--------|
| GET | `/api/blogs` | 🌐 public |
| POST | `/api/blogs` | 🔒 auth |
| GET | `/api/blogs/admin/all` | 🔒 auth |
| GET | `/api/blogs/admin/{id}` | 🔒 auth |
| DELETE | `/api/blogs/{id}` | 🔒 auth |
| PUT | `/api/blogs/{id}` | 🔒 auth |
| GET | `/api/blogs/{slug}` | 🌐 public |

**`BlogPostRequest`** (request body)

```json
{
  "title": "Sample title",
  "excerpt": "Short summary",
  "content": "Full content...",
  "coverImage": "https://cdn.example.com/img.jpg",
  "status": "ACTIVE",
  "tags": null
}
```

**`BlogPostResponse`**

```json
{
  "id": 1,
  "title": "Sample title",
  "slug": "sample-slug",
  "excerpt": "Short summary",
  "content": "Full content...",
  "coverImage": "https://cdn.example.com/img.jpg",
  "authorId": 1,
  "status": "ACTIVE",
  "tags": [
    "string"
  ],
  "publishedAt": "2026-06-10T12:00:00Z",
  "createdAt": "2026-06-10T12:00:00Z"
}
```

**`BlogPostSummary`**

```json
{
  "id": 1,
  "title": "Sample title",
  "slug": "sample-slug",
  "excerpt": "Short summary",
  "coverImage": "https://cdn.example.com/img.jpg",
  "tags": [
    "string"
  ],
  "publishedAt": "2026-06-10T12:00:00Z"
}
```

### Notifications

| Method | Path | Access |
|--------|------|--------|
| GET | `/api/notifications` | 🔒 auth |
| PATCH | `/api/notifications/read-all` | 🔒 auth |
| POST | `/api/notifications/send` | 🔒 auth |
| GET | `/api/notifications/unread-count` | 🔒 auth |
| DELETE | `/api/notifications/{id}` | 🔒 auth |
| PATCH | `/api/notifications/{id}/read` | 🔒 auth |

**`SendNotificationRequest`** (request body)

```json
{
  "userId": 1,
  "link": null
}
```

**`NotificationResponse`**

```json
{
  "id": 1,
  "type": "PERCENTAGE",
  "title": "Sample title",
  "message": "Lorem ipsum...",
  "link": "string",
  "read": false,
  "createdAt": "2026-06-10T12:00:00Z"
}
```

### Analytics

| Method | Path | Access |
|--------|------|--------|
| GET | `/api/admin/analytics/dashboard` | 👑 admin |
| GET | `/api/admin/analytics/low-stock` | 👑 admin |
| GET | `/api/admin/analytics/order-status` | 👑 admin |
| GET | `/api/admin/analytics/sales` | 👑 admin |
| GET | `/api/admin/analytics/top-products` | 👑 admin |

**`DashboardResponse`**

```json
{
  "totalRevenue": 999.0,
  "totalOrders": 1,
  "pendingOrders": 1,
  "totalCustomers": 1,
  "totalProducts": 1,
  "lowStockCount": 1
}
```

**`StatusCount`**

```json
{
  "status": "ACTIVE",
  "count": 1
}
```

### Search

| Method | Path | Access |
|--------|------|--------|
| GET | `/api/search` | 🌐 public |
| DELETE | `/api/search/history` | 🔒 auth |
| GET | `/api/search/history` | 🔒 auth |
| GET | `/api/search/suggest` | 🌐 public |
| GET | `/api/search/trending` | 🌐 public |

**`FacetCount`**

```json
{
  "name": "Sample Name",
  "count": 1
}
```

**`SearchResultResponse`**

```json
{
  "query": "shoes",
  "results": {
    "content": [
      {
        "id": 1,
        "name": "Sample Name",
        "slug": "sample-slug",
        "price": 999.0,
        "salePrice": 999.0,
        "thumbnail": "https://cdn.example.com/img.jpg",
        "brandName": "Sample Name",
        "ratingAverage": 4.5,
        "featured": false
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1,
    "last": true
  },
  "categoryFacets": [
    {
      "name": "Sample Name",
      "count": 1
    }
  ],
  "brandFacets": [
    {
      "name": "Sample Name",
      "count": 1
    }
  ],
  "suggestions": [
    "string"
  ]
}
```

### Recommendations

| Method | Path | Access |
|--------|------|--------|
| GET | `/api/products/{id}/frequently-bought-together` | 🌐 public |
| GET | `/api/products/{id}/similar` | 🌐 public |
| POST | `/api/products/{id}/view` | 🌐 public |
| GET | `/api/recommendations/for-you` | 🔒 auth |
| GET | `/api/recommendations/recently-viewed` | 🔒 auth |
| GET | `/api/recommendations/trending` | 🌐 public |

### Vendors

| Method | Path | Access |
|--------|------|--------|
| GET | `/api/vendors` | 🔒 auth |
| GET | `/api/vendors/me` | 🔒 auth |
| PUT | `/api/vendors/me` | 🔒 auth |
| GET | `/api/vendors/me/earnings` | 🔒 auth |
| GET | `/api/vendors/me/orders` | 🔒 auth |
| GET | `/api/vendors/me/payouts` | 🔒 auth |
| GET | `/api/vendors/me/products` | 🔒 auth |
| PATCH | `/api/vendors/payouts/{payoutId}/paid` | 🔒 auth |
| POST | `/api/vendors/register` | 🔒 auth |
| GET | `/api/vendors/store/{slug}` | 🌐 public |
| GET | `/api/vendors/{id}` | 🔒 auth |
| GET | `/api/vendors/{id}/payouts` | 🔒 auth |
| POST | `/api/vendors/{id}/payouts` | 🔒 auth |
| PATCH | `/api/vendors/{id}/status` | 🔒 auth |

**`PayoutRequest`** (request body)

```json
{
  "amount": 999.0,
  "periodStart": "2026-06-10T12:00:00Z",
  "periodEnd": "2026-06-10T12:00:00Z",
  "note": "note"
}
```

**`VendorRegistrationRequest`** (request body)

```json
{
  "storeName": "Sample Name",
  "description": "Lorem ipsum...",
  "logoUrl": "https://cdn.example.com/img.jpg",
  "contactEmail": "user@example.com",
  "contactPhone": "+91 90000 00000"
}
```

**`VendorStatusUpdateRequest`** (request body)

```json
{
  "status": "ACTIVE",
  "override": null
}
```

**`VendorUpdateRequest`** (request body)

```json
{
  "storeName": "Sample Name",
  "description": "Lorem ipsum...",
  "logoUrl": "https://cdn.example.com/img.jpg",
  "contactEmail": "user@example.com",
  "contactPhone": "+91 90000 00000"
}
```

**`PayoutResponse`**

```json
{
  "id": 1,
  "vendorId": 1,
  "amount": 999.0,
  "status": "ACTIVE",
  "periodStart": "2026-06-10T12:00:00Z",
  "periodEnd": "2026-06-10T12:00:00Z",
  "paidAt": "2026-06-10T12:00:00Z",
  "note": "note",
  "createdAt": "2026-06-10T12:00:00Z"
}
```

**`VendorEarningsResponse`**

```json
{
  "vendorId": 1,
  "grossSales": 999.0,
  "commissionRate": 10.0,
  "commission": 999.0,
  "netEarnings": 999.0,
  "totalPaidOut": 999.0,
  "pendingBalance": 999.0
}
```

**`VendorOrderItemResponse`**

```json
{
  "orderItemId": 1,
  "orderId": 1,
  "orderNumber": "string",
  "productId": 1,
  "productName": "Sample Name",
  "quantity": 2,
  "unitPrice": 999.0,
  "lineTotal": 999.0,
  "orderStatus": "ACTIVE"
}
```

**`VendorResponse`**

```json
{
  "id": 1,
  "userId": 1,
  "storeName": "Sample Name",
  "slug": "sample-slug",
  "description": "Lorem ipsum...",
  "logoUrl": "https://cdn.example.com/img.jpg",
  "status": "ACTIVE",
  "commissionRate": 10.0,
  "contactEmail": "user@example.com",
  "contactPhone": "+91 90000 00000"
}
```

### Warehouses

| Method | Path | Access |
|--------|------|--------|
| GET | `/api/warehouses` | 👑 admin |
| POST | `/api/warehouses` | 👑 admin |
| GET | `/api/warehouses/inventory/product/{productId}` | 👑 admin |
| GET | `/api/warehouses/transfers` | 👑 admin |
| POST | `/api/warehouses/transfers` | 👑 admin |
| DELETE | `/api/warehouses/{id}` | 👑 admin |
| GET | `/api/warehouses/{id}` | 👑 admin |
| PUT | `/api/warehouses/{id}` | 👑 admin |
| GET | `/api/warehouses/{id}/inventory` | 👑 admin |
| PUT | `/api/warehouses/{id}/inventory` | 👑 admin |
| GET | `/api/warehouses/{id}/low-stock` | 👑 admin |

**`InventoryUpdateRequest`** (request body)

```json
{
  "productId": 1,
  "quantity": 2,
  "(delta)": null
}
```

**`StockTransferRequest`** (request body)

```json
{
  "fromWarehouseId": 1,
  "toWarehouseId": 1,
  "productId": 1,
  "quantity": 2,
  "note": "note"
}
```

**`WarehouseRequest`** (request body)

```json
{
  "name": "Sample Name",
  "code": "SAVE10",
  "addressLine": "string",
  "city": "string",
  "state": "string",
  "country": "string",
  "postalCode": "SAVE10",
  "active": true
}
```

**`InventoryResponse`**

```json
{
  "warehouseId": 1,
  "warehouseName": "Sample Name",
  "productId": 1,
  "quantity": 2,
  "reserved": 1,
  "available": 1
}
```

**`ProductStockResponse`**

```json
{
  "productId": 1,
  "totalQuantity": 2,
  "byWarehouse": [
    {
      "warehouseId": 1,
      "warehouseName": "Sample Name",
      "productId": 1,
      "quantity": 2,
      "reserved": 1,
      "available": 1
    }
  ]
}
```

**`StockTransferResponse`**

```json
{
  "id": 1,
  "fromWarehouseId": 1,
  "toWarehouseId": 1,
  "productId": 1,
  "quantity": 2,
  "status": "ACTIVE",
  "note": "note",
  "createdAt": "2026-06-10T12:00:00Z"
}
```

**`WarehouseResponse`**

```json
{
  "id": 1,
  "name": "Sample Name",
  "code": "SAVE10",
  "addressLine": "string",
  "city": "string",
  "state": "string",
  "country": "string",
  "postalCode": "SAVE10",
  "active": true
}
```

### Reports

| Method | Path | Access |
|--------|------|--------|
| GET | `/api/admin/reports/inventory` | 👑 admin |
| GET | `/api/admin/reports/orders` | 👑 admin |
| GET | `/api/admin/reports/revenue-by-brand` | 👑 admin |
| GET | `/api/admin/reports/revenue-by-category` | 👑 admin |
| GET | `/api/admin/reports/revenue-by-vendor` | 👑 admin |
| GET | `/api/admin/reports/sales` | 👑 admin |
| GET | `/api/admin/reports/top-customers` | 👑 admin |

**`CustomerRow`**

```json
{
  "userId": 1,
  "name": "Sample Name",
  "orders": 1,
  "totalSpent": 999.0
}
```

**`InventoryReportResponse`**

```json
{
  "totalProducts": 1,
  "totalStockValue": 999.0,
  "lowStockCount": 1,
  "lowStock": [
    {
      "id": 1,
      "name": "Sample Name",
      "stock": 100
    }
  ]
}
```

**`LowStockRow`**

```json
{
  "id": 1,
  "name": "Sample Name",
  "stock": 100
}
```

**`OrderReportResponse`**

```json
{
  "from": "2026-06-10T12:00:00Z",
  "to": "2026-06-10T12:00:00Z",
  "totalOrders": 1,
  "byStatus": [
    {
      "status": "ACTIVE",
      "count": 1
    }
  ]
}
```

**`RevenueRow`**

```json
{
  "label": "string",
  "revenue": 999.0,
  "unitsSold": 1
}
```

**`SalesReportResponse`**

```json
{
  "from": "2026-06-10T12:00:00Z",
  "to": "2026-06-10T12:00:00Z",
  "totalRevenue": 999.0,
  "totalOrders": 1,
  "daily": [
    {
      "date": "2026-06-10",
      "revenue": 999.0,
      "orders": 1
    }
  ]
}
```

**`SalesReportRow`**

```json
{
  "date": "2026-06-10",
  "revenue": 999.0,
  "orders": 1
}
```

**`StatusRow`**

```json
{
  "status": "ACTIVE",
  "count": 1
}
```

## SQL — schema

### Phase 1 — core schema  (`db/schema.sql`)

```sql
-- ============================================================================
-- ShopMart — PostgreSQL schema (Phase 1 MVP)
-- Matches the JPA entities in com.shopmart.module.*
--
-- Create the database first (run once, outside this script):
--     CREATE DATABASE shopmart;
-- Then apply this file:
--     psql -d shopmart -f schema.sql
--
-- Money is NUMERIC(12,2); audit timestamps are TIMESTAMPTZ (entities use Instant).
-- Primary keys use identity columns (Hibernate GenerationType.IDENTITY on Postgres).
--
-- If you keep this as the source of truth, set in application.yml:
--     spring.jpa.hibernate.ddl-auto: validate   # or: none
-- so Hibernate checks against this schema instead of mutating it.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Users, roles, addresses
-- ----------------------------------------------------------------------------
CREATE TABLE users (
    id              BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    email           VARCHAR(255) NOT NULL,
    phone           VARCHAR(255),
    password_hash   VARCHAR(255) NOT NULL,
    avatar_url      VARCHAR(255),
    email_verified  BOOLEAN      NOT NULL DEFAULT FALSE,
    enabled         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ  DEFAULT now(),
    updated_at      TIMESTAMPTZ,
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE user_roles (
    user_id  BIGINT       NOT NULL,
    role     VARCHAR(255) NOT NULL,
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uk_user_roles UNIQUE (user_id, role)
);

CREATE TABLE addresses (
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id     BIGINT      NOT NULL,
    label       VARCHAR(255),
    full_name   VARCHAR(255),
    phone       VARCHAR(255),
    line1       VARCHAR(255),
    line2       VARCHAR(255),
    city        VARCHAR(255),
    state       VARCHAR(255),
    postal_code VARCHAR(255),
    country     VARCHAR(255),
    is_default  BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ DEFAULT now(),
    updated_at  TIMESTAMPTZ,
    CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE INDEX idx_addresses_user ON addresses (user_id);

-- ----------------------------------------------------------------------------
-- Auth: refresh tokens, OTPs
-- ----------------------------------------------------------------------------
CREATE TABLE refresh_tokens (
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    token       VARCHAR(512) NOT NULL,
    expires_at  TIMESTAMPTZ  NOT NULL,
    revoked     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ  DEFAULT now(),
    updated_at  TIMESTAMPTZ,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE INDEX idx_refresh_token ON refresh_tokens (token);
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens (user_id);

CREATE TABLE otp_verifications (
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email       VARCHAR(255) NOT NULL,
    code        VARCHAR(255) NOT NULL,
    purpose     VARCHAR(255) NOT NULL,   -- EMAIL_VERIFICATION | PASSWORD_RESET
    expires_at  TIMESTAMPTZ  NOT NULL,
    used        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ  DEFAULT now(),
    updated_at  TIMESTAMPTZ
);
CREATE INDEX idx_otp_email_purpose ON otp_verifications (email, purpose);

-- ----------------------------------------------------------------------------
-- Catalog: categories, brands, products (+ images, variants)
-- ----------------------------------------------------------------------------
CREATE TABLE categories (
    id               BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name             VARCHAR(255)  NOT NULL,
    slug             VARCHAR(255)  NOT NULL,
    description      VARCHAR(1000),
    banner_url       VARCHAR(255),
    meta_title       VARCHAR(255),
    meta_description VARCHAR(255),
    active           BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMPTZ   DEFAULT now(),
    updated_at       TIMESTAMPTZ,
    CONSTRAINT uk_categories_slug UNIQUE (slug)
);

CREATE TABLE brands (
    id               BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name             VARCHAR(255)  NOT NULL,
    slug             VARCHAR(255)  NOT NULL,
    description      VARCHAR(1000),
    logo_url         VARCHAR(255),
    banner_url       VARCHAR(255),
    meta_title       VARCHAR(255),
    meta_description VARCHAR(255),
    active           BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMPTZ   DEFAULT now(),
    updated_at       TIMESTAMPTZ,
    CONSTRAINT uk_brands_slug UNIQUE (slug)
);

CREATE TABLE products (
    id               BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name             VARCHAR(255)   NOT NULL,
    slug             VARCHAR(255)   NOT NULL,
    description      VARCHAR(5000),
    sku              VARCHAR(255),
    price            NUMERIC(12,2)  NOT NULL,
    sale_price       NUMERIC(12,2),
    stock            INTEGER        NOT NULL DEFAULT 0,
    category_id      BIGINT,
    brand_id         BIGINT,
    status           VARCHAR(255)   NOT NULL DEFAULT 'DRAFT',  -- DRAFT|ACTIVE|ARCHIVED|OUT_OF_STOCK
    featured         BOOLEAN        NOT NULL DEFAULT FALSE,
    rating_average   NUMERIC(3,2)   DEFAULT 0,
    rating_count     INTEGER        NOT NULL DEFAULT 0,
    meta_title       VARCHAR(255),
    meta_description VARCHAR(255),
    created_at       TIMESTAMPTZ    DEFAULT now(),
    updated_at       TIMESTAMPTZ,
    CONSTRAINT uk_products_slug UNIQUE (slug),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE SET NULL,
    CONSTRAINT fk_products_brand    FOREIGN KEY (brand_id)    REFERENCES brands (id)     ON DELETE SET NULL
);
CREATE INDEX idx_product_status   ON products (status);
CREATE INDEX idx_products_category ON products (category_id);
CREATE INDEX idx_products_brand    ON products (brand_id);
CREATE INDEX idx_products_featured ON products (featured);

CREATE TABLE product_images (
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    product_id  BIGINT       NOT NULL,
    url         VARCHAR(255) NOT NULL,
    alt         VARCHAR(255),
    position    INTEGER      NOT NULL DEFAULT 0,
    CONSTRAINT fk_product_images_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);
CREATE INDEX idx_product_images_product ON product_images (product_id);

CREATE TABLE product_variants (
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    product_id  BIGINT        NOT NULL,
    sku         VARCHAR(255),
    size        VARCHAR(255),
    color       VARCHAR(255),
    price       NUMERIC(12,2),
    stock       INTEGER       NOT NULL DEFAULT 0,
    CONSTRAINT fk_product_variants_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);
CREATE INDEX idx_product_variants_product ON product_variants (product_id);

-- ----------------------------------------------------------------------------
-- Cart
-- ----------------------------------------------------------------------------
CREATE TABLE carts (
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id     BIGINT      NOT NULL,
    created_at  TIMESTAMPTZ DEFAULT now(),
    updated_at  TIMESTAMPTZ,
    CONSTRAINT uk_carts_user UNIQUE (user_id),
    CONSTRAINT fk_carts_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE cart_items (
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    cart_id     BIGINT        NOT NULL,
    product_id  BIGINT        NOT NULL,
    variant_id  BIGINT,
    quantity    INTEGER       NOT NULL,
    unit_price  NUMERIC(12,2) NOT NULL,
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts (id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);
CREATE INDEX idx_cart_items_cart ON cart_items (cart_id);

-- ----------------------------------------------------------------------------
-- Wishlist
-- ----------------------------------------------------------------------------
CREATE TABLE wishlist_items (
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id     BIGINT      NOT NULL,
    product_id  BIGINT      NOT NULL,
    created_at  TIMESTAMPTZ DEFAULT now(),
    updated_at  TIMESTAMPTZ,
    CONSTRAINT uk_wishlist_user_product UNIQUE (user_id, product_id),
    CONSTRAINT fk_wishlist_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_wishlist_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);
CREATE INDEX idx_wishlist_user ON wishlist_items (user_id);

-- ----------------------------------------------------------------------------
-- Orders (+ items). Order items snapshot product name/price, so there is no
-- hard FK from order_items.product_id to products — products may be deleted
-- without erasing order history. Uncomment the constraint if you prefer strict
-- referential integrity over the snapshot model.
-- ----------------------------------------------------------------------------
CREATE TABLE orders (
    id              BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    order_number    VARCHAR(255)  NOT NULL,
    user_id         BIGINT        NOT NULL,
    status          VARCHAR(255)  NOT NULL DEFAULT 'PENDING',  -- PENDING|CONFIRMED|PACKED|SHIPPED|DELIVERED|CANCELLED|RETURNED
    payment_status  VARCHAR(255)  NOT NULL DEFAULT 'PENDING',  -- PENDING|PAID|FAILED|REFUNDED
    payment_method  VARCHAR(255),
    subtotal        NUMERIC(12,2) NOT NULL DEFAULT 0,
    shipping_fee    NUMERIC(12,2) NOT NULL DEFAULT 0,
    discount        NUMERIC(12,2) NOT NULL DEFAULT 0,
    total           NUMERIC(12,2) NOT NULL DEFAULT 0,
    ship_name        VARCHAR(255),
    ship_phone       VARCHAR(255),
    ship_line1       VARCHAR(255),
    ship_line2       VARCHAR(255),
    ship_city        VARCHAR(255),
    ship_state       VARCHAR(255),
    ship_postal_code VARCHAR(255),
    ship_country     VARCHAR(255),
    created_at      TIMESTAMPTZ   DEFAULT now(),
    updated_at      TIMESTAMPTZ,
    CONSTRAINT uk_orders_number UNIQUE (order_number),
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users (id)
);
CREATE INDEX idx_orders_user ON orders (user_id);
CREATE INDEX idx_orders_status ON orders (status);

CREATE TABLE order_items (
    id            BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    order_id      BIGINT        NOT NULL,
    product_id    BIGINT        NOT NULL,
    product_name  VARCHAR(255)  NOT NULL,
    thumbnail     VARCHAR(255),
    quantity      INTEGER       NOT NULL,
    unit_price    NUMERIC(12,2) NOT NULL,
    line_total    NUMERIC(12,2) NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
    -- , CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products (id)
);
CREATE INDEX idx_order_items_order ON order_items (order_id);

-- ----------------------------------------------------------------------------
-- Payments
-- ----------------------------------------------------------------------------
CREATE TABLE payments (
    id              BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    order_id        BIGINT        NOT NULL,
    method          VARCHAR(255)  NOT NULL,                  -- COD|UPI|CARD|NETBANKING|WALLET
    amount          NUMERIC(12,2) NOT NULL,
    status          VARCHAR(255)  NOT NULL DEFAULT 'CREATED', -- CREATED|PENDING|SUCCESS|FAILED|REFUNDED
    provider        VARCHAR(255),
    gateway_ref     VARCHAR(255),
    transaction_id  VARCHAR(255),
    failure_reason  VARCHAR(255),
    created_at      TIMESTAMPTZ   DEFAULT now(),
    updated_at      TIMESTAMPTZ,
    CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
);
CREATE INDEX idx_payments_order ON payments (order_id);
CREATE INDEX idx_payments_gateway_ref ON payments (gateway_ref);
```

### Phase 2 — coupons / reviews / blogs / notifications  (`db/phase2.sql`)

```sql
-- ============================================================================
-- ShopMart — Phase 2 schema additions
-- New tables for coupons, reviews, blogs, and in-app notifications.
-- Apply AFTER schema.sql (depends on users / products / orders):
--     psql -d shopmart -f db/phase2.sql
-- Conventions match schema.sql: identity PKs, TIMESTAMPTZ audit columns,
-- NUMERIC(12,2) money.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Coupons + redemptions
-- ----------------------------------------------------------------------------
CREATE TABLE coupons (
    id                  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    code                VARCHAR(255)  NOT NULL,
    description         VARCHAR(255),
    discount_type       VARCHAR(255)  NOT NULL,                 -- PERCENTAGE | FIXED
    discount_value      NUMERIC(12,2) NOT NULL,
    min_order_amount    NUMERIC(12,2),
    max_discount_amount NUMERIC(12,2),
    usage_limit         INTEGER,
    used_count          INTEGER       NOT NULL DEFAULT 0,
    per_user_limit      INTEGER,
    starts_at           TIMESTAMPTZ,
    expires_at          TIMESTAMPTZ,
    active              BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ   DEFAULT now(),
    updated_at          TIMESTAMPTZ,
    CONSTRAINT uk_coupons_code UNIQUE (code)
);

CREATE TABLE coupon_redemptions (
    id               BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    coupon_id        BIGINT        NOT NULL,
    user_id          BIGINT        NOT NULL,
    order_id         BIGINT,
    discount_applied NUMERIC(12,2) NOT NULL,
    created_at       TIMESTAMPTZ   DEFAULT now(),
    updated_at       TIMESTAMPTZ,
    CONSTRAINT fk_redemption_coupon FOREIGN KEY (coupon_id) REFERENCES coupons (id) ON DELETE CASCADE,
    CONSTRAINT fk_redemption_user   FOREIGN KEY (user_id)   REFERENCES users (id)   ON DELETE CASCADE,
    CONSTRAINT fk_redemption_order  FOREIGN KEY (order_id)  REFERENCES orders (id)  ON DELETE SET NULL
);
CREATE INDEX idx_redemption_coupon_user ON coupon_redemptions (coupon_id, user_id);

-- ----------------------------------------------------------------------------
-- Reviews (one per user per product; rating drives products.rating_average)
-- ----------------------------------------------------------------------------
CREATE TABLE reviews (
    id                BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    product_id        BIGINT        NOT NULL,
    user_id           BIGINT        NOT NULL,
    rating            INTEGER       NOT NULL,
    title             VARCHAR(255),
    comment           VARCHAR(2000),
    status            VARCHAR(255)  NOT NULL DEFAULT 'APPROVED',  -- PENDING | APPROVED | REJECTED
    verified_purchase BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMPTZ   DEFAULT now(),
    updated_at        TIMESTAMPTZ,
    CONSTRAINT uk_review_product_user UNIQUE (product_id, user_id),
    CONSTRAINT ck_review_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT fk_reviews_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_user    FOREIGN KEY (user_id)    REFERENCES users (id)    ON DELETE CASCADE
);
CREATE INDEX idx_reviews_product ON reviews (product_id);

-- ----------------------------------------------------------------------------
-- Blog posts (+ tags element collection)
-- ----------------------------------------------------------------------------
CREATE TABLE blog_posts (
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    slug         VARCHAR(255) NOT NULL,
    excerpt      VARCHAR(500),
    content      TEXT,
    cover_image  VARCHAR(255),
    author_id    BIGINT,
    status       VARCHAR(255) NOT NULL DEFAULT 'DRAFT',   -- DRAFT | PUBLISHED
    published_at TIMESTAMPTZ,
    created_at   TIMESTAMPTZ  DEFAULT now(),
    updated_at   TIMESTAMPTZ,
    CONSTRAINT uk_blog_posts_slug UNIQUE (slug),
    CONSTRAINT fk_blog_posts_author FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE SET NULL
);
CREATE INDEX idx_blog_posts_status ON blog_posts (status);

CREATE TABLE blog_post_tags (
    post_id BIGINT       NOT NULL,
    tag     VARCHAR(255),
    CONSTRAINT fk_blog_post_tags_post FOREIGN KEY (post_id) REFERENCES blog_posts (id) ON DELETE CASCADE
);
CREATE INDEX idx_blog_post_tags_post ON blog_post_tags (post_id);

-- ----------------------------------------------------------------------------
-- In-app notifications
-- ----------------------------------------------------------------------------
CREATE TABLE notifications (
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id     BIGINT        NOT NULL,
    type        VARCHAR(255)  NOT NULL DEFAULT 'SYSTEM',   -- ORDER|PAYMENT|PROMO|REVIEW|SYSTEM
    title       VARCHAR(255)  NOT NULL,
    message     VARCHAR(1000),
    link        VARCHAR(255),
    is_read     BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ   DEFAULT now(),
    updated_at  TIMESTAMPTZ,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE INDEX idx_notifications_user ON notifications (user_id);
```

### Phase 3 — vendors / warehouses / search / views  (`db/phase3.sql`)

```sql
-- ============================================================================
-- ShopMart — Phase 3 schema additions
-- Vendors, multi-warehouse inventory, search logs, and product-view tracking.
-- Apply AFTER schema.sql and phase2.sql (depends on users / products / orders):
--     psql -d shopmart -f db/phase3.sql
-- Conventions match earlier files: identity PKs, TIMESTAMPTZ audit columns,
-- NUMERIC(12,2) money, NUMERIC(5,2) percentages.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Vendors + payouts
-- ----------------------------------------------------------------------------
CREATE TABLE vendors (
    id              BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id         BIGINT        NOT NULL,
    store_name      VARCHAR(255)  NOT NULL,
    slug            VARCHAR(255)  NOT NULL,
    description     VARCHAR(2000),
    logo_url        VARCHAR(255),
    status          VARCHAR(255)  NOT NULL DEFAULT 'PENDING',  -- PENDING|APPROVED|SUSPENDED|REJECTED
    commission_rate NUMERIC(5,2)  NOT NULL DEFAULT 10.00,
    contact_email   VARCHAR(255),
    contact_phone   VARCHAR(255),
    created_at      TIMESTAMPTZ   DEFAULT now(),
    updated_at      TIMESTAMPTZ,
    CONSTRAINT uk_vendors_user UNIQUE (user_id),
    CONSTRAINT uk_vendors_slug UNIQUE (slug),
    CONSTRAINT fk_vendors_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE vendor_payouts (
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    vendor_id    BIGINT        NOT NULL,
    amount       NUMERIC(12,2) NOT NULL,
    status       VARCHAR(255)  NOT NULL DEFAULT 'PENDING',     -- PENDING | PAID
    period_start TIMESTAMPTZ,
    period_end   TIMESTAMPTZ,
    paid_at      TIMESTAMPTZ,
    note         VARCHAR(255),
    created_at   TIMESTAMPTZ   DEFAULT now(),
    updated_at   TIMESTAMPTZ,
    CONSTRAINT fk_payouts_vendor FOREIGN KEY (vendor_id) REFERENCES vendors (id) ON DELETE CASCADE
);
CREATE INDEX idx_payouts_vendor ON vendor_payouts (vendor_id);

-- ----------------------------------------------------------------------------
-- Warehouses + per-warehouse inventory + transfers
-- ----------------------------------------------------------------------------
CREATE TABLE warehouses (
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    code         VARCHAR(255) NOT NULL,
    address_line VARCHAR(255),
    city         VARCHAR(255),
    state        VARCHAR(255),
    country      VARCHAR(255),
    postal_code  VARCHAR(255),
    active       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMPTZ  DEFAULT now(),
    updated_at   TIMESTAMPTZ,
    CONSTRAINT uk_warehouses_code UNIQUE (code)
);

CREATE TABLE warehouse_inventory (
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    warehouse_id BIGINT  NOT NULL,
    product_id   BIGINT  NOT NULL,
    quantity     INTEGER NOT NULL DEFAULT 0,
    reserved     INTEGER NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ DEFAULT now(),
    updated_at   TIMESTAMPTZ,
    CONSTRAINT uk_wh_inventory UNIQUE (warehouse_id, product_id),
    CONSTRAINT fk_wh_inventory_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses (id) ON DELETE CASCADE,
    CONSTRAINT fk_wh_inventory_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);
CREATE INDEX idx_wh_inventory_product ON warehouse_inventory (product_id);

CREATE TABLE stock_transfers (
    id                BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    from_warehouse_id BIGINT  NOT NULL,
    to_warehouse_id   BIGINT  NOT NULL,
    product_id        BIGINT  NOT NULL,
    quantity          INTEGER NOT NULL,
    status            VARCHAR(255) NOT NULL DEFAULT 'COMPLETED', -- PENDING|COMPLETED|CANCELLED
    note              VARCHAR(255),
    created_at        TIMESTAMPTZ DEFAULT now(),
    updated_at        TIMESTAMPTZ,
    CONSTRAINT fk_transfer_from FOREIGN KEY (from_warehouse_id) REFERENCES warehouses (id),
    CONSTRAINT fk_transfer_to   FOREIGN KEY (to_warehouse_id)   REFERENCES warehouses (id)
);
CREATE INDEX idx_stock_transfers_product ON stock_transfers (product_id);

-- ----------------------------------------------------------------------------
-- Search logs (history + trending)
-- ----------------------------------------------------------------------------
CREATE TABLE search_logs (
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id      BIGINT,
    query_text   VARCHAR(255) NOT NULL,
    result_count BIGINT       NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  DEFAULT now(),
    updated_at   TIMESTAMPTZ,
    CONSTRAINT fk_search_logs_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL
);
CREATE INDEX idx_search_logs_user ON search_logs (user_id);

-- ----------------------------------------------------------------------------
-- Product views (recently viewed + personalization signal)
-- ----------------------------------------------------------------------------
CREATE TABLE product_views (
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id    BIGINT,
    product_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ,
    CONSTRAINT fk_product_views_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT fk_product_views_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);
CREATE INDEX idx_product_views_user ON product_views (user_id, created_at);

-- ----------------------------------------------------------------------------
-- Vendor attribution columns on existing tables
-- products.vendor_id  -> owning vendor (null = first-party / platform product)
-- order_items.vendor_id -> snapshot at order time (no FK, mirrors product_id snapshot)
-- ----------------------------------------------------------------------------
ALTER TABLE products ADD COLUMN vendor_id BIGINT;
ALTER TABLE products ADD CONSTRAINT fk_products_vendor
    FOREIGN KEY (vendor_id) REFERENCES vendors (id) ON DELETE SET NULL;
CREATE INDEX idx_products_vendor ON products (vendor_id);

ALTER TABLE order_items ADD COLUMN vendor_id BIGINT;
CREATE INDEX idx_order_items_vendor ON order_items (vendor_id);
```

## SQL — notable JPQL queries (from repositories)

These run against the entities above; Hibernate translates them to SQL.

**RefreshTokenRepository**
```sql
update RefreshToken r set r.revoked = true where r.userId = :userId
```

**NotificationRepository**
```sql
update Notification n set n.read = true where n.userId = :userId and n.read = false
```

**OrderItemRepository**
```sql
select case when count(oi) > 0 then true else false end from OrderItem oi where oi.order.userId = :userId and oi.productId = :productId
select oi.productId as productId, oi.productName as productName, sum(oi.quantity) as unitsSold, sum(oi.lineTotal) as revenue from OrderItem oi where oi.order.status <> com.shopmart.module.order.entity.OrderStatus.CANCELLED group by oi.productId, oi.productName order by sum(oi.quantity) desc
select oi.productId, count(oi) from OrderItem oi where oi.order.id in (select oi2.order.id from OrderItem oi2 where oi2.productId = :productId) and oi.productId <> :productId group by oi.productId order by count(oi) desc
select coalesce(sum(oi.lineTotal), 0) from OrderItem oi where oi.vendorId = :vendorId and oi.order.paymentStatus = com.shopmart.module.order.entity.PaymentStatus.PAID
select c.name, sum(oi.lineTotal), sum(oi.quantity) from OrderItem oi join Product p on p.id = oi.productId join p.category c where oi.order.status <> com.shopmart.module.order.entity.OrderStatus.CANCELLED and oi.order.createdAt between :from and :to group by c.name order by sum(oi.lineTotal) desc
select b.name, sum(oi.lineTotal), sum(oi.quantity) from OrderItem oi join Product p on p.id = oi.productId join p.brand b where oi.order.status <> com.shopmart.module.order.entity.OrderStatus.CANCELLED and oi.order.createdAt between :from and :to group by b.name order by sum(oi.lineTotal) desc
select oi.vendorId, sum(oi.lineTotal), sum(oi.quantity) from OrderItem oi where oi.vendorId is not null and oi.order.status <> com.shopmart.module.order.entity.OrderStatus.CANCELLED and oi.order.createdAt between :from and :to group by oi.vendorId order by sum(oi.lineTotal) desc
```

**OrderRepository**
```sql
select o.userId, count(o), sum(o.total) from Order o where o.paymentStatus = com.shopmart.module.order.entity.PaymentStatus.PAID and o.createdAt between :from and :to group by o.userId order by sum(o.total) desc
select o.status, count(o) from Order o group by o.status
```

**ProductRepository**
```sql
select p from Product p where p.status = com.shopmart.module.product.entity.ProductStatus.ACTIVE and (:q = '' or lower(p.name) like lower(concat('%', :q, '%')) or lower(p.description) like lower(concat('%', :q, '%'))) and (:categoryId is null or p.category.id = :categoryId) and (:brandId is null or p.brand.id = :brandId) order by p.featured desc, p.ratingAverage desc
select c.name, count(p) from Product p join p.category c where p.status = com.shopmart.module.product.entity.ProductStatus.ACTIVE and (:q = '' or lower(p.name) like lower(concat('%', :q, '%')) or lower(p.description) like lower(concat('%', :q, '%'))) group by c.name order by count(p) desc
select b.name, count(p) from Product p join p.brand b where p.status = com.shopmart.module.product.entity.ProductStatus.ACTIVE and (:q = '' or lower(p.name) like lower(concat('%', :q, '%')) or lower(p.description) like lower(concat('%', :q, '%'))) group by b.name order by count(p) desc
select coalesce(sum(p.price * p.stock), 0) from Product p
```

**SearchLogRepository**
```sql
select s.queryText, count(s) from SearchLog s where s.createdAt >= :since group by s.queryText order by count(s) desc
delete from SearchLog s where s.userId = :userId
```

**WarehouseInventoryRepository**
```sql
select coalesce(sum(i.quantity), 0) from WarehouseInventory i where i.productId = :productId
select i from WarehouseInventory i where i.warehouseId = :warehouseId and i.quantity <= :threshold
```
