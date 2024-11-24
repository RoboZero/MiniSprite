package MiniSprite.Internal;

import android.util.Pair;

import androidx.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.TreeMap;

import MiniSprite.Transform2D;

/**
 * MiniSpriteSurface will iterate and draw transforms in this collection.
 * Draw in back -> lower sorting order
 * Draw in front -> higher sorting order
 * Items drawn in front will overlay those drawn in back, hence nome sorting layer. (Painters algorithm)
 */
public class DrawnTransformCollection implements Iterable<Transform2D> {
    private final Map<Transform2D, Integer> transformToSortingLayer = new HashMap<>();
    private final Map<Integer, List<Transform2D>> sortingLayerToTransforms = new TreeMap<>();

    // Cannot add to transform while iterating in sorted order, need to delay add/remove
    private final Queue<Pair<Transform2D, Integer>> instantiateQueue = new ArrayDeque<>();
    private final Queue<Transform2D> destroyQueue = new ArrayDeque<>();
    private boolean dirty;

    /**
     * Add transform for MiniSpriteSurface to draw. Instantiate -> added next draw.
     * @param transform Transform for MiniSpriteSurface to draw and call components of
     * @param sortingLayer Draw in back -> lower sorting order. Draw in front -> higher sorting order
     */
    public void instantiateTransform(@NonNull Transform2D transform, int sortingLayer){
        instantiateQueue.add(new Pair<>(transform, sortingLayer));
        dirty = true;
    }

    /**
     * Find and remove transform from being drawn. Destroy -> destroy next draw;
     * @param transform Transform to try to remove.
     */
    public void destroyTransform(@NonNull Transform2D transform){
        destroyQueue.add(transform);
        dirty = true;
    }

    private void addTransform(@NonNull Transform2D transform, int sortingLayer){
        if(!sortingLayerToTransforms.containsKey(sortingLayer)){
            sortingLayerToTransforms.put(sortingLayer, new ArrayList<>());
        }

        List<Transform2D> transforms = sortingLayerToTransforms.get(sortingLayer);
        assert transforms != null;
        transforms.add(transform);
        transformToSortingLayer.put(transform, sortingLayer);
    }

    private void removeTransform(@NonNull Transform2D transform){
        if (!transformToSortingLayer.containsKey(transform)) {
            return;
        }

        Integer sortingLayer = transformToSortingLayer.get(transform);
        List<Transform2D> transforms = sortingLayerToTransforms.get(sortingLayer);
        Objects.requireNonNull(transforms).remove(transform);
    }

    private void resolveTransformUpdates(){
        for (Pair<Transform2D, Integer> pair: instantiateQueue) {
            addTransform(pair.first, pair.second);
        }
        instantiateQueue.clear();

        for (Transform2D transform: destroyQueue) {
            removeTransform(transform);
        }
        destroyQueue.clear();
    }

    @NonNull
    @Override
    public Iterator<Transform2D> iterator() {
        if(dirty){
            resolveTransformUpdates();
            dirty = false;
        }

        return new ListMapIterator<>(sortingLayerToTransforms).iterator();
    }
}
