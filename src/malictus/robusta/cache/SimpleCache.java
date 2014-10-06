package malictus.robusta.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A very simple cache that stores objects that are addressable
 * by keys.  A maximum size for the cache may be specified as
 * well as simple guidelines for when and how to purge objects
 * from a nearly full cache.
 */
public class SimpleCache<K, V> {

    /**
     * The map that backs the cache.
     */
    private Map<K, CacheEntry<V>> cache;

    /** 
     * A list of cache listeners that will received
     * notice when changes are made to the cache.
     */
    private List<SimpleCacheListener<K, V>> cacheListeners;
    
    /**
     * The number of milliseconds an item may be in the cache
     * without being accessed before it is considered eligible
     * for routine cleaning.  There is no guarantee that an 
     * item will remain in the cache for this long.  If this
     * value is less than or equal to zero, items will never
     * be purged from the cache unless it is full, in which 
     * case only one will be purged at a time.
     */
    private long timeToLive;
    
    /**
     * The maximum number of elements that may be stored in 
     * the cache.
     */
    private int maxSize;

    /**
     * Creates a simple cache with a maximum capacity of
     * 'max' items.
     */
    public SimpleCache(int max) {
        this(0, max);
    }

    /**
     * Creates a simple cache with a maximum capacity of
     * 'max' items which for performance reasons may choose
     * to purge any item that has been in the cache for 'ttl'
     * milliseconds.
     */
    public SimpleCache(long ttl, int max) {
        this.cache = new HashMap<K, CacheEntry<V>>();
        this.cacheListeners = new ArrayList<SimpleCacheListener<K, V>>();
        this.timeToLive = ttl;
        this.maxSize = max;
    }
    
    /**
     * Adds an item to the cache, potentially resulting in the
     * removal of one or more existing items.
     * @param key the key for the cached object which may be
     * used to later retrieve it.
     * @param value the object to be cached.
     */
    public void cacheObject(K key, V value) {
        if (this.cache.size() == this.maxSize) {
            this.makeSpace(1);
        }
        this.cache.put(key, new CacheEntry<V>(value));
        this.fireAddedItem(key, value);
    }
    
    /**
     * Fetches an item from the cache (if it has been
     * retained).
     * @param key the key for the cached object
     * @return the cached object or null if no item with
     * the given key has been retained by the cache.
     */
    public V getItem(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry != null) {
            entry.lastAccessedDate = System.currentTimeMillis();
            this.fireHit(key, entry.value);
            return entry.getValue();
        } else {
            this.fireMiss(key);
            return null;
        }
    }

    /**
     * Fetches a Collection containing all values currently 
     * cached.
     */
    public Collection<V> values() {
        ArrayList<V> values = new ArrayList<V>();
        for (CacheEntry<V> valueEntry : this.cache.values()) {
            values.add(valueEntry.getValue());
        }
        return values;
    }
    
    /**
     * Removes the item corresponding with the given key from
     * the cache.
     */
    public void invalidateItem(K key) {
        if (this.cache.containsKey(key)) {
            this.fireRemovedItem(key, this.cache.get(key).value);
        }
        this.cache.remove(key);
    }
    
    /**
     * Fully clears the cache.
     */
    public void invalidate() {
        for (Map.Entry<K, CacheEntry<V>> entry : this.cache.entrySet()) {
            this.fireRemovedItem(entry.getKey(), entry.getValue().value);
        }
        this.cache.clear();
    }

    private void makeSpace(int count) {
        long currentTime = System.currentTimeMillis();
        int numberLeftToPurge = count;
        /*
         * A List of entries whose first 'numberLeftToPurge'
         * entries are guaranteed to be sorted (if present)
         * from longest idle time to least.
         */
        List<Map.Entry<K, CacheEntry<V>>> oldest = new LinkedList<Map.Entry<K, CacheEntry<V>>>();
        Iterator<Map.Entry<K, CacheEntry<V>>> entryIt = this.cache.entrySet().iterator();
        while (entryIt.hasNext()) {
            Map.Entry<K, CacheEntry<V>> entry = entryIt.next();
            if (this.timeToLive > 0) {
                long idleTime = currentTime - entry.getValue().lastAccessedDate;
                if (idleTime > this.timeToLive) {
                    this.fireRemovedItem(entry.getKey(), entry.getValue().getValue());
                    entryIt.remove();
                    numberLeftToPurge --;
                    continue;
                }
            }
            /* do a partial insertion sort */
            for (int i = 0; i < numberLeftToPurge; i ++) {
                if (oldest.size() <= i) {
                    // first entry in this position
                    oldest.add(entry);
                    break;
                } else {
                    // more idle than the entry in this position
                    if (oldest.get(i).getValue().lastAccessedDate > entry.getValue().lastAccessedDate) {
                        oldest.add(i, entry);
                        break;
                    }
                }
            }
        }
        int i = 0;
        for (Map.Entry<K, CacheEntry<V>> oldestEntry : oldest) {
            if (i ++ >= numberLeftToPurge) { 
                break;
            }
            this.fireRemovedItem(oldestEntry.getKey(), oldestEntry.getValue().getValue());
            this.cache.remove(oldestEntry.getKey());
        }
    }
    
    /**
     * Adds a listener that will be notified for certain cache events.
     */
    public void addCacheListener(SimpleCacheListener<K, V> l) {
        this.cacheListeners.add(l);
    }

    /**
     * Removes a listener that will no longer be notified of
     * certain cache events.
     */
    public void removeCacheListener(SimpleCacheListener<K, V> l) {
        this.cacheListeners.remove(l);
    }
    
    private void fireRemovedItem(K key, V value) {
        for (SimpleCacheListener<K, V> l : this.cacheListeners) {
            l.notifyItemRemoved(key, value);
        }
    }
    
    private void fireAddedItem(K key, V value) {
        for (SimpleCacheListener<K, V> l : this.cacheListeners) {
            l.notifyItemAdded(key, value);
        }
    }
    
    private void fireHit(K key, V value) {
        for (SimpleCacheListener<K, V> l : this.cacheListeners) {
            l.notifyItemHit(key, value);
        }
    }
    
    private void fireMiss(K key) {
        for (SimpleCacheListener<K, V> l : this.cacheListeners) {
            l.notifyItemMiss(key);
        }
    }

    private static class CacheEntry<V> {
        private V value;
        private long lastAccessedDate;
        
        public CacheEntry(V value) {
            this.value = value;
            this.lastAccessedDate = System.currentTimeMillis();
        }
        
        public V getValue() {
            return this.value;
        }
    }

}
