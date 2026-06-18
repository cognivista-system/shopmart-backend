-- ShopMart - Phase 4 (vendor product approval)
-- Adds the rejection reason column used by the vendor product approval workflow.
-- New ProductStatus values (PENDING_APPROVAL, REJECTED) are application-level enum
-- strings stored in the existing products.status VARCHAR column, so no DDL is needed for them.

ALTER TABLE products ADD COLUMN IF NOT EXISTS rejection_reason VARCHAR(1000);
