package com.example.chefk.maddemo18.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DataItem implements Serializable {
    
    class DataItemChainedComparator implements Comparator<DataItem> {

        private List<Comparator<DataItem>> listComparators;

        @SafeVarargs
        public DataItemChainedComparator(Comparator<DataItem>... comparators) {
            this.listComparators = Arrays.asList(comparators);
        }

        @Override
        public int compare(DataItem emp1, DataItem emp2) {
            for (Comparator<DataItem> comparator : listComparators) {
                int result = comparator.compare(emp1, emp2);
                if (result != 0) {
                    return result;
                }
            }
            return 0;
        }
    }

    private long id = -1;
    private String name;
    private String description;
    private long expiry;
    private boolean done;
    private boolean favorite;

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }

    private List<String> contacts;

    public DataItem() {

    }

    public DataItem(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getExpiry() {
        return expiry;
    }

    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public  String toString() {
        return this.id + ":" + this.name;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public static Comparator<DataItem> SORT_BY_NAME = new Comparator<DataItem>() {
        @Override
        public int compare(DataItem item1, DataItem item2) {
            return String.valueOf(item1.getName()).toLowerCase().compareTo(String.valueOf(item2.getName()).toLowerCase());
        }
    };

    public static Comparator<DataItem> SORT_BY_ID = new Comparator<DataItem>() {
        @Override
        public int compare(DataItem item1, DataItem item2) {
            return (int)(item1.getId() - item2.getId());
        }
    };

    public static Comparator<DataItem> SORT_BY_DATE = new Comparator<DataItem>() {
        @Override
        public int compare(DataItem item1, DataItem item2) { // sort expiry, then done
            for (int x=1; x<=2; x++) { // test chained compare
                if (x==1) {
                    int result = -1 * Boolean.compare(item1.isDone(), item2.isDone());
                    if (result != 0) { return result; }
                }
                if (x==2) {
                    int result = -1 * (int)(item1.getExpiry() - item2.getExpiry());
                    if (result != 0) { return result; }
                }
            } // end for (int x=1; x<=2; x++)
            return 0;
        }
    };

    public static Comparator<DataItem> SORT_BY_FAVORITE = new Comparator<DataItem>() { // had to increase API level from 17 to 19
        @Override
        public int compare(DataItem item1, DataItem item2) {
            for (int x=1; x<=2; x++) { // test chained compare
                if (x==1) {
                    int result = -1 * Boolean.compare(item1.isDone(), item2.isDone());
                    if (result != 0) { return result; }
                }
                if (x==2) {
                    int result = -1 * Boolean.compare(item1.isFavorite(), item2.isFavorite());
                    if (result != 0) { return result; }
                }
            } // end for (int x=1; x<=2; x++)
            return 0;
            /*
            // vor multi sort
            boolean b1 = item1.isFavorite();
            boolean b2 = item2.isFavorite();
            //return Boolean.compare(b1, b2); // Call requires API level 19 (current min is 17): java.lang.Boolean#compare
            return -1 * Boolean.compare(b1, b2); // reverseOrder, Call requires API level 19 (current min is 17): java.lang.Boolean#compare
            */
        }
    };

    public static Comparator<DataItem> SORT_BY_DONE = new Comparator<DataItem>() { // had to increase API level from 17 to 19
        @Override
        public int compare(DataItem item1, DataItem item2) {
            boolean b1 = item1.isDone();
            boolean b2 = item2.isDone();
            //return Boolean.compare(b1, b2); // Call requires API level 19 (current min is 17): java.lang.Boolean#compare
            return -1 * Boolean.compare(b1, b2); // reverseOrder? Call requires API level 19 (current min is 17): java.lang.Boolean#compare
        }
    };
}