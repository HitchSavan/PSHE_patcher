package patcher.utils.data_utils;

public class Pair <F extends Comparable<F>, S extends Comparable<S>> implements Comparable<Pair <F , S >> {
    
    public F first;
    public S second;

    public Pair() {}

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public Pair(Pair<F, S> copy) {
        this.first = copy.first;
        this.second = copy.second;
    }

    public void set(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public void set(Pair<F, S> copy) {
        this.first = copy.first;
        this.second = copy.second;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (o instanceof Pair) {
            return (((Pair<F,S>)o).first == this.first && ((Pair<F,S>)o).second == this.second);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = 13;
        return 37 * result + this.first.hashCode() + this.second.hashCode();
    }

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append("{ ");
        str.append(this.first.toString());
        str.append(" : ");
        str.append(this.second.toString());
        str.append(" }");
        return str.toString();
    }

    @Override
    public int compareTo(Pair<F, S> o) {
        int result = 0;
        if ((result = this.first.compareTo(o.first)) == 0) {
            return (int)(this.second.compareTo(o.second));
        } else {
            return result;
        } 
    }
}
