/*
 *  Copyright (C) 2010 Markus Echterhoff <tam@edu.uni-klu.ac.at>,
 *                      Daniel Hoelbling (http://www.tigraine.at)
 *
 *  This file is part of EvoPaint.
 *
 *  EvoPaint is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with EvoPaint.  If not, see <http://www.gnu.org/licenses/>.
 */

package evopaint.gui;

import evopaint.Selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;

/**
 * Holds a list of selections
 *
 * @author Markus Echterhoff <tam@edu.uni-klu.ac.at>
 * @author Daniel Hoelbling (http://www.tigraine.at)
 */
public class SelectionList extends Observable implements Collection<Selection> {
    private ArrayList<Selection> selections = new ArrayList<Selection>();

    public boolean add(Selection selection) {
        boolean retVal = selections.add(selection);

        notifyOfChange(ChangeType.ITEM_ADDED, selection);
        return retVal;
    }

    public boolean remove(Object o) {
        boolean retVal = selections.remove(o);
        notifyOfChange(ChangeType.ITEM_DELETED, o);
        return retVal;
    }

    private void notifyOfChange(ChangeType type, Object o) {
        this.setChanged();
        notifyObservers(new SelectionListEventArgs(type, (Selection)o));
    }

    public boolean containsAll(Collection<?> c) {
        return selections.containsAll(c);
    }

    public boolean addAll(Collection<? extends Selection> c) {
        return selections.addAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return selections.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return selections.retainAll(c);
    }

    public void clear() {
        selections.clear();
        notifyOfChange(ChangeType.LIST_CLEARED, null);
    }

    public int size() {
        return selections.size();
    }

    public boolean isEmpty() {
        return selections.isEmpty();
    }

    public boolean contains(Object o) {
        return selections.contains(o);
    }

    public Iterator<Selection> iterator() {
        return selections.iterator();
    }

    public Object[] toArray() {
        return selections.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return selections.toArray(a);
    }

    public enum ChangeType { ITEM_ADDED, ITEM_DELETED, LIST_CLEARED };

    public class SelectionListEventArgs {
        private ChangeType changeType;
        private Selection selection;

        public ChangeType getChangeType() {
            return changeType;
        }

        public SelectionListEventArgs(ChangeType changeType, Selection selection) {
            this.changeType = changeType;
            this.selection = selection;
        }

        public Selection getSelection() {
            return selection;
        }
    }
}
