-- ============================================================================
-- ShopMart — sample catalog seed data (Phase 1)
-- Run AFTER schema.sql:   psql -d shopmart -f seed.sql
--
-- Users are intentionally NOT seeded here: the application seeds a default
-- admin on first startup (see DataInitializer), which produces a proper
-- BCrypt password hash. Seeding users in raw SQL would require a precomputed
-- hash and could collide with that startup logic.
--
-- Foreign keys are resolved by slug via sub-selects, so this script does not
-- depend on specific generated id values and is safe to re-run on a fresh DB.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Categories
-- ----------------------------------------------------------------------------
INSERT INTO categories (name, slug, description, active) VALUES
    ('Electronics', 'electronics', 'Phones, laptops, audio and accessories', TRUE),
    ('Fashion',     'fashion',     'Clothing, footwear and accessories',     TRUE),
    ('Home & Living','home-living', 'Furniture, decor and kitchen essentials', TRUE);

-- ----------------------------------------------------------------------------
-- Brands
-- ----------------------------------------------------------------------------
INSERT INTO brands (name, slug, description, active) VALUES
    ('Acme',     'acme',     'Everyday electronics and gadgets', TRUE),
    ('Northwind','northwind','Apparel and lifestyle',            TRUE),
    ('Contoso',  'contoso',  'Home and living products',         TRUE);

-- ----------------------------------------------------------------------------
-- Products
-- ----------------------------------------------------------------------------
INSERT INTO products (name, slug, description, sku, price, sale_price, stock, category_id, brand_id, status, featured, rating_average, rating_count)
VALUES
    ('Wireless Headphones', 'wireless-headphones',
     'Over-ear Bluetooth headphones with active noise cancellation and 30-hour battery.',
     'ACM-WH-001', 4999.00, 3999.00, 120,
     (SELECT id FROM categories WHERE slug = 'electronics'),
     (SELECT id FROM brands WHERE slug = 'acme'),
     'ACTIVE', TRUE, 4.50, 210),

    ('Smart Watch Series 5', 'smart-watch-series-5',
     'Fitness and health tracking smartwatch with AMOLED display and GPS.',
     'ACM-SW-005', 8999.00, NULL, 75,
     (SELECT id FROM categories WHERE slug = 'electronics'),
     (SELECT id FROM brands WHERE slug = 'acme'),
     'ACTIVE', TRUE, 4.20, 134),

    ('Classic Cotton T-Shirt', 'classic-cotton-t-shirt',
     'Soft 100% cotton crew-neck tee, pre-shrunk, available in multiple colours.',
     'NW-TS-010', 799.00, 599.00, 300,
     (SELECT id FROM categories WHERE slug = 'fashion'),
     (SELECT id FROM brands WHERE slug = 'northwind'),
     'ACTIVE', FALSE, 4.10, 88),

    ('Running Shoes Pro', 'running-shoes-pro',
     'Lightweight cushioned running shoes with breathable mesh upper.',
     'NW-RS-021', 3499.00, 2799.00, 60,
     (SELECT id FROM categories WHERE slug = 'fashion'),
     (SELECT id FROM brands WHERE slug = 'northwind'),
     'ACTIVE', TRUE, 4.40, 156),

    ('Ceramic Coffee Mug Set', 'ceramic-coffee-mug-set',
     'Set of 4 hand-glazed ceramic mugs, 350ml each, microwave and dishwasher safe.',
     'CON-MG-030', 1299.00, NULL, 200,
     (SELECT id FROM categories WHERE slug = 'home-living'),
     (SELECT id FROM brands WHERE slug = 'contoso'),
     'ACTIVE', FALSE, 4.00, 42),

    ('LED Desk Lamp', 'led-desk-lamp',
     'Adjustable LED desk lamp with 3 colour temperatures and USB charging port.',
     'CON-DL-031', 1899.00, 1499.00, 0,
     (SELECT id FROM categories WHERE slug = 'home-living'),
     (SELECT id FROM brands WHERE slug = 'contoso'),
     'OUT_OF_STOCK', FALSE, 4.30, 67);

-- ----------------------------------------------------------------------------
-- Product images (one primary image per product; position 0 = thumbnail)
-- ----------------------------------------------------------------------------
INSERT INTO product_images (product_id, url, alt, position) VALUES
    ((SELECT id FROM products WHERE slug = 'wireless-headphones'),    'https://picsum.photos/seed/headphones/800/800', 'Wireless Headphones', 0),
    ((SELECT id FROM products WHERE slug = 'smart-watch-series-5'),   'https://picsum.photos/seed/smartwatch/800/800', 'Smart Watch Series 5', 0),
    ((SELECT id FROM products WHERE slug = 'classic-cotton-t-shirt'), 'https://picsum.photos/seed/tshirt/800/800',     'Classic Cotton T-Shirt', 0),
    ((SELECT id FROM products WHERE slug = 'running-shoes-pro'),      'https://picsum.photos/seed/shoes/800/800',      'Running Shoes Pro', 0),
    ((SELECT id FROM products WHERE slug = 'ceramic-coffee-mug-set'), 'https://picsum.photos/seed/mugs/800/800',       'Ceramic Coffee Mug Set', 0),
    ((SELECT id FROM products WHERE slug = 'led-desk-lamp'),          'https://picsum.photos/seed/lamp/800/800',       'LED Desk Lamp', 0);

-- ----------------------------------------------------------------------------
-- A couple of variants for the apparel items
-- ----------------------------------------------------------------------------
INSERT INTO product_variants (product_id, sku, size, color, price, stock) VALUES
    ((SELECT id FROM products WHERE slug = 'classic-cotton-t-shirt'), 'NW-TS-010-S-BLK', 'S', 'Black', 599.00, 100),
    ((SELECT id FROM products WHERE slug = 'classic-cotton-t-shirt'), 'NW-TS-010-M-BLK', 'M', 'Black', 599.00, 120),
    ((SELECT id FROM products WHERE slug = 'classic-cotton-t-shirt'), 'NW-TS-010-L-WHT', 'L', 'White', 599.00, 80),
    ((SELECT id FROM products WHERE slug = 'running-shoes-pro'),      'NW-RS-021-9',     '9', 'Grey',  2799.00, 30),
    ((SELECT id FROM products WHERE slug = 'running-shoes-pro'),      'NW-RS-021-10',    '10','Grey',  2799.00, 30);
