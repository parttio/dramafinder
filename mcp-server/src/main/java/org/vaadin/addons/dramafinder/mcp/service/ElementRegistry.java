package org.vaadin.addons.dramafinder.mcp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.vaadin.addons.dramafinder.element.VaadinElement;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Registry for tracking found Vaadin elements during a session.
 * <p>
 * Elements are stored with a TTL and automatically cleaned up.
 */
@Service
@EnableScheduling
public class ElementRegistry {

    private static final Logger log = LoggerFactory.getLogger(ElementRegistry.class);

    private final Map<String, ElementEntry> elements = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    @Value("${dramafinder.session.max-elements:100}")
    private int maxElements;

    @Value("${dramafinder.session.element-ttl:300000}")
    private long elementTtl;

    /**
     * Register an element and return its ID.
     *
     * @param element     the Vaadin element
     * @param elementType the element type name
     * @return the assigned element ID
     */
    public String register(VaadinElement element, String elementType) {
        cleanupIfNeeded();

        String id = "el_" + idCounter.incrementAndGet();
        elements.put(id, new ElementEntry(element, elementType, Instant.now()));
        log.debug("Registered element {} of type {}", id, elementType);
        return id;
    }

    /**
     * Get an element by ID.
     *
     * @param id the element ID
     * @return the element entry or null if not found or expired
     */
    public ElementEntry get(String id) {
        ElementEntry entry = elements.get(id);
        if (entry == null) {
            return null;
        }

        if (isExpired(entry)) {
            elements.remove(id);
            log.debug("Element {} expired", id);
            return null;
        }

        // Update last accessed time
        entry.updateAccessTime();
        return entry;
    }

    /**
     * Check if an element exists and is valid.
     *
     * @param id the element ID
     * @return true if element exists and is not expired
     */
    public boolean exists(String id) {
        return get(id) != null;
    }

    /**
     * Remove an element from the registry.
     *
     * @param id the element ID
     */
    public void remove(String id) {
        elements.remove(id);
    }

    /**
     * Clear all elements from the registry.
     */
    public void clear() {
        elements.clear();
        log.info("Element registry cleared");
    }

    /**
     * Get the number of registered elements.
     *
     * @return the element count
     */
    public int size() {
        return elements.size();
    }

    private boolean isExpired(ElementEntry entry) {
        return Instant.now().toEpochMilli() - entry.lastAccessed().toEpochMilli() > elementTtl;
    }

    private void cleanupIfNeeded() {
        if (elements.size() >= maxElements) {
            cleanupExpired();
        }
    }

    @Scheduled(fixedRateString = "${dramafinder.session.cleanup-interval:60000}")
    public void cleanupExpired() {
        int before = elements.size();
        elements.entrySet().removeIf(entry -> isExpired(entry.getValue()));
        int removed = before - elements.size();
        if (removed > 0) {
            log.debug("Cleaned up {} expired elements", removed);
        }
    }

    /**
     * Entry representing a registered element.
     */
    public static class ElementEntry {
        private final VaadinElement element;
        private final String elementType;
        private final Instant createdAt;
        private volatile Instant lastAccessed;

        public ElementEntry(VaadinElement element, String elementType, Instant createdAt) {
            this.element = element;
            this.elementType = elementType;
            this.createdAt = createdAt;
            this.lastAccessed = createdAt;
        }

        public VaadinElement element() {
            return element;
        }

        public String elementType() {
            return elementType;
        }

        public Instant createdAt() {
            return createdAt;
        }

        public Instant lastAccessed() {
            return lastAccessed;
        }

        void updateAccessTime() {
            this.lastAccessed = Instant.now();
        }
    }
}
