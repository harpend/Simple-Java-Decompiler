
public class factorial {
    public static void main(String[] args) {
        int n = 12;
        int t = getF(n);
        System.out.println(t);
    }

    private static int getF(int n) {
        if (n > 1) {
            return n * getF(n-1);
        }

        return n;
    }
}
