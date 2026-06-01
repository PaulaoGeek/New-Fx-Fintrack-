package fintrack.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RepositorioGenerico<T> {
    private List<T> items = new ArrayList<>();

    public void add(T item) { items.add(item); }
    public void remove(T item) { items.remove(item); }
    public List<T> listAll() { return Collections.unmodifiableList(items); }
    public void addAll(List<? extends T> newItems) { this.items.addAll(newItems); }
    public void copyTo(List<? super T> destination) { destination.addAll(this.items); }
}
