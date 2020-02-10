package org.bric.gui.input;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

public class ListModel<E> extends AbstractListModel<E> {

    private final List<E> elements;

    public ListModel() {
        elements = new CopyOnWriteArrayList<>();
    }

    public void addElement(E element) {
        int index = elements.size();
        elements.add(element);
        fireIntervalAdded(this, index, index);
    }

    public E get(int index) {
        return elements.get(index);
    }

    public boolean contains(Predicate<E> predicate) {
        return elements.stream()
                       .anyMatch(predicate);
    }

    public void remove(int[] indices) {
        for (int i=indices.length-1; i>=0; i--) {
            elements.remove(indices[i]);
        }
        fireIntervalRemoved(this, indices[0], indices[indices.length-1]);
    }

    public void clear() {
        int lastIndex = elements.size()-1;
        elements.clear();
        fireIntervalRemoved(this, 0, lastIndex);
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public List<E> getElements() {
        return Collections.unmodifiableList(elements);
    }

    @Override
    public int getSize() {
        return elements.size();
    }

    @Override
    public E getElementAt(int index) {
        return elements.get(index);
    }
}
