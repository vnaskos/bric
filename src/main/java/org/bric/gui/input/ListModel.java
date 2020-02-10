package org.bric.gui.input;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class ListModel<E> extends AbstractListModel<E> {

    private final List<E> elements;

    public ListModel() {
        elements = new ArrayList<>();
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

    public void remove(int index) {
        if (index >= elements.size()) {
            return;
        }

        elements.remove(index);
        fireIntervalRemoved(this, index, index);
    }

    public void clear() {
        elements.clear();
        fireIntervalRemoved(this, 0, elements.size()-1);
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
