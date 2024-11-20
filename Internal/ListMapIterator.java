package RoboZero.MiniSprite.Internal;

import androidx.annotation.NonNull;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ListMapIterator<K, V> implements Iterable<V>{
    private final Map<K, List<V>> map;

    public ListMapIterator(Map<K, List<V>> map){
        this.map = map;
    }

    @NonNull
    @Override
    public Iterator<V> iterator() {
        return new Iterator<V>() {
            private final Iterator<Map.Entry<K, List<V>>> mapIterator = map.entrySet().iterator();
            private Iterator<V> listIterator = null;

            @Override
            public boolean hasNext() {
                // If current list iterator is known and not finished
                if (listIterator != null && listIterator.hasNext()) {
                    return true;
                }

                // If no current list iterator, get next one in map.
                while (mapIterator.hasNext()) {
                    listIterator = mapIterator.next().getValue().iterator();
                    if (listIterator.hasNext()) {
                        return true;
                    }
                }

                return false;
            }

            @Override
            public V next() {
                // Throw error if nothing to iterate.
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }

                return listIterator.next();
            }
        };
    }
}
