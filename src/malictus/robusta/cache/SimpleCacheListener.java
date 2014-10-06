package malictus.robusta.cache;

/**
 * A listener interface to receive messages from a 
 * {@link SimpleCacheListener}.  Implementations of this
 * interface may be useful to logging cache activities 
 * or to trigger any cleanup required for items that fall
 * out of the cache.
 */
public interface SimpleCacheListener<K, V> {

    /**
     * Invoked when an item is being added to the cache.
     */
    public void notifyItemAdded(K key, V value);

    /**
     * Invoked when an item is removed from the cache
     * for any reason. 
     */
    public void notifyItemRemoved(K key, V value);

    /**
     * Invoked on a cache hit: a request for an item 
     * that is already in the cache.
     */
    public void notifyItemHit(K key, V value);
    
    /**
     * Invoked on a cache miss: a request for an item
     * that is not currently cached.
     */
    public void notifyItemMiss(K key);
    
}
