package lazyassociativeclassifier;

import java.util.LinkedHashMap;

/**
 * A Last-Recently-Used cache implementation
 * 
 * @author Adriano Veloso (algorithm and original C++ implementation)
 * @author Gesse Dafe (Java implementation)
 */
public class LRU<K, V> extends LinkedHashMap<K, V>
{
        private static final long serialVersionUID = -8788545308834303897L;

        private final int capacity;

        /**
         * @param capacity
         */
        public LRU(int capacity)
        {
                super(capacity, 1.1f, true);
                this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest)
        {
                return size() > capacity;
        }
}
