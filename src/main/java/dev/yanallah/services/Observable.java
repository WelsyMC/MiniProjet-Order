package dev.yanallah.services;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Observable<T> {
    private final List<Consumer<T>> observers = new ArrayList<>();
    private T value;

    public Observable(T initialValue) {
        this.value = initialValue;
    }

    public Observable() {
        this(null);
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
        notifyObservers();
    }

    public void subscribe(Consumer<T> observer) {
        observers.add(observer);
        // Notifier immédiatement avec la valeur actuelle si elle existe
        if (value != null) {
            observer.accept(value);
        }
    }

    public void unsubscribe(Consumer<T> observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (Consumer<T> observer : observers) {
            observer.accept(value);
        }
    }

    public void reload() {
        notifyObservers();
    }
} 