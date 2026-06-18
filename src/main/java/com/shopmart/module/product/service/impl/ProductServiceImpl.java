package com.shopmart.module.product.service.impl;

import com.shopmart.common.dto.PageResponse;
import com.shopmart.common.exception.ResourceNotFoundException;
import com.shopmart.module.brand.entity.Brand;
import com.shopmart.module.brand.repository.BrandRepository;
import com.shopmart.module.category.entity.Category;
import com.shopmart.module.category.repository.CategoryRepository;
import com.shopmart.module.product.dto.*;
import com.shopmart.module.product.entity.Product;
import com.shopmart.module.product.entity.ProductImage;
import com.shopmart.module.product.entity.ProductStatus;
import com.shopmart.module.product.entity.ProductVariant;
import com.shopmart.module.product.mapper.ProductMapper;
import com.shopmart.module.product.repository.ProductRepository;
import com.shopmart.module.search.provider.SearchIndexer;
import com.shopmart.module.product.repository.ProductSpecifications;
import com.shopmart.module.product.service.ProductService;
import com.shopmart.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final SearchIndexer searchIndexer;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    @Override
    @CacheEvict(cacheNames = "products", allEntries = true)
    @Transactional
    public ProductResponse create(ProductRequest request) {
        String slug = uniqueSlug(SlugUtils.slugify(request.name()));
        Product p = new Product();
        p.setSlug(slug);
        apply(p, request);
        return saveAndIndex(p);
    }

    @Override
    @CacheEvict(cacheNames = "products", allEntries = true)
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product p = find(id);
        p.getImages().clear();
        p.getVariants().clear();
        apply(p, request);
        return saveAndIndex(p);
    }

    @Override
    @CacheEvict(cacheNames = "products", allEntries = true)
    @Transactional
    public void delete(Long id) {
        productRepository.delete(find(id));
        searchIndexer.delete(id);
    }

    @Override
    @Cacheable(cacheNames = "products", key = "'id:' + #id")
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return ProductMapper.toResponse(find(id));
    }

    @Override
    @Cacheable(cacheNames = "products", key = "'slug:' + #slug")
    @Transactional(readOnly = true)
    public ProductResponse getBySlug(String slug) {
        return ProductMapper.toResponse(productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "slug", slug)));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductSummary> search(ProductFilter filter, Pageable pageable) {
        Specification<Product> spec = Specification
                .where(ProductSpecifications.keyword(filter.q()))
                .and(ProductSpecifications.inCategory(filter.categoryId()))
                .and(ProductSpecifications.forBrand(filter.brandId()))
                .and(ProductSpecifications.featured(filter.featured()))
                .and(ProductSpecifications.priceBetween(filter.minPrice(), filter.maxPrice()))
                .and(ProductSpecifications.hasStatus(ProductStatus.ACTIVE));
        Page<ProductSummary> page = productRepository.findAll(spec, pageable).map(ProductMapper::toSummary);
        return PageResponse.from(page);
    }

    @Override
    @Cacheable(cacheNames = "products", key = "'featured:' + #limit")
    @Transactional(readOnly = true)
    public List<ProductSummary> featured(int limit) {
        Specification<Product> spec = Specification
                .where(ProductSpecifications.featured(true))
                .and(ProductSpecifications.hasStatus(ProductStatus.ACTIVE));
        Page<Product> page = productRepository.findAll(spec, PageRequest.of(0, limit));
        return ProductMapper.toSummaryList(page.getContent());
    }

    @Override
    @CacheEvict(cacheNames = "products", allEntries = true)
    @Transactional
    public void updateStock(Long id, int stock) {
        Product p = find(id);
        p.setStock(stock);
        if (stock <= 0 && p.getStatus() == ProductStatus.ACTIVE) {
            p.setStatus(ProductStatus.OUT_OF_STOCK);
        }
        productRepository.save(p);
        searchIndexer.index(p);
    }

    // ---- vendor & approval ----

    @Override
    @CacheEvict(cacheNames = "products", allEntries = true)
    @Transactional
    public ProductResponse createForVendor(Long vendorId, ProductRequest request) {
        String slug = uniqueSlug(SlugUtils.slugify(request.name()));
        Product p = new Product();
        p.setSlug(slug);
        apply(p, request);
        p.setVendorId(vendorId);
        p.setStatus(ProductStatus.PENDING_APPROVAL);
        p.setRejectionReason(null);
        return saveAndIndex(p);
    }

    @Override
    @CacheEvict(cacheNames = "products", allEntries = true)
    @Transactional
    public ProductResponse updateForVendor(Long vendorId, Long productId, ProductRequest request) {
        Product p = find(productId);
        if (p.getVendorId() == null || !p.getVendorId().equals(vendorId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        p.getImages().clear();
        p.getVariants().clear();
        apply(p, request);
        p.setVendorId(vendorId);
        p.setStatus(ProductStatus.PENDING_APPROVAL);   // re-submit for approval after edits
        p.setRejectionReason(null);
        return saveAndIndex(p);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductSummary> pendingApproval(Pageable pageable) {
        Page<ProductSummary> page = productRepository
                .findByStatus(ProductStatus.PENDING_APPROVAL, pageable).map(ProductMapper::toSummary);
        return PageResponse.from(page);
    }

    @Override
    @CacheEvict(cacheNames = "products", allEntries = true)
    @Transactional
    public ProductResponse approve(Long id) {
        Product p = find(id);
        p.setStatus(p.getStock() <= 0 ? ProductStatus.OUT_OF_STOCK : ProductStatus.ACTIVE);
        p.setRejectionReason(null);
        return saveAndIndex(p);
    }

    @Override
    @CacheEvict(cacheNames = "products", allEntries = true)
    @Transactional
    public ProductResponse reject(Long id, String reason) {
        Product p = find(id);
        p.setStatus(ProductStatus.REJECTED);
        p.setRejectionReason(reason);
        return saveAndIndex(p);
    }

    // ---- helpers ----

    private Product find(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    private ProductResponse saveAndIndex(Product p) {
        Product saved = productRepository.save(p);
        searchIndexer.index(saved);
        return ProductMapper.toResponse(saved);
    }

    private String uniqueSlug(String base) {
        String slug = base;
        if (productRepository.existsBySlug(slug)) {
            slug = base + "-" + UUID.randomUUID().toString().substring(0, 6);
        }
        return slug;
    }

    private void apply(Product p, ProductRequest r) {
        p.setName(r.name());
        p.setDescription(r.description());
        p.setSku(r.sku());
        p.setPrice(r.price());
        p.setSalePrice(r.salePrice());
        p.setStock(r.stock() == null ? 0 : r.stock());
        p.setFeatured(r.featured() != null && r.featured());
        p.setMetaTitle(r.metaTitle());
        p.setMetaDescription(r.metaDescription());
        p.setStatus(parseStatus(r.status()));

        if (r.categoryId() != null) {
            Category category = categoryRepository.findById(r.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", r.categoryId()));
            p.setCategory(category);
        } else {
            p.setCategory(null);
        }

        if (r.brandId() != null) {
            Brand brand = brandRepository.findById(r.brandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", r.brandId()));
            p.setBrand(brand);
        } else {
            p.setBrand(null);
        }

        if (r.images() != null) {
            int pos = 0;
            for (ProductImageDto dto : r.images()) {
                ProductImage img = new ProductImage();
                img.setUrl(dto.url());
                img.setAlt(dto.alt());
                img.setPosition(dto.position() != 0 ? dto.position() : pos++);
                p.addImage(img);
            }
        }

        if (r.variants() != null) {
            for (ProductVariantDto dto : r.variants()) {
                ProductVariant v = new ProductVariant();
                v.setSku(dto.sku());
                v.setSize(dto.size());
                v.setColor(dto.color());
                v.setPrice(dto.price());
                v.setStock(dto.stock());
                p.addVariant(v);
            }
        }
    }

    private ProductStatus parseStatus(String status) {
        if (status == null) return ProductStatus.DRAFT;
        try {
            return ProductStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ProductStatus.DRAFT;
        }
    }
}
