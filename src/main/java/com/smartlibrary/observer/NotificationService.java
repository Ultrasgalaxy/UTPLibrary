package com.smartlibrary.observer;
import java.util.ArrayList;
public class NotificationService implements Subject {
    private ArrayList<Observer> observers = new ArrayList<>();
    @Override public void addObserver(Observer o)    { observers.add(o); }
    @Override public void removeObserver(Observer o) { observers.remove(o); }
    @Override public void notifyObservers(String msg) {
        for (Observer o : observers) o.update(msg);
    }
}
